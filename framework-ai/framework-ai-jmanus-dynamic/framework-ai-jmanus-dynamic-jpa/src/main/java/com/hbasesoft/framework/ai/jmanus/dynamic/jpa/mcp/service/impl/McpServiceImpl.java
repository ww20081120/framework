/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.jmanus.dynamic.jpa.mcp.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hbasesoft.framework.ai.jmanus.dynamic.jpa.mcp.dao.McpConfigDao;
import com.hbasesoft.framework.ai.jmanus.dynamic.jpa.mcp.po.McpConfigPo4Jpa;
import com.hbasesoft.framework.ai.jmanus.dynamic.jpa.mcp.service.McpMangerService;
import com.hbasesoft.framework.ai.jmanus.dynamic.mcp.model.enums.McpConfigStatus;
import com.hbasesoft.framework.ai.jmanus.dynamic.mcp.model.enums.McpConfigType;
import com.hbasesoft.framework.ai.jmanus.dynamic.mcp.model.vo.McpConfigVO;
import com.hbasesoft.framework.ai.jmanus.dynamic.mcp.model.vo.McpServerConfig;
import com.hbasesoft.framework.ai.jmanus.dynamic.mcp.model.vo.McpServerRequestVO;
import com.hbasesoft.framework.ai.jmanus.dynamic.mcp.model.vo.McpServiceVo;
import com.hbasesoft.framework.ai.jmanus.dynamic.mcp.service.IMcpService;
import com.hbasesoft.framework.ai.jmanus.dynamic.mcp.service.McpCacheManager;
import com.hbasesoft.framework.ai.jmanus.dynamic.mcp.service.McpConfigValidator;
import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.utils.Assert;
import com.hbasesoft.framework.common.utils.ContextHolder;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年7月2日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.jmanus.dynamic.mcp.service <br>
 */
@Service
public class McpServiceImpl implements IMcpService, McpMangerService {

	private final McpConfigDao mcpConfigDao;

	private final McpConfigValidator configValidator;

	private final McpCacheManager cacheManager;

	private final ObjectMapper objectMapper;

	public McpServiceImpl(McpConfigDao McpConfigDao, McpConfigValidator configValidator, McpCacheManager cacheManager,
			ObjectMapper objectMapper) {
		this.mcpConfigDao = McpConfigDao;
		this.configValidator = configValidator;
		this.cacheManager = cacheManager;
		this.objectMapper = objectMapper;
	}

	/**
	 * Batch save MCP server configurations
	 * 
	 * @param configJson MCP configuration JSON string
	 * @return Configuration entity list
	 * @throws IOException IO exception
	 */
	@Override
	public List<McpConfigPo4Jpa> saveMcpServers(String configJson) throws IOException {
		List<McpConfigPo4Jpa> entityList = new ArrayList<>();

		JsonNode jsonNode = objectMapper.readTree(configJson);

		// Check if contains mcpServers field
		if (!jsonNode.has("mcpServers")) {
			throw new IllegalArgumentException("Missing 'mcpServers' field in JSON configuration");
		}

		JsonNode mcpServersNode = jsonNode.get("mcpServers");
		if (!mcpServersNode.isObject()) {
			throw new IllegalArgumentException("'mcpServers' must be an object");
		}

		// Parse directly as Map<String, McpServerConfig>
		Map<String, McpServerConfig> mcpServers = objectMapper.convertValue(mcpServersNode,
				new TypeReference<Map<String, McpServerConfig>>() {
				});

		// Iterate through each MCP server configuration
		for (Map.Entry<String, McpServerConfig> entry : mcpServers.entrySet()) {
			String serverName = entry.getKey();
			McpServerConfig serverConfig = entry.getValue();

			// Validate server configuration
			configValidator.validateServerConfig(serverConfig, serverName);

			// Get connection type
			McpConfigType connectionType = serverConfig.getConnectionType();
			LoggerUtil.info("Using connection type for server '{0}': {1}", serverName, connectionType);

			// Convert to JSON
			String serverConfigJson = serverConfig.toJson();

			// Find or create entity
			McpConfigPo4Jpa mcpConfigEntity = mcpConfigDao
					.getByLambda(q -> q.eq(McpConfigPo4Jpa::getMcpServerName, serverName));
			if (mcpConfigEntity == null) {
				mcpConfigEntity = ContextHolder.getContext().getBean(McpConfigPo4Jpa.class);
				mcpConfigEntity.setConnectionConfig(serverConfigJson);
				mcpConfigEntity.setMcpServerName(serverName);
				mcpConfigEntity.setConnectionType(connectionType);
				// Set status, use from serverConfig if available, otherwise use default
				// value
				if (serverConfig.getStatus() != null) {
					mcpConfigEntity.setStatus(serverConfig.getStatus());
				} else {
					mcpConfigEntity.setStatus(McpConfigStatus.ENABLE);
				}
			} else {
				mcpConfigEntity.setConnectionConfig(serverConfigJson);
				mcpConfigEntity.setConnectionType(connectionType);
				// Update status, use from serverConfig if available, otherwise keep
				// original value
				if (serverConfig.getStatus() != null) {
					mcpConfigEntity.setStatus(serverConfig.getStatus());
				}
			}

			mcpConfigDao.save(mcpConfigEntity);
			entityList.add(mcpConfigEntity);
			LoggerUtil.info("MCP server '{0}' has been saved to database with connection type: {1}", serverName,
					connectionType);
		}

		// Clear cache to reload services
		cacheManager.invalidateAllCache();
		return entityList;
	}

