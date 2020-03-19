/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.message.delay.cache;

import java.util.HashMap;
import java.util.Map;

import com.hbasesoft.framework.message.core.delay.StepDelayMessageQueue;

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
public final class QueueManager {

    /** cache node name */
    public static final String CACHE_NODE_NAME = "t_msg_delaymsg";

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

    /** 一小时队列 */
    public static final int HOUR_QUEUE = 60 * 60;

    /** 以天为单位的队列 */
    public static final int DAY_QUEUE = 60 * 60 * 24;

    /** 顺序由大到小 */
    private static int[] levels = new int[] {
        DAY_QUEUE, HOUR_QUEUE, HALF_HOUR_QUEUE, TEN_MINUTE_QUEUE, MINUTE_QUEUE, TEN_SECONDS_QUEUE, SECONDS_QUEUE
    };

    /** map */
    private static Map<Integer, StepDelayMessageQueue> map = new HashMap<>();

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    public static void init() {

        // 初始化队列
        MemeryStepDelayMessageQueue secondsQueue = new MemeryStepDelayMessageQueue(SECONDS_QUEUE);
        map.put(SECONDS_QUEUE, secondsQueue);

        MemeryStepDelayMessageQueue tenSecondsQueue = new MemeryStepDelayMessageQueue(TEN_SECONDS_QUEUE);
        map.put(TEN_SECONDS_QUEUE, tenSecondsQueue);

        map.put(MINUTE_QUEUE, new CacheStepDelayMessageQueue(MINUTE_QUEUE));
        map.put(TEN_MINUTE_QUEUE, new CacheStepDelayMessageQueue(TEN_MINUTE_QUEUE));
        map.put(HALF_HOUR_QUEUE, new CacheStepDelayMessageQueue(HALF_HOUR_QUEUE));
        map.put(HOUR_QUEUE, new CacheStepDelayMessageQueue(HOUR_QUEUE));
        map.put(DAY_QUEUE, new CacheStepDelayMessageQueue(DAY_QUEUE));

        // 启动监控
        new Thread(secondsQueue).start();
        new Thread(tenSecondsQueue).start();
    }

    public static int[] getLevels() {
        return levels;
    }

    public static Map<Integer, StepDelayMessageQueue> getMap() {
        return map;
    }
}
