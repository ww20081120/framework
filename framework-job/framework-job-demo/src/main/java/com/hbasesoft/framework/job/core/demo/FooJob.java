/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.job.core.demo;

import com.alibaba.fastjson.JSONObject;
import com.hbasesoft.framework.common.utils.date.DateUtil;
import com.hbasesoft.framework.job.core.JobContext;
import com.hbasesoft.framework.job.core.SimpleJob;
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
@Job(cron = "0/5 * * * * ?")
public class FooJob implements SimpleJob {

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param jobContext <br>
     */
    @Override
    public void execute(final JobContext jobContext) {
        System.out.println(DateUtil.getCurrentTimestamp() + JSONObject.toJSONString(jobContext));
    }

}
