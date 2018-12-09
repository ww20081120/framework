/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.cache.core.redis.lock;

import java.util.Random;

import com.hbasesoft.framework.cache.core.CacheHelper;
import com.hbasesoft.framework.cache.core.redis.AbstractRedisCache;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2017年1月5日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.cache.core.redis.lock <br>
 */
public class RedisLock {

    public static final int MILLI_NANO_TIME = 1000000;

    public static final String LOCKED = "LOCKED";

    private static final Random RANDOM = new Random();

    private String lockName;

    private AbstractRedisCache redisCache;

    private boolean lock;

    public RedisLock(String lockName) {
        this.lockName = lockName;
        this.redisCache = (AbstractRedisCache) CacheHelper.getCache();
    }

    /**
     * Description: 获取锁<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param timeOut
     * @param expireTime
     * @return <br>
     */
    public boolean lock(long timeout, int expireTime) {
        long lockTime = System.currentTimeMillis();
        int i = 0;
        try {
            // 在timeout的时间范围内不断轮询锁
            while (System.currentTimeMillis() - lockTime < timeout) {
                // 锁不存在的话，设置锁并设置锁过期时间，即加锁
                if (redisCache.setnx(lockName, LOCKED, expireTime)) {
                    // 锁的情况下锁过期后消失，不会造成永久阻塞
                    this.lock = true;
                    return this.lock;
                }

                if (i % 100 == 0) {
                    LoggerUtil.debug("等待锁[{0}]的施放, 已锁定{1}毫秒", lockName, i * 300);
                }
                // 短暂休眠，避免可能的活锁
                Thread.sleep(3, RANDOM.nextInt(30));
            }
        }
        catch (Exception e) {
            throw new RuntimeException("locking error", e);
        }
        return false;
    }

    /**
     * Description: 施放锁<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    public void unlock() {
        if (this.lock) {
            redisCache.evict(this.lockName);
        }
    }

    @Override
    public String toString() {
        return lockName;
    }
}
