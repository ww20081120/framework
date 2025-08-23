package com.hbasesoft.framework.ai.jmanus.llm;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.observation.ChatModelObservationConvention;
import org.springframework.ai.model.SimpleApiKey;
import org.springframework.ai.model.tool.DefaultToolExecutionEligibilityPredicate;
import org.springframework.ai.model.tool.ToolExecutionEligibilityPredicate;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.retry.RetryUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;

import com.hbasesoft.framework.ai.jmanus.config.IManusProperties;
import com.hbasesoft.framework.ai.jmanus.dynamic.model.model.vo.ModelConfig;
import com.hbasesoft.framework.ai.jmanus.dynamic.model.service.DynamicModelService;
import com.hbasesoft.framework.ai.jmanus.event.JmanusListener;
import com.hbasesoft.framework.ai.jmanus.event.ModelChangeEvent;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;

import io.micrometer.observation.ObservationRegistry;
import reactor.core.publisher.Flux;

@Service
public class LlmService implements ILlmService, JmanusListener<ModelChangeEvent> {

	private ChatClient agentExecutionClient;

	private ChatClient planningChatClient;

	private ChatClient finalizeChatClient;

	private ChatMemory conversationMemory;

	private ChatMemory agentMemory;

	private Map<Long, ChatClient> clients = new ConcurrentHashMap<>();

	/*
	 * Required for creating custom chatModel
	 */
	@Autowired
	private ObjectProvider<RestClient.Builder> restClientBuilderProvider;

	@Autowired
	private ObjectProvider<WebClient.Builder> webClientBuilderProvider;

	@Autowired
	private ObjectProvider<ObservationRegistry> observationRegistry;

	@Autowired
	private ObjectProvider<ChatModelObservationConvention> observationConvention;

	@Autowired
	private ObjectProvider<ToolExecutionEligibilityPredicate> openAiToolExecutionEligibilityPredicate;

	@Autowired(required = false)
	private IManusProperties manusProperties;

	@Autowired
	private DynamicModelService dynamicModelService;

	@Autowired
	private ChatMemoryRepository chatMemoryRepository;

	@Autowired
	private LlmTraceRecorder llmTraceRecorder;

	public LlmService() {
	}

	public void init() {
		try {
			LoggerUtil.info("Checking and init ChatClient instance...");

			ModelConfig defaultModel = dynamicModelService.getDefault();
			if (defaultModel == null) {
				List<ModelConfig> availableModels = dynamicModelService.queryAll();
				if (!availableModels.isEmpty()) {
					defaultModel = availableModels.get(0);
					LoggerUtil.info("Cannot find default model, use the first one: {0}", defaultModel.getModelName());
				}
			} else {
				LoggerUtil.info("Find default model: {0}", defaultModel.getModelName());
			}

			if (defaultModel != null) {
				initializeChatClientsWithModel(defaultModel);
				LoggerUtil.info("ChatClient init success");
			} else {
				LoggerUtil.warn("Cannot find any model，ChatClient will be initialize after model being configured");
			}
		} catch (Exception e) {
			LoggerUtil.error("Init ChatClient failed", e);
		}
	}

	private void initializeChatClientsWithModel(ModelConfig model) {
		OpenAiChatOptions.Builder optionsBuilder = OpenAiChatOptions.builder();

		if (model.getTemperature() != null) {
			optionsBuilder.temperature(model.getTemperature());
		}

		if (model.getTopP() != null) {
			optionsBuilder.topP(model.getTopP());
		}

		OpenAiChatOptions defaultOptions = optionsBuilder.build();

		if (this.planningChatClient == null) {
			this.planningChatClient = buildPlanningChatClient(model, defaultOptions);
			LoggerUtil.debug("Planning ChatClient init finish");
		}

		// Initialize agentExecutionClient
		if (this.agentExecutionClient == null) {
			this.agentExecutionClient = buildAgentExecutionClient(model, defaultOptions);
			LoggerUtil.debug("Agent Execution Client init finish");
		}

		// Initialize finalizeChatClient
		if (this.finalizeChatClient == null) {
			this.finalizeChatClient = buildFinalizeChatClient(model, defaultOptions);
			LoggerUtil.debug("Finalize ChatClient init finish");
		}

		// Ensure dynamic ChatClient is also created
		buildOrUpdateDynamicChatClient(model);
	}

	private void tryLazyInitialization() {
		try {
			ModelConfig defaultModel = dynamicModelService.getDefault();
			if (defaultModel == null) {
				List<ModelConfig> availableModels = dynamicModelService.queryAll();
				if (!availableModels.isEmpty()) {
					defaultModel = availableModels.get(0);
				}
			}

			if (defaultModel != null) {
				LoggerUtil.info("Lazy init ChatClient, using model: {0}", defaultModel.getModelName());
				initializeChatClientsWithModel(defaultModel);
			}
		} catch (Exception e) {
			LoggerUtil.error("Lazy init ChatClient failed", e);
		}
	}

