package com.hbasesoft.framework.job.core.event;

import com.dangdang.ddframe.job.event.JobEventIdentity;

/**
 * <Description> <br>
 * 
 * @author 大刘杰<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2018年6月3日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.job.core.event <br>
 */
public class JobEventJsonIdentity implements JobEventIdentity {

    @Override
    public String getIdentity() {
        return "TransLogger";
    }

}
