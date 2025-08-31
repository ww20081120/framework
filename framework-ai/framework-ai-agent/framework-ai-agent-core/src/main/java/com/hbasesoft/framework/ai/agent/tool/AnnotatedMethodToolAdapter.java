/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.agent.tool;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;

import org.springframework.ai.chat.model.ToolContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

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
        return actionAnnotation.description().isEmpty() ? 
            "Tool for executing method: " + method.getName() : actionAnnotation.description();
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
        for (Parameter param : methodParameters) {
            ObjectNode paramNode = objectMapper.createObjectNode();
            
            // Get parameter name (Java 8+ with -parameters compiler flag)
            String paramName = param.getName();
            
            // Check for @ActionParam annotation
            ActionParam actionParam = param.getAnnotation(ActionParam.class);
            if (actionParam != null) {
                if (!actionParam.description().isEmpty()) {
                    paramNode.put("description", actionParam.description());
                }
                paramNode.put("required", actionParam.required());
                
                // Try to infer type from parameter type
                Class<?> paramType = param.getType();
                if (paramType == String.class) {
                    paramNode.put("type", "string");
                } else if (paramType == Integer.class || paramType == int.class) {
                    paramNode.put("type", "integer");
                } else if (paramType == Long.class || paramType == long.class) {
                    paramNode.put("type", "integer");
                } else if (paramType == Double.class || paramType == double.class) {
                    paramNode.put("type", "number");
                } else if (paramType == Boolean.class || paramType == boolean.class) {
                    paramNode.put("type", "boolean");
                } else {
                    // Default to string for complex types
                    paramNode.put("type", "string");
                }
            } else {
                // Default parameter info
                paramNode.put("type", "string");
                paramNode.put("description", "Parameter for " + paramName);
            }
            
            properties.set(paramName, paramNode);
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
                    Class<?> paramType = param.getType();
                    if (paramType == String.class) {
                        args[i] = value.toString();
                    } else if (paramType == Integer.class || paramType == int.class) {
                        args[i] = Integer.valueOf(value.toString());
                    } else if (paramType == Long.class || paramType == long.class) {
                        args[i] = Long.valueOf(value.toString());
                    } else if (paramType == Double.class || paramType == double.class) {
                        args[i] = Double.valueOf(value.toString());
                    } else if (paramType == Boolean.class || paramType == boolean.class) {
                        args[i] = Boolean.valueOf(value.toString());
                    } else {
                        // For complex types, try to convert from JSON
                        args[i] = objectMapper.convertValue(value, paramType);
                    }
                } else {
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
        } catch (Exception e) {
            return new ToolExecuteResult("Error executing tool: " + e.getMessage(), true);
        }
    }
}
