package com.streamsphere.filters;

import com.streamsphere.config.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class JwtGatewayFilter implements GlobalFilter {
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        
        // Check if endpoint is public
        if (isPublicEndpoint(exchange)) {
            return chain.filter(exchange);
        }
        
        String authHeader = exchange.getRequest()
                .getHeaders()
                .getFirst("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Missing or invalid Authorization header for path: {}", exchange.getRequest().getURI().getPath());
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        
        String token = authHeader.substring(7);
        
        try {
            String username = jwtUtil.extractUsername(token);
            String role = jwtUtil.extractRole(token);
            
            log.info("Gateway extracted user: {}, role: {} for path: {}", username, role, exchange.getRequest().getURI().getPath());
            
            // Mutate request to add user info headers for downstream services
            ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                    .header("X-User-Name", username)
                    .header("X-User-Role", role)
                    .build();
            
            return chain.filter(exchange.mutate().request(mutatedRequest).build());
            
        } catch (Exception e) {
            log.error("JWT validation failed: {}", e.getMessage());
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }

    private boolean isPublicEndpoint(ServerWebExchange exchange) {
        String path = exchange.getRequest().getURI().getPath();
        HttpMethod method = exchange.getRequest().getMethod();

        // 1. Auth, Eureka, Actuator are always public
        if (path.contains("/auth") || path.contains("/eureka") || path.contains("/actuator")) {
            return true;
        }

        // 2. User Registration (POST /users) is public
        if (path.equals("/users") && HttpMethod.POST.equals(method)) {
            return true;
        }

        return false;
    }
}
