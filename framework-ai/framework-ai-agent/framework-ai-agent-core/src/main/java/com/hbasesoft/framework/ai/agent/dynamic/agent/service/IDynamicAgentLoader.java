/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.agent.dynamic.agent.service;

import java.util.List;
import java.util.Map;

import com.hbasesoft.framework.ai.agent.dynamic.agent.DynamicAgent;
import com.hbasesoft.framework.ai.agent.planning.model.vo.ExecutionContext;

/**
 * 动态代理加载器接口<br>
 * 该接口定义了动态加载和获取智能代理的方法。
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月20日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.agent.dynamic.agent.service <br>
 */
public interface IDynamicAgentLoader {

	/**
	 * 根据代理名称和初始设置加载代理
	 * 
	 * @param agentName 代理名称
	 * @param initialAgentSetting 初始代理设置
	 * @return 动态代理实例
	 */
	DynamicAgent loadAgent(String agentName, Map<String, Object> initialAgentSetting);

	/**
	 * 获取所有代理
	 * 
	 * @return 代理实体列表
	 */
	List<DynamicAgent> getAllAgents();

	/**
	 * 根据执行上下文获取代理列表
	 * 
	 * @param context 执行上下文
	 * @return 代理列表
	 */
	default List<DynamicAgent> getAgents(ExecutionContext context) {
		return getAllAgents();
	}

}