/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.jmanus.dynamic.jpa.prompt.service;

import java.util.List;
import java.util.Map;

import org.springframework.ai.chat.messages.Message;

import com.hbasesoft.framework.ai.jmanus.dynamic.prompt.model.vo.PromptVO;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月22日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.jmanus.dynamic.jpa.prompt.service <br>
 */
public interface PromptManagerService {

	List<PromptVO> getAll();

	List<PromptVO> getAllByNamespace(String namespace);

	PromptVO getById(Long id);

	void delete(Long id);

	Message createMessage(String promptName, Map<String, Object> variables);

	void importSpecificPromptFromLanguage(String promptName, String language);

	void importAllPromptsFromLanguage(String language);

	String[] getSupportedLanguages();
}
