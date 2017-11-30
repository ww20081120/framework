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

    private ArrayBlockingQueue<Consumer> consumerQueue = new ArrayBlockingQueue<Consumer>(10000);

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

                    // 初始化事件执行器
                    initExecutor();
                }
            }
        }
    }

    private void addSubscriber(String channel, EventLinsener linsener) {
        MessageHelper.createMessageSubcriberFactory().registSubscriber(channel, linsener);
    }

    private void addConsummer(final String channel, final EventLinsener linsener) {
        int coreReadSize = PropertyHolder.getIntProperty("message.event.coreConsummerSize", 1); // 核心读取线程大小
        MessageQueue queue = MessageHelper.createMessageQueue();
        for (int i = 0; i < coreReadSize; i++) {
            new Thread(() -> {
                try {
                    while (flag) {
                        try {
                            List<byte[]> datas = queue.pop(3, channel);
                            if (CommonUtil.isNotEmpty(datas)) {
                                for (byte[] data : datas) {
                                    String transId = CommonUtil.getTransactionID();
                                    LOGGER.info("receive message by thread[{0}], transId[{1}]",
                                        Thread.currentThread().getId(), transId);
                                    consumerQueue.put(new Consumer(transId, channel, linsener, data));
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
    }

    private void initExecutor() {
        int corePoolSize = PropertyHolder.getIntProperty("message.event.corePoolSize", 20); // 核心线程数
        for (int i = 0; i < corePoolSize; i++) {
            new Thread(() -> {
                long count = 0;
                try {
                    while (flag) {
                        try {
                            Consumer consumer = consumerQueue.poll(100, TimeUnit.MILLISECONDS);
                            if (consumer != null) {
                                consumer.excute();
                            }
                        }
                        catch (Exception e) {
                            LoggerUtil.error(e);
                            Thread.sleep(1000);
                        }

                        if (++count % 100 == 0) {
                            LOGGER.info("thread {0} for executor is alived.", Thread.currentThread().getId());
                        }
                    }
                }
                catch (InterruptedException e1) {
                    LoggerUtil.error(e1);
                }
            }).start();
        }
    }

    private static class Consumer {

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

        public void excute() {
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
