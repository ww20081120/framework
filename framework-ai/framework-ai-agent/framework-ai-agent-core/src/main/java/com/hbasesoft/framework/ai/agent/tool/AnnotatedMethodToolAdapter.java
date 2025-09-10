/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.agent.tool;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * <Description> Adapter for converting annotated methods to ToolCallBiFunctionDef <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月31日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.agent.tool <br>
 */
public class AnnotatedMethodToolAdapter extends AbstractBaseTool<Map<String, Object>> {

    private final Object targetObject;

    private final Method method;

    private final Action actionAnnotation;

    private final ObjectMapper objectMapper;

    public AnnotatedMethodToolAdapter(Object targetObject, Method method, Action actionAnnotation,
        ObjectMapper objectMapper) {
        this.targetObject = targetObject;
        this.method = method;
        this.actionAnnotation = actionAnnotation;
        this.objectMapper = objectMapper;
    }

    @Override
    public String getName() {
        // Use class name + method name as tool name to prevent conflicts
        return targetObject.getClass().getSimpleName() + "." + method.getName();
    }

    @Override
    public String getDescription() {
        // Use annotation description or generate default
        return actionAnnotation.description().isEmpty() ? "Tool for executing method: " + method.getName()
            : actionAnnotation.description();
    }

    @Override
    public String getServiceGroup() {
        return actionAnnotation.serviceGroup();
    }

    @Override
    public boolean isReturnDirect() {
        return actionAnnotation.returnDirect();
    }

    @Override
    public String getParameters() {
        ObjectNode parameters = objectMapper.createObjectNode();
        ObjectNode properties = objectMapper.createObjectNode();
        parameters.put("type", "object");

        // Add method parameters as tool parameters
        Parameter[] methodParameters = method.getParameters();

        // Process parameters
        for (Parameter param : methodParameters) {
            ObjectNode paramNode = objectMapper.createObjectNode();
            processParameterOrField(param, param.getName(), paramNode);
            properties.set(param.getName(), paramNode);
        }

        parameters.set("properties", properties);
        // Add required parameters
        ArrayNode requiredArray = objectMapper.createArrayNode();
        for (Parameter param : methodParameters) {
            ActionParam actionParam = param.getAnnotation(ActionParam.class);
            if (actionParam == null || actionParam.required()) {
                requiredArray.add(param.getName());
            }
        }

        if (requiredArray.size() > 0) {
            parameters.set("required", requiredArray);
        }

        return parameters.toString();
    }

    /**
     * Process type information and populate the parameter node
     * 
     * @param type The type to process
     * @param node The node to populate
     */
    private void processType(Class<?> type, ObjectNode node) {
        if (type == String.class) {
            node.put("type", "string");
        }
        else if (type == Integer.class || type == int.class) {
            node.put("type", "integer");
        }
        else if (type == Long.class || type == long.class) {
            node.put("type", "integer");
        }
        else if (type == Double.class || type == double.class) {
            node.put("type", "number");
        }
        else if (type == Boolean.class || type == boolean.class) {
            node.put("type", "boolean");
        }
        else if (type == Date.class || type == LocalDate.class || type == LocalDateTime.class) {
            node.put("type", "string");
            node.put("format", "date-time");
        }
        else if (type.isArray()) {
            // Handle array types
            node.put("type", "array");
            ObjectNode itemsNode = objectMapper.createObjectNode();
            Class<?> componentType = type.getComponentType();
            if (isPrimitiveType(componentType)) {
                processType(componentType, itemsNode);
            }
            else if (componentType == Date.class || componentType == LocalDate.class
                || componentType == LocalDateTime.class) {
                processType(componentType, itemsNode);
            }
            else {
                // Handle complex object types in arrays
                addObjectProperties(componentType, itemsNode);
            }
            node.set("items", itemsNode);
        }
        else if (Collection.class.isAssignableFrom(type)) {
            // Handle Collection types (List, Set, etc.)
            node.put("type", "array");
            ObjectNode itemsNode = objectMapper.createObjectNode();
            // For Collection types, we can't easily determine the generic type at runtime
            // So we'll use a generic object type for items
            itemsNode.put("type", "object");
            node.set("items", itemsNode);
        }
        else if (type.isPrimitive()) {
            // Handle other primitive types
            processPrimitiveType(type, node);
        }
        else {
            // Handle complex object types recursively
            addObjectProperties(type, node);
        }
    }

    /**
     * Check if a type is a primitive type
     * 
     * @param type The type to check
     * @return true if the type is a primitive type
     */
    private boolean isPrimitiveType(Class<?> type) {
        return type == String.class || type == Integer.class || type == int.class || type == Long.class
            || type == long.class || type == Double.class || type == double.class || type == Boolean.class
            || type == boolean.class || type == byte.class || type == short.class || type == float.class
            || type == char.class;
    }

