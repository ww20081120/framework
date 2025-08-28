/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.agent.dynamic.prompt.model.enums;

import org.springframework.ai.chat.messages.MessageType;

import com.hbasesoft.framework.ai.agent.prompt.PromptDescriptionLoader;

/**
 * 提示枚举类<br>
 * 该枚举类定义了系统中使用的所有提示模板，包括规划和代理相关的提示。
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月19日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.agent.dynamic.prompt.model.enums <br>
 */
public enum PromptEnum {

	/** 规划创建提示 */
	PLANNING_PLAN_CREATION("PLANNING_PLAN_CREATION", MessageType.SYSTEM, PromptType.PLANNING, true,
		"planning/plan-creation.txt"),

	/** 代理当前步骤环境提示 */
	AGENT_CURRENT_STEP_ENV("AGENT_CURRENT_STEP_ENV", MessageType.USER, PromptType.AGENT, true,
		"agent/current-step-env.txt"),

	/** 代理步骤执行提示 */
	AGENT_STEP_EXECUTION("AGENT_STEP_EXECUTION", MessageType.USER, PromptType.AGENT, true, "agent/step-execution.txt"),

	/** 规划终结器提示 */
	PLANNING_PLAN_FINALIZER("PLANNING_PLAN_FINALIZER", MessageType.USER, PromptType.PLANNING, true,
		"planning/plan-finalizer.txt"),

	/** 直接响应提示 */
	DIRECT_RESPONSE("DIRECT_RESPONSE", MessageType.USER, PromptType.PLANNING, true, "planning/direct-response.txt"),

	/** 代理卡住错误提示 */
	AGENT_STUCK_ERROR("AGENT_STUCK_ERROR", MessageType.SYSTEM, PromptType.AGENT, true, "agent/stuck-error.txt"),

	/** 摘要规划模板提示 */
	SUMMARY_PLAN_TEMPLATE("SUMMARY_PLAN_TEMPLATE", MessageType.SYSTEM, PromptType.PLANNING, true,
		"workflow/summary-plan-template.txt"),

	/** 代理调试详细输出提示 */
	AGENT_DEBUG_DETAIL_OUTPUT("AGENT_DEBUG_DETAIL_OUTPUT", MessageType.SYSTEM, PromptType.AGENT, true,
		"agent/debug-detail-output.txt"),

	/** 代理正常输出提示 */
	AGENT_NORMAL_OUTPUT("AGENT_NORMAL_OUTPUT", MessageType.SYSTEM, PromptType.AGENT, true, "agent/normal-output.txt"),

	/** 代理并行工具调用响应提示 */
	AGENT_PARALLEL_TOOL_CALLS_RESPONSE("AGENT_PARALLEL_TOOL_CALLS_RESPONSE", MessageType.SYSTEM, PromptType.AGENT, true,
		"agent/parallel-tool-calls-response.txt");

	/** 提示名称 */
	private String promptName;

	/** 消息类型 */
	private MessageType messageType;

	/** 提示类型 */
	private PromptType type;

	/** 是否为内置提示 */
	private Boolean builtIn;

	/** 提示路径 */
	private String promptPath;

	/** 支持的语言列表 */
	public static final String[] SUPPORTED_LANGUAGES = {
		"zh", "en"
	};

	/** 提示描述加载器 */
	private static PromptDescriptionLoader descriptionLoader;

	/**
	 * 构造函数
	 * 
	 * @param promptName 提示名称
	 * @param messageType 消息类型
	 * @param type 提示类型
	 * @param builtIn 是否为内置提示
	 * @param promptPath 提示路径
	 */
	PromptEnum(String promptName, MessageType messageType, PromptType type, Boolean builtIn, String promptPath) {
		this.promptName = promptName;
		this.messageType = messageType;
		this.type = type;
		this.builtIn = builtIn;
		this.promptPath = promptPath;
	}

	/**
	 * 根据语言获取提示路径
	 * 
	 * @param language 语言代码
	 * @return 对应语言的提示路径
	 */
	public String getPromptPathForLanguage(String language) {
		if (language == null || language.trim().isEmpty()) {
			language = "en"; // 默认使用英语
		}
		return language + "/" + this.promptPath;
	}

	/**
	 * 根据语言获取提示描述
	 * 
	 * @param language 语言代码
	 * @return 对应语言的提示描述
	 */
	public String getPromptDescriptionForLanguage(String language) {
		if (descriptionLoader == null) {
			// 如果加载器未初始化，则返回空字符串
			return "";
		}
		return descriptionLoader.loadDescription(this.promptName, language);
	}

	/**
	 * 获取支持的语言列表
	 * 
	 * @return 支持的语言数组
	 */
	public static String[] getSupportedLanguages() {
		return SUPPORTED_LANGUAGES.clone();
	}

	/**
	 * 设置描述加载器，用于从文件加载描述
	 * 
	 * @param loader PromptDescriptionLoader实例
	 */
	public static void setDescriptionLoader(PromptDescriptionLoader loader) {
		descriptionLoader = loader;
	}

	/**
	 * 获取提示名称
	 * 
	 * @return 提示名称
	 */
	public String getPromptName() {
		return promptName;
	}

	/**
	 * 设置提示名称
	 * 
	 * @param promptName 提示名称
	 */
	public void setPromptName(String promptName) {
		this.promptName = promptName;
	}

	/**
	 * 获取消息类型
	 * 
	 * @return 消息类型
	 */
	public MessageType getMessageType() {
		return messageType;
	}

	/**
	 * 获取是否为内置提示
	 * 
	 * @return 是否为内置提示
	 */
	public Boolean getBuiltIn() {
		return builtIn;
	}

	/**
	 * 设置是否为内置提示
	 * 
	 * @param builtIn 是否为内置提示
	 */
	public void setBuiltIn(Boolean builtIn) {
		this.builtIn = builtIn;
	}

	/**
	 * 设置消息类型
	 * 
	 * @param messageType 消息类型
	 */
	public void setMessageType(MessageType messageType) {
		this.messageType = messageType;
	}

	/**
	 * 获取提示类型
	 * 
	 * @return 提示类型
	 */
	public PromptType getType() {
		return type;
	}

	/**
	 * 设置提示类型
	 * 
	 * @param type 提示类型
	 */
	public void setType(PromptType type) {
		this.type = type;
	}

	/**
	 * 获取提示路径
	 * 
	 * @return 提示路径
	 */
	public String getPromptPath() {
		return promptPath;
	}

	/**
	 * 设置提示路径
	 * 
	 * @param promptPath 提示路径
	 */
	public void setPromptPath(String promptPath) {
		this.promptPath = promptPath;
	}

	/**
	 * 判断是否为内置提示
	 * 
	 * @return 是否为内置提示
	 */
	public boolean isBuiltIn() {
		return builtIn;
	}

	/**
	 * 设置是否为内置提示
	 * 
	 * @param builtIn 是否为内置提示
	 */
	public void setBuiltIn(boolean builtIn) {
		this.builtIn = builtIn;
	}

}