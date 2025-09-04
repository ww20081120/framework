/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.agent.config.model.vo;

import java.time.LocalDateTime;

import com.hbasesoft.framework.ai.agent.config.model.enums.ConfigInputType;

import lombok.Data;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月22日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.agent.config.model.vo <br>
 */
@Data
public class ConfigVo {
	private Long id;

	private String configGroup;

	private String configSubGroup;

	private String configKey;

	private String configPath;

	private String configValue;

	private String defaultValue;

	private String description;

	private ConfigInputType inputType;
	private String optionsJson;

	private LocalDateTime updateTime;
	private LocalDateTime createTime;
}
