/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.jmanus.dynamic.simple.prompt;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.hbasesoft.framework.ai.jmanus.dynamic.prompt.model.vo.PromptVO;
import com.hbasesoft.framework.ai.jmanus.dynamic.prompt.service.PromptService;
import com.hbasesoft.framework.ai.jmanus.tool.filesystem.UnifiedDirectoryManager;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月22日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.ai.jmanus.dynamic.simple.prompt <br>
 */
@Service
public class PromptServiceImpl implements PromptService {

	private static final String PROMPT_CONFIG_FILE = "prompt.json";

	private final UnifiedDirectoryManager unifiedDirectoryManager;

	@Value("${namespace.value:default}")
	private String namespace;

	private final ObjectMapper objectMapper = new ObjectMapper()
			.registerModule(new JavaTimeModule())
			.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

	/** Prompt缓存，用于提高性能 */
	private final Map<String, PromptVO> promptCache = new ConcurrentHashMap<>();

	public PromptServiceImpl(UnifiedDirectoryManager unifiedDirectoryManager) {
		this.unifiedDirectoryManager = unifiedDirectoryManager;
	}

	/**
	 * Description: <br>
	 * 
	 * @author 王伟<br>
	 * @taskId <br>
	 * @param namespace
	 * @param promptName
	 * @return <br>
	 */
	@Override
	public PromptVO getPromptByName(String namespace, String promptName) {
		if (StringUtils.isEmpty(namespace)) {
			namespace = this.namespace;
		}

		// 使用namespace和promptName作为缓存key
		String cacheKey = namespace + ":" + promptName;
		PromptVO cachedPrompt = promptCache.get(cacheKey);
		if (cachedPrompt != null) {
			return cachedPrompt;
		}

		try {
			List<PromptVO> prompts = loadPromptsFromFile(namespace);
			for (PromptVO prompt : prompts) {
				if (promptName.equals(prompt.getPromptName()) && namespace.equals(prompt.getNamespace())) {
					promptCache.put(cacheKey, prompt);
					return prompt;
				}
			}
		} catch (Exception e) {
			LoggerUtil.error("Failed to get prompt by name: " + promptName, e);
		}
		return null;
	}

	/**
	 * Description: <br>
	 * 
	 * @author 王伟<br>
	 * @taskId <br>
	 * @param promptName
	 * @param variables
	 * @return <br>
	 */
	@Override
	public Message createSystemMessage(String promptName, Map<String, Object> variables) {
		String renderedPrompt = renderPrompt(promptName, variables);
		if (renderedPrompt != null) {
			return new SystemMessage(renderedPrompt);
		}
		return null;
	}

	/**
	 * Description: <br>
	 * 
	 * @author 王伟<br>
	 * @taskId <br>
	 * @param promptName
	 * @param variables
	 * @return <br>
	 */
	@Override
	public Message createUserMessage(String promptName, Map<String, Object> variables) {
		String renderedPrompt = renderPrompt(promptName, variables);
		if (renderedPrompt != null) {
			return new UserMessage(renderedPrompt);
		}
		return null;
	}

	/**
	 * Description: <br>
	 * 
	 * @author 王伟<br>
	 * @taskId <br>
	 * @param promptName
	 * @param variables
	 * @return <br>
	 */
	@Override
	public String renderPrompt(String promptName, Map<String, Object> variables) {
		PromptVO prompt = getPromptByName(this.namespace, promptName);
		if (prompt == null) {
			return null;
		}

		String content = prompt.getPromptContent();
		if (content == null || variables == null || variables.isEmpty()) {
			return content;
		}

		PromptTemplate template = new PromptTemplate(content);
		return template.render(variables != null ? variables : Map.of());
	}

	/**
	 * Description: <br>
	 * 
	 * @author 王伟<br>
	 * @taskId <br>
	 *         <br>
	 */
	@Override
	public void reinitializePrompts() {
		promptCache.clear();
	}

	/**
	 * Description: <br>
	 * 
	 * @author 王伟<br>
	 * @taskId <br>
	 * @param promptVO
	 * @return <br>
	 */
	@Override
	public PromptVO create(PromptVO promptVO) {
		try {
			if (promptVO.getNamespace() == null) {
				promptVO.setNamespace(this.namespace);
			}

			List<PromptVO> prompts = loadPromptsFromFile(promptVO.getNamespace());
			prompts.add(promptVO);
			savePromptsToFile(promptVO.getNamespace(), prompts);

			// 更新缓存
			String cacheKey = promptVO.getNamespace() + ":" + promptVO.getPromptName();
			promptCache.put(cacheKey, promptVO);

			return promptVO;
		} catch (Exception e) {
			LoggerUtil.error("Failed to create prompt: " + promptVO.getPromptName(), e);
			return null;
		}
	}

	/**
	 * Description: <br>
	 * 
	 * @author 王伟<br>
	 * @taskId <br>
	 * @param promptVO
	 * @return <br>
	 */
	@Override
	public PromptVO update(PromptVO promptVO) {
		try {
			if (promptVO.getNamespace() == null) {
				promptVO.setNamespace(this.namespace);
			}

			List<PromptVO> prompts = loadPromptsFromFile(promptVO.getNamespace());
			boolean found = false;
			for (int i = 0; i < prompts.size(); i++) {
				PromptVO existing = prompts.get(i);
				if (promptVO.getPromptName().equals(existing.getPromptName())) {
					prompts.set(i, promptVO);
					found = true;
					break;
				}
			}

			if (!found) {
				prompts.add(promptVO);
			}

			savePromptsToFile(promptVO.getNamespace(), prompts);

			// 更新缓存
			String cacheKey = promptVO.getNamespace() + ":" + promptVO.getPromptName();
			promptCache.put(cacheKey, promptVO);

			return promptVO;
		} catch (Exception e) {
			LoggerUtil.error("Failed to update prompt: " + promptVO.getPromptName(), e);
			return null;
		}
	}

	/**
	 * 从文件加载Prompt配置
	 * 
	 * @param namespace 命名空间
	 * @return Prompt列表
	 * @throws IOException IO异常
	 */
	private List<PromptVO> loadPromptsFromFile(String namespace) throws IOException {
		Path promptFile = getPromptFilePath(namespace);
		if (!Files.exists(promptFile)) {
			return new ArrayList<>();
		}

		String content = Files.readString(promptFile);
		if (content.trim().isEmpty()) {
			return new ArrayList<>();
		}

		return objectMapper.readValue(content, new TypeReference<List<PromptVO>>() {
		});
	}

	/**
	 * 将Prompt配置保存到文件
	 * 
	 * @param namespace 命名空间
	 * @param prompts   Prompt列表
	 * @throws IOException IO异常
	 */
	private void savePromptsToFile(String namespace, List<PromptVO> prompts) throws IOException {
		Path promptFile = getPromptFilePath(namespace);
		unifiedDirectoryManager.ensureDirectoryExists(promptFile.getParent());
		String content = objectMapper.writeValueAsString(prompts);
		Files.writeString(promptFile, content);
	}

	/**
	 * 获取Prompt文件路径
	 * 
	 * @param namespace 命名空间
	 * @return Prompt文件路径
	 */
	private Path getPromptFilePath(String namespace) {
		return unifiedDirectoryManager.getWorkingDirectory().resolve(namespace).resolve(PROMPT_CONFIG_FILE);
	}
}
