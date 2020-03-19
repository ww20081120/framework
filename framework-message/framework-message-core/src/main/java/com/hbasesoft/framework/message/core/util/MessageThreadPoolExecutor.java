/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.message.core.util;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.hbasesoft.framework.common.utils.PropertyHolder;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;

/**
 * <Description> 消息线程处理工具<br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2018年12月27日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.message.core.util <br>
 */
public final class MessageThreadPoolExecutor {

    /** Number */
    private static final int NUM_10 = 10;

    /** Number */
    private static final int NUM_100 = 100;

    /** Number */
    private static final int NUM_600 = 600;

    /** executor map */
    private static Map<String, ThreadPoolExecutor> executorMap = new HashMap<>();

    /**
     * Description: 通过线程池来处理消息<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param channel
     * @param message
     * @throws InterruptedException <br>
     */
    public static void execute(final String channel, final Runnable message) {
        synchronized (channel) {
            ThreadPoolExecutor executor = executorMap.get(channel);
            if (executor == null) {
                executor = createThreadPoolExecutor();
                executorMap.put(channel, executor);
            }
            BlockingQueue<Runnable> bq = executor.getQueue();

            // 当线程池中的队列出现阻塞后，暂停从redis中进行获取
            try {
                long count = 0;
                while (bq.remainingCapacity() == 0 && executor.getMaximumPoolSize() == executor.getPoolSize()) {
                    if (count++ % NUM_100 == 0) {
                        LoggerUtil.debug("wait message[{0}] execute, current pool size is [{1}]", channel, bq.size());
                    }
                    Thread.sleep(NUM_100);
                }
                executor.execute(message);
            }
            catch (InterruptedException e) {
                LoggerUtil.error(e);
            }

        }
    }

    private static ThreadPoolExecutor createThreadPoolExecutor() {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
            PropertyHolder.getIntProperty("message.executor.coreSize", 1), // 设置核心线程数量
            PropertyHolder.getIntProperty("message.executor.maxPoolSize", NUM_10), // 线程池维护线程的最大数量
            PropertyHolder.getIntProperty("message.executor.keepAliveSeconds", NUM_600), TimeUnit.SECONDS, // 允许的空闲时间
            new ArrayBlockingQueue<>(PropertyHolder.getIntProperty("message.executor.queueCapacity", NUM_10))); // 缓存队列
        return executor;
    }
}
