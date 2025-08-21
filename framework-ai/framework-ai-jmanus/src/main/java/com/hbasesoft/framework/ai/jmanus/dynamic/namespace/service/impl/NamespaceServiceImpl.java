/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.jmanus.dynamic.namespace.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hbasesoft.framework.ai.jmanus.dynamic.namespace.dao.NamespaceDao;
import com.hbasesoft.framework.ai.jmanus.dynamic.namespace.model.po.NamespacePo;
import com.hbasesoft.framework.ai.jmanus.dynamic.namespace.model.vo.NamespaceConfig;
import com.hbasesoft.framework.ai.jmanus.dynamic.namespace.service.NamespaceService;
import com.hbasesoft.framework.ai.jmanus.dynamic.prompt.service.PromptInitializationService;
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
 * @see com.hbasesoft.framework.ai.jmanus.dynamic.namespace.service.impl <br>
 */

@Service
public class NamespaceServiceImpl implements NamespaceService {
	private final NamespaceDao repository;

	@Autowired
	public NamespaceServiceImpl(NamespaceDao repository) {
		this.repository = repository;
	}

	@Autowired
	private PromptInitializationService promptInitializationService;

	@Override
	public List<NamespaceConfig> getAllNamespaces() {
		return repository.queryAll().stream().map(NamespacePo::mapToNamespaceConfig).collect(Collectors.toList());
	}

	@Override
	public NamespaceConfig getNamespaceById(String id) {
		NamespacePo entity = repository.get(Long.parseLong(id));
		Assert.notNull(entity, ErrorCodeDef.PARAM_NOT_NULL, "id");
		return entity.mapToNamespaceConfig();
	}

	@Override
	public NamespaceConfig createNamespace(NamespaceConfig config) {
		try {
			// Check if a Namespace with the same name already exists
			NamespacePo existingNamespace = repository.getByLambda(q -> q.eq(NamespacePo::getName, config.getName()));
			if (existingNamespace != null) {
				LoggerUtil.info("Found Namespace with same name: {0}, updating Namespace", config.getName());
				config.setId(existingNamespace.getId());
				return updateNamespace(config);
			}

			// Check if a Namespace with the same code already exists
			NamespacePo existingNamespaceByCode = repository
					.getByLambda(q -> q.eq(NamespacePo::getCode, config.getCode()));
			if (existingNamespaceByCode != null) {
				LoggerUtil.info("Found Namespace with same code: {0}, updating Namespace", config.getCode());
				config.setId(existingNamespaceByCode.getId());
				return updateNamespace(config);
			}

			NamespacePo entity = new NamespacePo();
			updateEntityFromConfig(entity, config);
			repository.save(entity);

			// initialize prompts for the namespace
			try {
				promptInitializationService.initializePromptsForNamespace(config.getCode());
				LoggerUtil.info("Successfully initialized prompts for namespace: {0}", config.getCode());
			} catch (Exception e) {
				LoggerUtil.error(e, "Failed to initialize prompts for namespace: {0}", config.getCode());
				// Don't throw exception, because even if prompt initialization fails,
				// namespace creation itself is successful
			}

			LoggerUtil.info("Successfully created new Namespace: {0}", config.getName());
			return entity.mapToNamespaceConfig();
		} catch (Exception e) {
			LoggerUtil.warn("Exception occurred during Namespace creation: {0}, error message: {1}", config.getName(),
					e.getMessage());
			// If it's a uniqueness constraint violation exception, try returning the
			// existing Namespace
			if (e.getMessage() != null && e.getMessage().contains("Unique")) {
				NamespacePo existingNamespace = repository.getByLambda(q -> q.eq(NamespacePo::getName, config.getName()));
				if (existingNamespace != null) {
					LoggerUtil.info("Return existing Namespace: {0}", config.getName());
					return existingNamespace.mapToNamespaceConfig();
				}
			}
			throw e;
		}
	}

	@Override
	public NamespaceConfig updateNamespace(NamespaceConfig config) {
		NamespacePo entity = repository.get(config.getId());
		Assert.notNull(entity, ErrorCodeDef.PARAM_NOT_NULL, "id");
		updateEntityFromConfig(entity, config);
		repository.save(entity);
		return entity.mapToNamespaceConfig();
	}

	@Override
	public void deleteNamespace(String id) {
		repository.deleteById(Long.parseLong(id));
	}

	private void updateEntityFromConfig(NamespacePo entity, NamespaceConfig config) {
		entity.setName(config.getName());
		entity.setCode(config.getCode());
		entity.setDescription(config.getDescription());
		entity.setHost(config.getHost());
	}

}
