/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.agent.dynamic.mcp.service;

import java.io.IOException;

import org.springframework.ai.mcp.AsyncMcpToolCallbackProvider;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hbasesoft.framework.ai.agent.dynamic.mcp.config.McpProperties;
import com.hbasesoft.framework.ai.agent.dynamic.mcp.model.vo.McpConfigVO;
import com.hbasesoft.framework.ai.agent.dynamic.mcp.model.vo.McpServerConfig;
import com.hbasesoft.framework.ai.agent.dynamic.mcp.model.vo.McpServiceVo;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;

import io.modelcontextprotocol.client.McpAsyncClient;
import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.spec.McpClientTransport;
import io.modelcontextprotocol.spec.McpSchema;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月21日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.agent.dynamic.mcp.service <br>
 */
@Component
public class McpConnectionFactory {

    private final McpTransportBuilder transportBuilder;

    private final McpConfigValidator configValidator;

    private final McpProperties mcpProperties;

    private final ObjectMapper objectMapper;

    public McpConnectionFactory(McpTransportBuilder transportBuilder, McpConfigValidator configValidator,
        McpProperties mcpProperties, ObjectMapper objectMapper) {
        this.transportBuilder = transportBuilder;
        this.configValidator = configValidator;
        this.mcpProperties = mcpProperties;
        this.objectMapper = objectMapper;
    }

    /**
     * Create MCP connection
     * 
     * @param McpConfigPo MCP configuration entity
     * @return MCP service entity
     * @throws IOException Thrown when creation fails
     */
    public McpServiceVo createConnection(McpConfigVO McpConfigPo) throws IOException {
        String serverName = McpConfigPo.getMcpServerName();

        // Validate configuration entity
        configValidator.validateMcpConfigPo(McpConfigPo);

        // Check if enabled
        if (!configValidator.isEnabled(McpConfigPo)) {
            LoggerUtil.info("Skipping disabled MCP server: {0}", serverName);
            return null;
        }

        // Parse server configuration
        McpServerConfig serverConfig = parseServerConfig(McpConfigPo.getConnectionConfig(), serverName);

        // Build transport
        McpClientTransport transport = transportBuilder.buildTransport(McpConfigPo.getConnectionType(), serverConfig,
            serverName);

        // Configure MCP transport
        return configureMcpTransport(serverName, transport);
    }

    /**
     * Parse server configuration
     * 
     * @param connectionConfig Connection configuration JSON
     * @param serverName Server name
     * @return Server configuration object
     * @throws IOException Thrown when parsing fails
     */
    private McpServerConfig parseServerConfig(String connectionConfig, String serverName) throws IOException {
        try (JsonParser jsonParser = objectMapper.createParser(connectionConfig)) {
            return jsonParser.readValueAs(McpServerConfig.class);
        }
        catch (Exception e) {
            LoggerUtil.error("Failed to parse server config for server: {0}", serverName, e);
            throw new IOException("Failed to parse server config for server: " + serverName, e);
        }
    }

    /**
     * Configure MCP transport
     * 
     * @param mcpServerName MCP server name
     * @param transport MCP client transport
     * @return MCP service entity
     * @throws IOException Thrown when configuration fails
     */
    private McpServiceVo configureMcpTransport(String mcpServerName, McpClientTransport transport) throws IOException {
        if (transport == null) {
            return null;
        }

        McpAsyncClient mcpAsyncClient = McpClient.async(transport)
            .clientInfo(new McpSchema.Implementation(mcpServerName, "1.0.0")).build();

        // Retry mechanism
        int maxRetries = mcpProperties.getMaxRetries();
        Exception lastException = null;

        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                LoggerUtil.debug("Attempting to initialize MCP transport for: {0} (attempt {0}/{1})", mcpServerName,
                    attempt, maxRetries);

                mcpAsyncClient.initialize().timeout(mcpProperties.getTimeout())
                    .doOnSuccess(
                        result -> LoggerUtil.info("MCP client initialized successfully for {0}", mcpServerName))
                    .doOnError(error -> LoggerUtil.error("Failed to initialize MCP client for {0}: {1}", mcpServerName,
                        error.getMessage()))
                    .block();

                LoggerUtil.info("MCP transport configured successfully for: {0} (attempt {1})", mcpServerName, attempt);

                AsyncMcpToolCallbackProvider callbackProvider = new AsyncMcpToolCallbackProvider(mcpAsyncClient);
                return new McpServiceVo(mcpAsyncClient, callbackProvider, mcpServerName);
            }
            catch (Exception e) {
                lastException = e;
                LoggerUtil.warn("Failed to initialize MCP transport for {0} on attempt {1}/{2}: {3}", mcpServerName,
                    attempt, maxRetries, e.getMessage());

                if (attempt < maxRetries) {
                    try {
                        // Incremental wait time
                        Thread.sleep(1000L * mcpProperties.getRetryWaitMultiplier() * attempt);
                    }
                    catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        LoggerUtil.warn("Retry wait interrupted for server: {0}", mcpServerName);
                        break;
                    }
                }
            }
        }

        LoggerUtil.error("Failed to initialize MCP transport for {0} after {1} attempts", mcpServerName, maxRetries,
            lastException);
        return null;
    }

}
