/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.jmanus.dynamic.model.model.vo;

import java.util.Map;

import lombok.Data;

/**
 * 模型配置类，用于定义和管理AI模型的各项配置信息<br>
 * 该类包含了模型的基本信息、访问凭证、请求参数等配置项
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月19日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.jmanus.dynamic.model.model.vo <br>
 */
@Data
public class ModelConfig {

	/** 模型配置的唯一标识符 */
	private Long id;

	/** 模型服务的基础URL */
	private String baseUrl;

	/** 访问模型服务的API密钥 */
	private String apiKey;

	/** 请求头信息 */
	private Map<String, String> headers;

	/** 模型名称 */
	private String modelName;

	/** 模型描述信息 */
	private String modelDescription;

	/** 模型类型 */
	private String type;

	/** 是否为默认模型 */
	private Boolean isDefault;

	/** 采样温度，控制生成文本的随机性 */
	private Double temperature;

	/** 核采样参数，控制生成文本的多样性 */
	private Double topP;

	/** 补全请求的路径 */
	private String completionsPath;

}