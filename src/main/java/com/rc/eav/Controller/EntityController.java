package com.rc.eav.Controller;

import com.rc.eav.Dtos.AttributeRequestDto;
import com.rc.eav.Dtos.AttributeRuleRequestDto;
import com.rc.eav.Execption.AttributeNotFoundException;
import com.rc.eav.Execption.EntityNotFoundException;
import com.rc.eav.Validation.*;
import com.rc.eav.schema.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/entities")
@Tag(name = "Entity Controller", description = "APIs for managing entities, attributes, and validations")
public class EntityController {

    // ───────────────────────────────────────────────
    // ENTITY MANAGEMENT
    // ───────────────────────────────────────────────

    @GetMapping
    @Operation(summary = "Get all registered entities")
    public ResponseEntity<Set<String>> getAllEntities() {
        return ResponseEntity.ok(EntityRegistry.getAllEntities());
    }

    @PostMapping("/create")
    @Operation(summary = "Create a new entity")
    public ResponseEntity<String> createEntity(@RequestParam(defaultValue = "Person") String entityName) {
        if (EntityRegistry.entityExists(entityName)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Entity '" + entityName + "' already exists.");
        }

        new EntityDefinition(entityName);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Entity '" + entityName + "' created successfully.");
    }

    @DeleteMapping("/delete")
    @Operation(summary = "Delete an entity by name")
    public ResponseEntity<Boolean> deleteEntity(@RequestParam String entityName) {
        // Validate entity existence
        if (!EntityRegistry.entityExists(entityName)) {
            throw new EntityNotFoundException("Entity '" + entityName + "' does not exist.");
        }

        boolean instancesDeleted = EntityInstanceRegistry.deleteInstancesOfEntity(entityName);
        boolean entityDeleted = EntityRegistry.deleteEntityDefinition(entityName);

        if (!instancesDeleted || !entityDeleted) {
            throw new IllegalStateException("Failed to completely delete entity '" + entityName + "'.");
        }

        return ResponseEntity.ok(true);
        // Or use: return ResponseEntity.noContent().build(); // if no response body is needed
    }
    // ───────────────────────────────────────────────
    // ATTRIBUTE MANAGEMENT
    // ───────────────────────────────────────────────

    @PostMapping("/add-attributes")
    @Operation(summary = "Add attributes to an entity")
    public ResponseEntity<String> addAttributes(@RequestBody AttributeRequestDto requestDto) {
        EntityDefinition entityDefinition = Optional.ofNullable(EntityRegistry.getEntityDefinition(requestDto.getEntityName()))
                .orElseThrow(() -> new EntityNotFoundException("Entity '" + requestDto.getEntityName() + "' does not exist."));

        requestDto.getAttributesInfo().forEach((attrName, attrDataType) -> {
            if (attrName == null || attrDataType == null) {
                throw new IllegalArgumentException("Attribute name and data type must not be null.");
            }
            entityDefinition.addAttributeDefinition(new AttributeDefinition(attrName, attrDataType));
        });

        return ResponseEntity.ok("Attributes successfully added to entity '" + requestDto.getEntityName() + "'.");
    }

    @GetMapping("/definitions")
    @Operation(summary = "Get all attribute definitions of an entity")
    public ResponseEntity<Map<String, AttributeDefinition>> getEntityAttributes(@RequestParam(defaultValue = "Person") String entityName) {
        EntityDefinition entityDefinition = Optional.ofNullable(EntityRegistry.getEntityDefinition(entityName))
                .orElseThrow(() -> new EntityNotFoundException("Entity '" + entityName + "' does not exist."));

        return ResponseEntity.ok(entityDefinition.getAttributeDefinitions());
    }

    @GetMapping("/attribute/definition")
    @Operation(summary = "Get a specific attribute definition of an entity")
    public ResponseEntity<AttributeDefinition> getEntityAttributeDefinition(
            @RequestParam(defaultValue = "Person") String entityName,
            @RequestParam(defaultValue = "age") String attrName) {

        EntityDefinition entityDefinition = Optional.ofNullable(EntityRegistry.getEntityDefinition(entityName))
                .orElseThrow(() -> new EntityNotFoundException("Entity '" + entityName + "' does not exist."));

        AttributeDefinition attributeDefinition = Optional.ofNullable(entityDefinition.getAttributeDefinition(attrName))
                .orElseThrow(() -> new AttributeNotFoundException("Attribute '" + attrName + "' does not exist in entity '" + entityName + "'." ));

        return ResponseEntity.ok(attributeDefinition);
    }

