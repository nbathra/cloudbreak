/*
 * YARN Simplified API layer for services
 * Bringing a new service on YARN today is not a simple experience. The APIs of existing frameworks are either too low level (native YARN), require writing new code (for frameworks with programmatic APIs) or writing a complex spec (for declarative frameworks).  This simplified REST API can be used to create and manage the lifecycle of YARN services. In most cases, the application owner will not be forced to make any changes to their applications. This is primarily true if the application is packaged with containerization technologies like Docker.  This document describes the API specifications (aka. YarnFile) for deploying/managing containerized services on YARN. The same JSON spec can be used for both REST API and CLI to manage the services.
 *
 * OpenAPI spec version: 1.0.0
 *
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */


package org.apache.cb.yarn.service.api.records;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * ResourceInformation determines unit/value of resource types in addition to memory and vcores. It will be part of Resource object.
 */
@ApiModel(description = "ResourceInformation determines unit/value of resource types in addition to memory and vcores. It will be part of Resource object.")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2018-08-08T16:59:40.572+02:00")
public class ResourceInformation {
    @JsonProperty("value")
    private Long value = null;

    @JsonProperty("unit")
    private String unit = null;

    public ResourceInformation value(Long value) {
        this.value = value;
        return this;
    }

    /**
     * Integer value of the resource.
     *
     * @return value
     **/
    @ApiModelProperty(value = "Integer value of the resource.")
    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }

    public ResourceInformation unit(String unit) {
        this.unit = unit;
        return this;
    }

    /**
     * Unit of the resource, acceptable values are - p/n/u/m/k/M/G/T/P/Ki/Mi/Gi/Ti/Pi. By default it is empty means no unit.
     *
     * @return unit
     **/
    @ApiModelProperty(value = "Unit of the resource, acceptable values are - p/n/u/m/k/M/G/T/P/Ki/Mi/Gi/Ti/Pi. By default it is empty means no unit.")
    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }


    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ResourceInformation resourceInformation = (ResourceInformation) o;
        return Objects.equals(this.value, resourceInformation.value) &&
                Objects.equals(this.unit, resourceInformation.unit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, unit);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class ResourceInformation {\n");

        sb.append("    value: ").append(toIndentedString(value)).append("\n");
        sb.append("    unit: ").append(toIndentedString(unit)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(java.lang.Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }

}

