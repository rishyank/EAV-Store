package com.rc.eav.Execption;


public class AttributeNotFoundException extends RuntimeException {
    public AttributeNotFoundException(String message) {
        super(message);
    }
}