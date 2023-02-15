/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.cache.core.lock;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import com.hbasesoft.framework.common.GlobalConstants;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2020年12月3日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.cache.core.lock <br>
 */
public class DefaultLock implements Lock {

    /** */
    private static final int DEFAULT_IDEL_TIME = 100;

    /** DEFAULT_MIN_IDEL_TIME */
    private static final int DEFAULT_MIN_IDEL_TIME = 3;

    /** DEFAULT_MAX_IDEL_TIME */
    private static final int DEFAULT_MAX_IDEL_TIME = 30;

    /** RANDOM */
    private static final Random RANDOM = new Random();

    /** */
    private ReentrantLock reentrantLock = new ReentrantLock();

    /** */
    private String lockName;

    /**
     * @Method DefaultLock
     * @param lockName
     * @return
     * @Author 李煜龙
     * @Description TODD
     * @Date 2023/1/29 10:53
    */
    public DefaultLock(final String lockName) {
        this.lockName = lockName;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param timeout <br>
     */
    @Override
    public void lock(final int timeout) {

        LoggerUtil.debug("开始锁住{0},timeout={1},expireTime={2}", lockName, timeout);
        long lockTime = System.currentTimeMillis();
        int i = 0;
        try {
            // 在timeout的时间范围内不断轮询锁
            while (System.currentTimeMillis() - lockTime < timeout * GlobalConstants.SECONDS) {
                // 锁不存在的话，设置锁并设置锁过期时间，即加锁
                if (tryLock(timeout)) {
                    // 锁的情况下锁过期后消失，不会造成永久阻塞
                    LoggerUtil.debug("获取锁成功{0},共耗时{1}毫秒", lockName, System.currentTimeMillis() - lockTime);
                    return;
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

    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param timeout
     * @return <br>
     */
    @Override
    public boolean tryLock(final int timeout) {
        try {
            return reentrantLock.tryLock(timeout, TimeUnit.SECONDS);
        }
        catch (InterruptedException e) {
            throw new RuntimeException("locking error", e);
        }
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Override
    public void unlock() {
        if (reentrantLock.isLocked()) {
            reentrantLock.unlock();
        }
    }
}
