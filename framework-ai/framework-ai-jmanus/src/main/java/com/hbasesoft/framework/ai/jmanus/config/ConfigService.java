/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.jmanus.config;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hbasesoft.framework.ai.jmanus.config.dao.ConfigDao;
import com.hbasesoft.framework.ai.jmanus.config.model.ConfigCacheEntry;
import com.hbasesoft.framework.ai.jmanus.config.model.po.ConfigPo;
import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.utils.Assert;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月21日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.jmanus.config <br>
 */

@Service
public class ConfigService implements IConfigService, ApplicationListener<ContextRefreshedEvent> {

	@Autowired
	private ConfigDao configRepository;

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private Environment environment;

	private final Map<String, ConfigCacheEntry<String>> configCache = new ConcurrentHashMap<>();

	private boolean initialized = false;

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		if (!initialized) {
			initialized = true;
			init();
		}
	}

	private void init() {
		// Only get beans with @ConfigurationProperties annotation
		Map<String, Object> configBeans = applicationContext.getBeansWithAnnotation(ConfigurationProperties.class);
		LoggerUtil.info("Found {0} configuration beans", configBeans.size());

		// Initialize each configuration bean
		configBeans.values().forEach(this::initializeConfig);
	}

	private void initializeConfig(Object bean) {
		// Collect all valid config paths from the bean
		Set<String> validConfigPaths = Arrays.stream(bean.getClass().getDeclaredFields())
				.filter(field -> field.isAnnotationPresent(ConfigProperty.class))
				.map(field -> field.getAnnotation(ConfigProperty.class).path()).collect(Collectors.toSet());

		// Remove obsolete configurations that are no longer defined in ManusProperties
		if (bean instanceof ManusProperties) {
			LoggerUtil.info("Cleaning up obsolete configurations not defined in ManusProperties...");
			List<ConfigPo> allConfigs = configRepository.queryAll();
			List<ConfigPo> obsoleteConfigs = allConfigs.stream()
					.filter(config -> !validConfigPaths.contains(config.getConfigPath())).collect(Collectors.toList());

			if (!obsoleteConfigs.isEmpty()) {
				LoggerUtil.info("Found {0} obsolete configurations to remove:", obsoleteConfigs.size());
				obsoleteConfigs.forEach(config -> {
					LoggerUtil.info("  - Removing obsolete config: {0} ({1})", config.getConfigPath(),
							config.getDescription());
					configRepository.delete(config);
					// Remove from cache as well
					configCache.remove(config.getConfigPath());
				});
				LoggerUtil.info("✅ Obsolete configuration cleanup completed");
			} else {
				LoggerUtil.info("✅ No obsolete configurations found");
			}
		}

		// Initialize/update configurations defined in the bean
		Arrays.stream(bean.getClass().getDeclaredFields())
				.filter(field -> field.isAnnotationPresent(ConfigProperty.class)).forEach(field -> {
					ConfigProperty annotation = field.getAnnotation(ConfigProperty.class);
					String configPath = annotation.path();

					Integer count = configRepository.getByLambda(
							q -> q.count(ConfigPo::getId).eq(ConfigPo::getConfigPath, configPath), Integer.class);

					// Check if configuration already exists
					if (count == null || count == 0) {
						// Create new configuration entity
						ConfigPo entity = new ConfigPo();
						entity.setConfigGroup(annotation.group());
						entity.setConfigSubGroup(annotation.subGroup());
						entity.setConfigKey(annotation.key());
						entity.setConfigPath(configPath);
						entity.setDescription(annotation.description());
						entity.setDefaultValue(annotation.defaultValue());
						entity.setInputType(annotation.inputType());

						// Try to get configuration value from environment
						String value = environment.getProperty(configPath);
						if (value != null) {
							entity.setConfigValue(value);
						} else {
							entity.setConfigValue(annotation.defaultValue());
						}

						// If it's SELECT type, save options JSON
						if (annotation.inputType().name().equals("SELECT") && annotation.options().length > 0) {
							// Convert options to JSON string
							ConfigOption[] options = annotation.options();
							StringBuilder optionsJson = new StringBuilder("[");
							for (int i = 0; i < options.length; i++) {
								if (i > 0)
									optionsJson.append(",");
								optionsJson.append("{").append("\"value\":\"").append(options[i].value()).append("\",")
										.append("\"label\":\"").append(options[i].label()).append("\"").append("}");
							}
							optionsJson.append("]");
							entity.setOptionsJson(optionsJson.toString());
						}

						// Save configuration
						LoggerUtil.debug("Creating new config: {0}", configPath);
						configRepository.save(entity);

						// Set field value
						setFieldValue(bean, field, entity.getConfigValue());
					}
				});
	}

	public String getConfigValue(String configPath) {
		// Check cache
		ConfigCacheEntry<String> cacheEntry = configCache.get(configPath);
		if (cacheEntry != null && !cacheEntry.isExpired()) {
			return cacheEntry.getValue();
		}

		// If cache doesn't exist or is expired, get from database
		ConfigPo configOpt = configRepository.getByLambda(q -> q.eq(ConfigPo::getConfigPath, configPath));
		if (configOpt != null) {
			String value = configOpt.getConfigValue();
			configCache.put(configPath, new ConfigCacheEntry<>(value));
			return value;
		}
		return null;
	}

	@Transactional
	public void updateConfig(String configPath, String newValue) {
		ConfigPo entity = configRepository.getByLambda(q -> q.eq(ConfigPo::getConfigPath, configPath));
		Assert.notNull(entity, ErrorCodeDef.PARAM_NOT_NULL, "configPath");

		entity.setConfigValue(newValue);
		configRepository.save(entity);

		// Update cache
		configCache.put(configPath, new ConfigCacheEntry<>(newValue));

		// Update all beans using this configuration
		Map<String, Object> configBeans = applicationContext.getBeansWithAnnotation(ConfigurationProperties.class);
		configBeans.values().forEach(bean -> updateBeanConfig(bean, configPath, newValue));
	}

	private void updateBeanConfig(Object bean, String configPath, String newValue) {
		Arrays.stream(bean.getClass().getDeclaredFields())
				.filter(field -> field.isAnnotationPresent(ConfigProperty.class))
				.filter(field -> field.getAnnotation(ConfigProperty.class).path().equals(configPath))
				.forEach(field -> setFieldValue(bean, field, newValue));
	}

	private void setFieldValue(Object bean, Field field, String value) {
		try {
			field.setAccessible(true);

			// Convert value based on field type
			Object convertedValue = convertValue(value, field.getType());
			field.set(bean, convertedValue);

		} catch (IllegalAccessException e) {
			LoggerUtil.error("Failed to set field value", e);
		}
	}

	private Object convertValue(String value, Class<?> targetType) {
		if (value == null)
			return null;

		if (targetType == String.class) {
			return value;
		} else if (targetType == Boolean.class || targetType == boolean.class) {
			if ("on".equalsIgnoreCase(value))
				return Boolean.TRUE;
			return Boolean.valueOf(value);
		} else if (targetType == Integer.class || targetType == int.class) {
			return Integer.valueOf(value);
		} else if (targetType == Long.class || targetType == long.class) {
			return Long.valueOf(value);
		} else if (targetType == Double.class || targetType == double.class) {
			return Double.valueOf(value);
		}

		throw new IllegalArgumentException("Unsupported type: " + targetType);
	}

	public List<ConfigPo> getAllConfigs() {
		return configRepository.queryAll();
	}

	public Optional<ConfigPo> getConfig(String configPath) {
		return Optional.ofNullable(configRepository.getByLambda(q -> q.eq(ConfigPo::getConfigPath, configPath)));
	}

	public void resetConfig(String configPath) {
		ConfigPo entity = configRepository.getByLambda(q -> q.eq(ConfigPo::getConfigPath, configPath));
		Assert.notNull(entity, ErrorCodeDef.PARAM_NOT_NULL, "configPath");

		entity.setConfigValue(entity.getDefaultValue());
		configRepository.save(entity);

		// Update all beans using this configuration
		Map<String, Object> configBeans = applicationContext.getBeansWithAnnotation(ConfigurationProperties.class);
		configBeans.values().forEach(bean -> updateBeanConfig(bean, configPath, entity.getDefaultValue()));
	}

	/**
	 * Get configuration items by configuration group name
	 * 
	 * @param groupName Configuration group name
	 * @return All configuration items in this group
	 */
	public List<ConfigPo> getConfigsByGroup(String groupName) {
		return configRepository.queryByLambda(q -> q.eq(ConfigPo::getConfigGroup, groupName));
	}

	/**
	 * Batch update configuration items
	 * 
	 * @param configs List of configuration items to update
	 */
	@Transactional
	public void batchUpdateConfigs(List<ConfigPo> configs) {
		for (ConfigPo config : configs) {
			ConfigPo existingConfig = configRepository.get(config.getId());
			Assert.notNull(existingConfig, ErrorCodeDef.PARAM_NOT_NULL, "id");

			// Only update configuration value
			existingConfig.setConfigValue(config.getConfigValue());
			configRepository.save(existingConfig);

			// Update all beans using this configuration
			Map<String, Object> configBeans = applicationContext.getBeansWithAnnotation(ConfigurationProperties.class);
			configBeans.values().forEach(
					bean -> updateBeanConfig(bean, existingConfig.getConfigPath(), existingConfig.getConfigValue()));
		}
	}

	/**
	 * Reset all configurations to their default values
	 */
	@Transactional
	public void resetAllConfigsToDefaults() {
		List<ConfigPo> allConfigs = configRepository.queryAll();

		for (ConfigPo config : allConfigs) {
			if (config.getDefaultValue() != null && !config.getDefaultValue().equals(config.getConfigValue())) {
				config.setConfigValue(config.getDefaultValue());
				configRepository.save(config);

				// Update all beans using this configuration
				Map<String, Object> configBeans = applicationContext
						.getBeansWithAnnotation(ConfigurationProperties.class);
				configBeans.values()
						.forEach(bean -> updateBeanConfig(bean, config.getConfigPath(), config.getDefaultValue()));
			}
		}
	}

}
