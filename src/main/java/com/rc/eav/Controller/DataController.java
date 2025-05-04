package com.rc.eav.Controller;

import com.rc.eav.Dtos.InstanceDto;
import com.rc.eav.Execption.EntityNotFoundException;
import com.rc.eav.Execption.InstanceNotFoundException;
import com.rc.eav.schema.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.websocket.server.PathParam;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/entities")
@Tag(name = "Data Controller", description = "APIs for managing Entity Instance Data")
public class DataController {

    @PostMapping("/{entityName}/instances")
    @Operation(summary = "Create entity instances")
    public ResponseEntity<String> createEntityInstances(
            @PathVariable String entityName,
            @RequestBody InstanceDto instanceDto) {

        if (!EntityRegistry.entityExists(entityName)) {
            throw new EntityNotFoundException("Entity '" + entityName + "' does not exist.");
        }

        EntityDefinition entityDefinition = EntityRegistry.getEntityDefinition(entityName);

        instanceDto.getInstances().forEach(instanceData -> {
            EntityInstanceDefinition entityInstance = new EntityInstanceDefinition(entityDefinition);
            instanceData.forEach(entityInstance::setAttributeValue);
        });

        return ResponseEntity.ok("Instances successfully added to entity '" + entityName + "'.");
    }

    @GetMapping("/{entityName}/instances")
    @Operation(summary = "Get all instances of an entity")
    public ResponseEntity<List<Map<String, Object>>> getAllEntityInstance(@PathVariable String entityName) {
        // Validate entity existence
        EntityRegistry.getEntityDefinition(entityName); // will throw if null

        // Fetch instances
        List<Map<String, Object>> instances = EntityInstanceRegistry.getInstancesOfEntity(entityName);

        return ResponseEntity.ok(instances);
    }

    @GetMapping("/{entityName}/{instance_id}")
    @Operation(summary = "Get a single instance of an entity")
    public ResponseEntity<Map<String, Object>> findEntityInstanceByID(
            @Parameter(description = "Name of the Entity", example = "Person", required = true)
            @PathVariable String entityName,
            @Parameter(description = "Instance ID of the Entity", example = "123", required = true)
            @PathVariable String instance_id) {

        if (!EntityRegistry.entityExists(entityName)) {
            throw new EntityNotFoundException("Entity '" + entityName + "' does not exist.");
        }

        Map<String, Object> foundInstance = EntityInstanceRegistry.getInstanceDetails(entityName, instance_id);

        if (foundInstance == null || foundInstance.isEmpty()) {
            throw new InstanceNotFoundException("No instance found for entity '" + entityName + "' with ID '" + instance_id + "'.");
        }

        return ResponseEntity.ok(foundInstance);
    }
    @DeleteMapping("/{entityName}/instances/{id}")
    @Operation(summary ="Delete instance of an entity based on instance id")
    public ResponseEntity<String> deleteEntityInstanceById(@PathVariable String entityName, @PathVariable String id) {
        boolean deleted = EntityInstanceRegistry.deleteInstanceOfEntityByID(entityName, id);

        if (!deleted) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Instance with ID '" + id + "' not found in entity '" + entityName + "'.");
        }
        return ResponseEntity.ok("Instance with ID '" + id + "' deleted successfully.");
    }

    @PutMapping("/{entityName}/instances/{id}")
    @Operation(summary ="Update instance of an entity based on instance id")
    public ResponseEntity<String> updateEntityInstanceById(
            @PathVariable String entityName,
            @PathVariable String id,
            @RequestBody Map<String, Object> updateValues) {

        // Check if entity exists
        if (!EntityRegistry.entityExists(entityName)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Entity '" + entityName + "' does not exist.");
        }

        // Retrieve instance
        EntityInstanceDefinition instance = EntityInstanceRegistry.getEntityInstanceById(entityName, id);

        if (instance == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Instance with ID '" + id + "' not found in entity '" + entityName + "'.");
        }

        // Update only existing attributes
        Map<String, Object> instanceValues = instance.getInstanceValues();
        boolean updated = false;

        for (Map.Entry<String, Object> entry : updateValues.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (instanceValues.containsKey(key)) {
                instanceValues.put(key, value);
                updated = true;
            }
        }

        if (!updated) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("No valid attributes were updated.");
        }

        return ResponseEntity.ok("Instance with ID '" + id + "' updated successfully.");
    }

}
