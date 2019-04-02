package com.sequenceiq.freeipa.entity;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;

@Entity
public class Stack {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "stack_generator")
    @SequenceGenerator(name = "stack_generator", sequenceName = "stack_id_seq", allocationSize = 1)
    private Long id;

    private String name;

    private String region;

    private Long created;

    @Column(columnDefinition = "TEXT")
    private String platformvariant;

    private String availabilityzone;

    @Column(columnDefinition = "TEXT")
    private String cloudplatform;

    private Integer gatewayport;

    private String publickey;

    @OneToMany(mappedBy = "stack", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<InstanceMetaData> instanceMetaData;

    @OneToOne(mappedBy = "stack", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    private SecurityConfig securityConfig;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public Long getCreated() {
        return created;
    }

    public void setCreated(Long created) {
        this.created = created;
    }

    public String getPlatformvariant() {
        return platformvariant;
    }

    public void setPlatformvariant(String platformvariant) {
        this.platformvariant = platformvariant;
    }

    public String getAvailabilityzone() {
        return availabilityzone;
    }

    public void setAvailabilityzone(String availabilityzone) {
        this.availabilityzone = availabilityzone;
    }

    public String getCloudplatform() {
        return cloudplatform;
    }

    public void setCloudplatform(String cloudplatform) {
        this.cloudplatform = cloudplatform;
    }

    public Integer getGatewayport() {
        return gatewayport;
    }

    public void setGatewayport(Integer gatewayport) {
        this.gatewayport = gatewayport;
    }

    public String getPublickey() {
        return publickey;
    }

    public void setPublickey(String publickey) {
        this.publickey = publickey;
    }

    public Set<InstanceMetaData> getInstanceMetaData() {
        return instanceMetaData;
    }

    public void setInstanceMetaData(Set<InstanceMetaData> instanceMetaData) {
        this.instanceMetaData = instanceMetaData;
    }

    public SecurityConfig getSecurityConfig() {
        return securityConfig;
    }

    public void setSecurityConfig(SecurityConfig securityConfig) {
        this.securityConfig = securityConfig;
    }
}
