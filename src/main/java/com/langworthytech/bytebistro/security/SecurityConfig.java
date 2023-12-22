package com.langworthytech.bytebistro.security;

import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import com.nimbusds.jwt.proc.JWTClaimsSetAwareJWSKeySelector;
import com.nimbusds.jwt.proc.JWTProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authorization.AuthorityAuthorizationManager;
import org.springframework.security.authorization.AuthorizationManagers;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.*;
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

    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);
    private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter;
    private final TrustedIssuerRepository trustedIssuerRepository;
    private final TenantContext tenantContext;

    public SecurityConfig(JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter, TrustedIssuerRepository trustedIssuerRepository, TenantContext tenantContext) {
        this.jwtGrantedAuthoritiesConverter = jwtGrantedAuthoritiesConverter;
        this.trustedIssuerRepository = trustedIssuerRepository;
        this.tenantContext = tenantContext;
    }

    @Bean
    public SecurityFilterChain configure(HttpSecurity httpSecurity) throws Exception {

        Map<String, AuthenticationManager> authenticationManagers = new HashMap<>();

        JwtIssuerAuthenticationManagerResolver authenticationManagerResolver =
                new JwtIssuerAuthenticationManagerResolver(authenticationManagers::get);

        List<String> issuers = trustedIssuerRepository.findAllIssuers().stream().toList();
        issuers.forEach(issuer -> addManager(authenticationManagers, issuer));

        httpSecurity.authorizeHttpRequests(authorize ->
                authorize.requestMatchers("/menu/admin/**").access(AuthorizationManagers.allOf(
                                AuthorityAuthorizationManager.hasAnyAuthority("SCOPE_kitchen.admin"),
                                AuthorityAuthorizationManager.hasRole("kitchen-admin")
                        ))

                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> {
                            oauth2.authenticationManagerResolver(authenticationManagerResolver);
//                            oauth2.jwt(jwtConfigurer ->
//                                    jwtConfigurer.jwtAuthenticationConverter(customJwtAuthenticationConverter()));

                        }

                );
        return httpSecurity.build();
    }

    @Bean
    JWTProcessor jwtProcessor(JWTClaimsSetAwareJWSKeySelector keySelector) {
        // this is executed
        log.info("keySelector: {}", keySelector);
        ConfigurableJWTProcessor<SecurityContext> jwtProcessor = new DefaultJWTProcessor<>();
        jwtProcessor.setJWTClaimsSetAwareJWSKeySelector(keySelector);
        return jwtProcessor;
    }

    @Bean
    JwtDecoder jwtDecoder(JWTProcessor jwtProcessor, OAuth2TokenValidator<Jwt> jwtValidator) {
        // this is executed
        log.info("jwtProcessor: {}", jwtProcessor.toString());
        NimbusJwtDecoder decoder = new NimbusJwtDecoder(jwtProcessor);
        OAuth2TokenValidator<Jwt> validator = new DelegatingOAuth2TokenValidator<Jwt>(JwtValidators.createDefault(), jwtValidator);
        decoder.setJwtValidator(validator);
        return decoder;
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
        log.info("Issuer added to authentication manager: {}", issuer);
        JwtAuthenticationProvider authenticationProvider = new JwtAuthenticationProvider(JwtDecoders.fromIssuerLocation(issuer));
        authenticationManagers.put(issuer, authenticationProvider::authenticate);
    }

}
