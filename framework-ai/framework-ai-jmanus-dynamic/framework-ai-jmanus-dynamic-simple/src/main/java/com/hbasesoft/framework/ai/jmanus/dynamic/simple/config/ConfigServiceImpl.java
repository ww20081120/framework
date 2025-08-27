/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.jmanus.dynamic.simple.config;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hbasesoft.framework.ai.jmanus.config.ConfigProperty;
import com.hbasesoft.framework.ai.jmanus.config.IConfigService;
import com.hbasesoft.framework.ai.jmanus.config.model.ConfigCacheEntry;
import com.hbasesoft.framework.ai.jmanus.config.model.vo.ConfigVo;
import com.hbasesoft.framework.ai.jmanus.tool.filesystem.UnifiedDirectoryManager;
import com.hbasesoft.framework.common.utils.ContextHolder;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月22日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.ai.jmanus.dynamic.simple.config <br>
 */
@Service
public class ConfigServiceImpl implements IConfigService {

	@Autowired
	private UnifiedDirectoryManager unifiedDirectoryManager;

	/** ObjectMapper用于JSON序列化和反序列化 */
	private final ObjectMapper objectMapper = new ObjectMapper()
			.registerModule(new JavaTimeModule())
			.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

	/** 配置缓存，用于提高配置读取性能 */
	private final Map<String, ConfigCacheEntry<String>> configCache = new ConcurrentHashMap<>();

	/** 初始化标志，确保配置只初始化一次 */
	private boolean initialized = false;

	/**
	 * 根据配置路径获取配置值 首先从缓存中获取，如果缓存不存在或已过期，则从文件中读取
	 * 
	 * @param configPath 配置路径
	 * @return 配置值，如果未找到则返回null
	 */
	@Override
	public String getConfigValue(String configPath) {
		// Check cache
		ConfigCacheEntry<String> cacheEntry = configCache.get(configPath);
		if (cacheEntry != null && !cacheEntry.isExpired()) {
			return cacheEntry.getValue();
		}

		// If cache doesn't exist or is expired, get from file
		try {
			Map<String, Object> settings = loadSettingsFromFile();
			String value = (String) settings.get(configPath);
			if (value != null) {
				configCache.put(configPath, new ConfigCacheEntry<>(value));
			}
			return value;
		} catch (Exception e) {
			LoggerUtil.error("Failed to get config value for path: " + configPath, e);
			return null;
		}
	}

	/**
	 * 将指定配置重置为默认值
	 * 
	 * @param configPath 配置路径
	 */
	@Override
	public void resetConfig(String configPath) {
		try {
			Map<String, Object> settings = loadSettingsFromFile();

			// Find the default value from configuration beans
			Map<String, Object> configBeans = ContextHolder.getContext()
					.getBeansWithAnnotation(ConfigurationProperties.class);

			final String defaultValue = findDefaultValue(configBeans, configPath);

			if (defaultValue != null) {
				settings.put(configPath, defaultValue);
				saveSettingsToFile(settings);

				// Update cache
				configCache.put(configPath, new ConfigCacheEntry<>(defaultValue));

				// Update all beans using this configuration
				configBeans.values().forEach(bean -> updateBeanConfig(bean, configPath, defaultValue));
			}
		} catch (Exception e) {
			LoggerUtil.error("Failed to reset config for path: " + configPath, e);
		}
	}

	/**
	 * 根据配置组名获取配置项列表
	 * 
	 * @param groupName 配置组名
	 * @return 配置项列表
	 */
	@Override
	public List<ConfigVo> getConfigsByGroup(String groupName) {
		try {
			Map<String, Object> settings = loadSettingsFromFile();
			return settings.entrySet().stream().filter(entry -> entry.getKey().startsWith(groupName + "."))
					.map(entry -> {
						ConfigVo configVo = new ConfigVo();
						configVo.setConfigPath(entry.getKey());
						configVo.setConfigValue((String) entry.getValue());
						// Set other fields as needed
						return configVo;
					}).collect(Collectors.toList());
		} catch (Exception e) {
			LoggerUtil.error("Failed to get configs by group: " + groupName, e);
			return null;
		}
	}

	/**
	 * 批量更新配置项
	 * 
	 * @param configs 配置项列表
	 */
	@Override
	public void batchUpdateConfigs(List<ConfigVo> configs) {
		try {
			Map<String, Object> settings = loadSettingsFromFile();

			for (ConfigVo config : configs) {
				settings.put(config.getConfigPath(), config.getConfigValue());
			}

			saveSettingsToFile(settings);

			// Update cache and beans
			Map<String, Object> configBeans = ContextHolder.getContext()
					.getBeansWithAnnotation(ConfigurationProperties.class);

			for (ConfigVo config : configs) {
				configCache.put(config.getConfigPath(), new ConfigCacheEntry<>(config.getConfigValue()));
				configBeans.values()
						.forEach(bean -> updateBeanConfig(bean, config.getConfigPath(), config.getConfigValue()));
			}
		} catch (Exception e) {
			LoggerUtil.error("Failed to batch update configs", e);
		}
	}

