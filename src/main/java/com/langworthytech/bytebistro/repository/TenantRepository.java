package com.langworthytech.bytebistro.repository;

import com.langworthytech.bytebistro.model.Tenant;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;
import java.util.Optional;

public interface TenantRepository extends CrudRepository<Tenant, String> {

    Optional<Tenant> findByTenantId(String tenantId);

}
