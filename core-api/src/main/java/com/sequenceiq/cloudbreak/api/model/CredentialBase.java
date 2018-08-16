package com.sequenceiq.cloudbreak.api.model;

import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.sequenceiq.cloudbreak.doc.ModelDescriptions;
import com.sequenceiq.cloudbreak.doc.ModelDescriptions.CredentialModelDescription;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public abstract class CredentialBase implements JsonEntity {

    @ApiModelProperty(value = ModelDescriptions.NAME, required = true)
    private @Size(max = 100, min = 5, message = "The length of the credential's name has to be in range of 5 to 100")
    @Pattern(regexp = "(^[a-z][-a-z0-9]*[a-z0-9]$)",
            message = "The name of the credential can only contain lowercase alphanumeric characters and hyphens and has start with an alphanumeric character")
    @NotNull String name;

    @ApiModelProperty(value = ModelDescriptions.CLOUD_PLATFORM, required = true)
    private @NotNull String cloudPlatform;

    @ApiModelProperty(CredentialModelDescription.PARAMETERS)
    private Map<String, Object> parameters = new HashMap<>();

    @ApiModelProperty(ModelDescriptions.DESCRIPTION)
    private @Size(max = 1000) String description;

    @ApiModelProperty(ModelDescriptions.TOPOLOGY_ID)
    private Long topologyId;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCloudPlatform() {
        return cloudPlatform;
    }

    public void setCloudPlatform(String cloudPlatform) {
        this.cloudPlatform = cloudPlatform;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }

    public Long getTopologyId() {
        return topologyId;
    }

    public void setTopologyId(Long topologyId) {
        this.topologyId = topologyId;
    }

    @Override
    public String toString() {
        return "CredentialBase{"
                + "name='" + name + '\''
                + ", cloudPlatform='" + cloudPlatform + '\''
                + ", parameters=" + parameters
                + ", description='" + description + '\''
                + ", topologyId=" + topologyId
                + '}';
    }

}
