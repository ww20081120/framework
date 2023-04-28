/**************************************************************************************** 
 Copyright © 2022-2027 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.job.xxl;

import com.hbasesoft.framework.common.utils.CommonUtil;
import com.hbasesoft.framework.job.core.JobContext;
import com.hbasesoft.framework.job.core.SimpleJob;
import com.xxl.job.core.biz.model.ReturnT;
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

    /** */
    private final SimpleJob job;

    /** */
    private final String jobName;

    /** */
    private final String id;

    /**
     * @Method ProxyJob
     * @param jobName
     * @param job
     * @return
     * @Author 李煜龙
     * @Description TODD
     * @Date 2023/1/29 12:00
     */
    public ProxyJob(final String jobName, final SimpleJob job) {
        this.job = job;
        this.jobName = jobName;
        this.id = CommonUtil.getTransactionID();
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param arg0
     * @return
     * @throws Exception <br>
     */
    @Override
    public ReturnT<String> execute(final String arg0) throws Exception {
        JobContext jobContext = new JobContext(jobName, id, 1, arg0, 1, null);
        job.execute(jobContext);
        return ReturnT.SUCCESS;
    }

}
