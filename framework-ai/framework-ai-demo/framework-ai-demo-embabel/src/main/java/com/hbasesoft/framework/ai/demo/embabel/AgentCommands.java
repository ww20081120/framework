/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.demo.embabel;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import com.embabel.agent.api.common.autonomy.AgentProcessExecution;
import com.embabel.agent.api.common.autonomy.Autonomy;
import com.embabel.agent.core.ProcessOptions;
import com.embabel.agent.core.Verbosity;

/**
 * Shell commands for the Embabel agent demo.
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月21日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.demo.embabel <br>
 */
@ShellComponent
public class AgentCommands {

	/** The autonomy instance. */
	private final Autonomy autonomy;

	/**
	 * Constructor for AgentCommands.
	 *
	 * @param autonomy the autonomy instance
	 */
	public AgentCommands(final Autonomy autonomy) {
		this.autonomy = autonomy;
	}

	/**
	 * Solves a coding task using the Embabel agent.
	 *
	 * @param task the coding task description
	 * @return the result of the task processing
	 */
	@ShellMethod(key = "solve", value = "使用 Embabel 代理解决编码任务")
	public String solveCodingTask(@ShellOption(help = "编码任务描述") final String task) {
		try {
			// 创建 ProcessOptions
			ProcessOptions processOptions = ProcessOptions.builder()
					.verbosity(Verbosity.builder().showPlanning(true).build()).build();

			// 使用 Autonomy 执行任务
			AgentProcessExecution result = autonomy.chooseAndRunAgent(task, processOptions);

			// 返回结果
			return "任务处理完成:\n" + result.toString();
		} catch (Exception e) {
			return "处理任务时出错: " + e.getMessage();
		}
	}

	/**
	 * Displays available commands.
	 *
	 * @return the help text
	 */
	@ShellMethod(key = "help", value = "显示可用命令")
	public String help() {
		return """
				可用命令:
				solve --task "<description>"  - 解决编码任务
				help                          - 显示此帮助信息
				exit                          - 退出 shell

				示例:
				solve --task "创建一个简单的计算器 Java 类"
				""";
	}
}
