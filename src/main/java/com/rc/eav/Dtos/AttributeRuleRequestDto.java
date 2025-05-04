package com.rc.eav.Dtos;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public class AttributeRuleRequestDto {

    @Schema(description = "The name of the entity", example = "Person")
    private String entityName;

    @Schema(description = "The name of the attribute", example = "age")
    private String attributeName;

    @Schema(description = "The ID of the validation rule (e.g., LENGTH, MIN_MAX, ALLOWED_VALUES, REGEX)", example = "LENGTH")
    private String ruleId;

    @Schema(description = "List of rule parameters (e.g., [1, 32] for LENGTH, [10, 20] for MIN_MAX)", example = "[1, 32]")
    private List<String> ruleParams;

    // Getters and Setters
    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    public List<String> getRuleParams() {
        return ruleParams;
    }

    public void setRuleParams(List<String> ruleParams) {
        this.ruleParams = ruleParams;
    }
}