/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.jmanus.config;

/**
 * <Description> <br>
 * Interface for Manus configuration properties
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月19日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.jmanus.config <br>
 */
public interface IManusProperties {

	/**
	 * Get browser headless setting
	 */
	Boolean getBrowserHeadless();

	/**
	 * Set browser headless setting
	 */
	void setBrowserHeadless(Boolean browserHeadless);

	/**
	 * Get browser request timeout
	 */
	Integer getBrowserRequestTimeout();

	/**
	 * Set browser request timeout
	 */
	void setBrowserRequestTimeout(Integer browserRequestTimeout);

	/**
	 * Get debug detail setting
	 */
	Boolean getDebugDetail();

	/**
	 * Set debug detail setting
	 */
	void setDebugDetail(Boolean debugDetail);

	/**
	 * Get open browser auto setting
	 */
	Boolean getOpenBrowserAuto();

	/**
	 * Set open browser auto setting
	 */
	void setOpenBrowserAuto(Boolean openBrowserAuto);

	/**
	 * Get max steps
	 */
	Integer getMaxSteps();

	/**
	 * Set max steps
	 */
	void setMaxSteps(Integer maxSteps);

	/**
	 * Get force override from yaml setting
	 */
	Boolean getForceOverrideFromYaml();

	/**
	 * Set force override from yaml setting
	 */
	void setForceOverrideFromYaml(Boolean forceOverrideFromYaml);

	/**
	 * Get user input timeout
	 */
	Integer getUserInputTimeout();

	/**
	 * Set user input timeout
	 */
	void setUserInputTimeout(Integer userInputTimeout);

	/**
	 * Get max memory
	 */
	Integer getMaxMemory();

	/**
	 * Set max memory
	 */
	void setMaxMemory(Integer maxMemory);

	/**
	 * Get parallel tool calls setting
	 */
	Boolean getParallelToolCalls();

	/**
	 * Set parallel tool calls setting
	 */
	void setParallelToolCalls(Boolean parallelToolCalls);

	/**
	 * Get base directory
	 */
	String getBaseDir();

	/**
	 * Set base directory
	 */
	void setBaseDir(String baseDir);

	/**
	 * Description: <br>
	 * 
	 * @author 王伟<br>
	 * @taskId <br>
	 * @return <br>
	 */
	Boolean getAllowExternalAccess();

	/**
	 * Description: <br>
	 * 
	 * @author 王伟<br>
	 * @taskId <br>
	 * @return <br>
	 */
	Integer getInfiniteContextParallelThreads();

	/**
	 * Description: <br>
	 * 
	 * @author 王伟<br>
	 * @taskId <br>
	 * @return <br>
	 */
	Integer getInfiniteContextTaskContextSize();

	/**
	 * Description: <br>
	 * 
	 * @author 王伟<br>
	 * @taskId <br>
	 * @return <br>
	 */
	Boolean getInfiniteContextEnabled();

}
