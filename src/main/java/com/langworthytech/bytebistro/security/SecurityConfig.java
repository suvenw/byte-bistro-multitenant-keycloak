package com.langworthytech.bytebistro.security;

import com.langworthytech.bytebistro.repository.TenantRepository;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import com.nimbusds.jwt.proc.JWTClaimsSetAwareJWSKeySelector;
import com.nimbusds.jwt.proc.JWTProcessor;
import jakarta.servlet.http.HttpServletRequest;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);
    private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter;
    private final TenantRepository tenantRepository;

    public SecurityConfig(JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter, TenantRepository tenantRepository) {
        this.jwtGrantedAuthoritiesConverter = jwtGrantedAuthoritiesConverter;
        this.tenantRepository = tenantRepository;
    }

    @Bean
    public SecurityFilterChain configure(HttpSecurity httpSecurity) throws Exception {

        Map<String, AuthenticationManager> authenticationManagers = new HashMap<>();

        JwtIssuerAuthenticationManagerResolver authenticationManagerResolver =
                new JwtIssuerAuthenticationManagerResolver(authenticationManagers::get);

        List<String> issuers = tenantRepository.findAllIssuers().stream().toList();
        issuers.forEach(issuer -> addManager(authenticationManagers, issuer));

        httpSecurity.authorizeHttpRequests(authorize ->
                authorize.requestMatchers("/menu/admin/**").access(AuthorizationManagers.allOf(
                                AuthorityAuthorizationManager.hasAnyAuthority("SCOPE_kitchen.admin"),
                                AuthorityAuthorizationManager.hasRole("kitchen-admin")
                        ))

                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> {
                            oauth2.authenticationManagerResolver(authenticationManagerResolver);
                        });
        return httpSecurity.build();
    }

    @Bean
    JWTProcessor<SecurityContext> jwtProcessor(JWTClaimsSetAwareJWSKeySelector<SecurityContext> keySelector) {
        // this is executed
        log.info("keySelector: {}", keySelector.toString());
        ConfigurableJWTProcessor<SecurityContext> jwtProcessor = new DefaultJWTProcessor<>();
        jwtProcessor.setJWTClaimsSetAwareJWSKeySelector(keySelector);
        return jwtProcessor;
    }

    @Bean
    JwtDecoder jwtDecoder(JWTProcessor<SecurityContext> jwtProcessor, OAuth2TokenValidator<Jwt> jwtValidator) {
        // this is executed
        log.info("jwtProcessor: {}", jwtProcessor.toString());
        NimbusJwtDecoder decoder = new NimbusJwtDecoder(jwtProcessor);
        OAuth2TokenValidator<Jwt> validator = new DelegatingOAuth2TokenValidator<Jwt>(JwtValidators.createDefault(), jwtValidator);
        decoder.setJwtValidator(validator);
        return decoder;
    }

    private void addManager(Map<String, AuthenticationManager> authenticationManagers, String issuer) {
        log.info("Issuer added to authentication manager: {}", issuer);
        JwtAuthenticationProvider authenticationProvider = new JwtAuthenticationProvider(JwtDecoders.fromIssuerLocation(issuer));
        authenticationProvider.setJwtAuthenticationConverter(customJwtAuthenticationConverter());
        authenticationManagers.put(issuer, authenticationProvider::authenticate);
    }

    private JwtAuthenticationConverter customJwtAuthenticationConverter() {

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
        return converter;
    }

}
