/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.message.delay.db.job;

import com.hbasesoft.framework.job.core.JobContext;
import com.hbasesoft.framework.job.core.SimpleJob;
import com.hbasesoft.framework.job.core.annotation.Job;
import com.hbasesoft.framework.message.delay.db.DbStepDelayMessageQueueLoader;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2019年4月10日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.message.delay.db <br>
 */
@Job(cron = "0 0/30 * * * ?")
public class HalfHourCheckJob implements SimpleJob {

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param jobContext <br>
     */
    @Override
    public void execute(final JobContext jobContext) {
        DbStepDelayMessageQueueLoader.getMap().get(DbStepDelayMessageQueueLoader.TEN_MINUTE_QUEUE).check();
    }

}
