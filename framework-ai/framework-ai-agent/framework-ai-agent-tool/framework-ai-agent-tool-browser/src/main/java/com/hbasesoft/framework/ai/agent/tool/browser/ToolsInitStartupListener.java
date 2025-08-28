/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.agent.tool.browser;

import org.springframework.context.ApplicationContext;

import com.hbasesoft.framework.common.StartupListener;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月24日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.jmanus.tool.web.init <br>
 */
public class ToolsInitStartupListener implements StartupListener {

	/**
	 * Description: <br>
	 * 
	 * @author 王伟<br>
	 * @taskId <br>
	 * @param context <br>
	 */
	@Override
	public void complete(ApplicationContext context) {
		context.getBean(ChromeDriverService.class).init();
	}
}
