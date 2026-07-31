package com.example.Api_Gateway.service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcReactiveOAuth2UserService;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.userinfo.ReactiveOAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class KeycloakReactiveUserService
        implements ReactiveOAuth2UserService<OidcUserRequest, OidcUser> {

    private final OidcReactiveOAuth2UserService delegate =
            new OidcReactiveOAuth2UserService();
    @Override
    public Mono<OidcUser> loadUser(OidcUserRequest userRequest) {

        return delegate.loadUser(userRequest)
                .map(oidcUser -> {

                    Set<GrantedAuthority> authorities =
                            new HashSet<>(oidcUser.getAuthorities());


                    Map<String,Object> realmAccess =
                            oidcUser.getClaim("realm_access");


                    if (realmAccess != null) {

                        List<String> roles =
                                (List<String>) realmAccess.get("roles");


                        if (roles != null) {

                            roles.forEach(role ->
                                    authorities.add(
                                            new SimpleGrantedAuthority(
                                                    "ROLE_" + role
                                            )
                                    )
                            );
                        }
                    }


                    return new DefaultOidcUser(
                            authorities,
                            oidcUser.getIdToken(),
                            oidcUser.getUserInfo()
                    );
                });
    }
}


