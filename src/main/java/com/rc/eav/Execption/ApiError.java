package com.rc.eav.Execption;

import org.springframework.http.HttpStatus;

public class ApiError {
    private int status;

    private String message;
    private long timestamp;

    public ApiError(HttpStatus status, String message) {
        this.status = status.value();
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

}