/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.agent.dynamic.mcp.model.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月19日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.agent.dynamic.mcp.model.vo <br>
 */
public class McpServersRequestVO {

	private final ObjectMapper objectMapper;

	/**
	 * Default constructor for Jackson deserialization
	 */
	public McpServersRequestVO() {
		this.objectMapper = new ObjectMapper().registerModule(new JavaTimeModule())
				.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
	}

	public McpServersRequestVO(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	/**
	 * Complete JSON configuration format: {"mcpServers": {"server-name":
	 * {"command": "...", "args": [...], "env": {...}}}}
	 */
	@JsonProperty("configJson")
	private String configJson;

	/**
	 * Whether to override existing configuration
	 */
	@JsonProperty("overwrite")
	private boolean overwrite = false;

	// Getters and Setters
	public String getConfigJson() {
		return configJson;
	}

	public void setConfigJson(String configJson) {
		this.configJson = configJson;
	}

	public boolean isOverwrite() {
		return overwrite;
	}

	public void setOverwrite(boolean overwrite) {
		this.overwrite = overwrite;
	}

	/**
	 * Validate if JSON format is valid
	 * 
	 * @return true if valid, false if invalid
	 */
	public boolean isValidJson() {
		if (configJson == null || configJson.trim().isEmpty()) {
			return false;
		}

		try {
			JsonNode jsonNode = objectMapper.readTree(configJson);

			// Check if contains mcpServers field
			if (!jsonNode.has("mcpServers")) {
				return false;
			}

			// Check if mcpServers is an object
			JsonNode mcpServersNode = jsonNode.get("mcpServers");
			if (!mcpServersNode.isObject()) {
				return false;
			}

			// Check if has at least one server configuration
			if (mcpServersNode.size() == 0) {
				return false;
			}

			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Get server configuration count
	 * 
	 * @return Server count
	 */
	public int getServerCount() {
		if (!isValidJson()) {
			return 0;
		}

		try {
			JsonNode jsonNode = objectMapper.readTree(configJson);
			JsonNode mcpServersNode = jsonNode.get("mcpServers");
			return mcpServersNode.size();
		} catch (Exception e) {
			return 0;
		}
	}

	/**
	 * Get server name list
	 * 
	 * @return Server name array
	 */
	public String[] getServerNames() {
		if (!isValidJson()) {
			return new String[0];
		}

		try {
			JsonNode jsonNode = objectMapper.readTree(configJson);
			JsonNode mcpServersNode = jsonNode.get("mcpServers");

			String[] names = new String[mcpServersNode.size()];
			int index = 0;
			java.util.Iterator<String> fieldNames = mcpServersNode.fieldNames();
			while (fieldNames.hasNext()) {
				names[index++] = fieldNames.next();
			}
			return names;
		} catch (Exception e) {
			return new String[0];
		}
	}

	/**
	 * Normalize JSON format. If input is short format JSON, automatically convert
	 * to complete format
	 * 
	 * @return Normalized JSON string
	 */
	public String getNormalizedConfigJson() {
		if (configJson == null || configJson.trim().isEmpty()) {
			return configJson;
		}

		try {
			JsonNode jsonNode = objectMapper.readTree(configJson);

			// If already contains mcpServers field, return directly
			if (jsonNode.has("mcpServers")) {
				return configJson;
			}

			// If it's short format JSON, convert to complete format
			StringBuilder fullJsonBuilder = new StringBuilder();
			fullJsonBuilder.append("{\n  \"mcpServers\": ");
			fullJsonBuilder.append(configJson);
			fullJsonBuilder.append("\n}");

			return fullJsonBuilder.toString();
		} catch (Exception e) {
			// If parsing fails, return original JSON
			return configJson;
		}
	}

	/**
	 * Validate if request data is valid
	 * 
	 * @return true if valid, false if invalid
	 */
	public boolean isValid() {
		return isValidJson();
	}
}
