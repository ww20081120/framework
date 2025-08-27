/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.jmanus.planning;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.util.Timeout;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.function.FunctionToolCallback;
import org.springframework.ai.tool.metadata.ToolMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hbasesoft.framework.ai.jmanus.config.IManusProperties;
import com.hbasesoft.framework.ai.jmanus.dynamic.agent.DynamicAgent;
import com.hbasesoft.framework.ai.jmanus.dynamic.agent.ToolCallbackProvider;
import com.hbasesoft.framework.ai.jmanus.dynamic.agent.service.IDynamicAgentLoader;
import com.hbasesoft.framework.ai.jmanus.dynamic.mcp.model.vo.McpServiceVo;
import com.hbasesoft.framework.ai.jmanus.dynamic.mcp.model.vo.McpTool;
import com.hbasesoft.framework.ai.jmanus.dynamic.mcp.service.IMcpService;
import com.hbasesoft.framework.ai.jmanus.dynamic.mcp.service.McpStateHolderService;
import com.hbasesoft.framework.ai.jmanus.dynamic.prompt.service.PromptService;
import com.hbasesoft.framework.ai.jmanus.llm.ILlmService;
import com.hbasesoft.framework.ai.jmanus.llm.StreamingResponseHandler;
import com.hbasesoft.framework.ai.jmanus.planning.coordinator.PlanningCoordinator;
import com.hbasesoft.framework.ai.jmanus.planning.creator.PlanCreator;
import com.hbasesoft.framework.ai.jmanus.planning.executor.factory.PlanExecutorFactory;
import com.hbasesoft.framework.ai.jmanus.planning.finalizer.PlanFinalizer;
import com.hbasesoft.framework.ai.jmanus.planning.model.vo.ExecutionContext;
import com.hbasesoft.framework.ai.jmanus.recorder.PlanExecutionRecorder;
import com.hbasesoft.framework.ai.jmanus.tool.ToolCallBiFunctionDef;
import com.hbasesoft.framework.ai.jmanus.tool.ToolExecuteResult;
import com.hbasesoft.framework.ai.jmanus.tool.filesystem.UnifiedDirectoryManager;
import com.hbasesoft.framework.ai.jmanus.tool.innerStorage.SmartContentSavingService;
import com.hbasesoft.framework.ai.jmanus.tool.mapreduce.MapReduceSharedStateManager;
import com.hbasesoft.framework.ai.jmanus.tool.planning.PlanningTool;
import com.hbasesoft.framework.ai.jmanus.tool.planning.PlanningToolInterface;
import com.hbasesoft.framework.ai.jmanus.tool.terminate.TerminateTool;
import com.hbasesoft.framework.ai.jmanus.tool.workflow.SummaryWorkflow;
import com.hbasesoft.framework.common.utils.ContextHolder;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月19日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.jmanus.planning <br>
 */
@Service
public class PlanningFactory implements IPlanningFactory {

	// private final ChromeDriverService chromeDriverService;

	private final PlanExecutionRecorder recorder;

	private final IManusProperties manusProperties;

	private final SmartContentSavingService innerStorageService;

	private final UnifiedDirectoryManager unifiedDirectoryManager;

	private final IMcpService mcpService;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	@Lazy
	private ILlmService llmService;

	@Autowired
	@Lazy
	private ToolCallingManager toolCallingManager;

	private IDynamicAgentLoader dynamicAgentLoader;

	@Autowired
	private MapReduceSharedStateManager sharedStateManager;

	@Autowired
	@Lazy
	private SummaryWorkflow summaryWorkflow;

	@Autowired
	@Lazy
	private PlanExecutorFactory planExecutorFactory;

	@Autowired
	private PromptService promptService;

	@Autowired
	private StreamingResponseHandler streamingResponseHandler;

	// @Autowired
	// @Lazy
	// private CronService cronService;

	// @Autowired
	// private PptGeneratorOperator pptGeneratorOperator;

	@Value("${agent.init: true}")
	private Boolean agentInit = true;

	public PlanningFactory(PlanExecutionRecorder recorder, IManusProperties manusProperties, IMcpService mcpService,
			SmartContentSavingService innerStorageService, UnifiedDirectoryManager unifiedDirectoryManager) {
		// this.chromeDriverService = chromeDriverService;
		this.recorder = recorder;
		this.manusProperties = manusProperties;
		this.mcpService = mcpService;
		this.innerStorageService = innerStorageService;
		this.unifiedDirectoryManager = unifiedDirectoryManager;
		// this.dataSourceService = dataSourceService;
	}

	@Override
	public PlanningCoordinator createPlanningCoordinator(ExecutionContext context) {
		// Add all dynamic agents from the database
		List<DynamicAgent> agentEntities = getDynamicAgentLoader().getAgents(context);

		PlanningToolInterface planningTool = new PlanningTool();

		PlanCreator planCreator = new PlanCreator(agentEntities, llmService, planningTool, recorder, promptService,
				manusProperties, streamingResponseHandler);

		PlanFinalizer planFinalizer = new PlanFinalizer(llmService, recorder, promptService, manusProperties,
				streamingResponseHandler);

		PlanningCoordinator planningCoordinator = new PlanningCoordinator(planCreator, planExecutorFactory,
				planFinalizer);

		return planningCoordinator;
	}

