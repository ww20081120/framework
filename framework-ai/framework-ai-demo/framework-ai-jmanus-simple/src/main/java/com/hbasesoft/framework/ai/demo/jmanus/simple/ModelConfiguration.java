/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.demo.jmanus.simple;

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
		return ModelConfigBuilder.builder().baseUrl("https://api.openai.com/v1").apiKey("your-openai-api-key")
				.modelName("gpt-4-turbo").modelDescription("OpenAI GPT-4 Turbo模型").type("openai").isDefault(false)
				.temperature(0.7).topP(0.9).completionsPath("/chat/completions").build();
	}

	/**
	 * 创建Anthropic模型配置Bean
	 * 
	 * @return Anthropic模型配置
	 */
	@Bean
	public ModelConfig anthropicModelConfig() {
		return ModelConfigBuilder.builder().baseUrl("https://api.anthropic.com/v1").apiKey("your-anthropic-api-key")
				.modelName("claude-3-opus").modelDescription("Anthropic Claude-3 Opus模型").type("anthropic")
				.isDefault(false).temperature(0.8).topP(0.95).completionsPath("/messages").build();
	}
}
