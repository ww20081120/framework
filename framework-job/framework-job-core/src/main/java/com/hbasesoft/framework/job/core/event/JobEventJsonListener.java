package com.hbasesoft.framework.job.core.event;

import org.slf4j.MDC;

import com.alibaba.fastjson.JSON;
import com.dangdang.ddframe.job.event.JobEventListener;
import com.dangdang.ddframe.job.event.type.JobExecutionEvent;
import com.dangdang.ddframe.job.event.type.JobStatusTraceEvent;
import com.hbasesoft.framework.common.utils.CommonUtil;
import com.hbasesoft.framework.common.utils.logger.Logger;
import com.hbasesoft.framework.common.utils.logger.TransManager;

public class JobEventJsonListener extends JobEventJsonIdentity implements JobEventListener {

    /** logger */
    private Logger logger = new Logger(JobEventJsonListener.class);

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param jobExecutionEvent <br>
     */
    @Override
    public void listen(final JobExecutionEvent jobExecutionEvent) {
        // 开始执行时间
        long beginTime = System.currentTimeMillis();
        TransManager manager = TransManager.getInstance();
        // id
        String stackId = CommonUtil.getTransactionID();
        manager.push(stackId, beginTime);
        MDC.put("stackId", TransManager.getInstance().getStackId());
        logger.debug(JSON.toJSONString(jobExecutionEvent));
        MDC.clear();
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param jobStatusTraceEvent <br>
     */
    @Override
    public void listen(final JobStatusTraceEvent jobStatusTraceEvent) {
        // 开始执行时间
        long beginTime = System.currentTimeMillis();
        TransManager manager = TransManager.getInstance();
        // id
        String stackId = CommonUtil.getTransactionID();
        manager.push(stackId, beginTime);
        MDC.put("stackId", TransManager.getInstance().getStackId());
        logger.debug(JSON.toJSONString(jobStatusTraceEvent));
        MDC.clear();
    }

}
