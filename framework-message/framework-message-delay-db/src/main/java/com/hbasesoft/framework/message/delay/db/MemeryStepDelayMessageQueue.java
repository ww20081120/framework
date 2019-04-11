/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.message.delay.db;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.collections.MapUtils;
import org.springframework.transaction.annotation.Transactional;

import com.hbasesoft.framework.common.GlobalConstants;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;
import com.hbasesoft.framework.message.core.MessageHelper;
import com.hbasesoft.framework.message.core.delay.DelayMessage;
import com.hbasesoft.framework.message.core.delay.StepDelayMessageQueue;
import com.hbasesoft.framework.message.delay.db.entity.MsgDelaymsgEntity;
import com.hbasesoft.framework.message.delay.db.service.DelaymsgService;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2019年4月11日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.message.delay.db <br>
 */
@Transactional
public class MemeryStepDelayMessageQueue implements StepDelayMessageQueue, IndexQueue, Runnable {

    private DelaymsgService delaymsgService;

    private Map<String, Long> holder;

    private int level;

    public MemeryStepDelayMessageQueue(int level, DelaymsgService delaymsgService) {
        this.level = level;
        this.delaymsgService = delaymsgService;
        holder = new ConcurrentHashMap<>();
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
        return level;
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
        MsgDelaymsgEntity entity = new MsgDelaymsgEntity(delayMessage);
        entity.setMemeryFlag(GlobalConstants.YES);
        delaymsgService.save(entity);
        addIndex(delayMessage.getMessageId(), delayMessage.getCurrentTime() + this.level * 1000);
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
    public DelayMessage get(String msgId) {
        MsgDelaymsgEntity entity = delaymsgService.get(msgId);
        return entity == null ? null : entity.toVo();
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
        if (holder.remove(msgId) != null) {
            MsgDelaymsgEntity entity = delaymsgService.get(msgId);
            if (entity != null) {
                delaymsgService.delete(msgId);
                return entity.toVo();
            }
        }
        return null;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Override
    public synchronized void check() {
        if (MapUtils.isNotEmpty(holder)) {
            LoggerUtil.info("{0}级别的队列开始检查，当前队列中延迟消息的条数为{1}", this.getLevel(), holder.size());
            for (Entry<String, Long> entry : holder.entrySet()) {
                if (entry.getValue() <= System.currentTimeMillis()) {
                    LoggerUtil.info("{0}级别的队列中ID为{1}消息已经到期", this.getLevel(), entry.getKey());
                    DelayMessage delayMessage = this.remove(entry.getKey());
                    MessageHelper.createMessagePublisher().publish(delayMessage.getChannel(), delayMessage.getData());
                }
                else {
                    MessageHelper.getDelayMessageQueue().update(entry.getKey(), entry.getValue(), this.getLevel());
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
    public void removeIndex(String key) {
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
    public void addIndex(String key, Long expireTime) {
        holder.put(key, expireTime);
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
        LoggerUtil.info("{0}级别的内存启动check功能", this.getLevel());
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
