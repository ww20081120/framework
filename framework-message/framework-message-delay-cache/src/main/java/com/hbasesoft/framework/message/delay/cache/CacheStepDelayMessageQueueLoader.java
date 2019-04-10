/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.message.delay.cache;

import java.util.Collection;

import com.hbasesoft.framework.message.core.delay.StepDelayMessageQueue;
import com.hbasesoft.framework.message.core.delay.StepDelayMessageQueueLoader;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2019年4月10日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.message.delay.cache <br>
 */
public class CacheStepDelayMessageQueueLoader implements StepDelayMessageQueueLoader {

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    @Override
    public int[] getLevels() {
        return QueueManager.getLevels();
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param level
     * @return <br>
     */
    @Override
    public StepDelayMessageQueue getDelayMessageQueue(int level) {
        return QueueManager.getMap().get(level);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    @Override
    public Collection<StepDelayMessageQueue> loadDelayMessageQueues() {
        return QueueManager.getMap().values();
    }

}
