/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.jmanus.dynamic.mcp.config;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月19日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.jmanus.dynamic.mcp.config <br>
 */
@Getter
@Setter
@Component
@ConfigurationProperties("mcp")
public class McpProperties {

	/**
	 * Maximum retry count
	 */
	private int maxRetries = 3;

	/**
	 * Connection timeout duration
	 */
	private Duration timeout = Duration.ofSeconds(60);

	/**
	 * Cache expiration time after access
	 */
	private Duration cacheExpireAfterAccess = Duration.ofMinutes(10);

	/**
	 * Retry wait time multiplier (seconds)
	 */
	private int retryWaitMultiplier = 1;

	/**
	 * SSE URL path suffix
	 */
	private String ssePathSuffix = "/sse";

	/**
	 * User agent
	 */
	private String userAgent = "MCP-Client/1.0.0";
}
