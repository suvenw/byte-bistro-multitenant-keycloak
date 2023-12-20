package com.langworthytech.bytebistro.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authorization.AuthorityAuthorizationManager;
import org.springframework.security.authorization.AuthorizationManagers;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.oauth2.server.resource.authentication.JwtIssuerAuthenticationManagerResolver;
import org.springframework.security.web.SecurityFilterChain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter;

    private final TrustedIssuerRepository trustedIssuerRepository;

    public SecurityConfig(JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter, TrustedIssuerRepository trustedIssuerRepository) {
        this.jwtGrantedAuthoritiesConverter = jwtGrantedAuthoritiesConverter;
        this.trustedIssuerRepository = trustedIssuerRepository;
    }

    @Bean
    public SecurityFilterChain configure(HttpSecurity httpSecurity) throws Exception {

        Map<String, AuthenticationManager> authenticationManagers = new HashMap<>();

        JwtIssuerAuthenticationManagerResolver authenticationManagerResolver =
                new JwtIssuerAuthenticationManagerResolver(authenticationManagers::get);

        List<String> issuers = trustedIssuerRepository.findAllIssuers().stream().toList();
        issuers.stream().forEach(issuer -> addManager(authenticationManagers, issuer));

        httpSecurity.authorizeHttpRequests(authorize ->
                authorize.requestMatchers("/menu/admin/**").access(AuthorizationManagers.allOf(
                                AuthorityAuthorizationManager.hasAnyAuthority("SCOPE_kitchen.admin"),
                                AuthorityAuthorizationManager.hasRole("kitchen-admin")
                        ))

                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> {
//                            oauth2.jwt(jwtConfigurer ->
//                                    jwtConfigurer.jwtAuthenticationConverter(customJwtAuthenticationConverter()));
                            oauth2.authenticationManagerResolver(authenticationManagerResolver);
                        }

                );
        return httpSecurity.build();
    }

    private JwtAuthenticationConverter customJwtAuthenticationConverter() {

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
        return converter;
    }

//    private JwtIssuerAuthenticationManagerResolver authenticationManagerResolver() {
//        return JwtIssuerAuthenticationManagerResolver.fromTrustedIssuers(trustedIssuerRepository.findAllIssuers());
//    }

    private void addManager(Map<String, AuthenticationManager> authenticationManagers, String issuer) {
        JwtAuthenticationProvider authenticationProvider = new JwtAuthenticationProvider(JwtDecoders.fromIssuerLocation(issuer));
        authenticationManagers.put(issuer, authenticationProvider::authenticate);
    }

}
