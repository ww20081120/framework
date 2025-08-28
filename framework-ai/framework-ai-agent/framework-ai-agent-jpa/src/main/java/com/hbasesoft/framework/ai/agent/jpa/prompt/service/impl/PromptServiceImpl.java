/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.agent.jpa.prompt.service.impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.prompt.AssistantPromptTemplate;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hbasesoft.framework.ai.agent.dynamic.prompt.model.enums.PromptEnum;
import com.hbasesoft.framework.ai.agent.dynamic.prompt.model.vo.PromptVO;
import com.hbasesoft.framework.ai.agent.dynamic.prompt.service.PromptService;
import com.hbasesoft.framework.ai.agent.jpa.prompt.dao.PromptDao;
import com.hbasesoft.framework.ai.agent.jpa.prompt.po.PromptPo4Jpa;
import com.hbasesoft.framework.ai.agent.jpa.prompt.service.PromptManagerService;
import com.hbasesoft.framework.ai.agent.prompt.PromptLoader;
import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.utils.Assert;
import com.hbasesoft.framework.common.utils.ContextHolder;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月21日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.jmanus.agent.prompt.service.impl <br>
 */

@Service
public class PromptServiceImpl implements PromptService, PromptManagerService {

	private final PromptDao promptRepository;

	private final PromptLoader promptLoader;

	@Value("${namespace.value: default}")
	private String namespace;

	public PromptServiceImpl(PromptDao promptRepository, PromptLoader promptLoader) {
		this.promptRepository = promptRepository;
		this.promptLoader = promptLoader;
	}

