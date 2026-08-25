package com.example.Api_Gateway.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class RequestInfoFilter implements GlobalFilter {

    private static final Logger log = LoggerFactory.getLogger(RequestInfoFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("Create filter");
        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> securityContext.getAuthentication())
                .flatMap(authentication -> {

                    if (authentication.getPrincipal() instanceof OidcUser oidcUser) {
                        String preferredUsername =
                                oidcUser.getClaimAsString("preferred_username");
                        ServerHttpRequest request = exchange.getRequest()
                                .mutate()
                                .headers(headers -> headers.remove("X-User-Login"))
                                .header("X-User-Login", preferredUsername)
                                .build();
                        log.info("Request {}", preferredUsername);
                        return chain.filter(
                                exchange.mutate()
                                        .request(request)
                                        .build()
                        );
                    }
                    return chain.filter(exchange);
                });
    }
}