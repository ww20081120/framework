/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.cache.core.redis.lock;

import java.util.Random;

import com.hbasesoft.framework.cache.core.CacheHelper;
import com.hbasesoft.framework.cache.core.redis.AbstractRedisCache;
import com.hbasesoft.framework.common.GlobalConstants;
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

    /** MILLI_NANO_TIME */
    public static final int MILLI_NANO_TIME = 1000000;

    /** LOCKED */
    public static final String LOCKED = "LOCKED";

    /** DEFAULT_IDEL_TIME */
    private static final int DEFAULT_IDEL_TIME = 100;

    /** DEFAULT_MIN_IDEL_TIME */
    private static final int DEFAULT_MIN_IDEL_TIME = 3;

    /** DEFAULT_MAX_IDEL_TIME */
    private static final int DEFAULT_MAX_IDEL_TIME = 30;

    /** RANDOM */
    private static final Random RANDOM = new Random();

    /** lockName */
    private String lockName;

    /** redisCache */
    private AbstractRedisCache redisCache;

    /** lock */
    private boolean lock;

    /**
     * RedisLock
     * 
     * @param ln
     */
    public RedisLock(final String ln) {
        this.lockName = ln;
        this.redisCache = (AbstractRedisCache) CacheHelper.getCache();
    }

    /**
     * Description: 获取锁<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param timeout 超时时间
     * @return <br>
     */
    public boolean lock(final int timeout) {
        return lock(timeout, timeout * 2);
    }

    /**
     * Description: 获取锁<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param timeout
     * @param expireTime
     * @return <br>
     */
    public boolean lock(final int timeout, final int expireTime) {
        LoggerUtil.debug("开始锁住{0},timeout={1},expireTime={2}", lockName, timeout, expireTime);
        long lockTime = System.currentTimeMillis();
        int i = 0;
        try {
            // 在timeout的时间范围内不断轮询锁
            while (System.currentTimeMillis() - lockTime < timeout * GlobalConstants.ONE_SECONDS) {
                // 锁不存在的话，设置锁并设置锁过期时间，即加锁
                if (redisCache.setnx(lockName, LOCKED, expireTime)) {
                    // 锁的情况下锁过期后消失，不会造成永久阻塞
                    this.lock = true;
                    LoggerUtil.debug("获取锁成功{0},共耗时{1}毫秒", lockName, System.currentTimeMillis() - lockTime);
                    return this.lock;
                }

                if (i++ % DEFAULT_IDEL_TIME == 0) {
                    LoggerUtil.debug("等待锁[{0}]的施放, 已锁定{1}毫秒", lockName, i * DEFAULT_MIN_IDEL_TIME * DEFAULT_IDEL_TIME);
                }
                // 短暂休眠，避免可能的活锁
                Thread.sleep(DEFAULT_MIN_IDEL_TIME, RANDOM.nextInt(DEFAULT_MAX_IDEL_TIME));
            }
        }
        catch (Exception e) {
            throw new RuntimeException("locking error", e);
        }
        LoggerUtil.debug("获取锁失败{0},共耗时{1}毫秒", lockName, System.currentTimeMillis() - lockTime);
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

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    @Override
    public String toString() {
        return lockName;
    }
}
