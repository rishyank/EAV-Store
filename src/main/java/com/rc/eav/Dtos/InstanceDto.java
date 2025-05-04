package com.rc.eav.Dtos;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.Map;

public class InstanceDto {

    @Schema(description = "List of instances, each containing a map of attribute names to their values",
            example = "[ { \"name\": \"John\", \"height\": 172.05, \"age\": 25 }, { \"name\": \"Alice\", \"height\": 160.2, \"age\": 30 } ]")
    private List<Map<String, Object>> instances;

    // Getters and Setters
    public List<Map<String, Object>> getInstances() {
        return instances;
    }

    public void setInstances(List<Map<String, Object>> instances) {
        this.instances = instances;
    }
}
