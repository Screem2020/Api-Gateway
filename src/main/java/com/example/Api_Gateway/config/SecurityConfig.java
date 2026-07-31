package com.example.Api_Gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchange -> exchange
                        .pathMatchers("/login/**", "/oauth2/**").permitAll()
                        .pathMatchers("/debug/auth/**").authenticated()
                        .pathMatchers("/debug/roles/**").authenticated()
                        .pathMatchers("/api/v1/flow-manager/**").authenticated()
                        .anyExchange().authenticated())
//                .oauth2Login(Customizer.withDefaults())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
                .build();
//        return http
//                .csrf(ServerHttpSecurity.CsrfSpec::disable)
//                .authorizeExchange(exchange -> exchange
//                        .pathMatchers("/admin/**")
//                        .hasRole("ADMIN")
//                        .pathMatchers("/user/**")
//                        .hasRole("USER")
//                        .anyExchange()
//                        .authenticated())
//                .oauth2ResourceServer(oauth2 ->
//                        oauth2.jwt(Customizer.withDefaults()))
//                .build();
    }

}
