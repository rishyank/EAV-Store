package com.rc.eav.schema;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class EntityInstanceRegistry {
    private static final Map<String, List<EntityInstanceDefinition>> entityInstances = new ConcurrentHashMap<>();
    public static void addEntityInstance(EntityInstanceDefinition instance) {
        entityInstances.computeIfAbsent(instance.getEntitySchema().getEntityName(), k -> new LinkedList<>())
                .add(instance);
    }

    public static List<Map<String, Object>> getInstancesOfEntity(String entityName) {
        // Fetch entity instances or return empty list if none exist
        List<EntityInstanceDefinition> entityInstancesList = entityInstances.getOrDefault(entityName.toLowerCase(), Collections.emptyList());

        // Convert entity instances to a list of maps containing ID and values
        return entityInstancesList.stream().map(instance -> {
            Map<String, Object> instanceData = new HashMap<>();
            instanceData.put("id", instance.getId());
            instanceData.put("values", instance.getInstanceValues()); // Assuming getInstanceValues() returns a Map<String, Object>
            return instanceData;
        }).collect(Collectors.toList());
    }

    public static EntityInstanceDefinition getEntityInstanceById(String entityName, String id) {
        return entityInstances.getOrDefault(entityName.toLowerCase(), Collections.emptyList())
                .stream()
                .filter(instance -> instance.getId().equals(id))
                .findFirst()
                .orElse(null); // Return null if no matching instance is found
    }

    public static Map<String, Object> getInstanceDetails(String entityName, String id) {
        // Fetch entity instances or return empty list if none exist
        List<EntityInstanceDefinition> entityInstancesList = entityInstances.getOrDefault(entityName.toLowerCase(), Collections.emptyList());

        return entityInstancesList.stream()
                .filter(instance -> instance.getId().equals(id))
                .findFirst()
                .map(instance -> {
                    Map<String, Object> instanceData = new HashMap<>();
                    instanceData.put("id", instance.getId());
                    instanceData.put("values", instance.getInstanceValues());  // Always returning values
                    instanceData.put("definition", instance.getEntitySchema().getAttributeDefinitions()); // Always returning definition
                    return instanceData;
                })
                .orElse(Collections.emptyMap()); // Return an empty map if no matching instance is found
    }

    public static boolean deleteInstancesOfEntity(String entityName) {
        return entityInstances.remove(entityName) != null;
    }

    public static boolean deleteInstanceOfEntityByID(String entityName, String id) {

        List<EntityInstanceDefinition> entityInstancesList = entityInstances.get(entityName.toLowerCase());

        if (entityInstancesList == null || entityInstancesList.isEmpty()) {
            return false; // No instances exist for this entity
        }

        // Remove the instance with the given ID
        boolean removed = entityInstancesList.removeIf(instance -> instance.getId().equals(id));

        /*
        if (entityInstancesList.isEmpty()) {
            entityInstances.remove(entityName.toLowerCase());
        }
        */
        return removed; // Return true if an instance was deleted, false otherwise
    }

    public static List<Map<String, Object>> validateAllInstances(String entityName) {
        List<EntityInstanceDefinition> instances = entityInstances.getOrDefault(entityName.toLowerCase(), Collections.emptyList());

        if (instances.isEmpty()) {
            return Collections.singletonList(Map.of("message", "No instances found for entity '" + entityName + "'."));
        }

        List<Map<String, Object>> validationResults = new ArrayList<>();

        for (EntityInstanceDefinition instance : instances) {
            Map<String, Object> instanceValidationResult = validateSingleInstance(instance);
            validationResults.add(instanceValidationResult);
        }

        return validationResults;
    }

    public static Map<String, Object> validateSingleInstance(EntityInstanceDefinition instance) {

        Map<String, Object> errors = new HashMap<>();

        for (Map.Entry<String, Object> entry : instance.getInstanceValues().entrySet()) {
            String attributeName = entry.getKey();
            Object value = entry.getValue();
            // for each attribute
            AttributeDefinition attributeDefinition = instance.getEntitySchema().getAttributeDefinitions().get(attributeName);

            if (attributeDefinition != null) {
                try {
                    attributeDefinition.validateValue(value);
                } catch (IllegalArgumentException e) {
                    errors.put(attributeName, e.getMessage());
                }
            }
        }
        return Map.of(
                "id", instance.getId(),
                "errors", errors.isEmpty() ? "Data is Valid" : errors
        );
    }
}

