/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.langchain4j.elasticsearch;

import static dev.langchain4j.internal.Utils.isCollectionEmpty;
import static dev.langchain4j.internal.Utils.isNullOrBlank;
import static dev.langchain4j.internal.Utils.randomUUID;
import static dev.langchain4j.internal.ValidationUtils.ensureNotNull;
import static dev.langchain4j.internal.ValidationUtils.ensureTrue;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.apache.commons.collections.MapUtils;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;

import com.alibaba.fastjson.JSONObject;
import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.ServiceException;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.InlineScript;
import co.elastic.clients.elasticsearch._types.mapping.DenseVectorProperty;
import co.elastic.clients.elasticsearch._types.mapping.Property;
import co.elastic.clients.elasticsearch._types.mapping.TextProperty;
import co.elastic.clients.elasticsearch._types.mapping.TypeMapping;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.ScriptScoreQuery;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.DeleteByQueryRequest;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.bulk.BulkResponseItem;
import co.elastic.clients.json.JsonData;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.endpoints.BooleanResponse;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingStore;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2023年10月28日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.langchain4j.elasticsearch <br>
 */
public class ElasticsearchEmbeddingStore implements EmbeddingStore<TextSegment> {

    /** 4 */
    private static final int NUM_4 = 4;

    /** client */
    private final ElasticsearchClient client;

    /** indexName */
    private final String indexName;

    /** delMeta */
    private final boolean delMeta;

    /**
     * Creates an instance of ElasticsearchEmbeddingStore.
     *
     * @param serverUrl Elasticsearch Server URL
     * @param apiKey Elasticsearch API key (optional)
     * @param userName Elasticsearch userName (optional)
     * @param password Elasticsearch password (optional)
     * @param indexName Elasticsearch index name (optional). Default value: "default"
     * @param delMeta 根据元数据做删除
     */
    public ElasticsearchEmbeddingStore(final String serverUrl, final String apiKey, final String userName,
        final String password, final String indexName, final boolean delMeta) {

        RestClientBuilder restClientBuilder = RestClient
            .builder(HttpHost.create(ensureNotNull(serverUrl, "serverUrl")));

        if (!isNullOrBlank(userName)) {
            CredentialsProvider provider = new BasicCredentialsProvider();
            provider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(userName, password));
            restClientBuilder.setHttpClientConfigCallback(
                httpClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(provider));
        }

        if (!isNullOrBlank(apiKey)) {
            restClientBuilder.setDefaultHeaders(new Header[] {
                new BasicHeader("Authorization", "Apikey " + apiKey)
            });
        }

        ElasticsearchTransport transport = new RestClientTransport(restClientBuilder.build(), new JacksonJsonpMapper());

        this.client = new ElasticsearchClient(transport);
        this.indexName = ensureNotNull(indexName, "indexName");
        this.delMeta = delMeta;
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
     * @CreateDate 2023年10月28日 <br>
     * @since V1.0<br>
     * @see com.hbasesoft.framework.langchain4j.elasticsearch <br>
     */
    public static class Builder {

        /** serverUrl */
        private String serverUrl;

        /** apiKey */
        private String apiKey;

        /** userName */
        private String userName;

        /** password */
        private String password;

        /** delMeta */
        private boolean delMeta;

        /** indexName */
        private String indexName = "hbasesoft_langchain4j";

        /**
         * Description: <br>
         * 
         * @author 王伟<br>
         * @taskId <br>
         * @param serverurl Elasticsearch Server URL
         * @return <br>
         */
        public Builder serverUrl(final String serverurl) {
            this.serverUrl = serverurl;
            return this;
        }

        /**
         * @param apikey Elasticsearch API key (optional)
         * @return builder
         */
        public Builder apiKey(final String apikey) {
            this.apiKey = apikey;
            return this;
        }

        /**
         * @param username Elasticsearch userName (optional)
         * @return builder
         */
        public Builder userName(final String username) {
            this.userName = username;
            return this;
        }

