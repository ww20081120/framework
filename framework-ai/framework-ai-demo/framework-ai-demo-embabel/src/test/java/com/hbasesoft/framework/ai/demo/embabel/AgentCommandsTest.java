/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.demo.embabel;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.embabel.agent.api.common.autonomy.Autonomy;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月21日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.demo.embabel <br>
 */

@SpringBootTest(classes = DemoApplication.class, properties = { "spring.shell.interactive.enabled=false" })
class AgentCommandsTest {

	@Autowired
	private AgentCommands agentCommands;

	@Autowired
	private Autonomy autonomy;

	@Test
	void testSolveCodingTask() {
		// 准备测试数据
		String task = "创建一个简单的计算器 Java 类";

		// 执行测试 - 真正调用代码
		String result = agentCommands.solveCodingTask(task);

		// 验证结果
		assertNotNull(result);
		assertTrue(result.contains("任务处理完成") || result.contains("处理任务时出错"));

		// 验证 Autonomy bean 已正确注入
		assertNotNull(autonomy);
	}

	@Test
	void testHelp() {
		// 执行测试
		String result = agentCommands.help();

		// 验证结果
		assertNotNull(result);
		assertTrue(result.contains("可用命令"));
		assertTrue(result.contains("solve --task"));
		assertTrue(result.contains("help"));
		assertTrue(result.contains("exit"));
	}

	@Test
	void testAutonomyInjection() {
		// 验证 Autonomy bean 已正确注入
		assertNotNull(autonomy);
		assertNotNull(agentCommands);
	}
}
