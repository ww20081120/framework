/****************************************************************************************
 Copyright © 2023-2026 hbasesoft Corporation. All rights reserved. Reproduction or     <br>
 transmission in whole or in part, in any form or by any means, electronic or mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.cache.simple;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import com.hbasesoft.framework.cache.core.CacheObject;
import com.hbasesoft.framework.common.utils.bean.SerializationUtil;

/**
 * {@link AbstractCache} String 层对历史 {@link CacheObject} 包装字节的兼容读取契约——
 * 4.4.5 起写入侧不再包装（TTL 单一责任在缓存实现层：SIMPLE per-entry / Redis setex），
 * 但 Redis 等外部介质中的存量值为历史包装字节，读取侧经 instanceof 解包直至存量清零。
 *
 * @author 王伟
 * @version 1.0
 * @since 4.4.4
 */
class SimpleCacheLegacyValueCompatTest {

    /**
     * 历史 CacheObject 包装字节（含未过期 expreTime）：经 String 层 get 应解包返回原值。
     */
    @Test
    void shouldUnwrapLegacyCacheObjectBytesOnGet() {
        SimpleCache cache = new SimpleCache();
        byte[] legacyBytes = SerializationUtil.serial(new CacheObject(3600, "legacy-v"));
        cache.put("legacy:wrapped".getBytes(), 0, legacyBytes);
        assertEquals("legacy-v", cache.get("legacy:wrapped"), "历史包装字节应解包返回原值");
    }

    /**
     * 历史 CacheObject 包装字节已过 expreTime：get 判定过期返回 null（历史值的过期语义保留）。
     */
    @Test
    @Timeout(15)
    void shouldTreatExpiredLegacyCacheObjectAsAbsent() throws InterruptedException {
        SimpleCache cache = new SimpleCache();
        byte[] legacyBytes = SerializationUtil.serial(new CacheObject(1, "stale-v"));
        cache.put("legacy:expired".getBytes(), 0, legacyBytes);
        TimeUnit.MILLISECONDS.sleep(1500);
        assertNull(cache.get("legacy:expired"), "过期历史包装字节应判定不可见");
    }

    /**
     * 新链路写入（不包装直存）：get 原值读出；byte[] 层字节即 serial(原值) 本身——
     * String 层不再二次包装（Kryo writeClassAndObject 的类标记字节除外，与直存契约一致）。
     */
    @Test
    void shouldReadThroughNewPlainBytesWithoutWrapper() {
        SimpleCache cache = new SimpleCache();
        cache.put("legacy:plain", 60, "plain-v");
        assertEquals("plain-v", cache.get("legacy:plain"), "新链路原值字节应直接读出");
        byte[] rawBytes = cache.get("legacy:plain".getBytes());
        assertArrayEquals(SerializationUtil.serial("plain-v"), rawBytes,
            "byte[] 层字节应为 serial(原值) 本身（无 CacheObject 二次包装）");
    }
}
