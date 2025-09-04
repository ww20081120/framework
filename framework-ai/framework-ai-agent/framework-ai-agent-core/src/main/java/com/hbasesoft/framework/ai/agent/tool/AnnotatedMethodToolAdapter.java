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
import java.util.Date;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * <Description> Adapter for converting annotated methods to
 * ToolCallBiFunctionDef <br>
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
		for (Parameter param : methodParameters) {
			ObjectNode paramNode = objectMapper.createObjectNode();

			// Get parameter name (Java 8+ with -parameters compiler flag)
			String paramName = param.getName();

			// Check for @ActionParam annotation
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
			} else if (paramType == Date.class || paramType == LocalDate.class || paramType == LocalDateTime.class) {
				paramNode.put("type", "string");
				paramNode.put("format", "date-time");
			} else if (paramType.isPrimitive()) {
				// Handle other primitive types
				if (paramType == byte.class || paramType == short.class) {
					paramNode.put("type", "integer");
				} else if (paramType == float.class) {
					paramNode.put("type", "number");
				} else if (paramType == char.class) {
					paramNode.put("type", "string");
				} else {
					// Default to string for other primitives
					paramNode.put("type", "string");
				}
			} else {
				// Handle complex object types recursively
				addObjectProperties(paramType, paramNode);
			}

			ActionParam actionParam = param.getAnnotation(ActionParam.class);
			if (actionParam != null) {
				if (!actionParam.description().isEmpty()) {
					paramNode.put("description", actionParam.description());
				}
				paramNode.put("required", actionParam.required());
			} else {
				// Default parameter info
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

	/**
	 * Recursively add object properties to the parameter node
	 * 
	 * @param clazz     The class to analyze
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
			Class<?> fieldType = field.getType();

			if (fieldType == String.class) {
				fieldNode.put("type", "string");
			} else if (fieldType == Integer.class || fieldType == int.class) {
				fieldNode.put("type", "integer");
			} else if (fieldType == Long.class || fieldType == long.class) {
				fieldNode.put("type", "integer");
			} else if (fieldType == Double.class || fieldType == double.class) {
				fieldNode.put("type", "number");
			} else if (fieldType == Boolean.class || fieldType == boolean.class) {
				fieldNode.put("type", "boolean");
			} else if (fieldType == Date.class || fieldType == LocalDate.class || fieldType == LocalDateTime.class) {
				fieldNode.put("type", "string");
				fieldNode.put("format", "date-time");
			} else if (fieldType.isPrimitive()) {
				// Handle other primitive types
				if (fieldType == byte.class || fieldType == short.class) {
					fieldNode.put("type", "integer");
				} else if (fieldType == float.class) {
					fieldNode.put("type", "number");
				} else if (fieldType == char.class) {
					fieldNode.put("type", "string");
				} else {
					// Default to string for other primitives
					fieldNode.put("type", "string");
				}
			} else {
				// Recursively handle nested object types
				addObjectProperties(fieldType, fieldNode);
			}

			// Add description from field annotations if available
			// This would typically use @JsonProperty or similar annotations
			fieldNode.put("description", "Field " + field.getName());
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
					} else if (paramType == Date.class) {
						// Handle Date type
						if (value instanceof String) {
							// Try to parse as ISO date format
							try {
								args[i] = Date.from(java.time.Instant.parse((String) value));
							} catch (Exception e) {
								args[i] = new Date(value.toString());
							}
						} else {
							args[i] = new Date(value.toString());
						}
					} else if (paramType == LocalDate.class) {
						// Handle LocalDate type
						if (value instanceof String) {
							args[i] = LocalDate.parse((String) value, DateTimeFormatter.ISO_LOCAL_DATE);
						} else {
							args[i] = LocalDate.parse(value.toString(), DateTimeFormatter.ISO_LOCAL_DATE);
						}
					} else if (paramType == LocalDateTime.class) {
						// Handle LocalDateTime type
						if (value instanceof String) {
							args[i] = LocalDateTime.parse((String) value, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
						} else {
							args[i] = LocalDateTime.parse(value.toString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
						}
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
