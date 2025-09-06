/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.agent.dynamic.mcp.service;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hbasesoft.framework.ai.agent.dynamic.mcp.config.McpProperties;
import com.hbasesoft.framework.ai.agent.dynamic.mcp.model.enums.McpConfigType;
import com.hbasesoft.framework.ai.agent.dynamic.mcp.model.vo.McpServerConfig;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;

import io.modelcontextprotocol.client.transport.ServerParameters;
import io.modelcontextprotocol.client.transport.StdioClientTransport;
import io.modelcontextprotocol.client.transport.WebClientStreamableHttpTransport;
import io.modelcontextprotocol.client.transport.WebFluxSseClientTransport;
import io.modelcontextprotocol.spec.McpClientTransport;

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
public class McpTransportBuilder {

    private final McpConfigValidator configValidator;

    private final McpProperties mcpProperties;

    private final ObjectMapper objectMapper;

    public McpTransportBuilder(McpConfigValidator configValidator, McpProperties mcpProperties,
        ObjectMapper objectMapper) {
        this.configValidator = configValidator;
        this.mcpProperties = mcpProperties;
        this.objectMapper = objectMapper;
    }

    /**
     * Build MCP transport
     * 
     * @param configType Configuration type
     * @param serverConfig Server configuration
     * @param serverName Server name
     * @return MCP client transport
     * @throws IOException Thrown when build fails
     */
    public McpClientTransport buildTransport(McpConfigType configType, McpServerConfig serverConfig, String serverName)
        throws IOException {
        // Validate server configuration
        configValidator.validateServerConfig(serverConfig, serverName);

        switch (configType) {
            case SSE -> {
                return buildSseTransport(serverConfig, serverName);
            }
            case STUDIO -> {
                return buildStudioTransport(serverConfig, serverName);
            }
            case STREAMING -> {
                return buildStreamingTransport(serverConfig, serverName);
            }
            default -> {
                throw new IOException("Unsupported connection type: " + configType + " for server: " + serverName);
            }
        }
    }

    /**
     * Build SSE transport
     * 
     * @param serverConfig Server configuration
     * @param serverName Server name
     * @return SSE transport
     * @throws IOException Thrown when build fails
     */
    private McpClientTransport buildSseTransport(McpServerConfig serverConfig, String serverName) throws IOException {
        String url = serverConfig.getUrl().trim();
        configValidator.validateSseUrl(url, serverName);

        URL parsedUrl = new URL(url);
        String baseUrl = parsedUrl.getProtocol() + "://" + parsedUrl.getHost()
            + (parsedUrl.getPort() == -1 ? "" : ":" + parsedUrl.getPort());

        String path = parsedUrl.getPath();
        String sseEndpoint = path;

        // Remove leading slash
        if (sseEndpoint.startsWith("/")) {
            sseEndpoint = sseEndpoint.substring(1);
        }

        // Set to null if empty
        if (sseEndpoint.isEmpty()) {
            sseEndpoint = null;
        }

        LoggerUtil.info("Building SSE transport for server: {0} with baseUrl: {1}, endpoint: {2}", serverName, baseUrl,
            sseEndpoint);

        WebClient.Builder webClientBuilder = createWebClientBuilder(baseUrl);

        if (sseEndpoint != null && !sseEndpoint.isEmpty()) {
            return new WebFluxSseClientTransport(webClientBuilder, objectMapper, sseEndpoint);
        }
        else {
            return new WebFluxSseClientTransport(webClientBuilder, objectMapper);
        }
    }

    /**
     * Build STUDIO transport
     * 
     * @param serverConfig Server configuration
     * @param serverName Server name
     * @return STUDIO transport
     * @throws IOException Thrown when build fails
     */
    private McpClientTransport buildStudioTransport(McpServerConfig serverConfig, String serverName)
        throws IOException {
        String command = serverConfig.getCommand().trim();
        List<String> args = serverConfig.getArgs();
        Map<String, String> env = serverConfig.getEnv();

        LoggerUtil.debug("Building STUDIO transport for server: {0} with command: {1}", serverName, command);

        ServerParameters.Builder builder = ServerParameters.builder(command);

        // Add parameters
        if (args != null && !args.isEmpty()) {
            builder.args(args);
            LoggerUtil.debug("Added {0} arguments for server: {1}", args.size(), serverName);
        }

        // Add environment variables
        if (env != null && !env.isEmpty()) {
            builder.env(env);
            LoggerUtil.debug("Added {0} environment variables for server: {1}", env.size(), serverName);
        }

        ServerParameters serverParameters = builder.build();
        return new StdioClientTransport(serverParameters, objectMapper);
    }

    /**
     * Build STREAMING transport
     * 
     * @param serverConfig Server configuration
     * @param serverName Server name
     * @return STREAMING transport
     * @throws IOException Thrown when build fails
     */
    private McpClientTransport buildStreamingTransport(McpServerConfig serverConfig, String serverName)
        throws IOException {
        String url = serverConfig.getUrl().trim();
        configValidator.validateUrl(url, serverName);

        URL parsedUrl = new URL(url);
        String baseUrl = parsedUrl.getProtocol() + "://" + parsedUrl.getHost()
            + (parsedUrl.getPort() == -1 ? "" : ":" + parsedUrl.getPort());

        String streamEndpoint = parsedUrl.getPath();

        // Remove leading slash
        if (streamEndpoint.startsWith("/")) {
            streamEndpoint = streamEndpoint.substring(1);
        }

        // Set to null if empty
        if (streamEndpoint.isEmpty()) {
            streamEndpoint = null;
        }

        LoggerUtil.info("Building Streamable HTTP transport for server: {0} with Url: {1} and Endpoint: {2}",
            serverName, baseUrl, streamEndpoint);

        WebClient.Builder webClientBuilder = createWebClientBuilder(baseUrl);

        LoggerUtil.debug("Using WebClientStreamableHttpTransport with endpoint: {0} for STREAMING mode",
            streamEndpoint);
        return WebClientStreamableHttpTransport.builder(webClientBuilder).objectMapper(objectMapper)
            .endpoint(streamEndpoint).resumableStreams(true).openConnectionOnStartup(false).build();

    }

    /**
     * Create WebClient builder (with baseUrl)
     * 
     * @param baseUrl Base URL
     * @return WebClient builder
     */
    private WebClient.Builder createWebClientBuilder(String baseUrl) {
        return WebClient.builder().baseUrl(baseUrl).defaultHeader("Accept", "text/event-stream")
            .defaultHeader("Content-Type", "application/json").defaultHeader("User-Agent", mcpProperties.getUserAgent())
            .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 1024 * 10));

    }

}
