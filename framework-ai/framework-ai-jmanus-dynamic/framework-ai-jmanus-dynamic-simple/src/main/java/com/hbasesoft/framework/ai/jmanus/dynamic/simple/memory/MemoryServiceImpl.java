/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.jmanus.dynamic.simple.memory;

import org.springframework.stereotype.Service;

import com.hbasesoft.framework.ai.jmanus.dynamic.memory.service.MemoryService;
import com.hbasesoft.framework.ai.jmanus.dynamic.memory.vo.MemoryVo;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月22日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.ai.jmanus.dynamic.simple.memory <br>
 */
@Service
public class MemoryServiceImpl implements MemoryService {

	/**
	 * Description: <br>
	 * 
	 * @author 王伟<br>
	 * @taskId <br>
	 * @param memoryEntity
	 * @return <br>
	 */
	@Override
	public MemoryVo saveMemory(MemoryVo memoryEntity) {
		LoggerUtil.info("保存内存信息: {0}", memoryEntity);
		return memoryEntity;
	}

}
