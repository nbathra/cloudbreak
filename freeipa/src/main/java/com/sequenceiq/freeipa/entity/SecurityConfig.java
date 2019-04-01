package com.sequenceiq.freeipa.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;

import com.sequenceiq.cloudbreak.aspect.secret.SecretValue;
import com.sequenceiq.cloudbreak.domain.Secret;
import com.sequenceiq.cloudbreak.domain.SecretToString;

@Entity
public class SecurityConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "securityconfig_generator")
    @SequenceGenerator(name = "securityconfig_generator", sequenceName = "securityconfig_id_seq", allocationSize = 1)
    private Long id;

    private Long stackId;

    @Convert(converter = SecretToString.class)
    @SecretValue
    private Secret clientKey = Secret.EMPTY;

    @Convert(converter = SecretToString.class)
    @SecretValue
    private Secret clientCert = Secret.EMPTY;


    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private SaltSecurityConfig saltSecurityConfig;

    @Column(nullable = false)
    private boolean usePrivateIpToTls;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setClientKey(String clientKey) {
        this.clientKey = new Secret(clientKey);
    }

    public void setClientKey(Secret clientKey) {
        this.clientKey = clientKey;
    }

    public String getClientKey() {
        return clientKey.getRaw();
    }

    public String getClientKeySecret() {
        return clientKey.getSecret();
    }

    public String getClientCert() {
        return clientCert.getRaw();
    }

    public String getClientCertSecret() {
        return clientCert.getSecret();
    }

    public void setClientCert(String clientCert) {
        this.clientCert = new Secret(clientCert);
    }

    public SaltSecurityConfig getSaltSecurityConfig() {
        return saltSecurityConfig;
    }

    public void setSaltSecurityConfig(SaltSecurityConfig saltSecurityConfig) {
        this.saltSecurityConfig = saltSecurityConfig;
    }

    public boolean isUsePrivateIpToTls() {
        return usePrivateIpToTls;
    }

    public void setUsePrivateIpToTls(boolean usePrivateIpToTls) {
        this.usePrivateIpToTls = usePrivateIpToTls;
    }

    public Long getStackId() {
        return stackId;
    }

    public void setStackId(Long stackId) {
        this.stackId = stackId;
    }
}
