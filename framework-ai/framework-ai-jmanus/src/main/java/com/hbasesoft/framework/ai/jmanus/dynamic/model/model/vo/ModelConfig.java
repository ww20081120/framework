/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.jmanus.dynamic.model.model.vo;

import java.util.Map;

import lombok.Data;

/**
 * <Description> <br>
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

	private Long id;

	private String baseUrl;

	private String apiKey;

	private Map<String, String> headers;

	private String modelName;

	private String modelDescription;

	private String type;

	private Boolean isDefault;

	private Double temperature;

	private Double topP;

	private String completionsPath;

}
