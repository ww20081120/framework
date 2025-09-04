/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.agent.prompt;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.core.io.ClassPathResource;
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
public class PromptDescriptionLoader {

	private final ConcurrentHashMap<String, Properties> descriptionCache = new ConcurrentHashMap<>();

	/**
	 * Load prompt description for a specific language
	 * 
	 * @param promptName the prompt name
	 * @param language   the language code (en, zh)
	 * @return the description or empty string if not found
	 */
	public String loadDescription(String promptName, String language) {
		if (promptName == null || language == null) {
			return "";
		}

		Properties descriptions = getDescriptions(language);
		return descriptions.getProperty(promptName, "");
	}

	/**
	 * Get descriptions for a specific language, with caching
	 * 
	 * @param language the language code
	 * @return Properties containing descriptions
	 */
	private Properties getDescriptions(String language) {
		return descriptionCache.computeIfAbsent(language, this::loadDescriptionsFromFile);
	}

	/**
	 * Load descriptions from file
	 * 
	 * @param language the language code
	 * @return Properties containing descriptions
	 */
	private Properties loadDescriptionsFromFile(String language) {
		Properties properties = new Properties();
		String resourcePath = String.format("prompts/%s/descriptions.properties", language);

		try {
			ClassPathResource resource = new ClassPathResource(resourcePath);
			if (resource.exists()) {
				try (InputStream inputStream = resource.getInputStream()) {
					properties.load(inputStream);
					LoggerUtil.debug("Loaded {0} descriptions for language: {1}", properties.size(), language);
				}
			} else {
				LoggerUtil.info("Description file not found: {0}", resourcePath);
			}
		} catch (IOException e) {
			LoggerUtil.error(e, "Failed to load descriptions for language: {0}", language);
		}

		return properties;
	}

	/**
	 * Clear cache for a specific language
	 * 
	 * @param language the language code
	 */
	public void clearCache(String language) {
		descriptionCache.remove(language);
	}

	/**
	 * Clear all cache
	 */
	public void clearAllCache() {
		descriptionCache.clear();
	}

}
