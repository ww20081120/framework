/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.common.utils.security;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

import com.hbasesoft.framework.common.utils.logger.LoggerUtil;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2020年12月31日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.common.utils.security <br>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UUIDUtil {
    private static boolean IS_THREADLOCALRANDOM_AVAILABLE = false;

    private static Random random;

    private static final long leastSigBits;

    private static final ReentrantLock lock = new ReentrantLock();

    private static long lastTime;

    static {
        try {
            IS_THREADLOCALRANDOM_AVAILABLE = null != UUIDUtil.class.getClassLoader()
                .loadClass("java.util.concurrent.ThreadLocalRandom");
        }
        catch (ClassNotFoundException e) {
            LoggerUtil.error(e);
        }

        byte[] seed = new SecureRandom().generateSeed(8);
        leastSigBits = new BigInteger(seed).longValue();
        if (!IS_THREADLOCALRANDOM_AVAILABLE) {
            random = new Random(leastSigBits);
        }
    }

    /**
     * Create a new random UUID.
     *
     * @return the new UUID
     */
    public static UUID random() {
        byte[] randomBytes = new byte[16];
        if (IS_THREADLOCALRANDOM_AVAILABLE) {
            java.util.concurrent.ThreadLocalRandom.current().nextBytes(randomBytes);
        }
        else {
            random.nextBytes(randomBytes);
        }

        long mostSigBits = 0;
        for (int i = 0; i < 8; i++) {
            mostSigBits = (mostSigBits << 8) | (randomBytes[i] & 0xff);
        }
        long leastSigBits = 0;
        for (int i = 8; i < 16; i++) {
            leastSigBits = (leastSigBits << 8) | (randomBytes[i] & 0xff);
        }

        return new UUID(mostSigBits, leastSigBits);
    }

    /**
     * Create a new time-based UUID.
     *
     * @return the new UUID
     */
    public static UUID create() {
        long timeMillis = (System.currentTimeMillis() * 10000) + 0x01B21DD213814000L;

        lock.lock();
        try {
            if (timeMillis > lastTime) {
                lastTime = timeMillis;
            }
            else {
                timeMillis = ++lastTime;
            }
        }
        finally {
            lock.unlock();
        }

        // time low
        long mostSigBits = timeMillis << 32;

        // time mid
        mostSigBits |= (timeMillis & 0xFFFF00000000L) >> 16;

        // time hi and version
        mostSigBits |= 0x1000 | ((timeMillis >> 48) & 0x0FFF); // version 1

        return new UUID(mostSigBits, leastSigBits);
    }

}
