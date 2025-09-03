/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.framework.ai.demo.agent.file;

import java.time.LocalDateTime;
import java.util.Locale;

import org.springframework.context.i18n.LocaleContextHolder;

import com.hbasesoft.framework.ai.agent.file.agent.Agent;
import com.hbasesoft.framework.ai.agent.tool.Action;
import com.hbasesoft.framework.ai.agent.tool.ActionParam;

/**
 * <Description> Demo agent showing how to use the @Action annotation <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月31日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.agent.file.agent <br>
 */
@Agent(name = "DemoAgent", 
acions = { "bash", "planning",
		"python_execute" }, 
description = "我能看当前是什么时间，也能算两个数相加， 还能和人打招呼", 
systemPrompt = """
				 getCurrentDateTime 方法用来获取当前时间的， 
				 add 是用来相加的方法， 
				 greet是打招呼的s
				""")
public class AgentDemo {

	@Action(description = "Get the current date and time in the user's timezone")
	public String getCurrentDateTime() {
		System.out.println("获取系统时间");
		return LocalDateTime.now().atZone(LocaleContextHolder.getTimeZone().toZoneId()).toString();
	}

	@Action(description = "Add two numbers together")
	public int add(@ActionParam(description = "The first number to add", required = true) int a,
			@ActionParam(description = "The second number to add", required = true) int b) {
		System.out.println("两数相加:" + a + ":" + b);
		return a + b;
	}

	@Action(description = "Greet a person by name", serviceGroup = "greeting-tools")
	public String greet(@ActionParam(description = "The name of the person to greet", required = true) String name,
			@ActionParam(description = "The greeting language (e.g., 'en', 'zh')", required = false) String language) {
		String lang = (language != null) ? language : Locale.getDefault().getLanguage();
		System.out.println("打招呼:" + name);
		if ("zh".equals(lang)) {
			return "你好，" + name + "！";
		} else {
			return "Hello, " + name + "!";
		}
	}
}
