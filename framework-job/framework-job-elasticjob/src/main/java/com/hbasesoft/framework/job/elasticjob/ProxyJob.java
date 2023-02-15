/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.job.elasticjob;

import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.simple.job.SimpleJob;

import com.hbasesoft.framework.job.core.JobContext;

import lombok.RequiredArgsConstructor;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2020年12月13日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.job.elasticjob <br>
 */
@RequiredArgsConstructor
public class ProxyJob implements SimpleJob {

    /** */
    private final com.hbasesoft.framework.job.core.SimpleJob simpleJob;

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param shardingContext <br>
     */
    @Override
    public void execute(final ShardingContext shardingContext) {
        simpleJob.execute(new JobContext(shardingContext.getJobName(), shardingContext.getTaskId(),
            shardingContext.getShardingTotalCount(), shardingContext.getJobParameter(),
            shardingContext.getShardingItem(), shardingContext.getShardingParameter()));
    }

}
