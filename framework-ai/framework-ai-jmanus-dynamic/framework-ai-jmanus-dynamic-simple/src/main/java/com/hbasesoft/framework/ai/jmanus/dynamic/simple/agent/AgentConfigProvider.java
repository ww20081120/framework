/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.jmanus.dynamic.simple.agent;

import java.util.Collections;
import java.util.List;

/**
 * Agent配置提供者接口<br>
 * 该接口定义了获取Agent配置信息的方法，允许Agent类通过实现此接口来提供详细的配置信息
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月27日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.jmanus.dynamic.simple.agent <br>
 */
public interface AgentConfigProvider {

	/**
	 * 获取系统提示词
	 * 
	 * @return 系统提示词
	 */
	default String getSystemPrompt() {
		return "";
	}

	/**
	 * 获取下一步提示词
	 * 
	 * @return 下一步提示词
	 */
	default String getNextStepPrompt() {
		return "";
	}

	/**
	 * 获取可用工具列表
	 * 
	 * @return 可用工具列表
	 */
	default List<String> getAvailableTools() {
		return Collections.emptyList();
	}
}
