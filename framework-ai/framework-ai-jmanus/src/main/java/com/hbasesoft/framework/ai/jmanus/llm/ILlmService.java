/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.jmanus.llm;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;

import com.hbasesoft.framework.ai.jmanus.dynamic.model.model.vo.ModelConfig;

/** 
 * <Description> <br> 
 *  
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月18日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.jmanus.llm <br>
 */
/**
 * LLM service interface, providing chat client and memory management
 * functionality
 */
public interface ILlmService {

	/**
	 * Get Agent chat client
	 * 
	 * @return ChatClient
	 */
	ChatClient getAgentChatClient();

	/**
	 * Get dynamic chat client
	 * 
	 * @param model
	 * @return ChatClient
	 */
	ChatClient getDynamicChatClient(ModelConfig model);

	/**
	 * Get Agent memory
	 * 
	 * @param maxMessages maximum number of messages
	 * @return ChatMemory
	 */
	ChatMemory getAgentMemory(Integer maxMessages);

	/**
	 * Clear Agent memory
	 * 
	 * @param planId plan ID
	 */
	void clearAgentMemory(String planId);

	/**
	 * Get planning chat client
	 * 
	 * @return ChatClient
	 */
	ChatClient getPlanningChatClient();

	/**
	 * Clear conversation memory
	 * 
	 * @param planId plan ID
	 */
	void clearConversationMemory(String planId);

	/**
	 * Get finalize chat client
	 * 
	 * @return ChatClient
	 */
	ChatClient getFinalizeChatClient();

	/**
	 * Get conversation memory
	 * 
	 * @param maxMessages maximum number of messages
	 * @return ChatMemory
	 */
	ChatMemory getConversationMemory(Integer maxMessages);

	/**
	 * Get chat client by model ID
	 * 
	 * @param modelId model ID
	 * @return ChatClient
	 */
	ChatClient getChatClientByModelId(Long modelId);

	/**
	 * Get default chat client based on configuration
	 * 
	 * @return ChatClient
	 */
	ChatClient getDefaultChatClient();
}
