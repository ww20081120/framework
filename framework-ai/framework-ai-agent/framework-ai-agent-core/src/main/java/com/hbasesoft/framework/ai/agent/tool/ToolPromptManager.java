/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.agent.tool;

import org.springframework.stereotype.Component;

import com.hbasesoft.framework.ai.agent.dynamic.prompt.service.PromptService;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月19日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.agent.tool <br>
 */
@Component
public class ToolPromptManager {

	private final PromptService promptService;

	public ToolPromptManager(PromptService promptService) {
		this.promptService = promptService;
	}

	/**
	 * Get tool description from prompt service
	 * 
	 * @param toolName the tool name
	 * @param args     optional arguments for template variables
	 * @return the tool description
	 */
	public String getToolDescription(String toolName, Object... args) {
		try {
			String promptName = buildDescriptionPromptName(toolName);
			String template = promptService.getPromptByName(promptName).getPromptContent();

			if (args != null && args.length > 0) {
				return String.format(template, args);
			}
			return template;
		} catch (Exception e) {
			LoggerUtil.warn(e, "Failed to load prompt-based tool description for {0}, using fallback", toolName);
			return getDefaultDescription(toolName);
		}
	}

	/**
	 * Get tool parameters from prompt service
	 * 
	 * @param toolName the tool name
	 * @return the tool parameters JSON schema
	 */
	public String getToolParameters(String toolName) {
		try {
			String promptName = buildParametersPromptName(toolName);
			return promptService.getPromptByName(promptName).getPromptContent();
		} catch (Exception e) {
			LoggerUtil.warn(e, "Failed to load prompt-based tool parameters for {0}, using fallback", toolName);
			return getDefaultParameters(toolName);
		}
	}

	/**
	 * Build description prompt name for PromptService
	 * 
	 * @param toolName the tool name
	 * @return the prompt name
	 */
	private String buildDescriptionPromptName(String toolName) {
		return String.format("%s_TOOL_DESCRIPTION", toolName.toUpperCase());
	}

	/**
	 * Build parameters prompt name for PromptService
	 * 
	 * @param toolName the tool name
	 * @return the prompt name
	 */
	private String buildParametersPromptName(String toolName) {
		return String.format("%s_TOOL_PARAMETERS", toolName.toUpperCase());
	}

	/**
	 * Get default description when prompt is not found
	 * 
	 * @param toolName the tool name
	 * @return default description
	 */
	private String getDefaultDescription(String toolName) {
		return String.format("Tool: %s", toolName);
	}

	/**
	 * Get default parameters when prompt is not found
	 * 
	 * @param toolName the tool name
	 * @return default parameters
	 */
	private String getDefaultParameters(String toolName) {
		return """
				{
				    "type": "object",
				    "properties": {},
				    "required": []
				}
				""";
	}

	/**
	 * Refresh prompts from PromptService This will trigger PromptService to reload
	 * prompts from database
	 */
	public void refreshPrompts() {
		try {
			promptService.reinitializePrompts();
			LoggerUtil.info("Tool prompts refreshed from PromptService");
		} catch (Exception e) {
			LoggerUtil.error("Failed to refresh tool prompts", e);
		}
	}

}
