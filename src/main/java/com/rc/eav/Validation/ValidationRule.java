package com.rc.eav.Validation;

public interface ValidationRule {

    /**
     * A unique string that identifies the rule,
     * e.g. "NOT_NULL", "MAX_LENGTH(50)", "REGEX", etc.
     */
    String getRuleId();

    /**
     * Perform validation of the given value. Throws an exception if invalid.
     */
    void validate(Object value) throws IllegalArgumentException;

}
