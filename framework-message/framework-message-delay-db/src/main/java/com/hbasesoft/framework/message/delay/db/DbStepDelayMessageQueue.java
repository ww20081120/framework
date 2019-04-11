/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.message.delay.db;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.annotation.Transactional;

import com.hbasesoft.framework.common.utils.logger.LoggerUtil;
import com.hbasesoft.framework.common.utils.security.DataUtil;
import com.hbasesoft.framework.db.core.utils.PagerList;
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
 * @CreateDate 2019年4月10日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.message.delay.db <br>
 */
@Transactional
public class DbStepDelayMessageQueue implements StepDelayMessageQueue {

    private DelaymsgService delaymsgService;

    private int level;

    public DbStepDelayMessageQueue(int level, DelaymsgService delaymsgService) {
        this.delaymsgService = delaymsgService;
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
        delaymsgService.save(entity);
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
        MsgDelaymsgEntity entity = delaymsgService.get(msgId);
        if (entity != null) {
            delaymsgService.delete(msgId);
            return entity.toVo();
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
        int pageIndex = 1;
        int pageSize = 1000;
        PagerList<MsgDelaymsgEntity> entites = null;
        do {
            entites = delaymsgService.queryByTime(new Date(System.currentTimeMillis() + this.getLevel() * 1000),
                pageIndex++, pageSize);
            if (entites.size() > 0 && pageIndex == 2) {
                LoggerUtil.info("{0}级别的队列开始检查，当前队列中延迟消息的条数为{1}", this.getLevel(), entites.getTotalCount());
            }

            for (MsgDelaymsgEntity entity : entites) {
                if (entity.getExpireTime().getTime() <= System.currentTimeMillis()) {
                    LoggerUtil.info("{0}级别的队列中ID为{1}消息已经到期", this.getLevel(), entity.getId());
                    MsgDelaymsgEntity msg = delaymsgService.get(entity.getId());
                    MessageHelper.createMessagePublisher().publish(msg.getChannel(),
                        StringUtils.isNotEmpty(msg.getContent()) ? DataUtil.hexStr2Byte(msg.getContent()) : null);
                }
                else {
                    MessageHelper.getDelayMessageQueue().update(entity.getId(), entity.getExpireTime().getTime(),
                        this.getLevel());
                }
            }
        }
        while (entites != null && entites.hasNextPage());
    }

}
