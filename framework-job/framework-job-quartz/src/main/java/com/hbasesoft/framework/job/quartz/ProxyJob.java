/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.job.quartz;

import org.apache.commons.lang3.StringUtils;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.hbasesoft.framework.common.GlobalConstants;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;
import com.hbasesoft.framework.job.core.JobContext;
import com.hbasesoft.framework.job.core.SimpleJob;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2020年12月13日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.job.quartz <br>
 */
public class ProxyJob implements Job {

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param context
     * @throws JobExecutionException <br>
     */
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap dataMap = context.getJobDetail().getJobDataMap();
        if (dataMap != null) {
            String className = dataMap.getString(JobConstants.JOB_INSTANCE_CLASS);
            if (StringUtils.isNotEmpty(className)) {
                try {
                    SimpleJob instance = (SimpleJob) Class.forName(className).newInstance();
                    String shardingItemParameters = dataMap.getString(JobConstants.JOB_SHARDING_PARAM);
                    String[] params = StringUtils.isNotEmpty(shardingItemParameters)
                        ? StringUtils.split(shardingItemParameters, GlobalConstants.SPLITOR)
                        : new String[] {
                            GlobalConstants.BLANK
                        };
                    String jobName = dataMap.getString(JobConstants.JOB_NAME);

                    for (int i = 0, len = params.length; i < len; i++) {
                        try {
                            instance.execute(new JobContext(jobName, context.getFireInstanceId(), params.length,
                                params[i], i, null));
                        }
                        catch (Exception e) {
                            LoggerUtil.error(e);
                        }
                    }
                }
                catch (Exception e) {
                    LoggerUtil.error(e);
                    throw new JobExecutionException(e);
                }
            }
        }
    }

}
