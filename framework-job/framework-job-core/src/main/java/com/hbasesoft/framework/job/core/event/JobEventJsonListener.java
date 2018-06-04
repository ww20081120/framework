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

	private Logger logger = new Logger(JobEventJsonListener.class);

	@Override
	public void listen(JobExecutionEvent jobExecutionEvent) {
		// 开始执行时间
		long beginTime = System.currentTimeMillis();
		TransManager manager = TransManager.getInstance();
		// id
		String stackId = CommonUtil.getTransactionID();
		manager.push(stackId, beginTime);
		MDC.put("stackId", TransManager.getInstance().getStackId());
		logger.info(JSON.toJSONString(jobExecutionEvent));
		MDC.clear();
	}

	@Override
	public void listen(JobStatusTraceEvent jobStatusTraceEvent) {
		// 开始执行时间
		long beginTime = System.currentTimeMillis();
		TransManager manager = TransManager.getInstance();
		// id
		String stackId = CommonUtil.getTransactionID();
		manager.push(stackId, beginTime);
		MDC.put("stackId", TransManager.getInstance().getStackId());
		logger.info(JSON.toJSONString(jobStatusTraceEvent));
		MDC.clear();
	}

}
