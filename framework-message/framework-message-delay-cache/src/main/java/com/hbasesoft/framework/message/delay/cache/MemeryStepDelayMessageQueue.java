/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.message.delay.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.hbasesoft.framework.cache.core.CacheHelper;
import com.hbasesoft.framework.cache.core.ICache;
import com.hbasesoft.framework.common.GlobalConstants;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;
import com.hbasesoft.framework.message.core.delay.DelayMessage;

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
public class MemeryStepDelayMessageQueue extends AbstractStepDelayMessageQueue implements Runnable, IndexQueue {

    /** holder */
    private Map<String, Long> holder;

    /**
     * @param level
     */
    public MemeryStepDelayMessageQueue(final int level) {
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
    public void add(final DelayMessage delayMessage) {
        addIndex(delayMessage.getMessageId(),
            delayMessage.getCurrentTime() + delayMessage.getSeconds() * GlobalConstants.SECONDS);
        CacheHelper.getCache().put(QueueManager.CACHE_NODE_NAME, delayMessage.getSeconds() * 2,
            delayMessage.getMessageId(), delayMessage);
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
    public DelayMessage remove(final String msgId) {
        Long t = holder.remove(msgId);
        if (t != null) {
            ICache cache = CacheHelper.getCache();
            DelayMessage msg = cache.get(QueueManager.CACHE_NODE_NAME, msgId);
            cache.evict(QueueManager.CACHE_NODE_NAME, msgId);
            return msg;
        }
        return null;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    @Override
    protected Map<String, Long> getAll() {
        return holder;
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
        LoggerUtil.debug("{0}级别的内存启动check功能", this.getLevel());
        while (!Thread.currentThread().isInterrupted()) {
            try {
                this.check();
                Thread.sleep(this.getLevel() * GlobalConstants.SECONDS * 1L);
            }
            catch (Exception e) {
                LoggerUtil.error(e);
                try {
                    Thread.sleep(GlobalConstants.SECONDS * 1L);
                }
                catch (InterruptedException e1) {
                    LoggerUtil.error(e);
                }
            }
        }
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param key <br>
     */
    @Override
    public void removeIndex(final String key) {
        holder.remove(key);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param key
     * @param expireTime <br>
     */
    @Override
    public void addIndex(final String key, final Long expireTime) {
        holder.put(key, expireTime);
    }

}
