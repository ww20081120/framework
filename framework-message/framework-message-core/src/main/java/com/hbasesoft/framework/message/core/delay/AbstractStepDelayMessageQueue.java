/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.message.core.delay;

import java.util.Collection;

import org.apache.commons.collections.CollectionUtils;

import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.utils.Assert;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;
import com.hbasesoft.framework.message.core.MessageHelper;

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

    private int level;

    public AbstractStepDelayMessageQueue(int level) {
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

    public void check() {
        Collection<DelayMessage> delayMessages = getAll();
        if (CollectionUtils.isNotEmpty(delayMessages)) {
            LoggerUtil.info("{0}级别的队列开始检查，当前队列中延迟消息的条数为{1}", this.getLevel(), delayMessages.size());
            for (DelayMessage delayMessage : delayMessages) {
                if (delayMessage.getCurrentTime() + delayMessage.getSeconds() * 1000 >= System.currentTimeMillis()) {
                    LoggerUtil.info("{0}级别的队列中{1}消息已经到期，消息ID为{2}", this.getLevel(), delayMessage.getChannel(),
                        delayMessage.getMessageId());
                    this.remove(delayMessage.getMessageId());
                    MessageHelper.createMessagePublisher().publish(delayMessage.getChannel(), delayMessage.getData());
                }
                else {
                    MessageHelper.getDelayMessageQueue().update(this, delayMessage);
                }
            }
        }
    }

    protected abstract Collection<DelayMessage> getAll();
}
