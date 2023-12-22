package com.langworthytech.bytebistro.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tenants", schema = "common")
public class Tenant {

    @Id
    @Column(unique = true, nullable = false)
    private String id;


}
