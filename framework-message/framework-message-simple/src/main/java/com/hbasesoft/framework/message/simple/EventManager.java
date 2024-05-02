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
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections.CollectionUtils;

import com.hbasesoft.framework.common.utils.PropertyHolder;
import com.hbasesoft.framework.common.utils.logger.Logger;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;
import com.hbasesoft.framework.common.utils.thread.ThreadUtil;
import com.hbasesoft.framework.message.core.MessageSubscriber;

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
public final class EventManager {

    /** number */
    private static final long NUM_10L = 10L;

    /** number */
    private static final int NUM_500 = 500;

    /** number */
    private static final long NUM_1000L = 1000L;

    /** number */
    private static final int NUM_100000 = 100000;

    /** logger */
    private static final Logger LOGGER = new Logger("EventHandlerLogger");

    /** subscriberHolder */
    private Map<String, List<MessageSubscriber>> subscriberHolder;

    /** queueholder */
    private Map<String, ArrayBlockingQueue<byte[]>> queueHolder;

    /** eventManager */
    private static EventManager eventManager;

    /**
     * 
     */
    private EventManager() {
        subscriberHolder = new ConcurrentHashMap<>();
        queueHolder = new ConcurrentHashMap<>();
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public static EventManager getInstance() {
        if (eventManager == null) {
            eventManager = new EventManager();
        }
        return eventManager;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param channel
     * @param broadcast
     * @param messageSubscriber <br>
     */
    public void regist(final String channel, final boolean broadcast, final MessageSubscriber messageSubscriber) {
        synchronized (subscriberHolder) {
            List<MessageSubscriber> subscribers = subscriberHolder.get(channel);
            if (subscribers == null) {
                subscribers = new ArrayList<>();
                subscriberHolder.put(channel, subscribers);
                Thread thread = new Thread(new EventScanner(channel, broadcast, getBlockingQueue(channel)));
                thread.setName("Scanner_" + channel + thread.threadId());
                thread.setDaemon(true);
                thread.start();
            }
            subscribers.add(messageSubscriber);
        }
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param channel
     * @param data <br>
     */
    public void addMessage(final String channel, final byte[] data) {
        getBlockingQueue(channel).add(data == null ? new byte[0] : data);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param channel
     * @return <br>
     */
    private ArrayBlockingQueue<byte[]> getBlockingQueue(final String channel) {
        ArrayBlockingQueue<byte[]> queue = queueHolder.get(channel);
        if (queue == null) {
            queue = new ArrayBlockingQueue<>(PropertyHolder.getIntProperty("message.scanner.msgCapacity." + channel,
                PropertyHolder.getIntProperty("message.scanner.msgCapacity", NUM_100000)));
            queueHolder.put(channel, queue);
        }
        return queue;
    }

    /**
     * <Description> <br>
     * 
     * @author 王伟<br>
     * @version 1.0<br>
     * @taskId <br>
     * @CreateDate Mar 2, 2020 <br>
     * @since V1.0<br>
     * @see com.hbasesoft.framework.message.simple <br>
     */
    private class EventScanner implements Runnable {

        /** channel */
        private String channel;

        /** queue */
        private ArrayBlockingQueue<byte[]> queue;

        /** broadcast */
        private boolean broadcast;

        /**
         * @param channel
         * @param broadcast
         * @param queue
         */
        EventScanner(final String channel, final boolean broadcast, final ArrayBlockingQueue<byte[]> queue) {
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
                        byte[] data = queue.poll(NUM_10L, TimeUnit.MILLISECONDS);
                        if (data != null) {
                            List<MessageSubscriber> subscribers = subscriberHolder.get(channel);
                            if (CollectionUtils.isNotEmpty(subscribers)) {
                                if (broadcast) {
                                    for (MessageSubscriber subscriber : subscribers) {
                                        ThreadUtil.execute(channel, () -> {
                                            subscriber.onMessage(channel, data);
                                        });
                                    }
                                }
                                else {
                                    ThreadUtil.execute(channel, () -> {
                                        subscribers.get(random.nextInt(subscribers.size())).onMessage(channel, data);
                                    });
                                }
                            }
                        }
                    }
                    catch (Exception e) {
                        LoggerUtil.error(e);
                        Thread.sleep(NUM_1000L);
                    }
                    if (++count % NUM_500 == 0) {
                        LOGGER.debug("scanner for {0} is alived.", channel);
                    }
                }
            }
            catch (Exception e) {
                LoggerUtil.error(e);
            }
        }
    }

}
