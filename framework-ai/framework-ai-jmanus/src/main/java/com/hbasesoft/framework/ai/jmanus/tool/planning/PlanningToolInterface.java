/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.jmanus.tool.planning;

import org.springframework.ai.tool.function.FunctionToolCallback;

import com.hbasesoft.framework.ai.jmanus.planning.model.vo.PlanInterface;
import com.hbasesoft.framework.ai.jmanus.tool.ToolExecuteResult;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月19日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.jmanus.tool.planning <br>
 */
public interface PlanningToolInterface {

	/**
	 * Get current plan ID
	 * 
	 * @return Current plan ID, returns null if no plan exists
	 */
	String getCurrentPlanId();

	/**
	 * Get current execution plan
	 * 
	 * @return Current execution plan, returns null if no plan exists
	 */
	PlanInterface getCurrentPlan();

	/**
	 * Get function tool callback for LLM integration
	 * 
	 * @return FunctionToolCallback instance
	 */
	FunctionToolCallback<?, ToolExecuteResult> getFunctionToolCallback();
}
