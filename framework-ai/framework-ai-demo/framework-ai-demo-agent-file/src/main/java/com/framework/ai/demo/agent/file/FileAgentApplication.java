/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.framework.ai.demo.agent.file;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import com.hbasesoft.framework.ai.agent.EnableAgents;
import com.hbasesoft.framework.common.Bootstrap;

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
@EnableAgents
@SpringBootApplication
@ComponentScan(basePackages = "com.framework.ai.demo.agent.file")
public class FileAgentApplication {

	/**
	 * Description: <br>
	 *
	 * @param args <br>
	 * @author 王伟<br>
	 * @taskId <br>
	 */
	public static void main(final String[] args) {
		Bootstrap.before();
		ConfigurableApplicationContext context = new SpringApplicationBuilder(FileAgentApplication.class).run(args);
		Bootstrap.after(context);
	}
}
