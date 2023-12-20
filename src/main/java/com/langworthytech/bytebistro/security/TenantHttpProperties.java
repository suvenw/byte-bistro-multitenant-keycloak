package com.langworthytech.bytebistro.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "multitenancy.http")
public record TenantHttpProperties(
        String headerName
){}