/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.agent.dynamic.prompt.service;

import java.util.Map;

import org.springframework.ai.chat.messages.Message;

import com.hbasesoft.framework.ai.agent.dynamic.prompt.model.vo.PromptVO;

/**
 * Prompt服务接口，提供Prompt的管理与操作功能<br>
 * 该接口定义了Prompt的获取、创建、更新以及消息生成等相关操作
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月19日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.agent.dynamic.prompt.service <br>
 */
public interface PromptService {

	/**
	 * 根据命名空间和提示名称获取Prompt对象
	 * 
	 * @param namespace 命名空间，用于区分不同环境或模块下的Prompt
	 * @param promptName Prompt名称，用于唯一标识一个Prompt
	 * @return PromptVO对象，包含Prompt的详细信息
	 */
	PromptVO getPromptByName(String namespace, String promptName);

	/**
	 * 根据提示名称获取Prompt对象，默认使用空命名空间
	 * 
	 * @param promptName Prompt名称，用于唯一标识一个Prompt
	 * @return PromptVO对象，包含Prompt的详细信息
	 */
	default PromptVO getPromptByName(String promptName) {
		return getPromptByName(null, promptName);
	}

	/**
	 * 创建系统消息
	 * 
	 * @param promptName Prompt名称，用于获取对应的Prompt模板
	 * @param variables 变量映射，用于替换Prompt模板中的占位符
	 * @return Message对象，表示系统消息
	 */
	Message createSystemMessage(String promptName, Map<String, Object> variables);

	/**
	 * 创建用户消息
	 * 
	 * @param promptName Prompt名称，用于获取对应的Prompt模板
	 * @param variables 变量映射，用于替换Prompt模板中的占位符
	 * @return Message对象，表示用户消息
	 */
	Message createUserMessage(String promptName, Map<String, Object> variables);

	/**
	 * 渲染Prompt模板
	 * 
	 * @param promptName Prompt名称，用于获取对应的Prompt模板
	 * @param variables 变量映射，用于替换Prompt模板中的占位符
	 * @return 渲染后的Prompt字符串
	 */
	String renderPrompt(String promptName, Map<String, Object> variables);

	/**
	 * 重新初始化所有Prompt
	 * 该方法用于重新加载或刷新Prompt配置
	 */
	void reinitializePrompts();

	/**
	 * 创建新的Prompt
	 * 
	 * @param promptVO PromptVO对象，包含要创建的Prompt信息
	 * @return 创建成功的PromptVO对象
	 */
	PromptVO create(PromptVO promptVO);

	/**
	 * 更新现有的Prompt
	 * 
	 * @param promptVO PromptVO对象，包含要更新的Prompt信息
	 * @return 更新成功的PromptVO对象
	 */
	PromptVO update(PromptVO promptVO);
}
