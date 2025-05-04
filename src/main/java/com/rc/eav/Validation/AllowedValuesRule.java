package com.rc.eav.Validation;

import java.util.Set;

public class AllowedValuesRule implements ValidationRule {

    private final String ruleId;
    private final Set<String> allowedValues;

    public AllowedValuesRule(String ruleId, Set<String> allowedValues) {
        this.ruleId = ruleId;
        this.allowedValues = allowedValues;
    }

    @Override
    public String getRuleId() {
        return ruleId;
    }

    @Override
    public void validate(Object value) {
        if (value == null) {
            throw new IllegalArgumentException("Value cannot be null for rule: " + ruleId);
        }

        if (!allowedValues.contains(value.toString())) {
            throw new IllegalArgumentException("Value '" + value + "' is not allowed for rule: " + ruleId + ". Allowed values: " + allowedValues);
        }
    }
}