    /**
     * Process primitive types
     * 
     * @param type The primitive type
     * @param node The node to populate
     */
    private void processPrimitiveType(Class<?> type, ObjectNode node) {
        if (type == byte.class || type == short.class) {
            node.put("type", "integer");
        }
        else if (type == float.class) {
            node.put("type", "number");
        }
        else if (type == char.class) {
            node.put("type", "string");
        }
        else {
            // Default to string for other primitives
            node.put("type", "string");
        }
    }

    /**
     * Process a parameter or field, handling type information and metadata
     * 
     * @param paramOrField The parameter or field to process
     * @param name The name of the parameter or field
     * @param node The node to populate
     */
    private void processParameterOrField(Object paramOrField, String name, ObjectNode node) {
        if (paramOrField instanceof Parameter) {
            Parameter param = (Parameter) paramOrField;

            // Process parameter type
            processType(param.getType(), node);

            ActionParam actionParam = param.getAnnotation(ActionParam.class);
            if (actionParam != null) {
                if (!actionParam.description().isEmpty()) {
                    node.put("description", actionParam.description());
                }
                node.put("required", actionParam.required());
            }
            else {
                // Default parameter info
                node.put("description", "Parameter for " + name);
            }
        }
        else if (paramOrField instanceof Field) {
            Field field = (Field) paramOrField;

            // Process field type
            processType(field.getType(), node);

            // Add description from field annotations if available
            // This would typically use @JsonProperty or similar annotations
            node.put("description", "Field " + name);
        }
    }

    /**
     * Recursively add object properties to the parameter node
     * 
     * @param clazz The class to analyze
     * @param paramNode The parameter node to add properties to
     */
    private void addObjectProperties(Class<?> clazz, ObjectNode paramNode) {
        // For object types, we need to recursively add their properties
        paramNode.put("type", "object");
        ObjectNode properties = objectMapper.createObjectNode();

        // Get all declared fields of the class
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            ObjectNode fieldNode = objectMapper.createObjectNode();

            // Process field
            processParameterOrField(field, field.getName(), fieldNode);

            properties.set(field.getName(), fieldNode);
        }

