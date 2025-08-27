/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.demo.jmanus.simple;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import com.hbasesoft.framework.ai.jmanus.dynamic.memory.service.MemoryService;
import com.hbasesoft.framework.ai.jmanus.dynamic.memory.vo.MemoryVo;
import com.hbasesoft.framework.ai.jmanus.planning.PlanningFactory;
import com.hbasesoft.framework.ai.jmanus.planning.coordinator.PlanIdDispatcher;
import com.hbasesoft.framework.ai.jmanus.planning.coordinator.PlanningCoordinator;
import com.hbasesoft.framework.ai.jmanus.planning.model.vo.ExecutionContext;

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
@ShellComponent
public class AgentCommands {

	@Autowired
	@Lazy
	private PlanningFactory planningFactory;

	@Autowired
	private PlanIdDispatcher planIdDispatcher;

	@Autowired
	private MemoryService memoryService;

	private String memoryId;

	private String rootPlanId;

	@ShellMethod(key = "solve", value = "使用 JManus代理解决编码任务")
	public String solveCodingTask(@ShellOption(help = "编码任务描述") String task) {
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

	@ShellMethod(key = "clean", value = "清理上下文")
	public String clean() {
		memoryId = null;
		rootPlanId = null;
		return "清理成功";
	}

	@ShellMethod(key = "help", value = "显示可用命令")
	public String help() {
		return """
				可用命令:
				solve --task "<description>"  - 解决编码任务
				clean                         - 清理上下文
				help                          - 显示此帮助信息
				exit                          - 退出 shell

				示例:
				solve --task "创建一个简单的计算器 Java 类"
				""";
	}
}
