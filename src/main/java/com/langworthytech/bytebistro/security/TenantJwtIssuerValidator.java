package com.langworthytech.bytebistro.security;

import com.langworthytech.bytebistro.model.Tenant;
import com.langworthytech.bytebistro.repository.TenantRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtIssuerValidator;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TenantJwtIssuerValidator implements OAuth2TokenValidator<Jwt> {

    private static final Logger log = LoggerFactory.getLogger(TenantJwtIssuerValidator.class);
    private final TenantRepository tenants;
    private final Map<String, JwtIssuerValidator> validators = new ConcurrentHashMap<>();

    public TenantJwtIssuerValidator(TenantRepository tenants) {
        this.tenants = tenants;
    }

    @Override
    public OAuth2TokenValidatorResult validate(Jwt token) {
        return validators.computeIfAbsent(toTenant(token), this::fromTenant).validate(token);
    }

    private String toTenant(Jwt jwt) {
        return jwt.getIssuer().toString();
    }

    private JwtIssuerValidator fromTenant(String issuer) {

        int lastIndex = issuer.lastIndexOf("/");
        String tenantId = issuer.substring(lastIndex + 1);

        Tenant tenant = tenants.findByTenantId(tenantId).orElseThrow(() ->
                new IllegalArgumentException("Unknown tenant!"));
        return new JwtIssuerValidator(tenant.getIssuer());
    }
}
