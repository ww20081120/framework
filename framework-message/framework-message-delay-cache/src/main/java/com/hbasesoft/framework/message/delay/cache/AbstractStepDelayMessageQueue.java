/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.message.delay.cache;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.MapUtils;

import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.utils.Assert;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;
import com.hbasesoft.framework.message.core.MessageHelper;
import com.hbasesoft.framework.message.core.delay.DelayMessage;
import com.hbasesoft.framework.message.core.delay.StepDelayMessageQueue;

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
public abstract class AbstractStepDelayMessageQueue implements StepDelayMessageQueue {

    /** level */
    private int level;

    /**
     * @param level
     */
    public AbstractStepDelayMessageQueue(final int level) {
        Assert.isTrue(level >= 1, ErrorCodeDef.DELAY_TIME_TOO_SHORT, level);
        this.level = level;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    @Override
    public int getLevel() {
        return this.level;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    public synchronized void check() {
        Map<String, Long> delayMessages = getAll();
        if (MapUtils.isNotEmpty(delayMessages)) {
            LoggerUtil.debug("{0}级别的队列开始检查，当前队列中延迟消息的条数为{1}", this.getLevel(), delayMessages.size());
            for (Entry<String, Long> entry : delayMessages.entrySet()) {
                if (entry.getValue() <= System.currentTimeMillis()) {
                    LoggerUtil.debug("{0}级别的队列中ID为{1}消息已经到期", this.getLevel(), entry.getKey());
                    DelayMessage delayMessage = this.remove(entry.getKey());
                    MessageHelper.createMessagePublisher().publish(delayMessage.getChannel(), delayMessage.getData());
                }
                else {
                    MessageHelper.getDelayMessageQueue().update(entry.getKey(), entry.getValue(), this.getLevel());
                }
            }
        }
    }

    protected abstract Map<String, Long> getAll();
}