	@Transactional(readOnly = true)
	@Override
	public List<PromptVO> getAll() {
		return promptRepository.queryAll().stream().map(this::mapToPromptVO).collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	@Override
	public List<PromptVO> getAllByNamespace(String namespace) {
		List<PromptPo4Jpa> entities;
		if ("default".equalsIgnoreCase(namespace)) {
			entities = promptRepository.queryByLambda(q -> q.or(q1 -> q1.eq(PromptPo4Jpa::getNamespace, namespace)
					.isNull(PromptPo4Jpa::getNamespace).eq(PromptPo4Jpa::getNamespace, "")));
		} else {
			entities = promptRepository.queryByLambda(q -> q.eq(PromptPo4Jpa::getNamespace, namespace));
		}
		return entities.stream().map(this::mapToPromptVO).collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	@Override
	public PromptVO getById(Long id) {
		PromptPo4Jpa entity = promptRepository.get(id);
		Assert.notNull(entity, ErrorCodeDef.PARAM_NOT_NULL, "Prompt");
		return mapToPromptVO(entity);
	}

	@Transactional(readOnly = true)
	@Override
	public PromptVO getPromptByName(String ns, String promptName) {
		String na = StringUtils.isEmpty(ns) ? namespace : ns;
		PromptPo4Jpa entity = promptRepository
				.getByLambda(q -> q.eq(PromptPo4Jpa::getNamespace, na).eq(PromptPo4Jpa::getPromptName, promptName));
		if (entity == null) {
			throw new IllegalArgumentException("Prompt not found: " + promptName);
		}
		return mapToPromptVO(entity);
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public PromptVO create(PromptVO promptVO) {
		if (promptVO.invalid()) {
			throw new IllegalArgumentException("PromptVO filed is invalid");
		}

		if (Boolean.TRUE.equals(promptVO.getBuiltIn())) {
			throw new IllegalArgumentException("Cannot create built-in prompt");
		}
		PromptPo4Jpa prompt = promptRepository
				.getByLambda(q -> q.eq(PromptPo4Jpa::getNamespace, promptVO.getNamespace())
						.eq(PromptPo4Jpa::getPromptName, promptVO.getPromptName()));
		if (prompt != null) {
			LoggerUtil.error(
					"Found Prompt is existed: promptName :{0} , namespace:{1}, type :{2}, String messageType:{3}",
					promptVO.getPromptName(), promptVO.getNamespace(), promptVO.getType(), promptVO.getMessageType());
			throw new RuntimeException("Found Prompt is existed");
		}

		PromptPo4Jpa promptEntity = mapToPromptPo4Jpa(promptVO);
		promptRepository.save(promptEntity);
		LoggerUtil.info(
				"Successfully created new Prompt promptName :{0} , namespace:{1}, type :{2}, String messageType:{3}",
				promptEntity.getPromptName(), promptEntity.getNamespace(), promptEntity.getType(),
				promptEntity.getMessageType());
		return mapToPromptVO(promptEntity);
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public PromptVO update(PromptVO promptVO) {
		if (promptVO.invalid()) {
			throw new IllegalArgumentException("PromptVO filed is invalid");
		}

		PromptPo4Jpa oldPrompt = promptRepository.get(promptVO.getId());
		Assert.notNull(oldPrompt, ErrorCodeDef.PARAM_NOT_NULL, "Prompt");

		if (!oldPrompt.getPromptName().equals(promptVO.getPromptName())) {
			throw new IllegalArgumentException("Prompt name is not allowed to update");
		}

		PromptPo4Jpa promptEntity = mapToPromptPo4Jpa(promptVO);
		promptRepository.save(promptEntity);

		LoggerUtil.info(
				"Successfully update new Prompt promptName :{0} , namespace:{1}, type :{2}, String messageType:{3}",
				promptEntity.getPromptName(), promptEntity.getNamespace(), promptEntity.getType(),
				promptEntity.getMessageType());
		return mapToPromptVO(promptEntity);
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void delete(Long id) {
		PromptPo4Jpa entity = promptRepository.get(id);
		Assert.notNull(entity, ErrorCodeDef.PARAM_NOT_NULL, "Prompt");
		if (Boolean.TRUE.equals(entity.getBuiltIn())) {
			throw new IllegalArgumentException("Cannot delete built-in prompt");
		}

		promptRepository.deleteById(id);
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public Message createSystemMessage(String promptName, Map<String, Object> variables) {
		PromptPo4Jpa promptEntity = promptRepository.getByLambda(
				q -> q.eq(PromptPo4Jpa::getNamespace, namespace).eq(PromptPo4Jpa::getPromptName, promptName));
		if (promptEntity == null) {
			throw new IllegalArgumentException("Prompt not found: " + promptName);
		}

		SystemPromptTemplate template = new SystemPromptTemplate(promptEntity.getPromptContent());
		return template.createMessage(variables != null ? variables : Map.of());
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public Message createUserMessage(String promptName, Map<String, Object> variables) {
		PromptPo4Jpa promptEntity = promptRepository.getByLambda(
				q -> q.eq(PromptPo4Jpa::getNamespace, namespace).eq(PromptPo4Jpa::getPromptName, promptName));
		if (promptEntity == null) {
			throw new IllegalArgumentException("Prompt not found: " + promptName);
		}

		PromptTemplate template = new PromptTemplate(promptEntity.getPromptContent());
		return template.createMessage(variables != null ? variables : Map.of());
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public Message createMessage(String promptName, Map<String, Object> variables) {
		PromptPo4Jpa promptEntity = promptRepository.getByLambda(
				q -> q.eq(PromptPo4Jpa::getNamespace, namespace).eq(PromptPo4Jpa::getPromptName, promptName));
		if (promptEntity == null) {
			throw new IllegalArgumentException("Prompt not found: " + promptName);
		}

		if (MessageType.USER.name().equals(promptEntity.getMessageType())) {
			PromptTemplate template = new PromptTemplate(promptEntity.getPromptContent());
			return template.createMessage(variables != null ? variables : Map.of());
		} else if (MessageType.SYSTEM.name().equals(promptEntity.getMessageType())) {
			SystemPromptTemplate template = new SystemPromptTemplate(promptEntity.getPromptContent());
			return template.createMessage(variables != null ? variables : Map.of());
		} else if (MessageType.ASSISTANT.name().equals(promptEntity.getMessageType())) {
			AssistantPromptTemplate template = new AssistantPromptTemplate(promptEntity.getPromptContent());
			return template.createMessage(variables != null ? variables : Map.of());
		} else {
			throw new IllegalArgumentException("Prompt message type not support : " + promptEntity.getMessageType());
		}
	}

	/**
	 * Render prompt template
	 * 
	 * @param promptName Prompt Name
	 * @param variables  Variable mapping
	 * @return Rendered prompt
	 */
	@Transactional(readOnly = true)
	@Override
	public String renderPrompt(String promptName, Map<String, Object> variables) {
		PromptPo4Jpa promptEntity = promptRepository.getByLambda(
				q -> q.eq(PromptPo4Jpa::getNamespace, namespace).eq(PromptPo4Jpa::getPromptName, promptName));
		if (promptEntity == null) {
			throw new IllegalArgumentException("Prompt not found: " + promptName);
		}

		PromptTemplate template = new PromptTemplate(promptEntity.getPromptContent());
		return template.render(variables != null ? variables : Map.of());
	}

	@Override
	public String[] getSupportedLanguages() {
		return PromptEnum.getSupportedLanguages();
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void importSpecificPromptFromLanguage(String promptName, String language) {
		LoggerUtil.info("Starting to reset prompt: {0} to language default: {1}", promptName, language);

		PromptEnum promptEnum = null;
		for (PromptEnum pe : PromptEnum.values()) {
			if (pe.getPromptName().equals(promptName)) {
				promptEnum = pe;
				break;
			}
		}

		if (promptEnum == null) {
			throw new IllegalArgumentException("Unknown prompt: " + promptName);
		}

		PromptPo4Jpa entity = promptRepository.getByLambda(
				q -> q.eq(PromptPo4Jpa::getNamespace, namespace).eq(PromptPo4Jpa::getPromptName, promptName));
		if (entity != null) {
			String promptPath = promptEnum.getPromptPathForLanguage(language);
			String newContent = promptLoader.loadPrompt(promptPath);

			if (!newContent.isEmpty()) {
				entity.setPromptContent(newContent);
				entity.setPromptDescription(promptEnum.getPromptDescriptionForLanguage(language));
				promptRepository.save(entity);

				LoggerUtil.info("Successfully reset prompt: {0} to language default: {1}", promptName, language);
			} else {
				throw new RuntimeException(
						"Empty content loaded for prompt: " + promptName + " from language: " + language);
			}
		} else {
			throw new IllegalArgumentException("Prompt not found in database: " + promptName);
		}
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void importAllPromptsFromLanguage(String language) {
		LoggerUtil.info("Starting to reset all prompts to language default: {0}", language);

		for (PromptEnum promptEnum : PromptEnum.values()) {
			try {
				importSpecificPromptFromLanguage(promptEnum.getPromptName(), language);
			} catch (Exception e) {
				LoggerUtil.error("Failed to reset prompt: {0} to language: {1}", promptEnum.getPromptName(), language,
						e);
			}
		}

		LoggerUtil.info("Completed resetting all prompts to language default: {0}", language);
	}

	private PromptVO mapToPromptVO(PromptPo4Jpa entity) {
		PromptVO promptVO = new PromptVO();
		BeanUtils.copyProperties(entity, promptVO);
		return promptVO;
	}

	private PromptPo4Jpa mapToPromptPo4Jpa(PromptVO promptVO) {
		PromptPo4Jpa entity = ContextHolder.getContext().getBean(PromptPo4Jpa.class);
		BeanUtils.copyProperties(promptVO, entity);
		return entity;
	}

	@Transactional(rollbackFor = Exception.class)
	public void reinitializePrompts() {
		LoggerUtil.info("Starting prompt namespace correction");

		List<PromptPo4Jpa> allPrompts = promptRepository.queryAll();
		LoggerUtil.info("Found {0} prompts in total", allPrompts.size());

		int updatedCount = 0;
		int validCount = 0;

		for (PromptPo4Jpa prompt : allPrompts) {
			if (prompt.getNamespace() == null || prompt.getNamespace().trim().isEmpty()) {
				LoggerUtil.info("Updating prompt '{0}' (ID: {1}) namespace from '{2}' to 'default'",
						prompt.getPromptName(), prompt.getId(), prompt.getNamespace());

				prompt.setNamespace("default");
				promptRepository.save(prompt);
				updatedCount++;
			} else {
				validCount++;
				LoggerUtil.debug("Prompt '{0}' (ID: {1}) already has valid namespace: {2}", prompt.getPromptName(),
						prompt.getId(), prompt.getNamespace());
			}
		}

		LoggerUtil.info(
				"Prompt namespace correction completed. Summary: {0} prompts updated, {1} prompts already valid.",
				updatedCount, validCount);
	}

	/**
	 * Description: <br>
	 * 
	 * @author 王伟<br>
	 * @taskId <br>
	 * @param string
	 * @return <br>
	 */
	@Override
	public PromptVO getPromptByName(String string) {
		return getPromptByName(namespace, string);
	}

}
