package com.rc.eav.Execption;


public class InstanceNotFoundException extends RuntimeException {
    public InstanceNotFoundException(String message) {
        super(message);
    }
}