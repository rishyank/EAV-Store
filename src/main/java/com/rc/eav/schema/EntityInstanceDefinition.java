package com.rc.eav.schema;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

// Create an actual instance of an entity with values
public class EntityInstanceDefinition {
    private final EntityDefinition entityDefinition;
    private  final  String id;
    private final Map<String, Object> instanceValues = new ConcurrentHashMap<>(); // store attribute values

    public EntityInstanceDefinition(EntityDefinition entityDefinition) {
        this.id = UUID.randomUUID().toString();
        this.entityDefinition = entityDefinition;
        EntityInstanceRegistry.addEntityInstance(this); // Register instance t
    }
    public EntityInstanceDefinition(EntityDefinition entityDefinition, Map<String, Object> initialValues) {
        this.id = UUID.randomUUID().toString();
        this.entityDefinition = entityDefinition;

        initialValues.forEach((key,value) -> {
            setAttributeValue(key,value);
        });

        EntityInstanceRegistry.addEntityInstance(this); // Register instance
    }

    // set entity attribute value
    public void setAttributeValue(String attributeName, Object value) {
        if(!entityDefinition.getAttributeDefinitions().containsKey(attributeName)) {
            throw new IllegalArgumentException("Attribute " + attributeName + " is not defined in entity " + entityDefinition.getEntityName());
        }
        AttributeDefinition attrDef = entityDefinition.getAttributeDefinitions().get(attributeName);
        Object expectedType = AttributeTypeRegistry.getType(attrDef.getAttributeDataType());

        if (expectedType instanceof Class<?> expectedClass) {
            if (!expectedClass.isInstance(value)) {
                throw new IllegalArgumentException("Invalid value type for attribute '" + attributeName +
                        "'. Expected: " + expectedClass.getSimpleName() + ", Provided: " +
                        (value != null ? value.getClass().getSimpleName() : "null"));
            }
        }

        else if (expectedType instanceof Set<?> enumValues) {
            if (!enumValues.contains(value)) {
                throw new IllegalArgumentException("Invalid value for enum attribute '" + attributeName +
                        "'. Allowed values: " + enumValues);
            }
        }

        //attrDef.validateValue(value);

        instanceValues.put(attributeName, value);
    }

    public Object getAttributeValue(String attributeName) {
        return instanceValues.get(attributeName);
    }
    public Map<String, Object> getInstanceValues() {
        return instanceValues;
    }
    public EntityDefinition getEntitySchema() {
        return entityDefinition;
    }
    public String getId() {
        return id;
    }
}
