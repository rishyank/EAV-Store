package com.rc.eav.utils;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Globals {
    @Value("${kv.server}")
    private String kvServerIp;

    @Value("${kv.server.port}")
    private int kvServerPort;

    public String getKvServerIp() {
        return kvServerIp;
    }

    public int getKvServerPort() {
        return kvServerPort;
    }
}