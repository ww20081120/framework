/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.jmanus.llm;

import java.util.UUID;

import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.openai.api.OpenAiApi.ChatCompletionRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hbasesoft.framework.common.utils.logger.Logger;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月19日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.jmanus.llm <br>
 */
@Component
public class LlmTraceRecorder {

	private static final Logger logger = new Logger("LLM_REQUEST_LOGGER");

	private static final Logger selfLogger = new Logger(LlmTraceRecorder.class);

	private static final ThreadLocal<String> REQUEST_ID = new ThreadLocal<>();

	@Autowired
	private ObjectMapper objectMapper;

	public void recordRequest(ChatCompletionRequest chatRequest) {
		try {
			logger.info("Request[{0}]: {1}", REQUEST_ID.get(), objectMapper.writer().writeValueAsString(chatRequest));
		} catch (Throwable e) {
			selfLogger.error("Failed to serialize chat request", e);
		}
	}

	public void recordResponse(ChatResponse chatResponse) {
		try {
			logger.info("Response[{0}]: {1}", REQUEST_ID.get(), objectMapper.writer().writeValueAsString(chatResponse));
		} catch (Throwable e) {
			selfLogger.error("Failed to serialize chat response", e);
		}
	}

	public static void initRequest() {
		REQUEST_ID.set(UUID.randomUUID().toString());
	}

	public static void clearRequest() {
		REQUEST_ID.remove();
	}
}
