/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.message.delay.cache;

import java.util.Map;

import com.hbasesoft.framework.cache.core.CacheHelper;
import com.hbasesoft.framework.common.GlobalConstants;
import com.hbasesoft.framework.common.utils.PropertyHolder;
import com.hbasesoft.framework.message.core.delay.DelayMessage;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2019年4月10日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.message.delay.cache <br>
 */
public class CacheStepDelayMessageQueue extends AbstractStepDelayMessageQueue implements IndexQueue {

    /** nodeName */
    private String nodeName;

    /**
     * @param level
     */
    public CacheStepDelayMessageQueue(int level) {
        super(level);
        nodeName = new StringBuilder().append(QueueManager.CACHE_NODE_NAME).append(level)
            .append(GlobalConstants.UNDERLINE).append(PropertyHolder.getProjectName()).toString();
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
    public DelayMessage remove(String msgId) {
        DelayMessage delayMessage = CacheHelper.getCache().get(QueueManager.CACHE_NODE_NAME, msgId);
        if (delayMessage != null) {
            removeIndex(msgId);
            CacheHelper.getCache().evict(QueueManager.CACHE_NODE_NAME, msgId);
        }
        return delayMessage;
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
        return CacheHelper.getCache().getNode(nodeName, Long.class);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param key <br>
     */
    @Override
    public void removeIndex(String key) {
        CacheHelper.getCache().evict(nodeName, key);
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
    public void addIndex(String key, Long expireTime) {
        CacheHelper.getCache().put(nodeName, key, expireTime);
    }
}