	@Override
	public ChatClient getAgentChatClient() {
		if (agentExecutionClient == null) {
			LoggerUtil.warn("Agent ChatClient not initialized...");
			tryLazyInitialization();

			if (agentExecutionClient == null) {
				throw new IllegalStateException("Agent ChatClient not initialized, please specify model first");
			}
		}
		return agentExecutionClient;
	}

	@Override
	public ChatClient getDynamicChatClient(ModelConfig model) {
		Long modelId = model.getId();
		if (clients.containsKey(modelId)) {
			return clients.get(modelId);
		}
		return buildOrUpdateDynamicChatClient(model);
	}

	public ChatClient buildOrUpdateDynamicChatClient(ModelConfig model) {
		Long modelId = model.getId();
		String host = model.getBaseUrl();
		String apiKey = model.getApiKey();
		String modelName = model.getModelName();
		Map<String, String> headers = model.getHeaders();
		OpenAiApi openAiApi = OpenAiApi.builder().baseUrl(host).apiKey(apiKey).build();

		OpenAiChatOptions.Builder chatOptionsBuilder = OpenAiChatOptions.builder().model(modelName);

		if (model.getTemperature() != null) {
			chatOptionsBuilder.temperature(model.getTemperature());
		}

		if (model.getTopP() != null) {
			chatOptionsBuilder.topP(model.getTopP());
		}

		chatOptionsBuilder.internalToolExecutionEnabled(false);

		OpenAiChatOptions chatOptions = chatOptionsBuilder.build();
		if (headers != null) {
			chatOptions.setHttpHeaders(headers);
		}
		OpenAiChatModel openAiChatModel = OpenAiChatModel.builder().openAiApi(openAiApi).defaultOptions(chatOptions)
				.build();
		ChatClient client = ChatClient.builder(openAiChatModel)
				// .defaultAdvisors(MessageChatMemoryAdvisor.builder(agentMemory).build())
				.defaultAdvisors(new SimpleLoggerAdvisor()).build();
		clients.put(modelId, client);
		LoggerUtil.info("Build or update dynamic chat client for model: {0}", modelName);
		return client;
	}

	@Override
	public ChatMemory getAgentMemory(Integer maxMessages) {
		if (agentMemory == null) {
			agentMemory = MessageWindowChatMemory.builder()
					// in memory use by agent
					.chatMemoryRepository(new InMemoryChatMemoryRepository()).maxMessages(maxMessages).build();
		}
		return agentMemory;
	}

	@Override
	public void clearAgentMemory(String memoryId) {
		if (this.agentMemory != null) {
			this.agentMemory.clear(memoryId);
		}
	}

	@Override
	public ChatClient getPlanningChatClient() {
		if (planningChatClient == null) {
			// Try lazy initialization
			LoggerUtil.warn("Agent ChatClient not initialized...");
			tryLazyInitialization();

			if (planningChatClient == null) {
				throw new IllegalStateException("Agent ChatClient not initialized, please specify model first");
			}
		}
		return planningChatClient;
	}

	@Override
	public void clearConversationMemory(String memoryId) {
		if (this.conversationMemory == null) {
			// Default to 100 messages if not specified elsewhere
			this.conversationMemory = MessageWindowChatMemory.builder().chatMemoryRepository(chatMemoryRepository)
					.maxMessages(100).build();
		}
		this.conversationMemory.clear(memoryId);
	}

	@Override
	public ChatClient getFinalizeChatClient() {
		if (finalizeChatClient == null) {
			// Try lazy initialization
			LoggerUtil.warn("Agent ChatClient not initialized...");
			tryLazyInitialization();

			if (finalizeChatClient == null) {
				throw new IllegalStateException("Agent ChatClient not initialized, please specify model first");
			}
		}
		return finalizeChatClient;
	}

	@Override
	public ChatMemory getConversationMemory(Integer maxMessages) {
		if (conversationMemory == null) {
			conversationMemory = MessageWindowChatMemory.builder().chatMemoryRepository(chatMemoryRepository)
					.maxMessages(maxMessages).build();
		}
		return conversationMemory;
	}

	@Override
	public void onEvent(ModelChangeEvent event) {
		ModelConfig DynamicModelPo = event.getDynamicModelEntity();

		initializeChatClientsWithModel(DynamicModelPo);

		if (DynamicModelPo.getIsDefault()) {
			LoggerUtil.info("Model updated");
			this.planningChatClient = null;
			this.agentExecutionClient = null;
			this.finalizeChatClient = null;
			initializeChatClientsWithModel(DynamicModelPo);
		}
	}

	private ChatClient buildPlanningChatClient(ModelConfig DynamicModelPo, OpenAiChatOptions defaultOptions) {
		OpenAiChatModel chatModel = openAiChatModel(DynamicModelPo, defaultOptions);
		return ChatClient.builder(chatModel).defaultAdvisors(new SimpleLoggerAdvisor())
				.defaultOptions(OpenAiChatOptions.fromOptions(defaultOptions)).build();
	}