	// Use the enhanced PlanningCoordinator with dynamic executor selection
	public PlanningCoordinator createPlanningCoordinator(String planId) {

		// Add all dynamic agents from the database
		List<DynamicAgent> agentEntities = getDynamicAgentLoader().getAllAgents();

		PlanningToolInterface planningTool = new PlanningTool();

		PlanCreator planCreator = new PlanCreator(agentEntities, llmService, planningTool, recorder, promptService,
				manusProperties, streamingResponseHandler);

		PlanFinalizer planFinalizer = new PlanFinalizer(llmService, recorder, promptService, manusProperties,
				streamingResponseHandler);

		PlanningCoordinator planningCoordinator = new PlanningCoordinator(planCreator, planExecutorFactory,
				planFinalizer);

		return planningCoordinator;
	}

	@Override
	public Map<String, ToolCallBackContext> toolCallbackMap(String planId, String rootPlanId,
			String expectedReturnInfo) {
		Map<String, ToolCallBackContext> toolCallbackMap = new HashMap<>();
		List<ToolCallBiFunctionDef<?>> toolDefinitions = new ArrayList<>();
//		
		if (innerStorageService == null) {
			LoggerUtil.error("SmartContentSavingService is null, skipping BrowserUseTool registration");
			return toolCallbackMap;
		}
		if (agentInit) {
			toolDefinitions.add(new TerminateTool(planId, expectedReturnInfo));
			Map<String, ToolCallBiFunctionDef> toolsMap = ContextHolder.getContext()
					.getBeansOfType(ToolCallBiFunctionDef.class);
			toolsMap.values().forEach(tool -> {
				tool.setCurrentPlanId(planId);
				toolDefinitions.add(tool);
			});

		} else {
			toolDefinitions.add(new TerminateTool(planId, expectedReturnInfo));
		}

		List<McpServiceVo> functionCallbacks = mcpService.getFunctionCallbacks(planId);
		for (McpServiceVo toolCallback : functionCallbacks) {
			String serviceGroup = toolCallback.getServiceGroup();
			ToolCallback[] tCallbacks = toolCallback.getAsyncMcpToolCallbackProvider().getToolCallbacks();
			for (ToolCallback tCallback : tCallbacks) {
				// The serviceGroup is the name of the tool
				toolDefinitions.add(new McpTool(tCallback, serviceGroup, planId, new McpStateHolderService(),
						innerStorageService, objectMapper));
			}
		}

		// Create FunctionToolCallback for each tool
		for (ToolCallBiFunctionDef<?> toolDefinition : toolDefinitions) {
			FunctionToolCallback<?, ToolExecuteResult> functionToolcallback = FunctionToolCallback
					.builder(toolDefinition.getName(), toolDefinition).description(toolDefinition.getDescription())
					.inputSchema(toolDefinition.getParameters()).inputType(toolDefinition.getInputType())
					.toolMetadata(ToolMetadata.builder().returnDirect(toolDefinition.isReturnDirect()).build()).build();
			toolDefinition.setCurrentPlanId(planId);
			toolDefinition.setRootPlanId(rootPlanId);
			LoggerUtil.info("Registering tool: {0}", toolDefinition.getName());
			ToolCallBackContext functionToolcallbackContext = new ToolCallBackContext(functionToolcallback,
					toolDefinition);
			toolCallbackMap.put(toolDefinition.getName(), functionToolcallbackContext);
		}
		return toolCallbackMap;
	}

	@Bean
	public RestClient.Builder createRestClient() {
		// Create RequestConfig and set the timeout (10 minutes for all timeouts)
		// Set the connection timeout
		RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(Timeout.of(10, TimeUnit.MINUTES))
				.setResponseTimeout(Timeout.of(10, TimeUnit.MINUTES))
				.setConnectionRequestTimeout(Timeout.of(10, TimeUnit.MINUTES)).build();

		// Create CloseableHttpClient and apply the configuration
		HttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(requestConfig).build();

		// Use HttpComponentsClientHttpRequestFactory to wrap HttpClient
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);

		// Create RestClient and set the request factory
		return RestClient.builder().requestFactory(requestFactory);
	}

	/**
	 * Provides an empty ToolCallbackProvider implementation when MCP is disabled
	 */
	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnProperty(name = "spring.ai.mcp.client.enabled", havingValue = "false")
	public ToolCallbackProvider emptyToolCallbackProvider() {
		return () -> new HashMap<String, PlanningFactory.ToolCallBackContext>();
	}

	private IDynamicAgentLoader getDynamicAgentLoader() {
		if (dynamicAgentLoader == null) {
			dynamicAgentLoader = ContextHolder.getContext().getBean(IDynamicAgentLoader.class);
		}
		return dynamicAgentLoader;
	}
}
