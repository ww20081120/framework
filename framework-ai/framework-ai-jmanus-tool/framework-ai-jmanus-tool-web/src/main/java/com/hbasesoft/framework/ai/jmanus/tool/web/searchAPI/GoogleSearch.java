/*
 * Copyright 2025 the original author or authors.
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
package com.hbasesoft.framework.ai.jmanus.tool.web.searchAPI;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hbasesoft.framework.ai.jmanus.tool.AbstractBaseTool;
import com.hbasesoft.framework.ai.jmanus.tool.ToolExecuteResult;
import com.hbasesoft.framework.ai.jmanus.tool.web.searchAPI.serpapi.SerpApiProperties;
import com.hbasesoft.framework.ai.jmanus.tool.web.searchAPI.serpapi.SerpApiService;
import com.hbasesoft.framework.common.utils.PropertyHolder;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Component
public class GoogleSearch extends AbstractBaseTool<GoogleSearch.GoogleSearchInput> {

	private static final Logger log = LoggerFactory.getLogger(GoogleSearch.class);

	private SerpApiService service;

	private final ObjectMapper objectMapper;

	private static String PARAMETERS = """
			{
			    "type": "object",
			    "properties": {
			        "query": {
			            "type": "string",
			            "description": "(required) The search query to submit to Google."
			        },
			        "type": {
			            "type": "string",
			            "description": "search resource type parameter. Currently, only six types are supported. The default parameter is search. Supported types include: search, news, images, videos, places, maps.",
			        },
			        "number": {
			            "type": "integer",
			            "description": "(optional) The number of search results to return. Default is 2.",
			            "default": 2
			        }
			    },
			    "required": ["query"]
			}
			""";

	private static final String name = "google_search";

	private static final String description = """
			Perform a Google search and return a list of relevant links.
			Use this tool when you need to find information on the web, get up-to-date data, or research specific topics.
			The tool returns a list of URLs that match the search query.
			""";

	public static OpenAiApi.FunctionTool getToolDefinition() {
		OpenAiApi.FunctionTool.Function function = new OpenAiApi.FunctionTool.Function(description, name, PARAMETERS);
		OpenAiApi.FunctionTool functionTool = new OpenAiApi.FunctionTool(function);
		return functionTool;
	}

	private static final String SERP_API_KEY = PropertyHolder.getProperty("SERP_API_KEY",
			System.getenv("SERP_API_KEY"));

	private String lastQuery = "";

	private String lastSearchResults = "";

	private Integer lastNumResults = 0;

	private String lastType = "search";

	public GoogleSearch(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
		service = new SerpApiService(new SerpApiProperties(SERP_API_KEY, "google"));
	}

	public ToolExecuteResult run(String toolInput) {
		log.info("GoogleSearch toolInput:{}", toolInput);

		// Add exception handling for JSON deserialization
		try {
			Map<String, Object> toolInputMap = objectMapper.readValue(toolInput,
					new TypeReference<Map<String, Object>>() {
					});
			String query = (String) toolInputMap.get("query");
			this.lastQuery = query;

			Integer numResults = 2;
			if (toolInputMap.get("number") != null) {
				numResults = (Integer) toolInputMap.get("number");
			}
			this.lastNumResults = numResults;

			if (toolInputMap.get("type") != null) {
				this.lastType = (String) toolInputMap.get("type");
			} else {
				this.lastType = "search";
			}

			SerpApiService.Request request = new SerpApiService.Request(query, this.lastType, this.lastNumResults);
			JSONObject response = service.apply(request);
			return getResult(response);

		} catch (Exception e) {
			log.error("Error deserializing JSON", e);
			return new ToolExecuteResult("Error deserializing JSON: " + e.getMessage());
		}
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public String getParameters() {
		return PARAMETERS;
	}

	@Override
	public Class<GoogleSearchInput> getInputType() {
		return GoogleSearchInput.class;
	}

	private ToolExecuteResult getResult(JSONObject response) {

		if (response == null) {
			return new ToolExecuteResult("No good search result found");
		}

		String toret = "";
		if (response.containsKey("organic") && response.get("organic") instanceof List) {
			JSONArray array = response.getJSONArray("organic");
			if (!array.isEmpty()) {
				toret = array.getJSONObject(0).getString("snippet");
			}
		} else if (response.containsKey("images") && response.get("images") instanceof List) {
			JSONArray array = response.getJSONArray("images");
			if (!array.isEmpty()) {
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < array.size(); i++) {
					if (i > 0) {
						sb.append("\n");
					}
					sb.append(array.getJSONObject(i).getString("thumbnail_url"));
				}
				toret = sb.toString();
			}
		} else if (response.containsKey("news") && response.get("news") instanceof List) {
			JSONArray array = response.getJSONArray("news");
			if (!array.isEmpty()) {
				toret = array.getJSONObject(0).getString("snippet");
			}
		} else if (response.containsKey("places") && response.get("places") instanceof List) {
			JSONArray array = response.getJSONArray("places");
			if (!array.isEmpty()) {
				toret = array.getJSONObject(0).getString("title") + ", " + array.getJSONObject(0).getString("address");
			}
		} else if (response.containsKey("videos") && response.get("videos") instanceof List) {
			JSONArray array = response.getJSONArray("videos");
			if (!array.isEmpty()) {
				toret = array.getJSONObject(0).getString("snippet") + "\n "
						+ array.getJSONObject(0).getString("video_url");
			}
		}
		if (StringUtils.isEmpty(toret)) {
			toret = "No good search result found";
		}

		log.warn("SerpapiTool result:{}", toret);
		this.lastSearchResults = toret;
		return new ToolExecuteResult(toret);
	}

	@Override
	public ToolExecuteResult run(GoogleSearchInput input) {
		String query = input.getQuery();
		Integer numResults = input.getNumber() != null ? input.getNumber() : 2;

		log.info("GoogleSearch input: query={}, numResults={}", query, numResults);

		this.lastQuery = query;
		this.lastNumResults = numResults;

		try {
			SerpApiService.Request request = new SerpApiService.Request(query, "search", this.lastNumResults);
			JSONObject response = service.apply(request);

			return getResult(response);
		} catch (Exception e) {
			log.error("Error executing Google search", e);
			return new ToolExecuteResult("Error executing Google search: " + e.getMessage());
		}
	}

	@Override
	public String getCurrentToolStateString() {
		return String.format("""
				Google Search Status:
				- Search Location: %s
				- Recent Search: %s
				- Search Results: %s
				""", new java.io.File("").getAbsolutePath(),
				lastQuery.isEmpty() ? "No search performed yet"
						: String.format("Searched for: '%s' (max results: %d)", lastQuery, lastNumResults),
				lastSearchResults.isEmpty() ? "No results found" : lastSearchResults);
	}

	@Override
	public void cleanup(String planId) {
		// do nothing
	}

	@Override
	public String getServiceGroup() {
		return "default-service-group";
	}

	/**
	 * Internal input class for defining input parameters of Google search tool
	 */
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class GoogleSearchInput {

		private String query;

		private String type;

		@com.fasterxml.jackson.annotation.JsonProperty("number")
		private Integer number;

	}

	public static void main(String[] args) {
		GoogleSearch googleSearch = new GoogleSearch(new ObjectMapper());
		ToolExecuteResult result = googleSearch.run("{\"query\":\"What is AI?\",\"number\":2}");
		System.out.println(result.getOutput());
	}

}
