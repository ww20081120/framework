/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.message.delay.cache;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import com.hbasesoft.framework.cache.core.CacheHelper;
import com.hbasesoft.framework.common.GlobalConstants;
import com.hbasesoft.framework.common.utils.PropertyHolder;
import com.hbasesoft.framework.message.core.delay.AbstractStepDelayMessageQueue;
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
public class CacheStepDelayMessageQueue extends AbstractStepDelayMessageQueue {

    private String nodeName;

    public CacheStepDelayMessageQueue(int level) {
        super(level);
        nodeName = new StringBuilder().append("t_msg_delaymsg_").append(level).append(GlobalConstants.UNDERLINE)
            .append(PropertyHolder.getProjectName()).toString();
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
        CacheHelper.getCache().put(nodeName, delayMessage.getMessageId(), delayMessage);
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
        DelayMessage delayMessage = CacheHelper.getCache().get(nodeName, msgId);
        if (delayMessage != null) {
            CacheHelper.getCache().evict(nodeName, msgId);
            return true;
        }
        return false;
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
        Map<String, DelayMessage> maps = CacheHelper.getCache().getNode(nodeName, DelayMessage.class);
        return maps == null ? new HashSet<>() : maps.values();
    }
}
