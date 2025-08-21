/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.jmanus.tool;

import org.springframework.ai.chat.model.ToolContext;

import com.hbasesoft.framework.ai.jmanus.tool.code.ToolExecuteResult;

/** 
 * <Description> <br> 
 *  
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月19日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.jmanus.tool <br>
 */
public abstract class AbstractBaseTool<I> implements ToolCallBiFunctionDef<I> {

	/**
	 * Current plan ID for the tool execution context
	 */
	protected String currentPlanId;

	/**
	 * Root plan ID is the global parent of the whole execution plan
	 */
	protected String rootPlanId;

	@Override
	public boolean isReturnDirect() {
		return false;
	}

	@Override
	public void setCurrentPlanId(String planId) {
		this.currentPlanId = planId;
	}

	@Override
	public void setRootPlanId(String rootPlanId) {
		this.rootPlanId = rootPlanId;
	}

	/**
	 * Default implementation delegates to run method Subclasses can override this method
	 * if needed
	 */
	@Override
	public ToolExecuteResult apply(I input, ToolContext toolContext) {
		return run(input);
	}

	/**
	 * Abstract method that subclasses must implement to define tool-specific execution
	 * logic
	 * @param input Tool input parameters
	 * @return Tool execution result
	 */
	public abstract ToolExecuteResult run(I input);

}
