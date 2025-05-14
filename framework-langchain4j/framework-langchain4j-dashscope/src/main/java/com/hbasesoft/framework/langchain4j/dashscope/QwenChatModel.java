/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.langchain4j.dashscope;

import java.util.List;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationParam.ResultFormat;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.utils.UtilException;

import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.internal.Utils;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.output.Response;
import lombok.Getter;

/**
 * <Description> 通义千问聊天模型 <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2023年10月25日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.langchain4j.dashscope <br>
 */
@Getter
public class QwenChatModel implements ChatLanguageModel {

    /** apiKey */
    private final String apiKey;

    /** modelName */
    private final String modelName;

    /** topP */
    private final Double topP;

    /** topK */
    private final Integer topK;

    /** enableSearch */
    private final Boolean enableSearch;

    /** seed */
    private final Integer seed;

    /** generation */
    private final Generation generation;

    /**
     * @param apiKey
     * @param modelName
     * @param topP
     * @param topK
     * @param enableSearch
     * @param seed
     */
    protected QwenChatModel(final String apiKey, final String modelName, final Double topP, final Integer topK,
        final Boolean enableSearch, final Integer seed) {
        this.apiKey = apiKey;
        this.modelName = modelName;
        this.topP = topP;
        this.topK = topK;
        this.enableSearch = enableSearch;
        this.seed = seed;
        this.generation = new Generation();
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param messages
     * @return <br>
     */
    @Override
    public Response<AiMessage> generate(final List<ChatMessage> messages) {
        try {
            GenerationParam param = GenerationParam.builder().apiKey(apiKey).model(modelName).topP(topP).topK(topK)
                .enableSearch(enableSearch).seed(seed).messages(QwenHelper.toQwenMessages(messages))
                .resultFormat(ResultFormat.MESSAGE).build();

            GenerationResult generationResult = generation.call(param);
            String answer = QwenHelper.answerFrom(generationResult);
            return Response.from(AiMessage.from(answer), QwenHelper.tokenUsageFrom(generationResult),
                QwenHelper.finishReasonFrom(generationResult));
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
     * @return <br>
     */
    @Override
    public Response<AiMessage> generate(final List<ChatMessage> messages,
        final List<ToolSpecification> toolSpecifications) {
        throw new IllegalArgumentException("Tools are currently not supported for qwen models");
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param messages
     * @param toolSpecification
     * @return <br>
     */
    @Override
    public Response<AiMessage> generate(final List<ChatMessage> messages, final ToolSpecification toolSpecification) {
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
     * @CreateDate 2023年10月25日 <br>
     * @since V1.0<br>
     * @see com.hbasesoft.framework.langchain4j.dashscope <br>
     */
    @Getter
    public static class Builder {

        /** apiKey */
        private String apiKey;

        /** modelName */
        private String modelName;

        /** topP */
        private Double topP;

        /** topK */
        private Integer topK;

        /** enableSearch */
        private Boolean enableSearch;

        /** seed */
        private Integer seed;

        /**
         * Description: <br>
         * 
         * @author 王伟<br>
         * @taskId <br>
         * @param apikey
         * @return <br>
         */
        public Builder apiKey(final String apikey) {
            this.apiKey = apikey;
            return this;
        }

        /**
         * Description: <br>
         * 
         * @author 王伟<br>
         * @taskId <br>
         * @param modelname
         * @return <br>
         */
        public Builder modelName(final String modelname) {
            this.modelName = modelname;
            return this;
        }

        /**
         * Description: <br>
         * 
         * @author 王伟<br>
         * @taskId <br>
         * @param topp
         * @return <br>
         */
        public Builder topP(final Double topp) {
            this.topP = topp;
            return this;
        }

        /**
         * Description: <br>
         * 
         * @author 王伟<br>
         * @taskId <br>
         * @param topk
         * @return <br>
         */
        public Builder topK(final Integer topk) {
            this.topK = topk;
            return this;
        }

        /**
         * Description: <br>
         * 
         * @author 王伟<br>
         * @taskId <br>
         * @param enablesearch
         * @return <br>
         */
        public Builder enableSearch(final Boolean enablesearch) {
            this.enableSearch = enablesearch;
            return this;
        }

        /**
         * Description: <br>
         * 
         * @author 王伟<br>
         * @taskId <br>
         * @param sd
         * @return <br>
         */
        public Builder seed(final Integer sd) {
            this.seed = sd;
            return this;
        }

        /**
         * Description: <br>
         * 
         * @author 王伟<br>
         * @taskId <br>
         *         <br>
         */
        protected void ensureOptions() {
            if (Utils.isNullOrBlank(apiKey)) {
                throw new IllegalArgumentException("DashScope api key must be defined. It can be generated here: "
                    + "https://dashscope.console.aliyun.com/apiKey");
            }
            modelName = Utils.isNullOrBlank(modelName) ? QwenModelName.QWEN_TURBO : modelName;
            enableSearch = enableSearch != null && enableSearch;
        }

        /**
         * Description: <br>
         * 
         * @author 王伟<br>
         * @taskId <br>
         * @return <br>
         */
        public QwenChatModel build() {
            ensureOptions();
            return new QwenChatModel(apiKey, modelName, topP, topK, enableSearch, seed);
        }
    }
}
