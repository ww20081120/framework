/****************************************************************************************
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or     <br>
 transmission in whole or in part, in any form or by any means, electronic or mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.cache.simple;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import com.hbasesoft.framework.cache.core.AbstractCache;
import com.hbasesoft.framework.common.utils.PropertyHolder;

/**
 * <Description> <br>
 *
 * <p><b>per-entry 变体过期机制（4.4.5 缺陷修复）</b>：旧实现 {@code put(byte[], seconds, byte[])}
 * 丢弃 seconds 传 0、桶级 TTL 由首建者固化——带 TTL 写入（increment/put 计数与票据类键）全部永驻，
 * FC24 取图配额窗口（60s 固定窗）永不重置，设备被持续锁定（Redis 档实现层 setex 兜底故未暴露）。
 * 现机制：每桶为 Caffeine 实例 + 外置 TTL 元数据表（{@code ConcurrentHashMap<key, ttlNanos>}），
 * {@link Expiry} 回调按 per-entry 元数据返回时长（未登记即永驻）；写入前登记、驱逐时清理。
 * 桶级 expireAfterWrite 废除，setnx 专用桶命名保留（判重语义与既有测试契约不变），占位过期
 * 同样由 per-entry 元数据驱动。</p>
 *
 * @author 王伟 <br>
 * @version 1.0 <br>
 * @CreateDate 2014年10月24日 <br>
 * @see com.hbasesoft.framework.core.cache.simple <br>
 */
public class SimpleCache extends AbstractCache {

    /**
     * 默认缓存目录
     */
    private static final String DEFAULT_CACHE_DIR = "/";

    /** */
    private static final int MAX_SIZE = PropertyHolder.getIntProperty("cache.max_size", 10000);

    /**
     * 缓存模式
     */
    public static final String CACHE_MODEL = "SIMPLE";

    /** setnx 专用桶名后缀（按 seconds 拼接隔离，与业务 put 共享目录桶互不可见） */
    private static final String SETNX_BUCKET_SUFFIX = "-setnx-";

    /** 桶集合：桶名 → 变体过期 Caffeine 实例 + per-entry TTL 元数据 */
    private Map<String, TtlBucket> cachesMap;

    /**
     * 默认构造
     */
    public SimpleCache() {
        this.cachesMap = new HashMap<String, TtlBucket>();
    }

    /**
     * Description: <br>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    @Override
    public String getName() {
        return CACHE_MODEL;
    }

    /**
     * Description: <br>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    @Override
    public Object getNativeCache() {
        return cachesMap;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.hbasesoft.framework.cache.core.ICache#clean()
     */
    @Override
    public void clear() {
        cachesMap.clear();
    }

    /**
     * Description: <br>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @param key
     * @return <br>
     */
    @Override
    public byte[] get(final byte[] key) {
        return getNodeValue(DEFAULT_CACHE_DIR.getBytes(), key);
    }

    /**
     * Description: <br>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @param key
     * @param seconds
     * @param value <br>
     */
    @Override
    public void put(final byte[] key, final int seconds, final byte[] value) {
        putNodeValue(DEFAULT_CACHE_DIR.getBytes(), seconds, key, value);
    }

    /**
     * Description: <br>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @param node
     * @return <br>
     */
    @Override
    public Map<byte[], byte[]> getNode(final byte[] node) {
        TtlBucket bucket = this.cachesMap.get(new String(node));
        return bucket == null ? null : string2ByteMap(bucket.cache().asMap());
    }

    /**
     * Description: <br>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @param key
     * @param seconds
     * @param dataMap <br>
     */
    @Override
    public void putNode(final byte[] key, final int seconds, final Map<byte[], byte[]> dataMap) {
        String strkey = new String(key);
        TtlBucket bucket = bucketOf(strkey);
        Map<String, byte[]> stringMap = byte2StringMap(dataMap);
        registerTtl(bucket, stringMap.keySet(), seconds);
        bucket.cache().putAll(stringMap);
    }

    /**
     * Description: <br>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @param nodeName
     */
    @Override
    public void remove(final byte[] nodeName) {
        this.cachesMap.remove(new String(nodeName));
        removeNodeValue(DEFAULT_CACHE_DIR.getBytes(), nodeName);
    }

