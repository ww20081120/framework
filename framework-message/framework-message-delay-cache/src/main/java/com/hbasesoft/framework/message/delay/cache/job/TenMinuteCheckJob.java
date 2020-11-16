/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.message.delay.cache.job;

import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.simple.job.SimpleJob;

import com.hbasesoft.framework.job.core.annotation.Job;
import com.hbasesoft.framework.message.delay.cache.QueueManager;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2019年4月10日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.message.delay.cache.job <br>
 */
@Job(cron = "0 0/10 * * * ?")
public class TenMinuteCheckJob implements SimpleJob {

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param shardingContext <br>
     */
    @Override
    public void execute(final ShardingContext shardingContext) {
        QueueManager.getMap().get(QueueManager.TEN_MINUTE_QUEUE).check();
    }

}
