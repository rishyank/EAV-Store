package com.rc.eav.schema;

import com.rc.eav.Validation.ValidationRule;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AttributeDefinition {

    private final String attributeName;
    private final String attributeDataType;



    private final Map<String, ValidationRule> validationRules = new HashMap<>();

    public AttributeDefinition (String attributeName, String attributeDataType) {

        Objects.requireNonNull(attributeName, "Attribute name cannot be null.");
        Objects.requireNonNull(attributeDataType, "Attribute data type cannot be null.");
        this.attributeName = attributeName;
        this.attributeDataType = attributeDataType;

        if (!AttributeTypeRegistry.containsType(attributeDataType)) {
            throw new IllegalArgumentException("Invalid attribute type: " + attributeDataType);
        }
    }
    public void addValidationRule(ValidationRule rule) {
        String ruleId = rule.getRuleId();
        if (validationRules.containsKey(ruleId)) {
            throw new IllegalArgumentException("Rule with ID [" + ruleId + "] already exists on attribute [" + attributeName + "]");
        }
        validationRules.put(ruleId, rule);
    }


    /**
     * Runs all attached validation rules on the given value.
     * Throws an exception if any rule fails.
     */
    public void validateValue(Object value) {
        for (ValidationRule rule : validationRules.values()) {
            rule.validate(value);
        }
    }
    public String getAttributeName() {
        return attributeName;
    }

    public String getAttributeDataType() {
        return attributeDataType;
    }

    public Map<String, ValidationRule> getValidationRules() {
        return validationRules;
    }
}
