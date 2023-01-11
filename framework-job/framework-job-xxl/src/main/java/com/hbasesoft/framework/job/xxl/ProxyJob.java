/**************************************************************************************** 
 Copyright © 2022-2027 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.job.xxl;

import com.hbasesoft.framework.job.core.JobContext;
import com.hbasesoft.framework.job.core.SimpleJob;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.IJobHandler;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2022年12月8日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.job.xxl <br>
 */
public class ProxyJob extends IJobHandler {

    private final SimpleJob job;

    private final String jobName;

    public ProxyJob(String jobName, SimpleJob job) {
        this.job = job;
        this.jobName = jobName;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @throws Exception <br>
     */
    @Override
    public void execute() throws Exception {
        JobContext jobContext = new JobContext(jobName, String.valueOf(XxlJobHelper.getJobId()),
            XxlJobHelper.getShardTotal(), XxlJobHelper.getJobParam(), XxlJobHelper.getShardIndex(), null);
        job.execute(jobContext);
    }

}
