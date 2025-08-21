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
import com.hbasesoft.framework.ai.jmanus.planning.model.vo.ExecutionStep;
import com.hbasesoft.framework.ai.jmanus.planning.model.vo.PlanInterface;
import com.hbasesoft.framework.ai.jmanus.recorder.PlanExecutionRecorder;

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

public class PlanExecutor extends AbstractPlanExecutor {

	/**
	 * Constructor for PlanExecutor
	 * @param agents List of dynamic agent entities
	 * @param recorder Plan execution recorder
	 * @param agentService Agent service
	 * @param llmService LLM service
	 */
	public PlanExecutor(List<DynamicAgentPo> agents, PlanExecutionRecorder recorder, AgentService agentService,
			ILlmService llmService, IManusProperties manusProperties) {
		super(agents, recorder, agentService, llmService, manusProperties);
	}

	/**
	 * Execute all steps of the entire plan
	 * @param context Execution context containing user request and execution process
	 * information
	 */
	@Override
	public void executeAllSteps(ExecutionContext context) {
		BaseAgent lastExecutor = null;
		PlanInterface plan = context.getPlan();
		plan.updateStepIndices();

		try {
			recorder.recordPlanExecutionStart(context);
			List<ExecutionStep> steps = plan.getAllSteps();

			if (steps != null && !steps.isEmpty()) {
				for (ExecutionStep step : steps) {
					BaseAgent stepExecutor = executeStep(step, context);
					if (stepExecutor != null) {
						lastExecutor = stepExecutor;
					}
				}
			}

			context.setSuccess(true);
		}
		finally {
			performCleanup(context, lastExecutor);
		}
	}

}