    // ───────────────────────────────────────────────
    // VALIDATION RULE MANAGEMENT
    // ───────────────────────────────────────────────

    @PostMapping("/attribute/rule")
    @Operation(
            summary = "Add a validation rule to an attribute",
            description = "Adds a rule like LENGTH, MIN_MAX, ALLOWED_VALUES, or REGEX to a specific attribute.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(schema = @Schema(implementation = AttributeRuleRequestDto.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Rule added successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid entity, attribute, or rule parameters")
            }
    )
    public ResponseEntity<String> addAttributesRules(@RequestBody AttributeRuleRequestDto requestDto) {
        if (requestDto == null || requestDto.getEntityName() == null ||
                requestDto.getAttributeName() == null || requestDto.getRuleId() == null) {
            throw new IllegalArgumentException("Invalid request: Missing required fields.");
        }

        String entityName = requestDto.getEntityName();
        String attributeName = requestDto.getAttributeName();
        String ruleId = requestDto.getRuleId();
        List<String> ruleParams = requestDto.getRuleParams();

        EntityDefinition entityDefinition = Optional.ofNullable(EntityRegistry.getEntityDefinition(entityName))
                .orElseThrow(() -> new IllegalArgumentException("Entity '" + entityName + "' does not exist."));

        AttributeDefinition attributeDefinition = Optional.ofNullable(entityDefinition.getAttributeDefinition(attributeName))
                .orElseThrow(() -> new IllegalArgumentException("Attribute '" + attributeName + "' does not exist in entity '" + entityName + "'." ));

        ValidationRule rule = createValidationRule(ruleId, ruleParams);
        attributeDefinition.addValidationRule(rule);

        return ResponseEntity.ok("Rule '" + ruleId + "' added successfully to attribute '" + attributeName + "'.");
    }

    private ValidationRule createValidationRule(String ruleId, List<String> params) {
        switch (ruleId.toUpperCase()) {
            case "LENGTH":
                if (params.size() != 2) throw new IllegalArgumentException("LENGTH rule requires [min, max] parameters.");
                return new LengthRule("LENGTH_Limit", Integer.parseInt(params.get(0)), Integer.parseInt(params.get(1)));

            case "MIN_MAX":
                if (params.size() != 2) throw new IllegalArgumentException("MIN_MAX rule requires [min, max] parameters.");
                return new MinMaxValueRule("MIN_MAX", Double.parseDouble(params.get(0)), Double.parseDouble(params.get(1)));

            case "ALLOWED_VALUES":
                if (params.isEmpty()) throw new IllegalArgumentException("ALLOWED_VALUES rule requires at least one value.");
                return new AllowedValuesRule("Allowed_Values", Set.copyOf(params));

            case "REGEX":
                if (params.size() != 1) throw new IllegalArgumentException("REGEX rule requires a single regex pattern.");
                return new RegexRule("Regex_Validation", params.get(0));

            default:
                throw new IllegalArgumentException("Invalid rule ID '" + ruleId + "'. Supported: LENGTH, MIN_MAX, ALLOWED_VALUES, REGEX.");
        }
    }

    // ───────────────────────────────────────────────
    // INSTANCE VALIDATION
    // ───────────────────────────────────────────────

    @GetMapping("/validate/{entityName}")
    @Operation(summary = "Validate all instances of an entity")
    public ResponseEntity<List<Map<String, Object>>> validateAllInstance(@PathVariable String entityName) {
        List<Map<String, Object>> validationResult = EntityInstanceRegistry.validateAllInstances(entityName);
        return ResponseEntity.ok(validationResult);
    }
    @GetMapping("/validate/instances/{entityName}/{id}")
    @Operation(summary = "Validate a single instance of an entity")
    public ResponseEntity<Map<String, Object>> validateSingleInstance(
            @PathVariable String entityName,
            @PathVariable String id) {

        EntityInstanceDefinition instance = EntityInstanceRegistry.getEntityInstanceById(entityName, id);

        if (instance == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Instance with ID '" + id + "' not found in entity '" + entityName + "'." ));
        }

        Map<String, Object> validationResult = EntityInstanceRegistry.validateSingleInstance(instance);
        return ResponseEntity.ok(validationResult);
    }
}
