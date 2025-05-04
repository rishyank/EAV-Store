package com.rc.eav.Validation;

import java.util.regex.Pattern;

public class RegexRule implements ValidationRule {

    private final String ruleId;
    private final Pattern pattern;

    public RegexRule(String ruleId, String regexPattern) {
        this.ruleId = ruleId;
        this.pattern = Pattern.compile(regexPattern);
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

        if (!(value instanceof String)) {
            throw new IllegalArgumentException("Regex validation can only be applied to String values.");
        }

        String strValue = (String) value;
        if (!pattern.matcher(strValue).matches()) {
            throw new IllegalArgumentException("Value '" + strValue + "' does not match pattern for rule: " + ruleId);
        }
    }
}
