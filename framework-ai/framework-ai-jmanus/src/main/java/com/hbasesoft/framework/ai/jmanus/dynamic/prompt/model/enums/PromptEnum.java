/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.jmanus.dynamic.prompt.model.enums;

import org.springframework.ai.chat.messages.MessageType;

import com.hbasesoft.framework.ai.jmanus.prompt.PromptDescriptionLoader;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月19日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.jmanus.dynamic.prompt.model.enums <br>
 */
public enum PromptEnum {

	PLANNING_PLAN_CREATION("PLANNING_PLAN_CREATION", MessageType.SYSTEM, PromptType.PLANNING, true,
			"planning/plan-creation.txt"),
	AGENT_CURRENT_STEP_ENV("AGENT_CURRENT_STEP_ENV", MessageType.USER, PromptType.AGENT, true,
			"agent/current-step-env.txt"),
	AGENT_STEP_EXECUTION("AGENT_STEP_EXECUTION", MessageType.USER, PromptType.AGENT, true, "agent/step-execution.txt"),
	PLANNING_PLAN_FINALIZER("PLANNING_PLAN_FINALIZER", MessageType.USER, PromptType.PLANNING, true,
			"planning/plan-finalizer.txt"),
	DIRECT_RESPONSE("DIRECT_RESPONSE", MessageType.USER, PromptType.PLANNING, true, "planning/direct-response.txt"),
	AGENT_STUCK_ERROR("AGENT_STUCK_ERROR", MessageType.SYSTEM, PromptType.AGENT, true, "agent/stuck-error.txt"),
	SUMMARY_PLAN_TEMPLATE("SUMMARY_PLAN_TEMPLATE", MessageType.SYSTEM, PromptType.PLANNING, true,
			"workflow/summary-plan-template.txt"),
	AGENT_DEBUG_DETAIL_OUTPUT("AGENT_DEBUG_DETAIL_OUTPUT", MessageType.SYSTEM, PromptType.AGENT, true,
			"agent/debug-detail-output.txt"),
	AGENT_NORMAL_OUTPUT("AGENT_NORMAL_OUTPUT", MessageType.SYSTEM, PromptType.AGENT, true, "agent/normal-output.txt"),
	AGENT_PARALLEL_TOOL_CALLS_RESPONSE("AGENT_PARALLEL_TOOL_CALLS_RESPONSE", MessageType.SYSTEM, PromptType.AGENT, true,
			"agent/parallel-tool-calls-response.txt");

	private String promptName;

	private MessageType messageType;

	private PromptType type;

	private Boolean builtIn;

	private String promptPath;

	public static final String[] SUPPORTED_LANGUAGES = { "zh", "en" };

	private static PromptDescriptionLoader descriptionLoader;

	PromptEnum(String promptName, MessageType messageType, PromptType type, Boolean builtIn, String promptPath) {
		this.promptName = promptName;
		this.messageType = messageType;
		this.type = type;
		this.builtIn = builtIn;
		this.promptPath = promptPath;
	}

	public String getPromptPathForLanguage(String language) {
		if (language == null || language.trim().isEmpty()) {
			language = "en"; // Default to English
		}
		return language + "/" + this.promptPath;
	}

	public String getPromptDescriptionForLanguage(String language) {
		if (descriptionLoader == null) {
			// Fallback to empty string if loader is not initialized
			return "";
		}
		return descriptionLoader.loadDescription(this.promptName, language);
	}

	public static String[] getSupportedLanguages() {
		return SUPPORTED_LANGUAGES.clone();
	}

	/**
	 * Set the description loader for loading descriptions from files
	 * 
	 * @param loader the PromptDescriptionLoader instance
	 */
	public static void setDescriptionLoader(PromptDescriptionLoader loader) {
		descriptionLoader = loader;
	}

	public String getPromptName() {
		return promptName;
	}

	public void setPromptName(String promptName) {
		this.promptName = promptName;
	}

	public MessageType getMessageType() {
		return messageType;
	}

	public Boolean getBuiltIn() {
		return builtIn;
	}

	public void setBuiltIn(Boolean builtIn) {
		this.builtIn = builtIn;
	}

	public void setMessageType(MessageType messageType) {
		this.messageType = messageType;
	}

	public PromptType getType() {
		return type;
	}

	public void setType(PromptType type) {
		this.type = type;
	}

	public String getPromptPath() {
		return promptPath;
	}

	public void setPromptPath(String promptPath) {
		this.promptPath = promptPath;
	}

	public boolean isBuiltIn() {
		return builtIn;
	}

	public void setBuiltIn(boolean builtIn) {
		this.builtIn = builtIn;
	}

}
