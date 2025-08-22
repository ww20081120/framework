/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.jmanus.dynamic.mcp.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.hbasesoft.framework.ai.jmanus.config.ManusProperties;
import com.hbasesoft.framework.ai.jmanus.dynamic.mcp.config.McpProperties;
import com.hbasesoft.framework.ai.jmanus.dynamic.mcp.model.vo.McpConfigVO;
import com.hbasesoft.framework.ai.jmanus.dynamic.mcp.model.vo.McpServiceVo;
import com.hbasesoft.framework.common.StartupListener;
import com.hbasesoft.framework.common.utils.ContextHolder;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;

import jakarta.annotation.PreDestroy;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月21日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.jmanus.dynamic.mcp.service <br>
 */

@Component
public class McpCacheManager implements StartupListener {

	/**
	 * MCP connection result wrapper class
	 */
	private static class McpConnectionResult {

		private final boolean success;

		private final McpServiceVo serviceEntity;

		private final String serverName;

		private final String errorMessage;

		private final long connectionTime;

		private final int retryCount;

		private final String connectionType;

		public McpConnectionResult(boolean success, McpServiceVo serviceEntity, String serverName, String errorMessage,
				long connectionTime, int retryCount, String connectionType) {
			this.success = success;
			this.serviceEntity = serviceEntity;
			this.serverName = serverName;
			this.errorMessage = errorMessage;
			this.connectionTime = connectionTime;
			this.retryCount = retryCount;
			this.connectionType = connectionType;
		}

		public boolean isSuccess() {
			return success;
		}

		public McpServiceVo getServiceEntity() {
			return serviceEntity;
		}

		public String getServerName() {
			return serverName;
		}

		public String getErrorMessage() {
			return errorMessage;
		}

		public long getConnectionTime() {
			return connectionTime;
		}

		public int getRetryCount() {
			return retryCount;
		}

		public String getConnectionType() {
			return connectionType;
		}

	}

	/**
	 * Double cache wrapper - implements seamless updates
	 */
	private static class DoubleCacheWrapper {

		private volatile Map<String, McpServiceVo> activeCache = new ConcurrentHashMap<>();

		private volatile Map<String, McpServiceVo> backgroundCache = new ConcurrentHashMap<>();

		private final Object switchLock = new Object();

		/**
		 * Atomically switch cache
		 */
		public void switchCache() {
			synchronized (switchLock) {
				Map<String, McpServiceVo> temp = activeCache;
				activeCache = backgroundCache;
				backgroundCache = temp;
			}
		}

		/**
		 * Get current active cache
		 */
		public Map<String, McpServiceVo> getActiveCache() {
			return activeCache;
		}

		/**
		 * Get background cache (for building new data)
		 */
		public Map<String, McpServiceVo> getBackgroundCache() {
			return backgroundCache;
		}

		/**
		 * Update background cache
		 */
		public void updateBackgroundCache(Map<String, McpServiceVo> newCache) {
			backgroundCache = new ConcurrentHashMap<>(newCache);
		}

	}

	private final McpConnectionFactory connectionFactory;

	private IMcpService mcpService;

	private final McpProperties mcpProperties;

	private final ManusProperties manusProperties;

	// Double cache wrapper
	private final DoubleCacheWrapper doubleCache = new DoubleCacheWrapper();

	// Thread pool management
	private final AtomicReference<ExecutorService> connectionExecutorRef = new AtomicReference<>();

