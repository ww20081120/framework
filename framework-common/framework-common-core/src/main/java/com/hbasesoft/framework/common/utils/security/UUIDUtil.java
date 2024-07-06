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
    /** */
    private static boolean isThreadlocalrandomAvailable = false;

    /** */
    private static Random random;

    /** */
    private static final long LEAST_SIG_BITS;

    /** */
    private static final ReentrantLock LOCK = new ReentrantLock();

    /** */
    private static long lastTime;

    /** */
    private static final int NUM_8 = 8;

    /** */
    private static final int NUM_16 = 16;

    /** */
    private static final int NUM_32 = 32;

    /** */
    private static final int NUM_48 = 48;

    /** */
    private static final int NUM = 0xff;

    /** */
    private static final int NUM_0 = 0x1000;

    /** */
    private static final int NUMF_0 = 0x0FFF;

    /** */
    private static final int NUM_10000 = 10000;

    /** */
    private static final long NUMS = 0x01B21DD213814000L;

    /** */
    private static final long NUMSL = 0xFFFF00000000L;

    static {
        try {
            isThreadlocalrandomAvailable = null != UUIDUtil.class.getClassLoader()
                .loadClass("java.util.concurrent.ThreadLocalRandom");
        }
        catch (ClassNotFoundException e) {
            LoggerUtil.error(e);
        }

        byte[] seed = new SecureRandom().generateSeed(NUM_8);
        LEAST_SIG_BITS = new BigInteger(seed).longValue();
        if (!isThreadlocalrandomAvailable) {
            random = new Random(LEAST_SIG_BITS);
        }
    }

    /**
     * Create a new random UUID.
     *
     * @return the new UUID
     */
    public static UUID random() {
        byte[] randomBytes = new byte[NUM_16];
        if (isThreadlocalrandomAvailable) {
            java.util.concurrent.ThreadLocalRandom.current().nextBytes(randomBytes);
        }
        else {
            random.nextBytes(randomBytes);
        }

        long mostSigBits = 0;
        for (int i = 0; i < NUM_8; i++) {
            mostSigBits = (mostSigBits << NUM_8) | (randomBytes[i] & NUM);
        }
        long leastSigBits = 0;
        for (int i = NUM_8; i < NUM_16; i++) {
            leastSigBits = (leastSigBits << NUM_8) | (randomBytes[i] & NUM);
        }

        return new UUID(mostSigBits, leastSigBits);
    }

    /**
     * Create a new time-based UUID.
     *
     * @return the new UUID
     */
    public static UUID create() {
        return create(System.currentTimeMillis());
    }

    /**
     * Create a new time-based UUID.
     * @param currentTimeMillis
     * @return the new UUID
     */
    public static UUID create(final long currentTimeMillis) {
        long timeMillis = (currentTimeMillis * NUM_10000) + NUMS;

        LOCK.lock();
        try {
            if (timeMillis > lastTime) {
                lastTime = timeMillis;
            }
            else {
                timeMillis = ++lastTime;
            }
        }
        finally {
            LOCK.unlock();
        }

        // time low
        long mostSigBits = timeMillis << NUM_32;

        // time mid
        mostSigBits |= (timeMillis & NUMSL) >> NUM_16;

        // time hi and version
        mostSigBits |= NUM_0 | ((timeMillis >> NUM_48) & NUMF_0); // version 1

        return new UUID(mostSigBits, LEAST_SIG_BITS);
    }

}
