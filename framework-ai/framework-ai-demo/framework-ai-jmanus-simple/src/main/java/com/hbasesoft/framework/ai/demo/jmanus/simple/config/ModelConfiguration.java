/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.demo.jmanus.simple.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.hbasesoft.framework.ai.jmanus.dynamic.model.model.vo.ModelConfig;
import com.hbasesoft.framework.ai.jmanus.dynamic.simple.model.ModelConfigBuilder;

/**
 * ModelConfig配置示例类<br>
 * 展示如何通过@Configuration注解来注入ModelConfig实例
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月27日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.jmanus.dynamic.simple.model <br>
 */
@Configuration
public class ModelConfiguration {

	/**
	 * 创建OpenAI模型配置Bean
	 * 
	 * @return OpenAI模型配置
	 */
	@Bean
	public ModelConfig openAIModelConfig() {
		return ModelConfigBuilder.builder().baseUrl("https://api-inference.modelscope.cn")
				.apiKey("ms-375d4f20-87a5-4f25-a090-2568666c502f").modelName("Qwen/Qwen3-Coder-480B-A35B-Instruct")
				.modelDescription(
						"Qwen3-Coder 是通义千问团队开源的最新 AI 编程大模型，具备卓越的代码生成与智能代理能力，支持超长上下文，适用于多种智能编程场景。模型及工具已在魔搭社区、HuggingFace 等平台开源，开发者可免费下载和使用")
				.type("openai").isDefault(true).temperature(0.7).topP(0.9).completionsPath("/v1/chat/completions").build();
	}
}
