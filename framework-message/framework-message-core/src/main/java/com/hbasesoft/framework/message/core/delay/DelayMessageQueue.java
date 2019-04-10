/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.message.core.delay;

import java.util.Collection;

import com.hbasesoft.framework.common.utils.logger.LoggerUtil;

/**
 * <Description> 延迟消息队列<br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2019年4月10日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.message.core <br>
 */
public class DelayMessageQueue {

    private StepDelayMessageQueueLoader stepDelayMessageQueueLoader;

    public DelayMessageQueue(StepDelayMessageQueueLoader stepDelayMessageQueueLoader) {
        this.stepDelayMessageQueueLoader = stepDelayMessageQueueLoader;
    }

    public String add(DelayMessage message) {
        int newLevel = -1;
        int[] levels = stepDelayMessageQueueLoader.getLevels();
        for (int i = 0, l = levels.length; i < l; i++) {
            int level = levels[i];
            if (message.getSeconds() <= level) {
                newLevel = level;
                break;
            }
            if (i == l - 1) {
                newLevel = level;
            }
        }

        if (newLevel >= 0) {
            LoggerUtil.info("ID为{0}的{1}消息寄存到{2}级别的队列中", message.getMessageId(), message.getChannel(), newLevel);
            stepDelayMessageQueueLoader.getDelayMessageQueue(newLevel).add(message);
        }
        return message.getMessageId();
    }

    /**
     * Description: 取消延迟消息 <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param msgId <br>
     */
    public void delete(String msgId) {
        Collection<StepDelayMessageQueue> delayMessageQueues = stepDelayMessageQueueLoader.loadDelayMessageQueues();
        for (StepDelayMessageQueue queue : delayMessageQueues) {
            if (queue.remove(msgId)) {
                LoggerUtil.info("ID为{0}消息从{1}级别的队列中移除", msgId, queue.getLevel());
                break;
            }
        }
    }

    public void update(StepDelayMessageQueue oldQueue, DelayMessage message) {
        int newLevel = -1;
        int[] levels = stepDelayMessageQueueLoader.getLevels();
        for (int i = 0, l = levels.length; i < l; i++) {
            int level = levels[i];
            if (message.getSeconds() <= level) {
                newLevel = level;
                break;
            }
            if (i == l - 1) {
                newLevel = level;
            }
        }

        if (newLevel >= 0 && newLevel != oldQueue.getLevel()) {
            oldQueue.remove(message.getMessageId());
            stepDelayMessageQueueLoader.getDelayMessageQueue(newLevel).add(message);
            LoggerUtil.info("{0}级别的队列中ID为{1}消息被迁移到{2}级别的队列中 ", oldQueue.getLevel(), message.getMessageId(), newLevel);
        }
    }

}
