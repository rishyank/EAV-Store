package com.rc.eav.Validation;

public class MinMaxValueRule implements ValidationRule{

    private final String ruleId;
    private final double minValue;
    private final double maxValue;

    public MinMaxValueRule(String ruleId, double minValue, double maxValue) {
        if (minValue > maxValue) {
            throw new IllegalArgumentException("minValue must be <= maxValue.");
        }
        this.ruleId = ruleId;
        this.minValue = minValue;
        this.maxValue = maxValue;
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

        if (!(value instanceof Number)) {
            throw new IllegalArgumentException("Min/Max validation applies only to numeric values.");
        }

        double numericValue = ((Number) value).doubleValue();
        if (numericValue < minValue || numericValue > maxValue) {
            throw new IllegalArgumentException("Value " + numericValue + " is out of range [" + minValue + ", " + maxValue + "]");
        }
    }
}
