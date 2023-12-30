package com.langworthytech.bytebistro.model;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "tenants", schema = "common")
public class Tenant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tenantId;

    private String issuer;

    private String jwksUri;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getJwksUri() {
        return jwksUri;
    }

    public void setJwksUri(String jwksUri) {
        this.jwksUri = jwksUri;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tenant tenant = (Tenant) o;
        return Objects.equals(id, tenant.id) && Objects.equals(tenantId, tenant.tenantId) && Objects.equals(issuer, tenant.issuer) && Objects.equals(jwksUri, tenant.jwksUri);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, tenantId, issuer, jwksUri);
    }

    @Override
    public String toString() {
        return "Tenant{" +
                "id='" + id + '\'' +
                ", tenantId='" + tenantId + '\'' +
                ", issuer='" + issuer + '\'' +
                ", jwksUri='" + jwksUri + '\'' +
                '}';
    }
}
