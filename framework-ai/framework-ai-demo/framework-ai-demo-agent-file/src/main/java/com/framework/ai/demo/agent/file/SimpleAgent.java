/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.framework.ai.demo.agent.file;

import com.hbasesoft.framework.ai.agent.file.agent.Agent;
import com.hbasesoft.framework.ai.agent.file.agent.AgentConfigProvider;

/**
 * 简单Agent示例<br>
 * 该类演示了如何使用@Agent注解来标记和配置智能代理，并实现AgentConfigProvider接口提供详细配置
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月27日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.jmanus.dynamic.simple.agent <br>
 */
@Agent(name = "simpleAgent", description = "一个简单的示例代理", model = "Qwen/Qwen3-Coder-480B-A35B-Instruct", acions = {
		"bash", "text_file_operator", "planning", "python_execute", "google_search" })
public class SimpleAgent implements AgentConfigProvider {

	// 示例Agent类，可以添加具体的业务逻辑方法
	@Override
	public String getSystemPrompt() {
		return "你是一个简单的示例代理，用于演示如何提供系统提示词。";
	}

	@Override
	public String getNextStepPrompt() {
		return "请继续执行下一步操作。";
	}
}
