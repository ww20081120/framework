/****************************************************************************************
 Copyright © 2023-2026 hbasesoft Corporation. All rights reserved. Reproduction or     <br>
 transmission in whole or in part, in any form or by any means, electronic or mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.cache.simple;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

/**
 * {@link SimpleCache} 带 TTL 写入路径（put/increment）的过期行为契约测试——
 * 当前作为「increment TTL 参数被吞」缺陷的现状探针与修复后回归。
 *
 * <p>缺陷背景（FC24 sec-captcha-image-first 取图配额锁定事故）：ICache String 层虽经
 * AbstractCache 用 CacheObject 包装（expreTime 编码进 value），但 byte[] 层
 * {@code put(byte[], seconds, byte[])} 丢弃 seconds 传 0，桶级 TTL 由首建者固化——
 * 跨层组合的实际行为以本测试实测为准。</p>
 *
 * @author 王伟
 * @version 1.0
 * @since 4.4.4
 */
class SimpleCacheTtlTest {

    /**
     * increment 带 TTL：窗口内累加，过期后从 startNum 重新计数（固定窗口配额语义）。
     */
    @Test
    @Timeout(15)
    void shouldIncrementExpireAfterTtlAndRestartFromStartNum() throws InterruptedException {
        SimpleCache cache = new SimpleCache();
        assertEquals(1, cache.increment(1, "quota:probe", 1L), "首刷应从 startNum 起计");
        assertEquals(2, cache.increment(1, "quota:probe", 1L), "窗口内应累加");
        TimeUnit.MILLISECONDS.sleep(1500);
        assertEquals(1, cache.increment(1, "quota:probe", 1L),
            "TTL 过期后应从 startNum 重新计数（固定窗口重置）");
    }

    /**
     * put 带 TTL：过期后 get 不可见（经 AbstractCache String 层 CacheObject 判定）。
     */
    @Test
    @Timeout(15)
    void shouldPutValueExpireAfterTtl() throws InterruptedException {
        SimpleCache cache = new SimpleCache();
        cache.put("quota:put-probe", 1, "v");
        assertEquals("v", cache.get("quota:put-probe"), "TTL 内应可读");
        TimeUnit.MILLISECONDS.sleep(1500);
        assertNull(cache.get("quota:put-probe"), "TTL 过期后应不可见");
    }

    /**
     * put 不带 TTL（seconds=0）：常驻不过期（对齐既有语义）。
     */
    @Test
    @Timeout(10)
    void shouldPutValueStayResidentWithoutTtl() throws InterruptedException {
        SimpleCache cache = new SimpleCache();
        cache.put("quota:resident", 0, "v");
        TimeUnit.MILLISECONDS.sleep(300);
        assertEquals("v", cache.get("quota:resident"), "seconds=0 应常驻");
    }

    /**
     * 同桶混合 TTL：先写永驻键再写短 TTL 键，短 TTL 键过期而永驻键不受影响
     * （桶级 TTL 固化缺陷的判别用例——修复前桶由首建者 seconds=0 固化为永驻，
     * 后写条目的 TTL 形同虚设或被整桶牵连）。
     */
    @Test
    @Timeout(15)
    void shouldMixedTtlKeysExpireIndependently() throws InterruptedException {
        SimpleCache cache = new SimpleCache();
        cache.put("quota:mix:resident", 0, "r");
        cache.put("quota:mix:short", 1, "s");
        TimeUnit.MILLISECONDS.sleep(1500);
        assertNull(cache.get("quota:mix:short"), "短 TTL 键应独立过期");
        assertEquals("r", cache.get("quota:mix:resident"), "永驻键不受短 TTL 键影响");
    }
}
