package com.langworthytech.bytebistro.security;

import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtIssuerValidator;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TenantJwtIssuerValidator implements OAuth2TokenValidator<Jwt> {

    private final TrustedIssuerRepository tenants;
    private final Map<String, JwtIssuerValidator> validators = new ConcurrentHashMap<>();

    public TenantJwtIssuerValidator(TrustedIssuerRepository tenants) {
        this.tenants = tenants;
    }

    @Override
    public OAuth2TokenValidatorResult validate(Jwt token) {
        return validators.computeIfAbsent(toTenant(token), this::fromTenant).validate(token);
    }

    private String toTenant(Jwt jwt) {
        return jwt.getIssuer().toString();
    }

    private JwtIssuerValidator fromTenant(String tenantId) {
        return Optional.ofNullable(this.tenants.findById(tenantId))
                .map(JwtIssuerValidator::new)
                .orElseThrow(() -> new IllegalArgumentException("unknown tenant!"));
    }
}
