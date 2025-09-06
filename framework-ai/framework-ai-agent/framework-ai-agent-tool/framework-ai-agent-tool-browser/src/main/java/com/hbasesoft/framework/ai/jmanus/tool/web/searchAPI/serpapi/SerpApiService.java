/*
 * Copyright 2024-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hbasesoft.framework.ai.jmanus.tool.web.searchAPI.serpapi;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;

import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import reactor.core.publisher.Mono;

public class SerpApiService {

    private static final Logger logger = LoggerFactory.getLogger(SerpApiService.class);

    private final WebClient webClient;

    private final String apikey;

    private final String engine;

    private static final int MEMORY_SIZE = 5;

    private static final int BYTE_SIZE = 1024;

    private static final int MAX_MEMORY_SIZE = MEMORY_SIZE * BYTE_SIZE * BYTE_SIZE;

    public SerpApiService(SerpApiProperties properties) {
        this.apikey = properties.getApikey();
        this.engine = properties.getEngine();
        this.webClient = WebClient.builder().baseUrl(SerpApiProperties.SERP_API_URL)
            .defaultHeader(HttpHeaders.USER_AGENT, SerpApiProperties.USER_AGENT_VALUE)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + this.apikey)
            .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(MAX_MEMORY_SIZE)).build();
    }

    /**
     * Use SerpAPI to search data
     * 
     * @param request the function argument
     * @return responseMono
     */
    public JSONObject apply(Request request) {
        if (request == null || !StringUtils.hasText(request.query)) {
            return null;
        }
        try {
            Mono<String> responseMono = webClient.method(HttpMethod.POST).bodyValue(JSONObject.toJSONString(request))
                .retrieve().bodyToMono(String.class);
            String response = responseMono.block();
            assert response != null;
            logger.info("serpapi search: {},result:{}", request.query, response);
            return JSONObject.parseObject(response);
        }
        catch (Exception e) {
            logger.error("failed to invoke serpapi search, caused by:{}", e.getMessage());
            return null;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonClassDescription("serpapi search request")
    public record Request(
        @JsonProperty(required = true,
            value = "query") @JsonPropertyDescription("The query " + "keyword e.g. Alibaba") String query,
        @JsonProperty(required = false, value = "type",
            defaultValue = "search") @JsonPropertyDescription("search resource type parameter. Currently, only six types are supported. The default parameter is search. ") String type,

        @JsonProperty(required = false, value = "number",
            defaultValue = "10") @JsonPropertyDescription("The number of search results, default is 10") int number) {
    }

    @JsonClassDescription("serpapi search response")
    public record Response(List<SearchResult> results) {
    }

    public record SearchResult(String title, String text) {
    }

}
