package com.rc.eav.schema;

import javax.swing.plaf.PanelUI;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class EntityRegistry {
    private static final Map<String, EntityDefinition> entityDefinitions = new ConcurrentHashMap<>();;

    public static void registerEntityDefinition(EntityDefinition entityDef) {

        if (entityDefinitions.containsKey(entityDef.getEntityName())) {
            throw new IllegalArgumentException("Entity '" + entityDef.getEntityName() + "' is already registered.");
        }
        entityDefinitions.put(entityDef.getEntityName(), entityDef);
    }
    public static EntityDefinition getEntityDefinition(String entityName) {
        return entityDefinitions.get(entityName.toLowerCase());
    }

    public static boolean deleteEntityDefinition(String entityName) {
        return entityDefinitions.remove(entityName) != null;
    }
    public static boolean entityExists(String entityName) {
        return entityDefinitions.containsKey(entityName.toLowerCase());
    }
    public static Set<String> getAllEntities() {
        return Collections.unmodifiableSet(entityDefinitions.keySet());
    }
}
