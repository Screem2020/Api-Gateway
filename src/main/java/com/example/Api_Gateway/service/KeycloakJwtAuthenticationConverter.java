package com.example.Api_Gateway.service;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class KeycloakJwtAuthenticationConverter
        implements Converter<Jwt, Mono<? extends AbstractAuthenticationToken>> {

    @Override
    public Mono<? extends AbstractAuthenticationToken> convert(Jwt jwt) {

        Map<String, Object> realmAccess = jwt.getClaim("realm_access");

        if (realmAccess == null) {
            return Mono.just(new JwtAuthenticationToken(jwt));
        }

        Object rolesObject = realmAccess.get("roles");

        if (!(rolesObject instanceof List<?> roles)) {
            return Mono.just(new JwtAuthenticationToken(jwt));
        }

        Collection<SimpleGrantedAuthority> authorities = roles.stream()
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .toList();

        return Mono.just(
                new JwtAuthenticationToken(jwt, authorities)
        );
    }
}
