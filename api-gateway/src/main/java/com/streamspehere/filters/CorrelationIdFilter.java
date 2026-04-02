package com.streamspehere.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class CorrelationIdFilter implements GlobalFilter {

    private static final Logger log = LoggerFactory.getLogger(CorrelationIdFilter.class);

    private static final String HEADER = "X-Correlation-ID";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String correlationId = exchange.getRequest().getHeaders().getFirst(HEADER);

        if (correlationId == null) {
            correlationId = UUID.randomUUID().toString();
        }

        // Add header to request
        ServerHttpRequest mutatedRequest = exchange.getRequest()
                .mutate()
                .header(HEADER, correlationId)
                .build();

        log.info("Generated Correlation ID: {}", correlationId);

        return chain.filter(exchange.mutate().request(mutatedRequest).build());
    }
}
