package com.langworthytech.bytebistro.security;

import com.langworthytech.bytebistro.model.Tenant;
import com.langworthytech.bytebistro.repository.TenantRepository;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.KeySourceException;
import com.nimbusds.jose.proc.JWSAlgorithmFamilyJWSKeySelector;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.JWTClaimsSetAwareJWSKeySelector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.jwt.JwtIssuerValidator;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.security.Key;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TenantJwsKeySelector implements JWTClaimsSetAwareJWSKeySelector<SecurityContext> {

    private static final Logger log = LoggerFactory.getLogger(TenantJwsKeySelector.class);
    private final TenantRepository tenantRepository;
    private final Map<String, JWSKeySelector<SecurityContext>> selectors = new ConcurrentHashMap<>();

    public TenantJwsKeySelector(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    @Override
    public List<? extends Key> selectKeys(JWSHeader header, JWTClaimsSet claimsSet, SecurityContext context) throws KeySourceException {
        log.info("selectKeys method called");
        return selectors.computeIfAbsent(toTenant(claimsSet), this::fromTenant).selectJWSKeys(header, context);
    }

    private String toTenant(JWTClaimsSet claimSet) {
        log.info("toTenant iss claim: {}", claimSet.getClaim("iss"));
        return (String) claimSet.getClaim("iss");
    }

    private JWSKeySelector<SecurityContext> fromTenant(String issuer) {

        int lastIndex = issuer.lastIndexOf("/");
        String tenantId = issuer.substring(lastIndex + 1);

        log.info("fromTenant method in TenantJwsKeySelector - tenantId: {}", tenantId);

        Tenant tenant = tenantRepository.findByTenantId(tenantId).orElseThrow(() ->
                new IllegalArgumentException("Unknown tenant!"));
        return fromUri(tenant.getJwksUri());
    }

    private JWSKeySelector<SecurityContext> fromUri(String uri) {
        try {
            return JWSAlgorithmFamilyJWSKeySelector.fromJWKSetURL(new URL(uri));
        } catch (Exception ex) {
            throw new IllegalArgumentException(ex);
        }
    }
}
