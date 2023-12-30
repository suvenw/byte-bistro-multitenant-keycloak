package com.langworthytech.bytebistro.security;


import org.hibernate.cfg.AvailableSettings;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

@Component
public class TenantIdentifierResolver implements CurrentTenantIdentifierResolver, HibernatePropertiesCustomizer {

    private static final Logger log = LoggerFactory.getLogger(TenantIdentifierResolver.class);

//    private final String tenentId = "account_1";

    @Override
    public String resolveCurrentTenantIdentifier() {

        log.info("Current tenantId found in context: {}", TenantContext.getTenantId());

        return Objects.requireNonNullElse(TenantContext.getTenantId(), "common");
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return false;
    }

    @Override
    public void customize(Map<String, Object> hibernateProperties) {
        hibernateProperties.put(AvailableSettings.MULTI_TENANT_IDENTIFIER_RESOLVER, this);
    }
}
