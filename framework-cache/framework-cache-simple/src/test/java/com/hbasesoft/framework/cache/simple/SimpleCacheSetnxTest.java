/****************************************************************************************
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.cache.simple;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

/**
 * {@link SimpleCache#setnx(String, String, int)} 原子 set-if-absent 原语的行为契约测试。
 *
 * <p>每个用例使用独立的 {@link SimpleCache} 实例，避免分桶过期参数在用例间串扰。</p>
 *
 * @author 王伟
 * @version 1.0
 * @since 4.4.2
 */
class SimpleCacheSetnxTest {

    /** 并发竞争线程数。 */
    private static final int THREADS = 16;

    /** 常规占位过期秒数。 */
    private static final int TTL_SECONDS = 60;

    /**
     * 同一 key 首次 setnx 占位成功，窗口内第二次返回 false。
     */
    @Test
    @Timeout(10)
    void shouldSetnxSucceedOncePerKey() {
        SimpleCache cache = new SimpleCache();
        assertTrue(cache.setnx("idempotent:key", "holder-1", TTL_SECONDS),
            "first setnx on absent key should win");
        assertFalse(cache.setnx("idempotent:key", "holder-2", TTL_SECONDS),
            "second setnx on existing key should lose");
    }

    /**
     * 不同 key 之间互不影响，各自首次占位均成功。
     */
    @Test
    @Timeout(10)
    void shouldIsolateDifferentKeys() {
        SimpleCache cache = new SimpleCache();
        assertTrue(cache.setnx("idempotent:key-a", "holder", TTL_SECONDS));
        assertTrue(cache.setnx("idempotent:key-b", "holder", TTL_SECONDS),
            "setnx on another absent key should win");
        assertFalse(cache.setnx("idempotent:key-a", "holder", TTL_SECONDS));
        assertFalse(cache.setnx("idempotent:key-b", "holder", TTL_SECONDS));
    }

    /**
     * seconds 大于 0 时占位在过期后释放，同 key 再次 setnx 成功。
     */
    @Test
    @Timeout(15)
    void shouldExpireAndRelease() throws InterruptedException {
        SimpleCache cache = new SimpleCache();
        assertTrue(cache.setnx("idempotent:expiring", "holder", 1));
        assertFalse(cache.setnx("idempotent:expiring", "holder", 1),
            "placeholder should still exist before expiry");
        TimeUnit.MILLISECONDS.sleep(1500);
        assertTrue(cache.setnx("idempotent:expiring", "holder", 1),
            "expired placeholder should be released and setnx should win again");
    }

    /**
     * seconds 不大于 0 时占位常驻（对齐既有 put(key, 0, t) 无过期语义），不随时间释放。
     */
    @Test
    @Timeout(10)
    void shouldReturnFalseWhenValueExistsWithoutExpiry() throws InterruptedException {
        SimpleCache cache = new SimpleCache();
        assertTrue(cache.setnx("idempotent:permanent", "holder", 0));
        TimeUnit.MILLISECONDS.sleep(300);
        assertFalse(cache.setnx("idempotent:permanent", "holder", 0),
            "placeholder without expiry should stay resident");
    }

    /**
     * N 线程并发 setnx 同一 key，恰好 1 次 true（原子 set-if-absent）。
     */
    @Test
    @Timeout(30)
    void shouldWinExactlyOnceUnderConcurrency() throws InterruptedException {
        SimpleCache cache = new SimpleCache();
        ExecutorService pool = Executors.newFixedThreadPool(THREADS);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(THREADS);
        AtomicInteger trueCount = new AtomicInteger();
        try {
            for (int i = 0; i < THREADS; i++) {
                pool.submit(() -> {
                    try {
                        start.await();
                    }
                    catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    if (cache.setnx("idempotent:contended", "holder", TTL_SECONDS)) {
                        trueCount.incrementAndGet();
                    }
                    done.countDown();
                });
            }
            start.countDown();
            assertTrue(done.await(10, TimeUnit.SECONDS), "all concurrent workers should finish");
        }
        finally {
            pool.shutdownNow();
        }
        assertEquals(1, trueCount.get(), "exactly one concurrent setnx should win");
    }
}
