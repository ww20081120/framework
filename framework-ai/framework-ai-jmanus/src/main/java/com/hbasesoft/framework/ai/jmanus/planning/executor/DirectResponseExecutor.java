/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.jmanus.planning.executor;

import java.util.List;

import com.hbasesoft.framework.ai.jmanus.agent.BaseAgent;
import com.hbasesoft.framework.ai.jmanus.config.IManusProperties;
import com.hbasesoft.framework.ai.jmanus.dynamic.agent.model.po.DynamicAgentPo;
import com.hbasesoft.framework.ai.jmanus.dynamic.agent.service.AgentService;
import com.hbasesoft.framework.ai.jmanus.llm.ILlmService;
import com.hbasesoft.framework.ai.jmanus.planning.model.vo.ExecutionContext;
import com.hbasesoft.framework.ai.jmanus.recorder.PlanExecutionRecorder;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月20日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.jmanus.planning.executor <br>
 */

public class DirectResponseExecutor extends AbstractPlanExecutor {

	/**
	 * Constructor for DirectResponseExecutor
	 * 
	 * @param agents          List of dynamic agent entities
	 * @param recorder        Plan execution recorder
	 * @param agentService    Agent service
	 * @param llmService      LLM service
	 * @param manusProperties Manus properties
	 */
	public DirectResponseExecutor(List<DynamicAgentPo> agents, PlanExecutionRecorder recorder,
			AgentService agentService, ILlmService llmService, IManusProperties manusProperties) {
		super(agents, recorder, agentService, llmService, manusProperties);
	}

	/**
	 * Execute direct response plan - records plan execution start and marks as
	 * successful The actual direct response generation is handled by
	 * PlanningCoordinator
	 * 
	 * @param context Execution context containing user request and plan information
	 */
	@Override
	public void executeAllSteps(ExecutionContext context) {
		LoggerUtil.info("Executing direct response plan for planId: {0}", context.getCurrentPlanId());

		BaseAgent lastExecutor = null;

		try {
			// Record plan execution start
			recorder.recordPlanExecutionStart(context);

			LoggerUtil.info("Direct response executor completed successfully for planId: {0}",
					context.getCurrentPlanId());
			context.setSuccess(true);
		} catch (Exception e) {
			LoggerUtil.error("Error during direct response execution for planId: {0}", context.getCurrentPlanId(), e);
			context.setSuccess(false);
			// Set error message as result summary
			context.setResultSummary("Direct response execution failed: " + e.getMessage());
		} finally {
			performCleanup(context, lastExecutor);
		}
	}

}
