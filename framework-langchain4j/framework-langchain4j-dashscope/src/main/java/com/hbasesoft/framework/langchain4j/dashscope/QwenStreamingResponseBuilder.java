/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.langchain4j.dashscope;

import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.aigc.generation.GenerationUsage;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.model.output.FinishReason;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.model.output.TokenUsage;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2023年10月26日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.langchain4j.dashscope <br>
 */
public class QwenStreamingResponseBuilder {

    /** generatedContent */
    private String generatedContent = "";

    /** inputTokenCount */
    private Integer inputTokenCount;

    /** outputTokenCount */
    private Integer outputTokenCount;

    /** finishReason */
    private FinishReason finishReason;

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param partialResponse
     * @return <br>
     */
    public String append(final GenerationResult partialResponse) {
        if (partialResponse == null) {
            return null;
        }

        GenerationUsage usage = partialResponse.getUsage();
        if (usage != null) {
            inputTokenCount = usage.getInputTokens();
            outputTokenCount = usage.getOutputTokens();
        }

        FinishReason fr = QwenHelper.finishReasonFrom(partialResponse);
        if (fr != null) {
            this.finishReason = fr;
        }

        String partialContent = QwenHelper.answerFrom(partialResponse);
        String delta = null;
        if (partialContent.length() > generatedContent.length()) {
            delta = partialContent.substring(generatedContent.length());
            generatedContent = partialContent;
        }

        return delta;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public Response<AiMessage> build() {
        return Response.from(AiMessage.from(generatedContent), new TokenUsage(inputTokenCount, outputTokenCount),
            finishReason);
    }
}
