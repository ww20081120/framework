/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.jmanus.dynamic.simple.model;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import com.hbasesoft.framework.ai.jmanus.dynamic.model.model.vo.ModelConfig;
import com.hbasesoft.framework.ai.jmanus.dynamic.model.service.DynamicModelService;
import com.hbasesoft.framework.common.utils.ContextHolder;

/**
 * 动态模型服务实现类<br>
 * 通过Spring容器获取所有ModelConfig实例，并提供模型配置的查询功能
 * 支持从Spring容器中的Bean和application.yml配置文件中获取模型配置 支持自动生成稳定ID，确保重启后ID不变
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月22日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.ai.jmanus.dynamic.simple.model <br>
 */
@Service
public class DynamicModelServiceImpl implements DynamicModelService {

	/** 从配置文件读取的模型配置 */
	@Autowired(required = false)
	private ModelProperties modelProperties;

	/** 缓存所有模型配置，提高查询性能 */
	private final Map<Long, ModelConfig> modelConfigCache = new ConcurrentHashMap<>();

	/** Spring Bean配置的ID前缀 */
	private static final long BEAN_ID_PREFIX = 1000000000L;

	/** 配置文件配置的ID前缀 */
	private static final long CONFIG_ID_PREFIX = 2000000000L;

	/**
	 * 获取默认模型配置<br>
	 * 优先从Spring容器中查找标记为默认的模型配置，如果没有则从配置文件中查找
	 * 
	 * @return 默认模型配置，如果未找到则返回null
	 */
	@Override
	public ModelConfig getDefault() {
		// 先从Spring容器中的Bean查找
		Map<String, ModelConfig> modelConfigMap = ContextHolder.getContext().getBeansOfType(ModelConfig.class);
		for (Map.Entry<String, ModelConfig> entry : modelConfigMap.entrySet()) {
			ModelConfig config = entry.getValue();
			ensureModelConfigId(config, "bean", entry.getKey());
			if (Boolean.TRUE.equals(config.getIsDefault())) {
				return config;
			}
		}

		// 如果Spring容器中没有默认配置，则从配置文件中查找
		if (modelProperties != null && modelProperties.getConfigs() != null) {
			for (int i = 0; i < modelProperties.getConfigs().size(); i++) {
				ModelConfig config = modelProperties.getConfigs().get(i);
				ensureModelConfigId(config, "config", String.valueOf(i));
				if (Boolean.TRUE.equals(config.getIsDefault())) {
					return config;
				}
			}
		}

		return null;
	}

	/**
	 * 查询所有模型配置<br>
	 * 从Spring容器中的Bean和配置文件中获取所有ModelConfig实例 自动为没有ID的配置生成稳定ID
	 * 
	 * @return 所有模型配置列表
	 */
	@Override
	public List<ModelConfig> queryAll() {
		List<ModelConfig> allConfigs = new ArrayList<>();

		// 从Spring容器中的Bean获取
		Map<String, ModelConfig> modelConfigMap = ContextHolder.getContext().getBeansOfType(ModelConfig.class);
		for (Map.Entry<String, ModelConfig> entry : modelConfigMap.entrySet()) {
			ModelConfig config = entry.getValue();
			ensureModelConfigId(config, "bean", entry.getKey());
			allConfigs.add(config);
		}

		// 从配置文件获取
		if (modelProperties != null && modelProperties.getConfigs() != null) {
			for (int i = 0; i < modelProperties.getConfigs().size(); i++) {
				ModelConfig config = modelProperties.getConfigs().get(i);
				ensureModelConfigId(config, "config", String.valueOf(i));
				allConfigs.add(config);
			}
		}

		return allConfigs;
	}

	/**
	 * 根据ID获取模型配置<br>
	 * 从Spring容器中的Bean和配置文件中查找指定ID的模型配置
	 * 
	 * @param modelId 模型配置ID
	 * @return 指定ID的模型配置，如果未找到则返回null
	 */
	@Override
	public ModelConfig get(Long modelId) {
		if (modelId == null) {
			return null;
		}

		// 先从缓存中查找
		ModelConfig cachedConfig = modelConfigCache.get(modelId);
		if (cachedConfig != null) {
			return cachedConfig;
		}

		// 从Spring容器中的Bean查找
		Map<String, ModelConfig> modelConfigMap = ContextHolder.getContext().getBeansOfType(ModelConfig.class);
		for (Map.Entry<String, ModelConfig> entry : modelConfigMap.entrySet()) {
			ModelConfig config = entry.getValue();
			ensureModelConfigId(config, "bean", entry.getKey());
			if (modelId.equals(config.getId())) {
				modelConfigCache.put(modelId, config);
				return config;
			}
		}

		// 从配置文件查找
		if (modelProperties != null && modelProperties.getConfigs() != null) {
			for (int i = 0; i < modelProperties.getConfigs().size(); i++) {
				ModelConfig config = modelProperties.getConfigs().get(i);
				ensureModelConfigId(config, "config", String.valueOf(i));
				if (modelId.equals(config.getId())) {
					modelConfigCache.put(modelId, config);
					return config;
				}
			}
		}

		return null;
	}

	/**
	 * 确保ModelConfig有稳定ID 使用内容哈希生成稳定ID，确保重启后ID不变
	 * 
	 * @param config     ModelConfig实例
	 * @param source     ID来源（"bean"表示来自Spring Bean，"config"表示来自配置文件）
	 * @param identifier 标识符（Bean名称或配置索引）
	 */
	private void ensureModelConfigId(ModelConfig config, String source, String identifier) {
		if (config.getId() == null) {
			if ("bean".equals(source)) {
				// 为Bean生成稳定ID，使用Bean名称哈希
				long hash = hashString(identifier);
				config.setId(BEAN_ID_PREFIX + Math.abs(hash) % 100000000L);
			} else {
				// 为配置文件生成稳定ID，使用配置内容哈希
				long hash = hashModelConfig(config);
				config.setId(CONFIG_ID_PREFIX + Math.abs(hash) % 100000000L);
			}
		}
	}

	/**
	 * 计算字符串的哈希值
	 * 
	 * @param str 输入字符串
	 * @return 哈希值
	 */
	private long hashString(String str) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] digest = md.digest(str.getBytes(StandardCharsets.UTF_8));
			long hash = 0;
			for (int i = 0; i < 8; i++) {
				hash = (hash << 8) | (digest[i] & 0xFF);
			}
			return hash;
		} catch (NoSuchAlgorithmException e) {
			// 如果MD5不可用，使用简单哈希
			return str.hashCode();
		}
	}

	/**
	 * 计算ModelConfig的哈希值
	 * 
	 * @param config ModelConfig实例
	 * @return 哈希值
	 */
	private long hashModelConfig(ModelConfig config) {
		StringBuilder sb = new StringBuilder();
		sb.append(config.getBaseUrl() != null ? config.getBaseUrl() : "");
		sb.append(config.getApiKey() != null ? config.getApiKey() : "");
		sb.append(config.getModelName() != null ? config.getModelName() : "");
		sb.append(config.getModelDescription() != null ? config.getModelDescription() : "");
		sb.append(config.getType() != null ? config.getType() : "");
		sb.append(config.getIsDefault() != null ? config.getIsDefault() : "");
		sb.append(config.getTemperature() != null ? config.getTemperature() : "");
		sb.append(config.getTopP() != null ? config.getTopP() : "");
		sb.append(config.getCompletionsPath() != null ? config.getCompletionsPath() : "");
		return hashString(sb.toString());
	}
}