        /**
         * @param pwd Elasticsearch password (optional)
         * @return builder
         */
        public Builder password(final String pwd) {
            this.password = pwd;
            return this;
        }

        /**
         * Description: <br>
         * 
         * @author 王伟<br>
         * @taskId <br>
         * @param indexname Elasticsearch index name (optional). Default value: "default".
         * @return <br>
         */
        public Builder indexName(final String indexname) {
            this.indexName = indexname;
            return this;
        }

        /**
         * Description: <br>
         * 
         * @author 王伟<br>
         * @taskId <br>
         * @param delmeta
         * @return <br>
         */
        public Builder delMeta(final boolean delmeta) {
            this.delMeta = delmeta;
            return this;
        }

        /**
         * Description: <br>
         * 
         * @author 王伟<br>
         * @taskId <br>
         * @return <br>
         */
        public ElasticsearchEmbeddingStore build() {
            return new ElasticsearchEmbeddingStore(serverUrl, apiKey, userName, password, indexName, delMeta);
        }
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param embedding
     * @return <br>
     */
    @Override
    public String add(final Embedding embedding) {
        String id = randomUUID();
        add(id, embedding);
        return id;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param id
     * @param embedding <br>
     */
    @Override
    public void add(final String id, final Embedding embedding) {
        addInternal(id, embedding, null);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param embedding
     * @param textSegment
     * @return <br>
     */
    @Override
    public String add(final Embedding embedding, final TextSegment textSegment) {
        String id = randomUUID();
        addInternal(id, embedding, textSegment);
        return id;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param embeddings
     * @return <br>
     */
    @Override
    public List<String> addAll(final List<Embedding> embeddings) {
        List<String> ids = embeddings.stream().map(ignored -> randomUUID()).collect(toList());
        addAllInternal(ids, embeddings, null);
        return ids;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param embeddings
     * @param embedded
     * @return <br>
     */
    @Override
    public List<String> addAll(final List<Embedding> embeddings, final List<TextSegment> embedded) {
        List<String> ids = embeddings.stream().map(ignored -> randomUUID()).collect(toList());
        addAllInternal(ids, embeddings, embedded);
        return ids;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param referenceEmbedding
     * @param maxResults
     * @param minScore
     * @return <br>
     */
    @Override
    public List<EmbeddingMatch<TextSegment>> findRelevant(final Embedding referenceEmbedding, final int maxResults,
        final double minScore) {
        try {
            // Use Script Score and cosineSimilarity to calculate
            // see
            // https://www.elastic.co/guide/en/elasticsearch/reference/current
            // query-dsl-script-score-query.html#vector-functions-cosine
            ScriptScoreQuery scriptScoreQuery = buildDefaultScriptScoreQuery(referenceEmbedding.vector(),
                (float) minScore);
            SearchResponse<Document> response = client.search(
                SearchRequest.of(s -> s.index(indexName).query(n -> n.scriptScore(scriptScoreQuery)).size(maxResults)),
                Document.class);

            return toEmbeddingMatch(response);
        }
        catch (IOException e) {
            LoggerUtil.error("[ElasticSearch encounter I/O Exception]", e);
            throw new ServiceException(e);
        }
    }

    private void addInternal(final String id, final Embedding embedding, final TextSegment embedded) {
        addAllInternal(singletonList(id), singletonList(embedding), embedded == null ? null : singletonList(embedded));
    }

    private void addAllInternal(final List<String> ids, final List<Embedding> embeddings,
        final List<TextSegment> embedded) {
        if (isCollectionEmpty(ids) || isCollectionEmpty(embeddings)) {
            LoggerUtil.info("[do not add empty embeddings to elasticsearch]");
            return;
        }
        ensureTrue(ids.size() == embeddings.size(), "ids size is not equal to embeddings size");
        ensureTrue(embedded == null || embeddings.size() == embedded.size(),
            "embeddings size is not equal to embedded size");

        try {
            createIndexIfNotExist(embeddings.get(0).dimensions());

            bulk(ids, embeddings, embedded);
        }
        catch (IOException e) {
            LoggerUtil.error("[ElasticSearch encounter I/O Exception]", e);
            throw new ServiceException(e);
        }
    }

    private void createIndexIfNotExist(final int dim) throws IOException {
        BooleanResponse response = client.indices().exists(c -> c.index(indexName));
        if (!response.value()) {
            client.indices().create(c -> c.index(indexName).mappings(getDefaultMappings(dim)));
        }
    }

    private TypeMapping getDefaultMappings(final int dim) {
        Map<String, Property> properties = new HashMap<>(NUM_4);
        properties.put("text", Property.of(p -> p.text(TextProperty.of(t -> t))));
        properties.put("vector", Property.of(p -> p.denseVector(DenseVectorProperty.of(d -> d.dims(dim)))));
        return TypeMapping.of(c -> c.properties(properties));
    }

    private void bulk(final List<String> ids, final List<Embedding> embeddings, final List<TextSegment> embedded)
        throws IOException {
        int size = ids.size();
        BulkRequest.Builder bulkBuilder = new BulkRequest.Builder();
        Map<String, String> metaData = null;
        for (int i = 0; i < size; i++) {
            int finalI = i;
            Document document = Document.builder().vector(embeddings.get(i).vector())
                .text(embedded == null ? null : embedded.get(i).text()).metadata(embedded == null ? null
                    : Optional.ofNullable(embedded.get(i).metadata()).map(Metadata::asMap).orElse(null))
                .build();
            bulkBuilder.operations(op -> op.index(idx -> idx.index(indexName).id(ids.get(finalI)).document(document)));
            if (MapUtils.isEmpty(metaData)) {
                metaData = document.getMetadata();
            }
        }

        // 根据元数据做删除历史数据
        if (delMeta && MapUtils.isNotEmpty(metaData)) {
            BoolQuery.Builder boolQuery = new BoolQuery.Builder();
            boolean flag = false;
            for (Entry<String, String> entry : metaData.entrySet()) {
                if (!"index".equals(entry.getKey())) {
                    flag = true;
                    boolQuery.must(t -> t.term(t2 -> t2.field("metadata." + entry.getKey()).value(entry.getValue())));
                }
            }
            if (flag) {
                DeleteByQueryRequest.Builder builder = new DeleteByQueryRequest.Builder().index(indexName)
                    .query(boolQuery.build()._toQuery());
                client.deleteByQuery(builder.build());
            }
        }

        BulkResponse response = client.bulk(bulkBuilder.build());
        if (response.errors()) {
            for (BulkResponseItem item : response.items()) {
                if (item.error() != null) {
                    throw new ServiceException(ErrorCodeDef.LLM_ERROR,
                        "type: " + item.error().type() + ", reason: " + item.error().reason());

                }
            }
        }
    }

    private ScriptScoreQuery buildDefaultScriptScoreQuery(final float[] vector, final float minScore) {
        JsonData queryVector = toJsonData(vector);
        return ScriptScoreQuery.of(q -> q.minScore(minScore).query(Query.of(qu -> qu.matchAll(m -> m)))
            .script(s -> s.inline(InlineScript.of(i -> i
                // The script adds 1.0 to the cosine similarity to prevent the score from being negative.
                // divided by 2 to keep score in the range [0, 1]
                .source("(cosineSimilarity(params.query_vector, 'vector') + 1.0) / 2")
                .params("query_vector", queryVector)))));
    }

    private <T> JsonData toJsonData(final T rawData) {
        return JsonData.fromJson(JSONObject.toJSONString(rawData));
    }

    private List<EmbeddingMatch<TextSegment>> toEmbeddingMatch(final SearchResponse<Document> response) {
        return response.hits().hits().stream()
            .map(hit -> Optional.ofNullable(hit.source())
                .map(document -> new EmbeddingMatch<>(hit.score(), hit.id(), new Embedding(document.getVector()),
                    document.getText() == null ? null
                        : TextSegment.from(document.getText(), new Metadata(document.getMetadata()))))
                .orElse(null))
            .collect(toList());
    }
}
