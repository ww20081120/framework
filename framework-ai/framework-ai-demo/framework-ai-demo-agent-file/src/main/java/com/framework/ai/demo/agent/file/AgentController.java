/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.framework.ai.demo.agent.file;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hbasesoft.framework.ai.agent.dynamic.memory.service.MemoryService;
import com.hbasesoft.framework.ai.agent.dynamic.memory.vo.MemoryVo;
import com.hbasesoft.framework.ai.agent.planning.PlanningFactory;
import com.hbasesoft.framework.ai.agent.planning.coordinator.PlanIdDispatcher;
import com.hbasesoft.framework.ai.agent.planning.coordinator.PlanningCoordinator;
import com.hbasesoft.framework.ai.agent.planning.model.vo.ExecutionContext;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月27日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.demo.jmanus.simple <br>
 */
@RequestMapping("/api/agent")
@RestController
public class AgentController {

	@Autowired
	@Lazy
	private PlanningFactory planningFactory;

	@Autowired
	private PlanIdDispatcher planIdDispatcher;

	@Autowired
	private MemoryService memoryService;

	private String memoryId;

	private String rootPlanId;

	@GetMapping("/solve")
	public String solveCodingTask(@RequestParam("task") String task) {
		if (StringUtils.isAllBlank(task)) {
			return "处理任务失败: Query content cannot be empty";
		}

		try {
			// 创建 ProcessOptions

			ExecutionContext context = new ExecutionContext();
			context.setUserRequest(task);

			// Use PlanIdDispatcher to generate a unique plan ID
			String planId = planIdDispatcher.generatePlanId();
			context.setCurrentPlanId(planId);

			context.setNeedSummary(true);
			if (rootPlanId == null) {
				rootPlanId = planId;
			}
			context.setRootPlanId(rootPlanId);

			if (StringUtils.isEmpty(memoryId)) {
				memoryId = RandomStringUtils.randomAlphabetic(8);
			}
			context.setMemoryId(memoryId);

			// Get or create planning flow
			PlanningCoordinator planningFlow = planningFactory.createPlanningCoordinator(context);

			// Asynchronous execution of task
			memoryService.saveMemory(new MemoryVo(context.getMemoryId(), task));

			ExecutionContext ctx = planningFlow.executePlan(context);

			// 返回结果
			return "任务处理完成:\n" + ctx.getResultSummary();
		} catch (Exception e) {
			return "处理任务时出错: " + e.getMessage();
		}
	}

	@GetMapping("/clean")
	public String clean() {
		memoryId = null;
		rootPlanId = null;
		return "清理成功";
	}
}
