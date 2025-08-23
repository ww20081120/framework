/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.jmanus.dynamic.jpa.model.po;

import java.util.Map;

import com.hbasesoft.framework.ai.jmanus.dynamic.model.model.vo.ModelConfig;
import com.hbasesoft.framework.db.core.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月22日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.jmanus.dynamic.jpa.model.po <br>
 */
@Getter
@Setter
@Entity
@Table(name = "dynamic_models")
public class DynamicModelPo4Jpa extends BaseEntity {

	/**
	 * serialVersionUID <br>
	 */
	private static final long serialVersionUID = -1262687376925857455L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "base_url", nullable = false)
	private String baseUrl;

	@Column(name = "api_key", nullable = false)
	private String apiKey;

	@Column(name = "headers", columnDefinition = "VARCHAR(2048)")
	private String headers;

	@Column(name = "model_name", nullable = false)
	private String modelName;

	@Column(name = "model_description", nullable = false, length = 1000)
	private String modelDescription;

	@Column(name = "type", nullable = false)
	private String type;

	@Column(name = "is_default", nullable = false)
	private Integer isDefault;

	@Column(name = "temperature")
	private Double temperature;

	@Column(name = "top_p")
	private Double topP;

	@Column(name = "completions_path")
	private String completionsPath;

	public DynamicModelPo4Jpa() {
	}

	public DynamicModelPo4Jpa(Long id) {
		this.id = id;
	}

	public ModelConfig mapToModelConfig() {
		ModelConfig config = new ModelConfig();
		config.setId(this.getId());
		config.setHeaders(new MapToStringConverter().convertToEntityAttribute(this.getHeaders()));
		config.setBaseUrl(this.getBaseUrl());
		config.setApiKey(maskValue(this.getApiKey()));
		config.setModelName(this.getModelName());
		config.setModelDescription(this.getModelDescription());
		config.setType(this.getType());
		config.setIsDefault(this.getIsDefault() == 0 ? false : true);
		config.setTemperature(this.getTemperature());
		config.setTopP(this.getTopP());
		config.setCompletionsPath(this.getCompletionsPath());
		return config;
	}

	/**
	 * Obscures the string, keeping the first 4 and last 4 characters visible,
	 * replacing the rest with asterisks (*)
	 */
	private String maskValue(String value) {
		if (value == null || value.length() <= 8) {
			return "*";
		}
		int length = value.length();
		String front = value.substring(0, 4);
		String end = value.substring(length - 4);
		return front + "*".repeat(length - 8) + end;
	}

	public void setHeaders(Map<String, String> headers) {
		this.headers = new MapToStringConverter().convertToDatabaseColumn(headers);
	}

}