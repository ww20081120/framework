/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.jmanus.config;

import java.util.List;
import java.util.Optional;

import org.springframework.transaction.annotation.Transactional;

import com.hbasesoft.framework.ai.jmanus.config.model.po.ConfigPo;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月20日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.jmanus.config <br>
 */
public interface IConfigService {

	/**
	 * Get configuration value by path
	 * 
	 * @param configPath the configuration path
	 * @return the configuration value, or null if not found
	 */
	@Transactional(readOnly = true)
	String getConfigValue(String configPath);

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
	@Transactional(readOnly = true)
	List<ConfigPo> getAllConfigs();

	/**
	 * Get configuration by path
	 * 
	 * @param configPath the configuration path
	 * @return optional configuration entity
	 */
	@Transactional(readOnly = true)
	Optional<ConfigPo> getConfig(String configPath);

	/**
	 * Reset configuration to default value
	 * 
	 * @param configPath the configuration path
	 */
	void resetConfig(String configPath);

	/**
	 * Get configurations by group
	 * 
	 * @param groupName the group name
	 * @return list of configurations in the group
	 */
	@Transactional(readOnly = true)
	List<ConfigPo> getConfigsByGroup(String groupName);

	/**
	 * Batch update configurations
	 * 
	 * @param configs list of configurations to update
	 */
	void batchUpdateConfigs(List<ConfigPo> configs);

	/**
	 * Reset all configurations to their default values
	 */
	void resetAllConfigsToDefaults();

}