	// Scheduled task executor
	private final ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
		Thread t = new Thread(r, "McpCacheUpdateTask");
		t.setDaemon(true);
		return t;
	});

	private ScheduledFuture<?> updateTask;

	private volatile int lastConfigHash = 0;

	// Cache update interval (10 minutes)
	private static final long CACHE_UPDATE_INTERVAL_MINUTES = 10;

	public McpCacheManager(McpConnectionFactory connectionFactory, McpProperties mcpProperties,
			ManusProperties manusProperties) {
		this.connectionFactory = connectionFactory;
		this.mcpProperties = mcpProperties;
		this.manusProperties = manusProperties;
	}

	public void init() {

	}

	/**
	 * Description: <br>
	 * 
	 * @author 王伟<br>
	 * @taskId <br>
	 * @param context <br>
	 */
	@Override
	public void complete(ApplicationContext context) {
		McpCacheManager mm = context.getBean(McpCacheManager.class);
		mm.updateConnectionExecutor();
		mm.initializeCache();
	}

	/**
	 * Automatically load cache on startup
	 */
	public void initializeCache() {
		LoggerUtil.info("Initializing MCP cache manager with double buffer mechanism");

		try {
			// Load initial cache on startup
			Map<String, McpServiceVo> initialCache = loadMcpServices(getMcpService().queryServices());

			// Set both active cache and background cache
			doubleCache.updateBackgroundCache(initialCache);
			doubleCache.switchCache(); // Switch to initial cache

			LoggerUtil.info("Initial cache loaded successfully with {0} services", initialCache.size());

			// Start scheduled update task
			startScheduledUpdate();

		} catch (Exception e) {
			LoggerUtil.error("Failed to initialize cache", e);
		}
	}

	/**
	 * Start scheduled update task
	 */
	private void startScheduledUpdate() {
		if (updateTask != null && !updateTask.isCancelled()) {
			updateTask.cancel(false);
		}

		updateTask = scheduledExecutor.scheduleAtFixedRate(this::updateCacheTask, CACHE_UPDATE_INTERVAL_MINUTES,
				CACHE_UPDATE_INTERVAL_MINUTES, TimeUnit.MINUTES);

		LoggerUtil.info("Scheduled cache update task started, interval: {0} minutes", CACHE_UPDATE_INTERVAL_MINUTES);
	}

	/**
	 * Scheduled cache update task
	 */
	private void updateCacheTask() {
		try {
			LoggerUtil.debug("Starting scheduled cache update task");

			// Query all enabled configurations
			List<McpConfigVO> configs = getMcpService().queryServices();

			// Build new data in background cache
			Map<String, McpServiceVo> newCache = loadMcpServices(configs);

			// Update background cache
			doubleCache.updateBackgroundCache(newCache);

			// Atomically switch cache
			doubleCache.switchCache();

			LoggerUtil.info("Cache updated successfully via scheduled task, services count: {0}", newCache.size());

		} catch (Exception e) {
			LoggerUtil.error("Failed to update cache via scheduled task", e);
		}
	}

	/**
	 * Update connection thread pool (supports dynamic configuration adjustment)
	 */
	private void updateConnectionExecutor() {
		int currentConfigHash = calculateConfigHash();

		// Check if configuration has changed
		if (currentConfigHash != lastConfigHash) {
			LoggerUtil.info("MCP service loader configuration changed, updating thread pool");

			// Close old thread pool
			ExecutorService oldExecutor = connectionExecutorRef.get();
			if (oldExecutor != null && !oldExecutor.isShutdown()) {
				shutdownExecutor(oldExecutor);
			}

			// Create new thread pool
			int maxConcurrentConnections = manusProperties.getMcpMaxConcurrentConnections();
			ExecutorService newExecutor = Executors.newFixedThreadPool(maxConcurrentConnections);
			connectionExecutorRef.set(newExecutor);

			lastConfigHash = currentConfigHash;
			LoggerUtil.info("Updated MCP service loader thread pool with max {0} concurrent connections",
					maxConcurrentConnections);
		}
	}

	/**
	 * Calculate configuration hash value for detecting configuration changes
	 */
	private int calculateConfigHash() {
		int hash = 17;
		hash = 31 * hash + manusProperties.getMcpConnectionTimeoutSeconds();
		hash = 31 * hash + manusProperties.getMcpMaxRetryCount();
		hash = 31 * hash + manusProperties.getMcpMaxConcurrentConnections();
		return hash;
	}

	/**
	 * Safely shutdown thread pool
	 */
	private void shutdownExecutor(ExecutorService executor) {
		if (executor != null && !executor.isShutdown()) {
			executor.shutdown();
			try {
				if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
					executor.shutdownNow();
					if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
						LoggerUtil.warn("Thread pool did not terminate");
					}
				}
			} catch (InterruptedException e) {
				executor.shutdownNow();
				Thread.currentThread().interrupt();
			}
		}
	}

	/**
	 * Get current connection thread pool
	 */
	private ExecutorService getConnectionExecutor() {
		// Check if configuration needs to be updated
		updateConnectionExecutor();
		return connectionExecutorRef.get();
	}

	/**
	 * Load MCP services (parallel processing version)
	 * 
	 * @param mcpConfigEntities MCP configuration entity list
	 * @return MCP service entity mapping
	 * @throws IOException Thrown when loading fails
	 */
	private Map<String, McpServiceVo> loadMcpServices(List<McpConfigVO> mcpConfigEntities) throws IOException {
		Map<String, McpServiceVo> toolCallbackMap = new ConcurrentHashMap<>();

		if (mcpConfigEntities == null || mcpConfigEntities.isEmpty()) {
			LoggerUtil.info("No MCP server configurations found");
			return toolCallbackMap;
		}

		// Record main thread start time
		long mainStartTime = System.currentTimeMillis();
		LoggerUtil.info("Loading {0} MCP server configurations in parallel", mcpConfigEntities.size());

		// Get current configured thread pool
		ExecutorService executor = getConnectionExecutor();

		// Create connections in parallel
		List<CompletableFuture<McpConnectionResult>> futures = mcpConfigEntities.stream()
				.map(config -> CompletableFuture.supplyAsync(() -> createConnectionWithRetry(config), executor))
				.collect(Collectors.toList());

		// Wait for all tasks to complete, set timeout
		CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

		try {
			// Set overall timeout (using current configuration)
			allFutures.get(manusProperties.getMcpConnectionTimeoutSeconds(), TimeUnit.SECONDS);

			// Collect results
			for (int i = 0; i < mcpConfigEntities.size(); i++) {
				try {
					McpConnectionResult result = futures.get(i).get();
					if (result.isSuccess()) {
						toolCallbackMap.put(result.getServerName(), result.getServiceEntity());
					}
				} catch (Exception e) {
					String serverName = mcpConfigEntities.get(i).getMcpServerName();
					LoggerUtil.error("Failed to get result for MCP server: {0}", serverName, e);
				}
			}
		} catch (Exception e) {
			LoggerUtil.error("Timeout or error occurred during parallel MCP connection creation", e);
			// Try to get completed results
			for (int i = 0; i < futures.size(); i++) {
				if (futures.get(i).isDone()) {
					try {
						McpConnectionResult result = futures.get(i).get();
						if (result.isSuccess()) {
							toolCallbackMap.put(result.getServerName(), result.getServiceEntity());
						}
					} catch (Exception ex) {
						LoggerUtil.debug("Failed to get completed result for index: {0}", i, ex);
					}
				}
			}
		}

		// Calculate main thread total time
		long mainEndTime = System.currentTimeMillis();
		long mainTotalTime = mainEndTime - mainStartTime;

		// Collect all results for detailed log output
		List<McpConnectionResult> allResults = new ArrayList<>();
		for (int i = 0; i < mcpConfigEntities.size(); i++) {
			try {
				if (futures.get(i).isDone()) {
					allResults.add(futures.get(i).get());
				}
			} catch (Exception e) {
				// If getting result fails, create a failed result record
				String serverName = mcpConfigEntities.get(i).getMcpServerName();
				allResults.add(new McpConnectionResult(false, null, serverName, "Failed to get result", 0, 0, "N/A"));
			}
		}

		// Output detailed execution log
		LoggerUtil.info("\n"
				+ "╔══════════════════════════════════════════════════════════════════════════════════════════════════════╗\n"
				+ "║                                    MCP Service Loader Execution Report                                ║\n"
				+ "╠══════════════════════════════════════════════════════════════════════════════════════════════════════╣\n"
				+ "║  Main Thread: Started at {0}, Completed at {1}, Total Time: {2}ms                                      ║\n"
				+ "║  Configuration: Timeout={3}s, MaxRetry={4}, MaxConcurrent={5}                                           ║\n"
				+ "║  Summary: {6}/{7} servers loaded successfully                                                         ║\n"
				+ "╠══════════════════════════════════════════════════════════════════════════════════════════════════════╣\n"
				+ "║  Individual Server Results:                                                                          ║\n"
				+ "{8}"
				+ "╚══════════════════════════════════════════════════════════════════════════════════════════════════════╝",
				formatTime(mainStartTime), formatTime(mainEndTime), mainTotalTime,
				manusProperties.getMcpConnectionTimeoutSeconds(), manusProperties.getMcpMaxRetryCount(),
				manusProperties.getMcpMaxConcurrentConnections(), toolCallbackMap.size(), mcpConfigEntities.size(),
				formatIndividualResults(allResults));

		return toolCallbackMap;
	}

	/**
	 * Connection creation method with retry
	 * 
	 * @param config MCP configuration entity
	 * @return Connection result
	 */
	private McpConnectionResult createConnectionWithRetry(McpConfigVO config) {
		String serverName = config.getMcpServerName();
		String connectionType = config.getConnectionType().toString();
		long startTime = System.currentTimeMillis();
		int retryCount = 0;

		// Try to connect, retry at most MAX_RETRY_COUNT times
		for (int attempt = 0; attempt <= manusProperties.getMcpMaxRetryCount(); attempt++) {
			try {
				McpServiceVo serviceEntity = connectionFactory.createConnection(config);

				if (serviceEntity != null) {
					long connectionTime = System.currentTimeMillis() - startTime;
					return new McpConnectionResult(true, serviceEntity, serverName, null, connectionTime, retryCount,
							connectionType);
				} else {
					if (attempt == manusProperties.getMcpMaxRetryCount()) {
						long connectionTime = System.currentTimeMillis() - startTime;
						return new McpConnectionResult(false, null, serverName, "Service entity is null",
								connectionTime, retryCount, connectionType);
					}
					LoggerUtil.debug("Attempt {0} failed for server: {1}, retrying...", attempt + 1, serverName);
					retryCount++;
				}
			} catch (Exception e) {
				if (attempt == manusProperties.getMcpMaxRetryCount()) {
					long connectionTime = System.currentTimeMillis() - startTime;
					return new McpConnectionResult(false, null, serverName, e.getMessage(), connectionTime, retryCount,
							connectionType);
				}
				LoggerUtil.debug("Attempt {0} failed for server: {1}, error: {2}, retrying...", attempt + 1, serverName,
						e.getMessage());
				retryCount++;
			}
		}

		// This line should theoretically never be reached, but for compilation safety
		long connectionTime = System.currentTimeMillis() - startTime;
		return new McpConnectionResult(false, null, serverName, "Max retry attempts exceeded", connectionTime,
				retryCount, connectionType);
	}

	/**
	 * Get MCP services (uniformly use default cache)
	 * 
	 * @param planId Plan ID (use default if null)
	 * @return MCP service entity mapping
	 */
	public Map<String, McpServiceVo> getOrLoadServices(String planId) {
		try {
			// planId is not used.
			// Directly read active cache, no locking needed, ensures seamless operation
			Map<String, McpServiceVo> activeCache = doubleCache.getActiveCache();

			return new ConcurrentHashMap<>(activeCache);
		} catch (Exception e) {
			LoggerUtil.error("Failed to get MCP services for plan: {0}", planId, e);
			return new ConcurrentHashMap<>();
		}
	}

	/**
	 * Get MCP service entity list
	 * 
	 * @param planId Plan ID
	 * @return MCP service entity list
	 */
	public List<McpServiceVo> getServiceEntities(String planId) {
		try {
			return new ArrayList<>(getOrLoadServices(planId).values());
		} catch (Exception e) {
			LoggerUtil.error("Failed to get MCP service entities for plan: {0}", planId, e);
			return new ArrayList<>();
		}
	}

	/**
	 * Manually trigger cache reload
	 */
	public void triggerCacheReload() {
		try {
			LoggerUtil.info("Manually triggering cache reload");

			// Query all enabled configurations
			List<McpConfigVO> configs = getMcpService().queryServices();

			// Build new data in background cache
			Map<String, McpServiceVo> newCache = loadMcpServices(configs);

			// Update background cache
			doubleCache.updateBackgroundCache(newCache);

			// Atomically switch cache
			doubleCache.switchCache();

			LoggerUtil.info("Manual cache reload completed, services count: {0}", newCache.size());

		} catch (Exception e) {
			LoggerUtil.error("Failed to manually reload cache", e);
		}
	}

	/**
	 * Clear cache (compatibility method, actually uses double cache mechanism)
	 * 
	 * @param planId Plan ID
	 */
	public void invalidateCache(String planId) {
		LoggerUtil.info(
				"Cache invalidation requested for plan: {0}, but using double buffer mechanism - no action needed",
				planId);
		// Under double cache mechanism, no need to manually clear cache, will
		// auto-update
	}

	/**
	 * Clear all cache (compatibility method, actually uses double cache mechanism)
	 */
	public void invalidateAllCache() {
		LoggerUtil.info(
				"All cache invalidation requested, but using double buffer mechanism - triggering reload instead");
		// Trigger reload instead of clearing
		triggerCacheReload();
	}

	/**
	 * Refresh cache (compatibility method, actually uses double cache mechanism)
	 * 
	 * @param planId Plan ID
	 */
	public void refreshCache(String planId) {
		LoggerUtil.info("Cache refresh requested for plan: {0}, triggering reload", planId);
		triggerCacheReload();
	}

	/**
	 * Get cache statistics
	 * 
	 * @return Cache statistics
	 */
	public String getCacheStats() {
		Map<String, McpServiceVo> activeCache = doubleCache.getActiveCache();
		return String.format("Double Buffer Cache Stats - Active Services: %d, Last Update: %s", activeCache.size(),
				formatTime(System.currentTimeMillis()));
	}

	/**
	 * Manually update connection configuration (supports runtime dynamic
	 * adjustment)
	 */
	public void updateConnectionConfiguration() {
		LoggerUtil.info("Manually updating MCP service loader configuration");
		updateConnectionExecutor();
	}

	/**
	 * Get current connection configuration information
	 * 
	 * @return Configuration information string
	 */
	public String getConnectionConfigurationInfo() {
		return String.format("MCP Service Loader Config - Timeout: %ds, MaxRetry: %d, MaxConcurrent: %d",
				manusProperties.getMcpConnectionTimeoutSeconds(), manusProperties.getMcpMaxRetryCount(),
				manusProperties.getMcpMaxConcurrentConnections());
	}

	/**
	 * Get cache update configuration information
	 * 
	 * @return Cache update configuration information
	 */
	public String getCacheUpdateConfigurationInfo() {
		return String.format("Cache Update Config - Interval: %d minutes, Double Buffer: enabled",
				CACHE_UPDATE_INTERVAL_MINUTES);
	}

	/**
	 * Close resources (called when application shuts down)
	 */
	@PreDestroy
	public void shutdown() {
		LoggerUtil.info("Shutting down MCP cache manager");

		// Stop scheduled task
		if (updateTask != null && !updateTask.isCancelled()) {
			updateTask.cancel(false);
		}

		// Close scheduled executor
		if (scheduledExecutor != null && !scheduledExecutor.isShutdown()) {
			scheduledExecutor.shutdown();
			try {
				if (!scheduledExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
					scheduledExecutor.shutdownNow();
				}
			} catch (InterruptedException e) {
				scheduledExecutor.shutdownNow();
				Thread.currentThread().interrupt();
			}
		}

		// Close connection thread pool
		ExecutorService executor = connectionExecutorRef.get();
		if (executor != null) {
			shutdownExecutor(executor);
		}

		// Close all MCP client connections
		Map<String, McpServiceVo> activeCache = doubleCache.getActiveCache();
		for (McpServiceVo serviceEntity : activeCache.values()) {
			try {
				serviceEntity.getMcpAsyncClient().close();
			} catch (Throwable t) {
				LoggerUtil.error("Failed to close MCP client", t);
			}
		}

		LoggerUtil.info("MCP cache manager shutdown completed");
	}

	private String formatTime(long time) {
		return String.format("%tF %tT", time, time);
	}

	private String formatIndividualResults(List<McpConnectionResult> results) {
		StringBuilder sb = new StringBuilder();
		for (McpConnectionResult result : results) {
			String status = result.isSuccess() ? "✅ Success" : "❌ Failed";
			String errorInfo = result.getErrorMessage() != null
					? (result.getErrorMessage().length() > 15 ? result.getErrorMessage().substring(0, 12) + "..."
							: result.getErrorMessage())
					: "N/A";

			sb.append(String.format("║  %-20s | %-12s | Type: %-8s | Time: %-6dms | Retry: %-2d | Error: %-15s ║\n",
					result.getServerName(), status, result.getConnectionType(), result.getConnectionTime(),
					result.getRetryCount(), errorInfo));
		}
		return sb.toString();
	}

	private IMcpService getMcpService() {
		if (mcpService == null) {
			mcpService = ContextHolder.getContext().getBean(IMcpService.class);
		}
		return mcpService;
	}
}
