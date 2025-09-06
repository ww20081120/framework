/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.agent.llm;

import java.util.UUID;

import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.openai.api.OpenAiApi.ChatCompletionRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hbasesoft.framework.common.utils.logger.Logger;

/**
 * LLM调用追踪记录器
 *
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月19日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.agent.llm <br>
 */
@Component
public class LlmTraceRecorder {

    /** LLM请求日志记录器 */
    private static final Logger LOGGER = new Logger("LLM_REQUEST_LOGGER");

    /** 本类的日志记录器 */
    private static final Logger SELF_LOGGER = new Logger(LlmTraceRecorder.class);

    /** 请求ID的线程局部变量 */
    private static final ThreadLocal<String> REQUEST_ID = new ThreadLocal<>();

    /** JSON对象映射器 */
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 记录请求
     *
     * @param chatRequest 聊天请求
     */
    public void recordRequest(final ChatCompletionRequest chatRequest) {
        try {
            LOGGER.info("Request[{0}]: {1}", REQUEST_ID.get(), objectMapper.writer().writeValueAsString(chatRequest));
        }
        catch (Throwable e) {
            SELF_LOGGER.error("Failed to serialize chat request", e);
        }
    }

    /**
     * 记录响应
     *
     * @param chatResponse 聊天响应
     */
    public void recordResponse(final ChatResponse chatResponse) {
        try {
            LOGGER.info("Response[{0}]: {1}", REQUEST_ID.get(), objectMapper.writer().writeValueAsString(chatResponse));
        }
        catch (Throwable e) {
            SELF_LOGGER.error("Failed to serialize chat response", e);
        }
    }

    /**
     * 初始化请求
     */
    public static void initRequest() {
        REQUEST_ID.set(UUID.randomUUID().toString());
    }

    /**
     * 清除请求
     */
    public static void clearRequest() {
        REQUEST_ID.remove();
    }
}