	private ChatClient buildAgentExecutionClient(ModelConfig DynamicModelPo, OpenAiChatOptions defaultOptions) {
		defaultOptions.setInternalToolExecutionEnabled(false);
		OpenAiChatModel chatModel = openAiChatModel(DynamicModelPo, defaultOptions);
		return ChatClient.builder(chatModel)
				// .defaultAdvisors(MessageChatMemoryAdvisor.builder(agentMemory).build())
				.defaultAdvisors(new SimpleLoggerAdvisor())
				.defaultOptions(OpenAiChatOptions.fromOptions(defaultOptions)).build();
	}

	private ChatClient buildFinalizeChatClient(ModelConfig DynamicModelPo, OpenAiChatOptions defaultOptions) {
		OpenAiChatModel chatModel = openAiChatModel(DynamicModelPo, defaultOptions);
		return ChatClient.builder(chatModel)
				// .defaultAdvisors(MessageChatMemoryAdvisor.builder(conversationMemory).build())
				.defaultAdvisors(new SimpleLoggerAdvisor()).build();
	}

	public OpenAiChatModel openAiChatModel(ModelConfig DynamicModelPo, OpenAiChatOptions defaultOptions) {
		defaultOptions.setModel(DynamicModelPo.getModelName());
		if (defaultOptions.getTemperature() == null && DynamicModelPo.getTemperature() != null) {
			defaultOptions.setTemperature(DynamicModelPo.getTemperature());
		}
		if (defaultOptions.getTopP() == null && DynamicModelPo.getTopP() != null) {
			defaultOptions.setTopP(DynamicModelPo.getTopP());
		}
		Map<String, String> headers = DynamicModelPo.getHeaders();
		if (headers == null) {
			headers = new HashMap<>();
		}
		headers.put("User-Agent", "JManus/3.0.2-SNAPSHOT");
		defaultOptions.setHttpHeaders(headers);
		var openAiApi = openAiApi(restClientBuilderProvider.getIfAvailable(RestClient::builder),
				webClientBuilderProvider.getIfAvailable(WebClient::builder), DynamicModelPo);
		OpenAiChatOptions options = OpenAiChatOptions.fromOptions(defaultOptions);
		var chatModel = OpenAiChatModel.builder().openAiApi(openAiApi).defaultOptions(options)
				// .toolCallingManager(toolCallingManager)
				.toolExecutionEligibilityPredicate(openAiToolExecutionEligibilityPredicate
						.getIfUnique(DefaultToolExecutionEligibilityPredicate::new))
				// .retryTemplate(retryTemplate)
				.observationRegistry(observationRegistry.getIfUnique(() -> ObservationRegistry.NOOP)).build();

		observationConvention.ifAvailable(chatModel::setObservationConvention);

		return chatModel;
	}

	@Override
	public ChatClient getChatClientByModelId(Long modelId) {
		if (modelId == null) {
			return getDefaultChatClient();
		}

		ModelConfig model = dynamicModelService.get(modelId);
		if (model == null) {
			return getDefaultChatClient();
		}

		return getDynamicChatClient(model);
	}

	@Override
	public ChatClient getDefaultChatClient() {
		ModelConfig defaultModel = dynamicModelService.getDefault();
		if (defaultModel != null) {
			return getDynamicChatClient(defaultModel);
		}

		List<ModelConfig> availableModels = dynamicModelService.queryAll();
		if (!availableModels.isEmpty()) {
			return getDynamicChatClient(availableModels.get(0));
		}

		throw new IllegalStateException("Agent ChatClient not initialized, please specify model first");
	}

	private OpenAiApi openAiApi(RestClient.Builder restClientBuilder, WebClient.Builder webClientBuilder,
			ModelConfig DynamicModelPo) {
		Map<String, String> headers = DynamicModelPo.getHeaders();
		MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
		if (headers != null) {
			headers.forEach((key, value) -> multiValueMap.add(key, value));
		}

		// Clone WebClient.Builder and add timeout configuration
		WebClient.Builder enhancedWebClientBuilder = webClientBuilder.clone()
				// Add 5 minutes default timeout setting
				.codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024)) // 10MB
				.filter((request, next) -> next.exchange(request).timeout(Duration.ofMinutes(10)));

		String completionsPath = DynamicModelPo.getCompletionsPath();

		return new OpenAiApi(DynamicModelPo.getBaseUrl(), new SimpleApiKey(DynamicModelPo.getApiKey()), multiValueMap,
				completionsPath, "/v1/embeddings", restClientBuilder, enhancedWebClientBuilder,
				RetryUtils.DEFAULT_RESPONSE_ERROR_HANDLER) {
			@Override
			public ResponseEntity<ChatCompletion> chatCompletionEntity(ChatCompletionRequest chatRequest,
					MultiValueMap<String, String> additionalHttpHeader) {
				llmTraceRecorder.recordRequest(chatRequest);
				return super.chatCompletionEntity(chatRequest, additionalHttpHeader);
			}

			@Override
			public Flux<ChatCompletionChunk> chatCompletionStream(ChatCompletionRequest chatRequest,
					MultiValueMap<String, String> additionalHttpHeader) {
				llmTraceRecorder.recordRequest(chatRequest);
				return super.chatCompletionStream(chatRequest, additionalHttpHeader);
			}
		};
	}

}