        paramNode.set("properties", properties);
    }

    @Override
    public Class<Map<String, Object>> getInputType() {
        return (Class<Map<String, Object>>) (Class<?>) Map.class;
    }

    @Override
    public String getCurrentToolStateString() {
        return "AnnotatedMethodToolAdapter for method: " + method.getName();
    }

    @Override
    public void cleanup(String planId) {
        // No specific cleanup needed for method-based tools
    }

    @Override
    public ToolExecuteResult run(Map<String, Object> input) {
        try {
            // Convert input parameters to method arguments
            Parameter[] methodParameters = method.getParameters();
            Object[] args = new Object[methodParameters.length];

            for (int i = 0; i < methodParameters.length; i++) {
                Parameter param = methodParameters[i];
                String paramName = param.getName();
                Object value = input.get(paramName);

                // Handle type conversion
                if (value != null) {
                    args[i] = convertValue(value, param.getType());
                }
                else {
                    // Check if parameter is required
                    ActionParam actionParam = param.getAnnotation(ActionParam.class);
                    if (actionParam != null && actionParam.required()) {
                        throw new IllegalArgumentException("Required parameter '" + paramName + "' is missing");
                    }
                    args[i] = null;
                }
            }

            // Invoke the method
            Object result = method.invoke(targetObject, args);

            // Return result as ToolExecuteResult
            return new ToolExecuteResult(result != null ? result.toString() : "", false);
        }
        catch (Exception e) {
            return new ToolExecuteResult("Error executing tool: " + e.getMessage(), true);
        }
    }

    /**
     * Convert a value to the specified type
     * 
     * @param value The value to convert
     * @param type The target type
     * @return The converted value
     */
    private Object convertValue(Object value, Class<?> type) {
        if (type == String.class) {
            return value.toString();
        }
        else if (type == Integer.class || type == int.class) {
            return Integer.valueOf(value.toString());
        }
        else if (type == Long.class || type == long.class) {
            return Long.valueOf(value.toString());
        }
        else if (type == Double.class || type == double.class) {
            return Double.valueOf(value.toString());
        }
        else if (type == Boolean.class || type == boolean.class) {
            return Boolean.valueOf(value.toString());
        }
        else if (type == Date.class) {
            // Handle Date type
            if (value instanceof String) {
                // Try to parse as ISO date format
                try {
                    return Date.from(java.time.Instant.parse((String) value));
                }
                catch (Exception e) {
                    return new Date(value.toString());
                }
            }
            else {
                return new Date(value.toString());
            }
        }
        else if (type == LocalDate.class) {
            // Handle LocalDate type
            if (value instanceof String) {
                return LocalDate.parse((String) value, DateTimeFormatter.ISO_LOCAL_DATE);
            }
            else {
                return LocalDate.parse(value.toString(), DateTimeFormatter.ISO_LOCAL_DATE);
            }
        }
        else if (type == LocalDateTime.class) {
            // Handle LocalDateTime type
            if (value instanceof String) {
                return LocalDateTime.parse((String) value, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            }
            else {
                return LocalDateTime.parse(value.toString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            }
        }
        else if (type.isArray()) {
            return convertToArray(value, type);
        }
        else if (Collection.class.isAssignableFrom(type)) {
            return convertToCollection(value);
        }
        else {
            // For complex types, try to convert from JSON
            return objectMapper.convertValue(value, type);
        }
    }

    /**
     * Convert a value to an array of the specified type
     * 
     * @param value The value to convert
     * @param arrayType The target array type
     * @return The converted array
     */
    private Object convertToArray(Object value, Class<?> arrayType) {
        if (value instanceof Iterable) {
            Iterable<?> iterable = (Iterable<?>) value;
            Class<?> componentType = arrayType.getComponentType();

            // Convert to array
            if (componentType == String.class) {
                if (iterable instanceof Collection) {
                    return ((Collection<?>) iterable).toArray(new String[0]);
                }
                else {
                    // Handle non-Collection Iterable
                    StringBuilder sb = new StringBuilder();
                    for (Object item : iterable) {
                        sb.append(item.toString());
                    }
                    return new String[] {
                        sb.toString()
                    };
                }
            }
            else if (componentType == Integer.class || componentType == int.class) {
                int[] intArray = new int[iterable instanceof Collection ? ((Collection<?>) iterable).size() : 10];
                int index = 0;
                for (Object item : iterable) {
                    intArray[index++] = Integer.valueOf(item.toString());
                }
                return intArray;
            }
            else if (componentType == Long.class || componentType == long.class) {
                long[] longArray = new long[iterable instanceof Collection ? ((Collection<?>) iterable).size() : 10];
                int index = 0;
                for (Object item : iterable) {
                    longArray[index++] = Long.valueOf(item.toString());
                }
                return longArray;
            }
            else if (componentType == Double.class || componentType == double.class) {
                double[] doubleArray = new double[iterable instanceof Collection ? ((Collection<?>) iterable).size()
                    : 10];
                int index = 0;
                for (Object item : iterable) {
                    doubleArray[index++] = Double.valueOf(item.toString());
                }
                return doubleArray;
            }
            else if (componentType == Boolean.class || componentType == boolean.class) {
                boolean[] booleanArray = new boolean[iterable instanceof Collection ? ((Collection<?>) iterable).size()
                    : 10];
                int index = 0;
                for (Object item : iterable) {
                    booleanArray[index++] = Boolean.valueOf(item.toString());
                }
                return booleanArray;
            }
            else {
                // For complex types, try to convert from JSON
                Object[] objectArray = new Object[iterable instanceof Collection ? ((Collection<?>) iterable).size()
                    : 10];
                int index = 0;
                for (Object item : iterable) {
                    objectArray[index++] = objectMapper.convertValue(item, componentType);
                }
                return objectArray;
            }
        }
        else {
            // Handle as single value array
            Class<?> componentType = arrayType.getComponentType();
            if (componentType == String.class) {
                return new String[] {
                    value.toString()
                };
            }
            else if (componentType == Integer.class || componentType == int.class) {
                return new int[] {
                    Integer.valueOf(value.toString())
                };
            }
            else if (componentType == Long.class || componentType == long.class) {
                return new long[] {
                    Long.valueOf(value.toString())
                };
            }
            else if (componentType == Double.class || componentType == double.class) {
                return new double[] {
                    Double.valueOf(value.toString())
                };
            }
            else if (componentType == Boolean.class || componentType == boolean.class) {
                return new boolean[] {
                    Boolean.valueOf(value.toString())
                };
            }
            else {
                // For complex types, try to convert from JSON
                Object[] objectArray = new Object[1];
                objectArray[0] = objectMapper.convertValue(value, componentType);
                return objectArray;
            }
        }
    }

    /**
     * Convert a value to a collection
     * 
     * @param value The value to convert
     * @return The converted collection
     */
    private Object convertToCollection(Object value) {
        // Handle Collection types (List, Set, etc.)
        if (value instanceof Iterable) {
            Iterable<?> iterable = (Iterable<?>) value;
            // Convert to List
            if (iterable instanceof Collection) {
                return iterable;
            }
            else {
                // Convert non-Collection Iterable to List
                List<Object> list = new ArrayList<>();
                for (Object item : iterable) {
                    list.add(item);
                }
                return list;
            }
        }
        else {
            // Handle as single value collection
            List<Object> list = new ArrayList<>();
            list.add(value);
            return list;
        }
    }
}
