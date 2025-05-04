package com.rc.eav.Dtos;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.Map;

public class AttributeRequestDto {
    @Schema(description = "The name of the entity to which attributes will be added", example = "Person")
    private String entityName;


    @Schema(description = "attribute names and data types to be added to the entity",
            example = "{\"name\": \"STRING\", \"height\": \"DOUBLE\", \"age\": \"INTEGER\"}")
    private Map<String, String> attributesInfo;

    // Getters and Setters
    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public Map<String, String> getAttributesInfo() {
        return attributesInfo;
    }

    public void setAttributesInfo(Map<String, String> attributesInfo) {
        this.attributesInfo = attributesInfo;
    }


}
