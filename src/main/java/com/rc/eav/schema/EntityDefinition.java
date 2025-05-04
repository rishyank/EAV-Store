package com.rc.eav.schema;
import com.rc.eav.Validation.ValidationRule;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class EntityDefinition {
    private String entityName;
    private final Map<String, AttributeDefinition> entityAttributes = new HashMap<>();

    public EntityDefinition () {};
    public EntityDefinition(String entityName) {
        String normalizedName = entityName.toLowerCase();
        if (EntityRegistry.entityExists(normalizedName)) {
            throw new IllegalArgumentException("Entity '" + entityName + "' already exists.");
        }
        this.entityName = normalizedName;
        EntityRegistry.registerEntityDefinition(this); // Register definition creation
    }

    // Add attributes definitions (populating properties)
    public void addAttributeDefinition(AttributeDefinition attributeDef) {
        // check if attribute with name already exist in the entity attributes
        String normalizedAttrName = attributeDef.getAttributeName().toLowerCase();
        if (!entityAttributes.containsKey(normalizedAttrName))
            entityAttributes.put(normalizedAttrName, attributeDef); // store attribute name and definition
        else {
            throw new IllegalArgumentException("Attribute " + attributeDef.getAttributeName() + " is already defined in entity");
        }
    }

    public AttributeDefinition getAttributeDefinition(String attributeName) {
        return entityAttributes.get(attributeName);
    }

    public Map<String, ValidationRule> getAttributeRuleDefinition(String attributeName) {
        AttributeDefinition attributeDefinition = entityAttributes.get(attributeName);
        return attributeDefinition.getValidationRules();
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName.toLowerCase();
    }

    // get all attributes of entity
    public Map<String, AttributeDefinition> getAttributeDefinitions() {
        return entityAttributes;
    }
}