/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.agent.prompt;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import com.hbasesoft.framework.common.utils.logger.LoggerUtil;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月19日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.agent.prompt <br>
 */
@Component
public class PromptLoader implements IPromptLoader {

	private static final String PROMPT_BASE_PATH = "prompts/";

	// Cache for loaded prompt content
	private final Map<String, String> promptCache = new ConcurrentHashMap<>();

	/**
	 * Load prompt template content
	 * 
	 * @param promptPath Relative path of prompt file (relative to prompts
	 *                   directory)
	 * @return Prompt content
	 */
	public String loadPrompt(String promptPath) {
		return promptCache.computeIfAbsent(promptPath, this::loadPromptFromResource);
	}

	/**
	 * Load prompt content from resource file
	 * 
	 * @param promptPath Prompt file path
	 * @return Prompt content
	 */
	private String loadPromptFromResource(String promptPath) {
		try {
			String fullPath = PROMPT_BASE_PATH + promptPath;
			Resource resource = new ClassPathResource(fullPath);

			if (!resource.exists()) {
				LoggerUtil.info("Prompt file not found: {0}", fullPath);
				return "";
			}

			String content = resource.getContentAsString(StandardCharsets.UTF_8);
			LoggerUtil.debug("Loaded prompt from: {0}", fullPath);
			return content;

		} catch (IOException e) {
			LoggerUtil.error(e, "Failed to load prompt from: {0}", promptPath);
			return "";
		}
	}

	/**
	 * Clear prompt cache
	 */
	public void clearCache() {
		promptCache.clear();
		LoggerUtil.info("Prompt cache cleared");
	}

}
