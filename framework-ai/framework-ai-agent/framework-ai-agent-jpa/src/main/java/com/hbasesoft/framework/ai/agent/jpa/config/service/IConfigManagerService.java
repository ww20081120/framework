/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.agent.jpa.config.service;

import java.util.List;
import java.util.Optional;

import com.hbasesoft.framework.ai.agent.jpa.config.po.ConfigPo4Jpa;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月22日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.agent.jpa.config.service <br>
 */
public interface IConfigManagerService {

	/**
	 * Update configuration value
	 * 
	 * @param configPath the configuration path
	 * @param newValue   the new value
	 */
	void updateConfig(String configPath, String newValue);

	/**
	 * Get all configurations
	 * 
	 * @return list of all configurations
	 */
	List<ConfigPo4Jpa> getAllConfigs();

	/**
	 * Get configuration by path
	 * 
	 * @param configPath the configuration path
	 * @return optional configuration entity
	 */
	Optional<ConfigPo4Jpa> getConfig(String configPath);
}
