/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.jmanus.planning.executor.factory;

import com.hbasesoft.framework.ai.jmanus.planning.executor.PlanExecutorInterface;
import com.hbasesoft.framework.ai.jmanus.planning.model.vo.PlanInterface;

/** 
 * <Description> <br> 
 *  
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月20日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.jmanus.planning.executor.factory <br>
 */
public interface IPlanExecutorFactory {

	/**
	 * Create executor for the given plan
	 */
	PlanExecutorInterface createExecutor(PlanInterface plan);

	/**
	 * Get all supported plan types
	 */
	String[] getSupportedPlanTypes();

	/**
	 * Check if a plan type is supported
	 */
	boolean isPlanTypeSupported(String planType);

	/**
	 * Create executor by plan type and ID
	 */
	PlanExecutorInterface createExecutorByType(String planType, String planId);

}

