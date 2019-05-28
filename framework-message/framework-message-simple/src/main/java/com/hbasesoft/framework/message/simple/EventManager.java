/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.message.simple;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections.CollectionUtils;

import com.hbasesoft.framework.common.utils.PropertyHolder;
import com.hbasesoft.framework.common.utils.logger.Logger;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;
import com.hbasesoft.framework.message.core.MessageSubscriber;
import com.hbasesoft.framework.message.core.util.MessageThreadPoolExecutor;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2019年4月18日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.message.simple <br>
 */
public class EventManager {

    private static final Logger LOGGER = new Logger("EventHandlerLogger");

    private Map<String, List<MessageSubscriber>> subscriberHolder;

    private Map<String, ArrayBlockingQueue<byte[]>> queueHolder;

    private ThreadPoolExecutor executor;

    private static EventManager eventManager;

    private EventManager() {
        subscriberHolder = new ConcurrentHashMap<>();
        queueHolder = new ConcurrentHashMap<>();
        executor = new ThreadPoolExecutor(PropertyHolder.getIntProperty("message.scanner.coreSize", 5), // 设置核心线程数量
            PropertyHolder.getIntProperty("message.scanner.maxPoolSize", 20), // 线程池维护线程的最大数量
            PropertyHolder.getIntProperty("message.scanner.keepAliveSeconds", 600), TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(PropertyHolder.getIntProperty("message.scanner.queueCapacity", 10)) // 允许的空闲时间
        ); // 缓存队列
    }

    public static EventManager getInstance() {
        if (eventManager == null) {
            eventManager = new EventManager();
        }
        return eventManager;
    }

    public void regist(String channel, boolean broadcast, MessageSubscriber messageSubscriber) {
        synchronized (subscriberHolder) {
            List<MessageSubscriber> subscribers = subscriberHolder.get(channel);
            if (subscribers == null) {
                subscribers = new ArrayList<>();
                subscriberHolder.put(channel, subscribers);
                executor.execute(new EventScanner(channel, broadcast, getBlockingQueue(channel)));
            }
            subscribers.add(messageSubscriber);
        }
    }

    public void addMessage(String channel, byte[] data) {
        getBlockingQueue(channel).add(data == null ? new byte[0] : data);
    }

    private ArrayBlockingQueue<byte[]> getBlockingQueue(String channel) {
        ArrayBlockingQueue<byte[]> queue = queueHolder.get(channel);
        if (queue == null) {
            queue = new ArrayBlockingQueue<>(PropertyHolder.getIntProperty("message.scanner.msgCapacity." + channel,
                PropertyHolder.getIntProperty("message.scanner.msgCapacity", 100000)));
            queueHolder.put(channel, queue);
        }
        return queue;
    }

    private class EventScanner implements Runnable {

        private String channel;

        private ArrayBlockingQueue<byte[]> queue;

        private boolean broadcast;

        /** 
         *  
         */
        public EventScanner(String channel, boolean broadcast, ArrayBlockingQueue<byte[]> queue) {
            this.channel = channel;
            this.broadcast = broadcast;
            this.queue = queue;
        }

        /**
         * Description: <br>
         * 
         * @author 王伟<br>
         * @taskId <br>
         *         <br>
         */
        @Override
        public void run() {
            try {
                long count = 0;
                Random random = new Random();
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        byte[] data = queue.poll(10l, TimeUnit.MILLISECONDS);
                        if (data != null) {
                            List<MessageSubscriber> subscribers = subscriberHolder.get(channel);
                            if (CollectionUtils.isNotEmpty(subscribers)) {
                                if (broadcast) {
                                    for (MessageSubscriber subscriber : subscribers) {
                                        MessageThreadPoolExecutor.execute(channel, () -> {
                                            subscriber.onMessage(channel, data);
                                        });
                                    }
                                }
                                else {
                                    MessageThreadPoolExecutor.execute(channel, () -> {
                                        subscribers.get(random.nextInt(subscribers.size())).onMessage(channel, data);
                                    });
                                }
                            }
                        }
                    }
                    catch (Exception e) {
                        LoggerUtil.error(e);
                        Thread.sleep(1000L);
                    }
                    if (++count % 500 == 0) {
                        LOGGER.info("scanner for {0} is alived.", channel);
                    }
                }
            }
            catch (Exception e) {
                LoggerUtil.error(e);
            }
        }
    }

}