	/**
	 * 将所有配置重置为默认值
	 */
	@Override
	public void resetAllConfigsToDefaults() {
		try {
			Map<String, Object> settings = loadSettingsFromFile();

			// Get all configuration beans
			Map<String, Object> configBeans = ContextHolder.getContext()
					.getBeansWithAnnotation(ConfigurationProperties.class);

			// Reset all configurations to their default values
			for (Object bean : configBeans.values()) {
				Arrays.stream(bean.getClass().getDeclaredFields())
						.filter(field -> field.isAnnotationPresent(ConfigProperty.class)).forEach(field -> {
							ConfigProperty annotation = field.getAnnotation(ConfigProperty.class);
							String configPath = annotation.path();
							String defaultValue = annotation.defaultValue();

							settings.put(configPath, defaultValue);

							// Update cache
							configCache.put(configPath, new ConfigCacheEntry<>(defaultValue));

							// Update bean
							updateBeanConfig(bean, configPath, defaultValue);
						});
			}

			saveSettingsToFile(settings);
		} catch (Exception e) {
			LoggerUtil.error("Failed to reset all configs to defaults", e);
		}
	}

	/**
	 * 初始化配置服务 确保只初始化一次
	 */
	@Override
	public void init() {
		if (!initialized) {
			initialized = true;
			initConfig();
		}
	}

	/**
	 * 初始化配置 获取所有带有@ConfigurationProperties注解的Bean并初始化它们
	 */
	private void initConfig() {
		// Only get beans with @ConfigurationProperties annotation
		Map<String, Object> configBeans = ContextHolder.getContext()
				.getBeansWithAnnotation(ConfigurationProperties.class);
		LoggerUtil.info("Found {0} configuration beans", configBeans.size());

		// Initialize each configuration bean
		configBeans.values().forEach(this::initializeConfig);
	}

	/**
	 * 初始化单个配置Bean 收集Bean中所有带有@ConfigProperty注解的字段，并初始化它们的配置
	 * 
	 * @param bean 配置Bean
	 */
	private void initializeConfig(Object bean) {
		// Initialize/update configurations defined in the bean
		Arrays.stream(bean.getClass().getDeclaredFields())
				.filter(field -> field.isAnnotationPresent(ConfigProperty.class)).forEach(field -> {
					ConfigProperty annotation = field.getAnnotation(ConfigProperty.class);
					String configPath = annotation.path();

					try {
						Map<String, Object> settings = loadSettingsFromFile();

						// Check if configuration already exists
						if (!settings.containsKey(configPath)) {
							// If not exists, set default value
							settings.put(configPath, annotation.defaultValue());
							saveSettingsToFile(settings);
						}

						// Set field value
						setFieldValue(bean, field, (String) settings.get(configPath));
					} catch (Exception e) {
						LoggerUtil.error("Failed to initialize config: " + configPath, e);
					}
				});
	}

	/**
	 * 从文件加载配置设置
	 * 
	 * @return 配置设置映射
	 * @throws IOException IO异常
	 */
	private Map<String, Object> loadSettingsFromFile() throws IOException {
		Path settingsFile = unifiedDirectoryManager.getWorkingDirectory().resolve("settings.json");
		if (!Files.exists(settingsFile)) {
			return new ConcurrentHashMap<>();
		}

		String content = Files.readString(settingsFile);
		if (content.trim().isEmpty()) {
			return new ConcurrentHashMap<>();
		}
		return objectMapper.readValue(content, Map.class);
	}

	/**
	 * 将配置设置保存到文件
	 * 
	 * @param settings 配置设置映射
	 * @throws IOException IO异常
	 */
	private void saveSettingsToFile(Map<String, Object> settings) throws IOException {
		Path settingsFile = unifiedDirectoryManager.getWorkingDirectory().resolve("settings.json");
		unifiedDirectoryManager.ensureDirectoryExists(settingsFile.getParent());
		String content = objectMapper.writeValueAsString(settings);
		Files.writeString(settingsFile, content);
	}

	/**
	 * 根据配置路径查找字段
	 * 
	 * @param clazz      类
	 * @param configPath 配置路径
	 * @return 字段，如果未找到则返回null
	 */
	private Field findFieldWithConfigPath(Class<?> clazz, String configPath) {
		return Arrays.stream(clazz.getDeclaredFields()).filter(field -> field.isAnnotationPresent(ConfigProperty.class))
				.filter(field -> field.getAnnotation(ConfigProperty.class).path().equals(configPath)).findFirst()
				.orElse(null);
	}

	/**
	 * 更新Bean配置 根据配置路径更新Bean中的字段值
	 * 
	 * @param bean       Bean
	 * @param configPath 配置路径
	 * @param newValue   新值
	 */
	private void updateBeanConfig(Object bean, String configPath, String newValue) {
		Arrays.stream(bean.getClass().getDeclaredFields())
				.filter(field -> field.isAnnotationPresent(ConfigProperty.class))
				.filter(field -> field.getAnnotation(ConfigProperty.class).path().equals(configPath))
				.forEach(field -> setFieldValue(bean, field, newValue));
	}

	/**
	 * 设置字段值 根据字段类型转换值并设置到Bean中
	 * 
	 * @param bean  Bean
	 * @param field 字段
	 * @param value 值
	 */
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

	/**
	 * 查找默认值 在配置Bean中查找指定配置路径的默认值
	 * 
	 * @param configBeans 配置Bean映射
	 * @param configPath  配置路径
	 * @return 默认值，如果未找到则返回null
	 */
	private String findDefaultValue(Map<String, Object> configBeans, String configPath) {
		for (Object bean : configBeans.values()) {
			Field field = findFieldWithConfigPath(bean.getClass(), configPath);
			if (field != null) {
				ConfigProperty annotation = field.getAnnotation(ConfigProperty.class);
				return annotation.defaultValue();
			}
		}
		return null;
	}

	/**
	 * 转换值 根据目标类型转换字符串值
	 * 
	 * @param value      值
	 * @param targetType 目标类型
	 * @return 转换后的值
	 */
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
}
