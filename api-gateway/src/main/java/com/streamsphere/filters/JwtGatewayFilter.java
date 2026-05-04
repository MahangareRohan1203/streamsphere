package com.streamsphere.filters;

import com.streamsphere.components.GatewayConstants;
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
        String authHeader = exchange.getRequest()
                .getHeaders()
                .getFirst("Authorization");
        
        boolean isPublic = isPublicEndpoint(exchange);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                String username = jwtUtil.extractUsername(token);
                String role = jwtUtil.extractRole(token);
                
                log.info("Gateway extracted user: {}, role: {} for path: {}", username, role, exchange.getRequest().getURI().getPath());
                
                // Mutate request to add user info headers for downstream services
                // AND COMPLETELY REMOVE Authorization header so downstream services don't try to re-validate
                ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                        .header(GatewayConstants.USER_NAME_HEADER, username)
                        .header(GatewayConstants.USER_ROLE_HEADER, role)
                        .headers(httpHeaders -> httpHeaders.remove("Authorization"))
                        .build();
                
                return chain.filter(exchange.mutate().request(mutatedRequest).build());
            } catch (Exception e) {
                log.error("JWT validation failed: {}", e.getMessage());
                if (!isPublic) {
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    return exchange.getResponse().setComplete();
                }
            }
        } else if (!isPublic) {
            log.warn("Missing or invalid Authorization header for non-public path: {}", exchange.getRequest().getURI().getPath());
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        
        return chain.filter(exchange);
    }

    /**
     * Determines if a request should bypass JWT validation.
     * 
     * Rationale for Video Endpoints:
     * GET and HEAD requests to /api/videos/** are permitted without a JWT at the gateway level.
     * This enables unauthenticated users to browse the video catalog and view thumbnails/previews.
     * Access control for full video streaming (tier-based) is enforced downstream in the 
     * Video Service, ensuring granular security without blocking discovery.
     */
    private boolean isPublicEndpoint(ServerWebExchange exchange) {
        String path = exchange.getRequest().getURI().getPath();
        HttpMethod method = exchange.getRequest().getMethod();

        // Always allow OPTIONS requests for CORS preflight
        if (HttpMethod.OPTIONS.equals(method)) {
            return true;
        }

        // 1. Auth, Eureka, Actuator are always public
        if (path.contains(GatewayConstants.AUTH_PATH_PREFIX) || 
            path.contains(GatewayConstants.EUREKA_PATH_PREFIX) || 
            path.contains(GatewayConstants.ACTUATOR_PATH_PREFIX)) {
            return true;
        }

        // 2. User Registration (POST /users) is public
        if (path.startsWith(GatewayConstants.USERS_PATH) && HttpMethod.POST.equals(method)) {
            return true;
        }

        // 3. Video Discovery (GET /api/videos/**) is public
        if (path.startsWith("/api/videos") && HttpMethod.GET.equals(method)) {
            return true;
        }

        // 3. Video Listing and Streaming (GET/HEAD /api/videos/**) is public
        if (path.startsWith("/api/videos") && (HttpMethod.GET.equals(method) || HttpMethod.HEAD.equals(method))) {
            return true;
        }

        return false;
    }
}
