/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.jmanus.config;

import java.util.List;

import com.hbasesoft.framework.ai.jmanus.config.model.vo.ConfigVo;

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
	String getConfigValue(String configPath);

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
	List<ConfigVo> getConfigsByGroup(String groupName);

	/**
	 * Batch update configurations
	 * 
	 * @param configs list of configurations to update
	 */
	void batchUpdateConfigs(List<ConfigVo> configs);

	/**
	 * Reset all configurations to their default values
	 */
	void resetAllConfigsToDefaults();

}
