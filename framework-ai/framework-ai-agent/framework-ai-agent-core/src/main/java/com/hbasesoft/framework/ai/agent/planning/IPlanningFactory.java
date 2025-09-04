/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.agent.planning;

import java.util.Map;

import org.springframework.ai.tool.ToolCallback;
import org.springframework.web.client.RestClient;

import com.hbasesoft.framework.ai.agent.dynamic.agent.ToolCallbackProvider;
import com.hbasesoft.framework.ai.agent.planning.coordinator.PlanningCoordinator;
import com.hbasesoft.framework.ai.agent.planning.model.vo.ExecutionContext;
import com.hbasesoft.framework.ai.agent.tool.ToolCallBiFunctionDef;

import lombok.Data;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月19日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.agent.planning <br>
 */
public interface IPlanningFactory {

	/**
	 * Create planning coordinator
	 * 
	 * @param context
	 * @return Planning coordinator
	 */
	PlanningCoordinator createPlanningCoordinator(ExecutionContext context);

	/**
	 * Create tool callback mapping
	 * 
	 * @param planId             Plan ID
	 * @param rootPlanId         Root plan ID
	 * @param expectedReturnInfo Expected return information
	 * @return Tool callback mapping
	 */
	Map<String, PlanningFactory.ToolCallBackContext> toolCallbackMap(String planId, String rootPlanId,
			String expectedReturnInfo);

	/**
	 * Create RestClient
	 * 
	 * @return RestClient builder
	 */
	RestClient.Builder createRestClient();

	/**
	 * Create empty tool callback provider
	 * 
	 * @return Tool callback provider functional interface
	 */
	ToolCallbackProvider emptyToolCallbackProvider();

	@Data
	public static class ToolCallBackContext {

		private final ToolCallback toolCallback;

		private final ToolCallBiFunctionDef<?> functionInstance;
	}
}
