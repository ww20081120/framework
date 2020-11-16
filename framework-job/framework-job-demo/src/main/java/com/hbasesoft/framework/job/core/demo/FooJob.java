/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.job.core.demo;

import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.simple.job.SimpleJob;

import com.alibaba.fastjson.JSONObject;
import com.hbasesoft.framework.common.utils.date.DateUtil;
import com.hbasesoft.framework.job.core.annotation.Job;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2018年4月14日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.job.core.demo <br>
 */
@Job(cron = "0/5 * * * * ?", enable = "${job.FooJob}")
public class FooJob implements SimpleJob {

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param shardingContext <br>
     */
    @Override
    public void execute(final ShardingContext shardingContext) {
        System.out.println(DateUtil.getCurrentTimestamp() + JSONObject.toJSONString(shardingContext));
    }

}
