package com.example.Api_Gateway.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class RequestInfoFilter implements GlobalFilter {

    private static final Logger log = LoggerFactory.getLogger(RequestInfoFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest requestBuilder = exchange.getRequest().mutate().build();
        return ReactiveSecurityContextHolder.getContext()
                .flatMap(securityContext -> Mono.justOrEmpty(securityContext.getAuthentication()))
                .flatMap(Mono::justOrEmpty)
                .map(principal -> (Jwt) principal)
                .flatMap(jwt -> {
                    String preferredUsername = jwt.getClaimAsString("X-User-Login");
                    String subject = jwt.getSubject();
                    requestBuilder.mutate().headers(headers -> headers.remove("X-User-login")).build();
                    if (preferredUsername != null && subject != null) {
                        requestBuilder.mutate()
                                .header("X-User-Login", preferredUsername)
                                .header("X-User-Id", subject)
                                .build();
                    }
                    return chain.filter(exchange.mutate().request(requestBuilder).build());
                });
    }
}