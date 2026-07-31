package com.example.Api_Gateway.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class TestController {

    @GetMapping("/test")
    public Mono<String> test(
            @AuthenticationPrincipal Mono<OAuth2User> user
    ) {

        return user
                .map(OAuth2User::getAttributes)
                .map(Object::toString);
    }

    @GetMapping("/debug/auth")
    public String auth(Authentication authentication) {
        return authentication.getAuthorities().toString();
    }

    @GetMapping("/debug/roles")
    public Mono<String> roles(Authentication authentication) {
        return Mono.just(
                authentication.getAuthorities().toString()
        );
    }
}
