/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.message.delay.db;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.annotation.Transactional;

import com.hbasesoft.framework.common.GlobalConstants;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;
import com.hbasesoft.framework.common.utils.security.DataUtil;
import com.hbasesoft.framework.db.core.utils.PagerList;
import com.hbasesoft.framework.message.core.MessageHelper;
import com.hbasesoft.framework.message.core.delay.StepDelayMessageQueue;
import com.hbasesoft.framework.message.core.delay.StepDelayMessageQueueLoader;
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
public class DbStepDelayMessageQueueLoader implements StepDelayMessageQueueLoader {

    /** 秒级队列 */
    public static final int SECONDS_QUEUE = 1;

    /** 十秒级别的队列 */
    public static final int TEN_SECONDS_QUEUE = 10;

    /** 分钟队列 */
    public static final int MINUTE_QUEUE = 60;

    /** 10分钟队列 */
    public static final int TEN_MINUTE_QUEUE = 60 * 10;

    /** 半小时队列 */
    public static final int HALF_HOUR_QUEUE = 60 * 30;

    /** 顺序由大到小 */
    private static int[] levels = new int[] {
        HALF_HOUR_QUEUE, TEN_MINUTE_QUEUE, MINUTE_QUEUE, TEN_SECONDS_QUEUE, SECONDS_QUEUE
    };

    /** map */
    private static Map<Integer, StepDelayMessageQueue> map = new HashMap<>();

    /** delaymsgService */
    private static DelaymsgService delaymsgService;

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param ds <br>
     */
    public static void init(final DelaymsgService ds) {

        DbStepDelayMessageQueueLoader.delaymsgService = ds;

        // 初始化存储表
        ds.createTable();

        // 初始化队列
        MemeryStepDelayMessageQueue secondsQueue = new MemeryStepDelayMessageQueue(SECONDS_QUEUE, ds);
        map.put(SECONDS_QUEUE, secondsQueue);

        MemeryStepDelayMessageQueue tenSecondsQueue = new MemeryStepDelayMessageQueue(TEN_SECONDS_QUEUE, ds);
        map.put(TEN_SECONDS_QUEUE, tenSecondsQueue);

        MemeryStepDelayMessageQueue minuteSecondsQueue = new MemeryStepDelayMessageQueue(MINUTE_QUEUE, ds);
        map.put(MINUTE_QUEUE, minuteSecondsQueue);

        MemeryStepDelayMessageQueue tenMinuteSecondsQueue = new MemeryStepDelayMessageQueue(TEN_MINUTE_QUEUE, ds);

        map.put(TEN_MINUTE_QUEUE, tenMinuteSecondsQueue);

        map.put(HALF_HOUR_QUEUE, new DbStepDelayMessageQueue(HALF_HOUR_QUEUE, ds));

        // 启动监控
        new Thread(secondsQueue).start();
        new Thread(tenSecondsQueue).start();
        new Thread(minuteSecondsQueue).start();
        new Thread(tenMinuteSecondsQueue).start();

        // 将数据库中遗留的消息都加载到队列中
        loadMemery(TEN_MINUTE_QUEUE);

    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param level <br>
     */
    public static void loadMemery(final int level) {

        int pageIndex = 1;
        int pageSize = GlobalConstants.DEFAULT_LINES;
        PagerList<MsgDelaymsgEntity> entites = null;
        String shardMsg = MsgDelaymsgEntity.getShardMsg();
        do {
            entites = delaymsgService.queryByTimeAndShard(
                new Date(System.currentTimeMillis() + level * GlobalConstants.SECONDS), shardMsg, pageIndex++,
                pageSize);
            if (entites.size() > 0 && pageIndex == 2) {
                LoggerUtil.debug("{0}级别的队列开始检查，当前队列中延迟消息的条数为{1}", level, entites.getTotalCount());
            }

            for (MsgDelaymsgEntity entity : entites) {
                if (entity.getExpireTime().getTime() <= System.currentTimeMillis()) {
                    LoggerUtil.debug("{0}级别的队列中ID为{1}消息已经到期", level, entity.getId());
                    MsgDelaymsgEntity msg = delaymsgService.delete(entity.getId());
                    MessageHelper.createMessagePublisher().publish(msg.getChannel(),
                        StringUtils.isNotEmpty(msg.getContent()) ? DataUtil.hexStr2Byte(msg.getContent()) : null);
                }
                else {
                    MessageHelper.getDelayMessageQueue().update(entity.getId(), entity.getExpireTime().getTime(),
                        HALF_HOUR_QUEUE);
                }
            }
        }
        while (entites != null && entites.hasNextPage());
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    @Override
    public int[] getLevels() {
        return levels;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param level
     * @return <br>
     */
    @Override
    public StepDelayMessageQueue getDelayMessageQueue(final int level) {
        return map.get(level);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    @Override
    public Collection<StepDelayMessageQueue> loadDelayMessageQueues() {
        return map.values();
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param messageId
     * @param expireTime
     * @param oldLevel
     * @param newLevel <br>
     */
    @Override
    public void changeData(final String messageId, final long expireTime, final int oldLevel, final int newLevel) {
        if (oldLevel != newLevel) {
            StepDelayMessageQueue oldQueue = getDelayMessageQueue(oldLevel);
            if (oldQueue instanceof MemeryStepDelayMessageQueue mm) {
                mm.removeIndex(messageId);
            }
            else if (oldQueue instanceof DbStepDelayMessageQueue) {
                delaymsgService.updateMemeryFlag(messageId);
            }

            StepDelayMessageQueue newQueue = getDelayMessageQueue(newLevel);
            if (newQueue instanceof MemeryStepDelayMessageQueue mm) {
                mm.addIndex(messageId, expireTime);
            }

        }
    }

    public static Map<Integer, StepDelayMessageQueue> getMap() {
        return map;
    }

}
