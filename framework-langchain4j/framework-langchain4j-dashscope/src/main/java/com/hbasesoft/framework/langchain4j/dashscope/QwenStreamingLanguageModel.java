/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.langchain4j.dashscope;

import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationParam.ResultFormat;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.ResultCallback;
import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.utils.UtilException;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.model.StreamingResponseHandler;
import dev.langchain4j.model.language.StreamingLanguageModel;
import dev.langchain4j.model.output.Response;

/**
 * <Description> 通义千问流式回复大语言模型<br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2023年10月26日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.langchain4j.dashscope <br>
 */
public class QwenStreamingLanguageModel extends QwenLanguageModel implements StreamingLanguageModel {

    /**
     * @param apiKey
     * @param modelName
     * @param topP
     * @param topK
     * @param enableSearch
     * @param seed
     */
    public QwenStreamingLanguageModel(final String apiKey, final String modelName, final Double topP,
        final Integer topK, final Boolean enableSearch, final Integer seed) {
        super(apiKey, modelName, topP, topK, enableSearch, seed);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param prompt
     * @param handler <br>
     */
    @Override
    public void generate(final String prompt, final StreamingResponseHandler<String> handler) {
        try {
            GenerationParam param = GenerationParam.builder().apiKey(getApiKey()).model(getModelName()).topP(getTopP())
                .topK(getTopK()).enableSearch(getEnableSearch()).seed(getSeed()).prompt(prompt)
                .resultFormat(ResultFormat.MESSAGE).build();

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
                    Response<AiMessage> response = responseBuilder.build();
                    handler.onComplete(
                        Response.from(response.content().text(), response.tokenUsage(), response.finishReason()));
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
    public static class Builder extends QwenLanguageModel.Builder {

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
        public QwenStreamingLanguageModel build() {
            ensureOptions();
            return new QwenStreamingLanguageModel(getApiKey(), getModelName(), getTopP(), getTopK(), getEnableSearch(),
                getSeed());
        }
    }

}
