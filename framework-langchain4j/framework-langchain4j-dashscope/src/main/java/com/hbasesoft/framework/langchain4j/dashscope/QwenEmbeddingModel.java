/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.langchain4j.dashscope;

import static com.alibaba.dashscope.embeddings.TextEmbeddingParam.TextType.DOCUMENT;
import static com.alibaba.dashscope.embeddings.TextEmbeddingParam.TextType.QUERY;
import static java.util.Collections.singletonList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.alibaba.dashscope.embeddings.TextEmbedding;
import com.alibaba.dashscope.embeddings.TextEmbeddingOutput;
import com.alibaba.dashscope.embeddings.TextEmbeddingParam;
import com.alibaba.dashscope.embeddings.TextEmbeddingResult;
import com.alibaba.dashscope.embeddings.TextEmbeddingResultItem;
import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.utils.UtilException;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.internal.Utils;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.model.output.TokenUsage;
import lombok.Getter;

/**
 * <Description>通义千问Embedding模型 <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2023年10月26日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.langchain4j.dashscope <br>
 */
@Getter
public class QwenEmbeddingModel implements EmbeddingModel {

    /** type */
    public static final String TYPE_KEY = "type";

    /** query */
    public static final String TYPE_QUERY = "query";

    /** document */
    public static final String TYPE_DOCUMENT = "document";

    /** apiKey */
    private final String apiKey;

    /** modelName */
    private final String modelName;

    /** embedding */
    private final TextEmbedding embedding;

    /**
     * @param apiKey
     * @param modelName
     */
    public QwenEmbeddingModel(final String apiKey, final String modelName) {
        this.apiKey = apiKey;
        this.modelName = modelName;
        this.embedding = new TextEmbedding();
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param textSegments
     * @return <br>
     */
    @Override
    public Response<List<Embedding>> embedAll(final List<TextSegment> textSegments) {
        boolean queries = containsQueries(textSegments);

        if (!queries) {
            // default all documents
            return embedTexts(textSegments, DOCUMENT);
        }
        else {
            boolean documents = containsDocuments(textSegments);
            if (!documents) {
                return embedTexts(textSegments, QUERY);
            }
            else {
                // This is a mixed collection of queries and documents. Embed one by one.
                List<Embedding> embeddings = new ArrayList<>(textSegments.size());
                Integer tokens = null;
                for (TextSegment textSegment : textSegments) {
                    Response<List<Embedding>> result;
                    if (TYPE_QUERY.equalsIgnoreCase(textSegment.metadata(TYPE_KEY))) {
                        result = embedTexts(singletonList(textSegment), QUERY);
                    }
                    else {
                        result = embedTexts(singletonList(textSegment), DOCUMENT);
                    }
                    embeddings.addAll(result.content());
                    if (result.tokenUsage() == null) {
                        continue;
                    }
                    if (tokens == null) {
                        tokens = result.tokenUsage().inputTokenCount();
                    }
                    else {
                        tokens += result.tokenUsage().inputTokenCount();
                    }
                }
                return Response.from(embeddings, new TokenUsage(tokens));
            }
        }
    }

    private boolean containsDocuments(final List<TextSegment> textSegments) {
        return textSegments.stream().map(TextSegment::metadata).map(metadata -> metadata.get(TYPE_KEY))
            .filter(TYPE_DOCUMENT::equalsIgnoreCase).anyMatch(Utils::isNullOrBlank);
    }

    private boolean containsQueries(final List<TextSegment> textSegments) {
        return textSegments.stream().map(TextSegment::metadata).map(metadata -> metadata.get(TYPE_KEY))
            .filter(TYPE_QUERY::equalsIgnoreCase).anyMatch(Utils::isNullOrBlank);
    }

    private Response<List<Embedding>> embedTexts(final List<TextSegment> textSegments,
        final TextEmbeddingParam.TextType textType) {
        TextEmbeddingParam param = TextEmbeddingParam.builder().apiKey(apiKey).model(modelName).textType(textType)
            .texts(textSegments.stream().map(TextSegment::text).collect(Collectors.toList())).build();
        try {
            TextEmbeddingResult generationResult = embedding.call(param);
            // total_tokens are the same as input_tokens in the embedding model
            TokenUsage usage = new TokenUsage(generationResult.getUsage().getTotalTokens());
            List<Embedding> embeddings = Optional.of(generationResult).map(TextEmbeddingResult::getOutput)
                .map(TextEmbeddingOutput::getEmbeddings).orElse(Collections.emptyList()).stream()
                .map(TextEmbeddingResultItem::getEmbedding)
                .map(doubleList -> doubleList.stream().map(Double::floatValue).collect(Collectors.toList()))
                .map(Embedding::from).collect(Collectors.toList());
            return Response.from(embeddings, usage);
        }
        catch (Exception e) {
            throw new UtilException(ErrorCodeDef.LLM_ERROR, e);
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
    public static class Builder {

        /** apiKey */
        private String apiKey;

        /** modelName */
        private String modelName;

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
         *         <br>
         */
        protected void ensureOptions() {
            if (Utils.isNullOrBlank(apiKey)) {
                throw new IllegalArgumentException("DashScope api key must be defined. "
                    + "It can be generated here: https://dashscope.console.aliyun.com/apiKey");
            }
            modelName = Utils.isNullOrBlank(modelName) ? QwenModelName.TEXT_EMBEDDING_V1 : modelName;
        }

        /**
         * Description: <br>
         * 
         * @author 王伟<br>
         * @taskId <br>
         * @return <br>
         */
        public QwenEmbeddingModel build() {
            ensureOptions();
            return new QwenEmbeddingModel(apiKey, modelName);
        }
    }

}