	/**
	 * Save single MCP server configuration
	 * 
	 * @param requestVO MCP server form request
	 * @return Configuration entity
	 * @throws IOException IO exception
	 */
	@Override
	public McpConfigPo4Jpa saveMcpServer(McpServerRequestVO requestVO) throws IOException {
		// Validate request data
		List<String> validationErrors = requestVO.validateWithDetails();
		if (!validationErrors.isEmpty()) {
			String errorMessage = "MCP server configuration validation failed: " + String.join("; ", validationErrors);
			throw new IllegalArgumentException(errorMessage);
		}

		// Build server configuration
		McpServerConfig serverConfig = new McpServerConfig(objectMapper);
		serverConfig.setCommand(requestVO.getCommand());
		serverConfig.setUrl(requestVO.getUrl());
		serverConfig.setArgs(requestVO.getArgs());
		serverConfig.setEnv(requestVO.getEnv());

		// Set status
		if (requestVO.getStatus() != null) {
			serverConfig.setStatus(McpConfigStatus.valueOf(requestVO.getStatus()));
		}

		// Validate server configuration
		configValidator.validateServerConfig(serverConfig, requestVO.getMcpServerName());

		// Get connection type
		McpConfigType connectionType = serverConfig.getConnectionType();
		LoggerUtil.info("Using connection type for server '{0}': {1}", requestVO.getMcpServerName(), connectionType);

		// Convert to JSON
		String configJson = serverConfig.toJson();

		// Find or create entity
		McpConfigPo4Jpa mcpConfigEntity;
		if (requestVO.isUpdate()) {
			// Update mode
			mcpConfigEntity = mcpConfigDao.get(requestVO.getId());
			Assert.notNull(mcpConfigEntity, ErrorCodeDef.PARAM_NOT_NULL, "id");
		} else {
			// Add mode - check if server name already exists
			McpConfigPo4Jpa existingServer = mcpConfigDao
					.getByLambda(q -> q.eq(McpConfigPo4Jpa::getMcpServerName, requestVO.getMcpServerName()));
			configValidator.validateServerNameNotExists(requestVO.getMcpServerName(), existingServer);
			mcpConfigEntity = ContextHolder.getContext().getBean(McpConfigPo4Jpa.class);
		}

		// Update entity
		mcpConfigEntity.setMcpServerName(requestVO.getMcpServerName());
		mcpConfigEntity.setConnectionConfig(configJson);
		mcpConfigEntity.setConnectionType(connectionType);
		mcpConfigEntity.setStatus(serverConfig.getStatus());

		// Save to database
		mcpConfigDao.save(mcpConfigEntity);
		LoggerUtil.info("MCP server '{0}' has been saved to database with connection type: {1}",
				requestVO.getMcpServerName(), connectionType);

		// Clear cache to reload services
		cacheManager.invalidateAllCache();

		return mcpConfigEntity;
	}

	/**
	 * Delete MCP server (by ID)
	 * 
	 * @param id Server ID
	 */
	@Override
	public void removeMcpServer(long id) {
		removeMcpServer((Object) id);
	}

