/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.jmanus.tool;

import java.util.function.BiFunction;

import org.springframework.ai.chat.model.ToolContext;

import com.hbasesoft.framework.ai.jmanus.tool.code.ToolExecuteResult;

/**
 * <Description> 工具定义接口，提供统一的工具定义方法 <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年7月1日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.jmanus.tool <br>
 */
public interface ToolCallBiFunctionDef<I> extends BiFunction<I, ToolContext, ToolExecuteResult> {

	/**
	 * Get the name of the tool group
	 * 
	 * @return Returns the unique identifier name of the tool
	 */
	String getServiceGroup();

	/**
	 * Get the name of the tool
	 * 
	 * @return Returns the unique identifier name of the tool
	 */
	String getName();

	/**
	 * Get the description information of the tool
	 * 
	 * @return Returns the functional description of the tool
	 */
	String getDescription();

	/**
	 * Get the parameter definition schema of the tool
	 * 
	 * @return Returns JSON format parameter definition schema
	 */
	String getParameters();

	/**
	 * Get the input type of the tool
	 * 
	 * @return Returns the input parameter type Class that the tool accepts
	 */
	Class<I> getInputType();

	/**
	 * Determine whether the tool returns results directly
	 * 
	 * @return Returns true if the tool returns results directly, otherwise false
	 */
	boolean isReturnDirect();

	/**
	 * Set the associated Agent instance
	 * 
	 * @param planId The plan ID to associate
	 */
	public void setCurrentPlanId(String planId);

	/**
	 * root plan id is the global parent of the whole execution plan id .
	 * 
	 * @param rootPlanId
	 */
	public void setRootPlanId(String rootPlanId);

	/**
	 * Get the current status string of the tool
	 * 
	 * @return Returns a string describing the current status of the tool
	 */
	String getCurrentToolStateString();

	/**
	 * Clean up all related resources for the specified planId
	 * 
	 * @param planId Plan ID
	 */
	void cleanup(String planId);
}
