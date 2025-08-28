/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.agent.config;

/**
 * Manus 配置属性接口<br>
 * 该接口定义了 Manus 应用的各种配置属性的 getter 和 setter 方法。
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月19日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.agent.config <br>
 */
public interface IManusProperties {

	/**
	 * 获取浏览器无头模式设置
	 * 
	 * @return Boolean 浏览器是否以无头模式运行 (默认值: false)
	 */
	Boolean getBrowserHeadless();

	/**
	 * 设置浏览器无头模式
	 * 
	 * @param browserHeadless 浏览器是否以无头模式运行
	 */
	void setBrowserHeadless(Boolean browserHeadless);

	/**
	 * 获取浏览器请求超时时间
	 * 
	 * @return Integer 请求超时时间（毫秒） (默认值: 180)
	 */
	Integer getBrowserRequestTimeout();

	/**
	 * 设置浏览器请求超时时间
	 * 
	 * @param browserRequestTimeout 请求超时时间（毫秒）
	 */
	void setBrowserRequestTimeout(Integer browserRequestTimeout);

	/**
	 * 获取调试详细信息设置
	 * 
	 * @return Boolean 是否显示调试详细信息 (默认值: false)
	 */
	Boolean getDebugDetail();

	/**
	 * 设置调试详细信息
	 * 
	 * @param debugDetail 是否显示调试详细信息
	 */
	void setDebugDetail(Boolean debugDetail);

	/**
	 * 获取自动打开浏览器设置
	 * 
	 * @return Boolean 是否自动打开浏览器 (默认值: true)
	 */
	Boolean getOpenBrowserAuto();

	/**
	 * 设置自动打开浏览器
	 * 
	 * @param openBrowserAuto 是否自动打开浏览器
	 */
	void setOpenBrowserAuto(Boolean openBrowserAuto);

	/**
	 * 获取最大步骤数
	 * 
	 * @return Integer 最大步骤数 (默认值: 5)
	 */
	Integer getMaxSteps();

	/**
	 * 设置最大步骤数
	 * 
	 * @param maxSteps 最大步骤数
	 */
	void setMaxSteps(Integer maxSteps);

	/**
	 * 获取强制从 YAML 文件覆盖设置
	 * 
	 * @return Boolean 是否强制从 YAML 文件覆盖配置 (默认值: true)
	 */
	Boolean getForceOverrideFromYaml();

	/**
	 * 设置强制从 YAML 文件覆盖
	 * 
	 * @param forceOverrideFromYaml 是否强制从 YAML 文件覆盖配置
	 */
	void setForceOverrideFromYaml(Boolean forceOverrideFromYaml);

	/**
	 * 获取用户输入超时时间
	 * 
	 * @return Integer 用户输入超时时间（毫秒） (默认值: 300)
	 */
	Integer getUserInputTimeout();

	/**
	 * 设置用户输入超时时间
	 * 
	 * @param userInputTimeout 用户输入超时时间（毫秒）
	 */
	void setUserInputTimeout(Integer userInputTimeout);

	/**
	 * 获取最大内存
	 * 
	 * @return Integer 最大内存（MB） (默认值: 1000)
	 */
	Integer getMaxMemory();

	/**
	 * 设置最大内存
	 * 
	 * @param maxMemory 最大内存（MB）
	 */
	void setMaxMemory(Integer maxMemory);

	/**
	 * 获取并行工具调用设置
	 * 
	 * @return Boolean 是否允许并行工具调用 (默认值: false)
	 */
	Boolean getParallelToolCalls();

	/**
	 * 设置并行工具调用
	 * 
	 * @param parallelToolCalls 是否允许并行工具调用
	 */
	void setParallelToolCalls(Boolean parallelToolCalls);

	/**
	 * 获取基础目录
	 * 
	 * @return String 基础目录路径
	 */
	String getBaseDir();

	/**
	 * 设置基础目录
	 * 
	 * @param baseDir 基础目录路径
	 */
	void setBaseDir(String baseDir);

	/**
	 * 获取是否允许外部访问
	 * 
	 * @return Boolean 是否允许外部访问 (默认值: false)
	 */
	Boolean getAllowExternalAccess();

	/**
	 * 获取无限上下文并行线程数
	 * 
	 * @return Integer 无限上下文并行线程数 (默认值: 6)
	 */
	Integer getInfiniteContextParallelThreads();

	/**
	 * 获取无限上下文任务上下文大小
	 * 
	 * @return Integer 无限上下文任务上下文大小 (默认值: 20000)
	 */
	Integer getInfiniteContextTaskContextSize();

	/**
	 * 获取无限上下文是否启用
	 * 
	 * @return Boolean 无限上下文是否启用 (默认值: true)
	 */
	Boolean getInfiniteContextEnabled();

}