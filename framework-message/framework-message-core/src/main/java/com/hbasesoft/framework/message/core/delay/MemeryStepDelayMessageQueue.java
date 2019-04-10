/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.message.core.delay;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.hbasesoft.framework.common.utils.logger.LoggerUtil;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2019年4月10日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.message.core.delay <br>
 */
public class MemeryStepDelayMessageQueue extends AbstractStepDelayMessageQueue implements Runnable {

    private Map<String, DelayMessage> holder;

    public MemeryStepDelayMessageQueue(int level) {
        super(level);
        holder = new ConcurrentHashMap<>();
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param delayMessage <br>
     */
    @Override
    public void add(DelayMessage delayMessage) {
        holder.put(delayMessage.getMessageId(), delayMessage);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param msgId
     * @return <br>
     */
    @Override
    public boolean remove(String msgId) {
        return holder.remove(msgId) != null;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    @Override
    protected Collection<DelayMessage> getAll() {
        return holder.values();
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
        LoggerUtil.info("{0}级别的内存启动check功能");
        while (!Thread.currentThread().isInterrupted()) {
            try {
                this.check();
                Thread.sleep(this.getLevel() * 1000L);
            }
            catch (Exception e) {
                LoggerUtil.error(e);
                try {
                    Thread.sleep(1000L);
                }
                catch (InterruptedException e1) {
                    LoggerUtil.error(e);
                }
            }
        }
    }

}
