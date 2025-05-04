package com.rc.eav.schema;

import java.util.*;

public class AttributeTypeRegistry {
    private static final Map<String,Class<?>> typeRegistry = new HashMap<>();
    private static final Map<String, Set<String>> enumRegistry = new HashMap<>();

    static  {
        typeRegistry.put("STRING",String.class);
        typeRegistry.put("INTEGER",Integer.class);
        typeRegistry.put("DOUBLE",Double.class);
        typeRegistry.put("BOOLEAN",Boolean.class);
        typeRegistry.put("LIST",List.class);

    }
    public static void registerEnum(String enumName, Set<String> values) {
        enumRegistry.put(enumName.toUpperCase(), new HashSet<>(values)); // add enums
    }

    // Get all values for a registered enum
    public static Set<String> getEnumValues(String enumName) {
        return enumRegistry.getOrDefault(enumName, Collections.emptySet());
    }

    // Check if a type exists (checks both typeRegistry and enumRegistry)
    public static boolean containsType(String typeName) {
        return typeRegistry.containsKey(typeName.toUpperCase()) || enumRegistry.containsKey(typeName.toUpperCase());
    }
    // Get the registered type (for primitive types) or enum values (as Set<String>)
    public static Object getType(String typeName) {
        if (typeRegistry.containsKey(typeName.toUpperCase())) {
            return typeRegistry.get(typeName);
        }
        return enumRegistry.getOrDefault(typeName.toUpperCase(), Collections.emptySet());
    }

}
