/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.agent.file.mcp;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.hbasesoft.framework.ai.agent.dynamic.mcp.model.enums.McpConfigStatus;
import com.hbasesoft.framework.ai.agent.dynamic.mcp.model.vo.McpConfigVO;
import com.hbasesoft.framework.ai.agent.dynamic.mcp.service.AbstractMcpService;
import com.hbasesoft.framework.ai.agent.dynamic.mcp.service.McpCacheManager;
import com.hbasesoft.framework.ai.agent.tool.filesystem.UnifiedDirectoryManager;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;

/**
 * <Description> 基于文件系统的MCP服务实现类 <br>
 * 实现了IMcpService接口，将MCP配置信息存储在mcp.json文件中
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月22日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.ai.jmanus.dynamic.simple.mcp <br>
 */
@Service
public class McpServiceImpl extends AbstractMcpService {

	private static final String MCP_CONFIG_FILE = "mcp.json";

	private final UnifiedDirectoryManager unifiedDirectoryManager;

	/** ObjectMapper用于JSON序列化和反序列化 */
	private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule())
			.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

	public McpServiceImpl(McpCacheManager cacheManager, UnifiedDirectoryManager unifiedDirectoryManager) {
		super(cacheManager);
		this.unifiedDirectoryManager = unifiedDirectoryManager;
	}

	/**
	 * 更新MCP服务器状态
	 * 
	 * @param id     MCP服务器ID
	 * @param status 目标状态
	 * @return 更新成功返回true，否则返回false
	 */
	@Override
	public boolean updateMcpServerStatus(Long id, McpConfigStatus status) {
		try {
			// 从文件加载MCP配置
			Map<String, Object> mcpConfig = loadMcpConfigFromFile();

			// 获取mcpServers配置
			@SuppressWarnings("unchecked")
			Map<String, Object> mcpServers = (Map<String, Object>) mcpConfig.get("mcpServers");
			if (mcpServers == null) {
				mcpServers = new ConcurrentHashMap<>();
				mcpConfig.put("mcpServers", mcpServers);
			}

			// 查找并更新指定ID的服务器状态
			for (Map.Entry<String, Object> entry : mcpServers.entrySet()) {
				@SuppressWarnings("unchecked")
				Map<String, Object> serverConfig = (Map<String, Object>) entry.getValue();
				Object serverId = serverConfig.get("id");

				if (serverId != null && Long.valueOf(serverId.toString()).equals(id)) {
					// 更新状态
					serverConfig.put("status", status.name());

					// 保存到文件
					saveMcpConfigToFile(mcpConfig);

					// 清除缓存以重新加载
					cacheManager.invalidateAllCache();

					LoggerUtil.info("MCP server {0} status updated to {1}", id, status);
					return true;
				}
			}

			LoggerUtil.warn("MCP server with id {0} not found", id);
			return false;
		} catch (Exception e) {
			LoggerUtil.error("Failed to update MCP server status for id: " + id, e);
			return false;
		}
	}

	/**
	 * 查询所有启用的MCP服务
	 * 
	 * @return MCP配置VO列表
	 */
	@Override
	public List<McpConfigVO> queryServices() {
		try {
			// 从文件加载MCP配置
			Map<String, Object> mcpConfig = loadMcpConfigFromFile();

			// 获取mcpServers配置
			@SuppressWarnings("unchecked")
			Map<String, Object> mcpServers = (Map<String, Object>) mcpConfig.get("mcpServers");
			if (mcpServers == null) {
				return new ArrayList<>();
			}

			// 转换为McpConfigVO列表，只返回启用的服务
			List<McpConfigVO> result = new ArrayList<>();
			for (Map.Entry<String, Object> entry : mcpServers.entrySet()) {
				String serverName = entry.getKey();
				@SuppressWarnings("unchecked")
				Map<String, Object> serverConfig = (Map<String, Object>) entry.getValue();

				// 只返回启用的服务
				String status = (String) serverConfig.get("status");
				if (status == null || McpConfigStatus.valueOf(status) == McpConfigStatus.ENABLE) {
					McpConfigVO vo = new McpConfigVO();
					vo.setId(getLongValue(serverConfig.get("id")));
					vo.setMcpServerName(serverName);

					// 设置连接类型
					String connectionType = (String) serverConfig.get("connectionType");
					if (connectionType != null) {
						// 这里需要根据实际的枚举类型来设置
						// vo.setConnectionType(McpConfigType.valueOf(connectionType));
					}

					// 设置连接配置（转换为JSON字符串）
					String connectionConfig = objectMapper.writeValueAsString(serverConfig);
					vo.setConnectionConfig(connectionConfig);

					// 设置状态
					if (status != null) {
						vo.setStatus(McpConfigStatus.valueOf(status));
					} else {
						vo.setStatus(McpConfigStatus.ENABLE);
					}

					result.add(vo);
				}
			}

			return result;
		} catch (Exception e) {
			LoggerUtil.error("Failed to query MCP services", e);
			return new ArrayList<>();
		}
	}

	/**
	 * 从mcp.json文件加载MCP配置
	 * 
	 * @return MCP配置映射
	 * @throws IOException IO异常
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Object> loadMcpConfigFromFile() throws IOException {
		Path mcpConfigFile = unifiedDirectoryManager.getWorkingDirectory().resolve(MCP_CONFIG_FILE);
		if (!Files.exists(mcpConfigFile)) {
			return new ConcurrentHashMap<>();
		}

		String content = Files.readString(mcpConfigFile);
		if (content.trim().isEmpty()) {
			return new ConcurrentHashMap<>();
		}

		return objectMapper.readValue(content, Map.class);
	}

	/**
	 * 将MCP配置保存到mcp.json文件
	 * 
	 * @param mcpConfig MCP配置映射
	 * @throws IOException IO异常
	 */
	private void saveMcpConfigToFile(Map<String, Object> mcpConfig) throws IOException {
		Path mcpConfigFile = unifiedDirectoryManager.getWorkingDirectory().resolve(MCP_CONFIG_FILE);
		unifiedDirectoryManager.ensureDirectoryExists(mcpConfigFile.getParent());
		String content = objectMapper.writeValueAsString(mcpConfig);
		Files.writeString(mcpConfigFile, content);
	}

	/**
	 * 安全地将对象转换为Long值
	 * 
	 * @param value 对象值
	 * @return Long值，如果转换失败则返回null
	 */
	private Long getLongValue(Object value) {
		if (value == null) {
			return null;
		}
		if (value instanceof Long) {
			return (Long) value;
		}
		if (value instanceof Number) {
			return ((Number) value).longValue();
		}
		try {
			return Long.valueOf(value.toString());
		} catch (NumberFormatException e) {
			return null;
		}
	}
}
