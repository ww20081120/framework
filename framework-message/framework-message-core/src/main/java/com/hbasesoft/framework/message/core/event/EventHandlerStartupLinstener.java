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
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.context.ApplicationContext;

import com.hbasesoft.framework.common.FrameworkException;
import com.hbasesoft.framework.common.StartupListener;
import com.hbasesoft.framework.common.utils.CommonUtil;
import com.hbasesoft.framework.common.utils.PropertyHolder;
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
public class EventHandlerStartupLinstener implements StartupListener {

    private ThreadPoolExecutor executor;

    private boolean flag = true;

    public EventHandlerStartupLinstener() {
        int corePoolSize = PropertyHolder.getIntProperty("message.event.corePoolSize", 20); // 核心线程数
        int maximumPoolSize = PropertyHolder.getIntProperty("message.event.maximumPoolSize", 100); // 最大线程数
        long keepAliveTime = PropertyHolder.getIntProperty("message.event.keepAliveTime", 600);
        int maxConsummer = PropertyHolder.getIntProperty("message.event.maxConsummer", 10000);

        executor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS,
            new ArrayBlockingQueue<Runnable>(maxConsummer));
    }

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
        if (MapUtils.isNotEmpty(eventLinseners)) {
            for (Entry<String, EventLinsener> entry : eventLinseners.entrySet()) {
                EventLinsener linsener = entry.getValue();
                String[] events = linsener.events();
                if (CommonUtil.isNotEmpty(events)) {
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
        int coreReadSize = PropertyHolder.getIntProperty("message.event.coreConsummerSize", 3); // 核心读取线程大小
        MessageQueue queue = MessageHelper.createMessageQueue();
        for (int i = 0; i < coreReadSize; i++) {
            new Thread(() -> {
                try {
                    while (flag) {
                        try {
                            List<byte[]> datas = queue.pop(3, channel);
                            if (CollectionUtils.isNotEmpty(datas)) {
                                for (byte[] data : datas) {
                                    LoggerUtil.info("receive message by thread[{0}]", Thread.currentThread().getId());

                                    executor.execute(new Runnable() {

                                        @Override
                                        public void run() {
                                            try {
                                                linsener.onMessage(channel, data);
                                            }
                                            catch (Exception e) {
                                                LoggerUtil.error(e);
                                            }
                                        }
                                    });
                                }
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
