/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.jmanus.dynamic.simple.model;

import com.hbasesoft.framework.ai.jmanus.dynamic.model.model.vo.ModelConfig;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月27日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.jmanus.dynamic.simple.model <br>
 */
public class ModelConfigBuilder {
	private ModelConfig modelConfig = new ModelConfig();

	public static ModelConfigBuilder builder() {
		return new ModelConfigBuilder();
	}

	public ModelConfigBuilder id(Long id) {
		modelConfig.setId(id);
		return this;
	}

	public ModelConfigBuilder baseUrl(String baseUrl) {
		modelConfig.setBaseUrl(baseUrl);
		return this;
	}

	public ModelConfigBuilder apiKey(String apiKey) {
		modelConfig.setApiKey(apiKey);
		return this;
	}

	public ModelConfigBuilder modelName(String modelName) {
		modelConfig.setModelName(modelName);
		return this;
	}

	public ModelConfigBuilder modelDescription(String modelDescription) {
		modelConfig.setModelDescription(modelDescription);
		return this;
	}

	public ModelConfigBuilder type(String type) {
		modelConfig.setType(type);
		return this;
	}

	public ModelConfigBuilder isDefault(Boolean isDefault) {
		modelConfig.setIsDefault(isDefault);
		return this;
	}

	public ModelConfigBuilder temperature(Double temperature) {
		modelConfig.setTemperature(temperature);
		return this;
	}

	public ModelConfigBuilder topP(Double topP) {
		modelConfig.setTopP(topP);
		return this;
	}

	public ModelConfigBuilder completionsPath(String completionsPath) {
		modelConfig.setCompletionsPath(completionsPath);
		return this;
	}

	public ModelConfig build() {
		return modelConfig;
	}
}