    /**
     * Description: <br>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @param nodeName
     * @param key
     * @return <br>
     */
    @Override
    public byte[] getNodeValue(final byte[] nodeName, final byte[] key) {
        TtlBucket bucket = this.cachesMap.get(new String(nodeName));
        return bucket == null ? null : bucket.cache().getIfPresent(new String(key));
    }

    /**
     * Description: <br>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @param nodeName
     * @param key
     * @param t <br>
     */
    @Override
    public void putNodeValue(final byte[] nodeName, final int seconds, final byte[] key, final byte[] t) {
        TtlBucket bucket = bucketOf(new String(nodeName));
        registerTtl(bucket, Collections.singletonList(new String(key)), seconds);
        bucket.cache().put(new String(key), t);
    }

    /**
     * Description: <br>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @param nodeName
     * @param key <br>
     */
    @Override
    public void removeNodeValue(final byte[] nodeName, final byte[] key) {
        TtlBucket bucket = this.cachesMap.get(new String(nodeName));
        if (bucket != null) {
            bucket.cache().invalidate(new String(key));
            bucket.ttlNanos().remove(new String(key));
        }
    }

    /**
     * Description: 原子 set-if-absent 占位——按 seconds 隔离的专用桶（桶名
     * {@code <目录>-setnx-<seconds>}），不复用业务 put 的共享目录桶；value 原样字节
     * 存储，对齐 Redis 实现语义。<br>
     *
     * <p><b>专用桶动因</b>（4.4.3 缺陷修复，FC24 sec-common-guards Task 5 端到端
     * IT 抓出）：共享目录桶被业务写入（token / 验证码 / OTP 等登录链路 put）先建立，
     * setnx 占位不得继承他人桶 TTL。4.4.5 起桶级 TTL 废除、占位过期统一由 per-entry
     * 元数据驱动：占位 TTL 恒等于本次 seconds（同 seconds 共桶判重正确，跨 seconds
     * 各自独立，与业务桶互不可见）；Redis 原生 setnx 的 TTL 亦属于本次调用，契约一致。</p>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @param key
     * @param value
     * @param seconds
     * @return <br>
     */
    @Override
    public boolean setnx(final String key, final String value, final int seconds) {
        TtlBucket bucket = bucketOf(DEFAULT_CACHE_DIR + SETNX_BUCKET_SUFFIX + seconds);
        registerTtl(bucket, Collections.singletonList(key), seconds);
        return bucket.cache().asMap().putIfAbsent(key, value.getBytes()) == null;
    }

    /**
     * Description: <br>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @param seconds
     * @param key
     * @param startNum
     * @return <br>
     */
    @Override
    public synchronized long increment(final int seconds, final String key, final long startNum) {
        Long number = get(key);
        if (number == null) {
            number = startNum;
        }
        else {
            number = number.longValue() + startNum;
        }
        put(key, seconds, number);
        return number;
    }

    /**
     * Description: <br>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @param hashKey
     * @param subTaskCode
     * @return <br>
     */
    @Override
    public boolean hasNodeKey(final String hashKey, final String subTaskCode) {
        return getNodeValue(subTaskCode, hashKey) != null;
    }

    /**
     * Description: <br>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @param key
     * @return <br>
     */
    @Override
    public boolean hasKey(final String key) {
        return get(key) != null;
    }

