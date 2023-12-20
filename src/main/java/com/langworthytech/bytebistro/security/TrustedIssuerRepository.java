package com.langworthytech.bytebistro.security;

import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class TrustedIssuerRepository {

    private final Map<String, String> issuers;

    public TrustedIssuerRepository() {
        issuers = new HashMap<>(3);
        issuers.put("karens_kitchen", "http://localhost:8080/realms/karens-kitchen");
        issuers.put("joes_grub_hub", "http://localhost:8080/realms/joes-grub-hub");
        issuers.put("code_and_coffee", "http://localhost:8080/realms/code-and-coffee");
    }

    public Collection<String> findAllIssuers() {
        return issuers.values();
    }

    public String findById(String tenantId) {
        return issuers.get(tenantId);
    }
}
