package com.rc.eav.Validation;

public class LengthRule implements ValidationRule {

    private final String ruleId;
    private final int minLength;
    private final int maxLength;

    /**
     * Constructor for LengthRule that ensures a string is within the specified length range.
     *
     * @param minLength Minimum length allowed (inclusive)
     * @param maxLength Maximum length allowed (inclusive)
     */
    public LengthRule(String ruleId,int minLength, int maxLength) {
        if (minLength < 0 || maxLength < minLength) {
            throw new IllegalArgumentException("Invalid length constraints: minLength must be >= 0 and maxLength must be >= minLength.");
        }
        this.ruleId = ruleId;
        this.minLength = minLength;
        this.maxLength = maxLength;
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
            throw new IllegalArgumentException("Length validation can only be applied to String values.");
        }

        String strValue = ((String) value).trim();
        int length = strValue.length();

        if (length < minLength) {
            throw new IllegalArgumentException("Value '" + strValue + "' is too short. Minimum length is " + minLength + " characters.");
        }

        if (length > maxLength) {
            throw new IllegalArgumentException("Value '" + strValue + "' is too long. Maximum length is " + maxLength + " characters.");
        }
    }
}

