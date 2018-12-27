/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.message.core.event;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.springframework.context.ApplicationContext;

import com.hbasesoft.framework.common.FrameworkException;
import com.hbasesoft.framework.common.StartupListenerAdapter;
import com.hbasesoft.framework.common.utils.CommonUtil;
import com.hbasesoft.framework.common.utils.PropertyHolder;
import com.hbasesoft.framework.common.utils.logger.Logger;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;
import com.hbasesoft.framework.message.core.MessageHelper;
import com.hbasesoft.framework.message.core.MessageQueue;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2017年2月21日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.message.core.event <br>
 */
public class EventHandlerStartupLinstener extends StartupListenerAdapter {

    private static final Logger LOGGER = new Logger("EventHandlerLogger");

    private boolean flag = true;

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param context
     * @throws FrameworkException <br>
     */
    @Override
    public void complete(ApplicationContext context) throws FrameworkException {
        Map<String, EventLinsener> eventLinseners = context.getBeansOfType(EventLinsener.class);
        if (CommonUtil.isNotEmpty(eventLinseners)) {
            for (Entry<String, EventLinsener> entry : eventLinseners.entrySet()) {
                EventLinsener linsener = entry.getValue();
                String[] events = linsener.events();
                if (CommonUtil.isNotEmpty(events)) {
                    // 注册事件
                    for (String channel : linsener.events()) {
                        if (linsener.subscriber()) {
                            addSubscriber(channel, linsener);
                        }
                        else {
                            addConsummer(channel, linsener);
                        }
                    }
                }
            }
        }
    }

    private void addSubscriber(String channel, EventLinsener linsener) {
        MessageHelper.createMessageSubcriberFactory().registSubscriber(channel, linsener);
    }

    private void addConsummer(final String channel, final EventLinsener linsener) {
        MessageQueue queue = MessageHelper.createMessageQueue();

        new Thread(() -> {
            // 建一个线程池
            ThreadPoolExecutor executor = createThreadPoolExecutor();
            BlockingQueue<Runnable> bq = executor.getQueue();

            try {
                while (flag) {
                    try {
                        List<byte[]> datas = queue.pop(3, channel);
                        if (CommonUtil.isNotEmpty(datas)) {
                            String transId = CommonUtil.getTransactionID();
                            LOGGER.info("receive message by thread[{0}], transId[{1}], channel[{2}]",
                                Thread.currentThread().getId(), transId, channel);

                            // 当线程池中的出现阻塞后，暂停从消息队列中进行获取
                            while (bq.remainingCapacity() == 0
                                && executor.getMaximumPoolSize() == executor.getPoolSize()) {
                                LOGGER.info("wait message[{0}] execute, current pool size is [{1}]", channel,
                                    bq.size());
                                Thread.sleep(100);
                            }

                            for (byte[] data : datas) {
                                executor.execute(new Consumer(transId, channel, linsener, data));
                            }
                        }
                        else {
                            LOGGER.info("channel {0} consumer is alived.", channel);
                        }
                    }
                    catch (Exception e) {
                        LoggerUtil.error(e);
                        Thread.sleep(1000);
                    }
                }
            }
            catch (InterruptedException e) {
                LoggerUtil.error(e);
            }
        }).start();

    }

    private ThreadPoolExecutor createThreadPoolExecutor() {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
            PropertyHolder.getIntProperty("message.executor.coreSize", 1), // 设置核心线程数量
            PropertyHolder.getIntProperty("message.executor.maxPoolSize", 10), // 线程池维护线程的最大数量
            PropertyHolder.getIntProperty("message.executor.keepAliveSeconds", 600), TimeUnit.SECONDS, // 允许的空闲时间
            new ArrayBlockingQueue<>(PropertyHolder.getIntProperty("message.executor.queueCapacity", 10))); // 缓存队列
        return executor;
    }

    private static class Consumer implements Runnable {

        private String transId;

        private String channel;

        private EventLinsener linsener;

        private byte[] data;

        public Consumer(String transId, String channel, EventLinsener linsener, byte[] data) {
            this.transId = transId;
            this.channel = channel;
            this.linsener = linsener;
            this.data = data;
        }

        public void run() {
            try {
                LOGGER.info("{0}|{1} before execute event.", transId, channel);
                this.linsener.onMessage(this.channel, data);
                LOGGER.info("{0}|{1} after execute event.", transId, channel);
            }
            catch (Exception e) {
                LOGGER.error(e, "{0}|{1}|FAIL|execute event error.", transId, channel);
            }
        }
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Override
    public void destory() {
        flag = false;
    }

}
