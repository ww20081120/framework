/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.langchain4j.dashscope;

import static com.alibaba.dashscope.aigc.generation.models.QwenParam.ResultFormat.MESSAGE;

import java.util.List;

import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.aigc.generation.models.QwenParam;
import com.alibaba.dashscope.common.ResultCallback;
import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.utils.UtilException;

import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.model.StreamingResponseHandler;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;

/**
 * <Description> 通义千问流式回复模型 <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2023年10月26日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.langchain4j.dashscope <br>
 */
public class QwenStreamingChatModel extends QwenChatModel implements StreamingChatLanguageModel {

    /**
     * @param apiKey
     * @param modelName
     * @param topP
     * @param topK
     * @param enableSearch
     * @param seed
     */
    public QwenStreamingChatModel(final String apiKey, final String modelName, final Double topP, final Integer topK,
        final Boolean enableSearch, final Integer seed) {
        super(apiKey, modelName, topP, topK, enableSearch, seed);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param messages
     * @param handler <br>
     */
    @Override
    public void generate(final List<ChatMessage> messages, final StreamingResponseHandler<AiMessage> handler) {
        try {
            QwenParam param = QwenParam.builder().apiKey(getApiKey()).model(getModelName()).topP(getTopP())
                .topK(getTopK()).enableSearch(getEnableSearch()).seed(getSeed())
                .messages(QwenHelper.toQwenMessages(messages)).resultFormat(MESSAGE).build();

            QwenStreamingResponseBuilder responseBuilder = new QwenStreamingResponseBuilder();

            getGeneration().streamCall(param, new ResultCallback<GenerationResult>() {
                @Override
                public void onEvent(final GenerationResult result) {
                    String delta = responseBuilder.append(result);
                    if (delta != null) {
                        handler.onNext(delta);
                    }
                }

                @Override
                public void onComplete() {
                    handler.onComplete(responseBuilder.build());
                }

                @Override
                public void onError(final Exception e) {
                    handler.onError(e);
                }
            });
        }
        catch (Exception e) {
            throw new UtilException(e, ErrorCodeDef.LLM_ERROR, e.getMessage());
        }
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param messages
     * @param toolSpecifications
     * @param handler <br>
     */
    @Override
    public void generate(final List<ChatMessage> messages, final List<ToolSpecification> toolSpecifications,
        final StreamingResponseHandler<AiMessage> handler) {
        throw new IllegalArgumentException("Tools are currently not supported for qwen models");
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param messages
     * @param toolSpecification
     * @param handler <br>
     */
    @Override
    public void generate(final List<ChatMessage> messages, final ToolSpecification toolSpecification,
        final StreamingResponseHandler<AiMessage> handler) {
        throw new IllegalArgumentException("Tools are currently not supported for qwen models");
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public static Builder builder() {
        return new Builder();
    }

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
    public static class Builder extends QwenChatModel.Builder {

        /**
         * Description: <br>
         * 
         * @author 王伟<br>
         * @taskId <br>
         * @param apiKey
         * @return <br>
         */
        public Builder apiKey(final String apiKey) {
            return (Builder) super.apiKey(apiKey);
        }

        /**
         * Description: <br>
         * 
         * @author 王伟<br>
         * @taskId <br>
         * @param modelName
         * @return <br>
         */
        public Builder modelName(final String modelName) {
            return (Builder) super.modelName(modelName);
        }

        /**
         * Description: <br>
         * 
         * @author 王伟<br>
         * @taskId <br>
         * @param topP
         * @return <br>
         */
        public Builder topP(final Double topP) {
            return (Builder) super.topP(topP);
        }

        /**
         * Description: <br>
         * 
         * @author 王伟<br>
         * @taskId <br>
         * @param topK
         * @return <br>
         */
        public Builder topK(final Integer topK) {
            return (Builder) super.topK(topK);
        }

        /**
         * Description: <br>
         * 
         * @author 王伟<br>
         * @taskId <br>
         * @param enableSearch
         * @return <br>
         */
        public Builder enableSearch(final Boolean enableSearch) {
            return (Builder) super.enableSearch(enableSearch);
        }

        /**
         * Description: <br>
         * 
         * @author 王伟<br>
         * @taskId <br>
         * @param seed
         * @return <br>
         */
        public Builder seed(final Integer seed) {
            return (Builder) super.seed(seed);
        }

        /**
         * Description: <br>
         * 
         * @author 王伟<br>
         * @taskId <br>
         * @return <br>
         */
        public QwenStreamingChatModel build() {
            ensureOptions();
            return new QwenStreamingChatModel(getApiKey(), getModelName(), getTopP(), getTopK(), getEnableSearch(),
                getSeed());
        }
    }

}
