/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.agent.jpa.memory.service;

import java.util.List;

import com.hbasesoft.framework.ai.agent.jpa.memory.po.MemoryPo4Jpa;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月22日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.agent.jpa.memory.service <br>
 */
public interface MemoryManagerService {

    List<MemoryPo4Jpa> getMemories();

    void deleteMemory(String id);

    MemoryPo4Jpa updateMemory(MemoryPo4Jpa memoryEntity);

    MemoryPo4Jpa singleMemory(String memoryId);

}