    /**
     * 取桶（懒建）；统一变体过期 builder——per-entry TTL 元数据外置，写入前登记、驱逐时清理
     *
     * @param bucketName 桶名
     * @return 桶实例
     */
    private synchronized TtlBucket bucketOf(final String bucketName) {
        return this.cachesMap.computeIfAbsent(bucketName, name -> {
            ConcurrentHashMap<String, Long> ttlNanos = new ConcurrentHashMap<>();
            Cache<String, byte[]> cache = Caffeine.newBuilder()
                .maximumSize(MAX_SIZE)
                .expireAfter(new Expiry<String, byte[]>() {
                    /**
                     * 写入建条目：按登记元数据返回时长，未登记即永驻
                     *
                     * @param key 条目键
                     * @param value 条目值
                     * @param now 写入时刻（单调纳秒）
                     * @return 时长（纳秒）
                     */
                    @Override
                    public long expireAfterCreate(final String key, final byte[] value, final long now) {
                        Long ttl = ttlNanos.get(key);
                        return ttl == null ? Long.MAX_VALUE : ttl;
                    }

                    /**
                     * 覆写更新：同 create 口径
                     *
                     * @param key 条目键
                     * @param value 条目值
                     * @param now 写入时刻（单调纳秒）
                     * @param currentDuration 当前剩余时长（纳秒）
                     * @return 时长（纳秒）
                     */
                    @Override
                    public long expireAfterUpdate(final String key, final byte[] value, final long now,
                        final long currentDuration) {
                        Long ttl = ttlNanos.get(key);
                        return ttl == null ? Long.MAX_VALUE : ttl;
                    }

                    /**
                     * 读访问：不续期（固定窗口语义）
                     *
                     * @param key 条目键
                     * @param value 条目值
                     * @param now 读取时刻（单调纳秒）
                     * @param currentDuration 当前剩余时长（纳秒）
                     * @return 原剩余时长
                     */
                    @Override
                    public long expireAfterRead(final String key, final byte[] value, final long now,
                        final long currentDuration) {
                        return currentDuration;
                    }
                })
                .removalListener((key, value, cause) -> {
                    if (cause.wasEvicted()) {
                        ttlNanos.remove(key);
                    }
                })
                .build();
            return new TtlBucket(cache, ttlNanos);
        });
    }

    /**
     * 登记写入条目的 per-entry TTL（seconds &gt; 0 登记纳秒时长，否则移除登记=永驻）——
     * 必须先于 cache 写入调用（Expiry 回调在写入路径内同步读取元数据）
     *
     * @param bucket 目标桶
     * @param keys 写入条目键
     * @param seconds TTL 秒数（&le; 0 表示永驻）
     */
    private static void registerTtl(final TtlBucket bucket, final Iterable<String> keys, final int seconds) {
        for (String key : keys) {
            if (seconds > 0) {
                bucket.ttlNanos().put(key, TimeUnit.SECONDS.toNanos(seconds));
            }
            else {
                bucket.ttlNanos().remove(key);
            }
        }
    }

    private Map<String, byte[]> byte2StringMap(final Map<byte[], byte[]> map) {
        Map<String, byte[]> m = new HashMap<>();
        if (map != null) {
            for (Entry<byte[], byte[]> entry : map.entrySet()) {
                m.put(new String(entry.getKey()), entry.getValue());
            }
        }
        return m;
    }

    private Map<byte[], byte[]> string2ByteMap(final Map<String, byte[]> map) {
        Map<byte[], byte[]> m = new HashMap<>();
        if (map != null) {
            for (Entry<String, byte[]> entry : map.entrySet()) {
                m.put(entry.getKey().getBytes(), entry.getValue());
            }
        }
        return m;
    }

    /**
     * 缓存桶：变体过期 Caffeine 实例 + per-entry TTL 元数据表（key → 下次写入时长/纳秒）
     */
    private static final class TtlBucket {

        /** 变体过期 Caffeine 实例 */
        private final Cache<String, byte[]> cache;

        /** per-entry TTL 元数据（写入前登记，Expiry 回调消费，驱逐时清理） */
        private final ConcurrentHashMap<String, Long> ttlNanos;

        /**
         * 构造桶
         *
         * @param cacheInstance Caffeine 实例
         * @param ttlNanosMap TTL 元数据表
         */
        TtlBucket(final Cache<String, byte[]> cacheInstance, final ConcurrentHashMap<String, Long> ttlNanosMap) {
            this.cache = cacheInstance;
            this.ttlNanos = ttlNanosMap;
        }

        /**
         * 取 Caffeine 实例
         *
         * @return 实例
         */
        private Cache<String, byte[]> cache() {
            return cache;
        }

        /**
         * 取 TTL 元数据表
         *
         * @return 元数据表
         */
        private ConcurrentHashMap<String, Long> ttlNanos() {
            return ttlNanos;
        }
    }
}