	/**
	 * Delete MCP server (by name)
	 * 
	 * @param mcpServerName Server name
	 */
	@Override
	public void removeMcpServer(String mcpServerName) {
		removeMcpServer((Object) mcpServerName);
	}

	/**
	 * Delete MCP server (generic method)
	 * 
	 * @param identifier Server ID (Long) or server name (String)
	 */
	private void removeMcpServer(Object identifier) {
		McpConfigPo4Jpa mcpConfig = null;

		if (identifier instanceof Long id) {
			mcpConfig = mcpConfigDao.get(id);
		} else if (identifier instanceof String serverName) {
			mcpConfig = mcpConfigDao.getByLambda(q -> q.eq(McpConfigPo4Jpa::getMcpServerName, serverName));
		} else {
			throw new IllegalArgumentException("Identifier must be Long (ID) or String (server name)");
		}

		if (mcpConfig != null) {
			mcpConfigDao.delete(mcpConfig);
			cacheManager.invalidateAllCache();
			LoggerUtil.info("MCP server '{0}' has been removed", mcpConfig.getMcpServerName());
		} else {
			LoggerUtil.warn("MCP server not found for identifier: {0}", identifier);
		}
	}

	/**
	 * Get all MCP server configurations
	 * 
	 * @return MCP configuration entity list
	 */
	@Override
	public List<McpConfigPo4Jpa> getMcpServers() {
		return mcpConfigDao.queryAll();
	}

	/**
	 * Find MCP configuration by ID
	 * 
	 * @param id MCP configuration ID
	 * @return Optional MCP configuration entity
	 */
	public Optional<McpConfigPo4Jpa> findById(Long id) {
		return Optional.ofNullable(mcpConfigDao.get(id));
	}

	/**
	 * Get MCP service entity list
	 * 
	 * @param planId Plan ID
	 * @return MCP service entity list
	 */
	@Override
	public List<McpServiceVo> getFunctionCallbacks(String planId) {
		return cacheManager.getServiceEntities(planId);
	}

	/**
	 * Close MCP service for specified plan
	 * 
	 * @param planId Plan ID
	 */
	@Override
	public void close(String planId) {
		cacheManager.invalidateCache(planId);
	}

	/**
	 * Enable MCP server
	 * 
	 * @param id MCP server ID
	 * @return true if enabled successfully, false otherwise
	 */
	public boolean enableMcpServer(Long id) {
		return updateMcpServerStatus(id, McpConfigStatus.ENABLE);
	}

	/**
	 * Disable MCP server
	 * 
	 * @param id MCP server ID
	 * @return true if disabled successfully, false otherwise
	 */
	public boolean disableMcpServer(Long id) {
		return updateMcpServerStatus(id, McpConfigStatus.DISABLE);
	}

	/**
	 * Update MCP server status
	 * 
	 * @param id     MCP server ID
	 * @param status Target status
	 * @return true if updated successfully, false otherwise
	 */
	@Override
	public boolean updateMcpServerStatus(Long id, McpConfigStatus status) {
		McpConfigPo4Jpa entity = mcpConfigDao.get(id);
		Assert.notNull(entity, ErrorCodeDef.PARAM_NOT_NULL, "id");
		if (entity.getStatus() == status) {
			LoggerUtil.info("MCP server {0} is already {1}", entity.getMcpServerName(), status);
			return true;
		}

		try {
			entity.setStatus(status);
			mcpConfigDao.save(entity);

			// Clear cache to reload services
			cacheManager.invalidateAllCache();

			LoggerUtil.info("MCP server {0} {1} successfully", entity.getMcpServerName(), status);
			return true;
		} catch (Exception e) {
			LoggerUtil.error("Failed to {0} MCP server {1}: {2}", status, entity.getMcpServerName(), e.getMessage(), e);
			return false;
		}
	}

	private McpConfigVO convert(McpConfigPo4Jpa po) {
		McpConfigVO vo = new McpConfigVO();
		BeanUtils.copyProperties(po, vo);
		return vo;
	}

	/**
	 * Description: <br>
	 * 
	 * @author 王伟<br>
	 * @taskId <br>
	 * @return <br>
	 */
	@Override
	public List<McpConfigVO> queryServices() {
		return mcpConfigDao.queryByLambda(q -> q.eq(McpConfigPo4Jpa::getStatus, McpConfigStatus.ENABLE)).stream()
				.map(this::convert).toList();
	}

}
