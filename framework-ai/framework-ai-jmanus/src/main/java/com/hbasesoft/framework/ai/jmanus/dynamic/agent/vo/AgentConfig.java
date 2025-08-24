/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.jmanus.dynamic.agent.vo;

import java.util.List;

import com.hbasesoft.framework.ai.jmanus.dynamic.model.model.vo.ModelConfig;

import lombok.Data;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年7月1日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.jmanus.dynamic.agent.vo <br>
 */
@Data
public class AgentConfig {

	private String id;

	private String name;

	private String description;

	private String systemPrompt;

	private String nextStepPrompt;

	private List<String> availableTools;

	private String className;

	private ModelConfig model;

	private String namespace;

	private Boolean builtIn = false;

	private Boolean isBuiltIn = false;
}
