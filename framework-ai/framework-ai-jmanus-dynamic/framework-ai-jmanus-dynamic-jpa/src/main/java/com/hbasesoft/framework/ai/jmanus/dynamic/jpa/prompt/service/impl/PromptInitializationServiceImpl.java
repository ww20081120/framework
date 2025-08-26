/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.jmanus.dynamic.jpa.prompt.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hbasesoft.framework.ai.jmanus.dynamic.jpa.prompt.dao.PromptDao;
import com.hbasesoft.framework.ai.jmanus.dynamic.jpa.prompt.po.PromptPo4Jpa;
import com.hbasesoft.framework.ai.jmanus.dynamic.prompt.model.enums.PromptEnum;
import com.hbasesoft.framework.ai.jmanus.dynamic.prompt.model.vo.PromptVO;
import com.hbasesoft.framework.ai.jmanus.dynamic.prompt.service.PromptInitializationService;
import com.hbasesoft.framework.ai.jmanus.dynamic.prompt.service.PromptService;
import com.hbasesoft.framework.ai.jmanus.prompt.PromptLoader;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;
import com.hbasesoft.framework.db.core.utils.TransactionUtil;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月21日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.jmanus.dynamic.prompt.service.impl <br>
 */
@Service
public class PromptInitializationServiceImpl implements PromptInitializationService {

	private final PromptService promptService;

	private final PromptLoader promptLoader;

	public PromptInitializationServiceImpl(PromptService promptService, PromptLoader promptLoader) {
		this.promptService = promptService;
		this.promptLoader = promptLoader;
	}

	/**
	 * Create prompt templates if they don't exist
	 * 
	 * @param namespace Namespace
	 */
	@Transactional(rollbackFor = Exception.class)
	public void initializePromptsForNamespace(String namespace) {
		String defaultLanguage = "zh";
		for (PromptEnum prompt : PromptEnum.values()) {
			createPromptIfNotExists(namespace, prompt, defaultLanguage);
		}
	}

	@Transactional(rollbackFor = Exception.class)
	public void initializePromptsForNamespaceWithLanguage(String namespace, String language) {
		for (PromptEnum prompt : PromptEnum.values()) {
			updatePromptForLanguage(namespace, prompt, language);
		}
	}

	/**
	 * The error "could not execute statement [Unique index or primary key
	 * violation: "public.unique_prompt_name_INDEX_C ON public.prompt(prompt_name
	 * NULLS FIRST) VALUES ( 'PLANNING_PLAN_CREATION' )"; SQL statement:" occurs
	 * because the old constraint was a unique constraint on prompt_name, while the
	 * new constraint is a unique constraint on both namespace and prompt_name. JPA
	 * cannot handle this situation, so you need to delete the prompt table and
	 * restart the application, which will automatically handle it.
	 */
	private void createPromptIfNotExists(String namespace, PromptEnum prompt, String language) {
		// Start transaction to handle PostgreSQL large object compatibility issues in
		// auto-commit mode
		PromptVO promptEntity = promptService.getPromptByName(namespace, prompt.getPromptName());
		if (promptEntity == null) {
			promptEntity = new PromptVO();
			promptEntity.setPromptName(prompt.getPromptName());
			promptEntity.setNamespace(namespace);
			promptEntity.setPromptDescription(prompt.getPromptDescriptionForLanguage(language));
			promptEntity.setMessageType(prompt.getMessageType().name());
			promptEntity.setType(prompt.getType().name());
			promptEntity.setBuiltIn(prompt.getBuiltIn());

			String promptPath = prompt.getPromptPathForLanguage(language);
			String promptContent = promptLoader.loadPrompt(promptPath);
			promptEntity.setPromptContent(promptContent);

			try {
				promptService.create(promptEntity);
				LoggerUtil.info("Created prompt: {0} for namespace: {1} with language: {2}", prompt.getPromptName(),
						namespace, language);
			} catch (Exception e) {
				LoggerUtil.error("Failed to create prompt: {0} for namespace: {1} with language: {2}",
						prompt.getPromptName(), namespace, language, e);
			}
		} else {
			LoggerUtil.debug("Prompt already exists: {0} for namespace: {1}", prompt.getPromptName(), namespace);
		}
	}

	private void updatePromptForLanguage(String namespace, PromptEnum prompt, String language) {
		PromptVO promptEntity = promptService.getPromptByName(namespace, prompt.getPromptName());

		if (promptEntity != null) {
			promptEntity.setPromptDescription(prompt.getPromptDescriptionForLanguage(language));

			String promptPath = prompt.getPromptPathForLanguage(language);
			String promptContent = promptLoader.loadPrompt(promptPath);
			promptEntity.setPromptContent(promptContent);

			try {
				promptService.update(promptEntity);
				LoggerUtil.info("Updated prompt: {0} for namespace: {1} with language: {2}", prompt.getPromptName(),
						namespace, language);
			} catch (Exception e) {
				LoggerUtil.error("Failed to update prompt: {0} for namespace: {1} with language: {2}",
						prompt.getPromptName(), namespace, language, e);
			}
		} else {
			createPromptIfNotExists(namespace, prompt, language);
		}
	}

}
