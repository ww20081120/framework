package com.hbasesoft.framework.wechat.executor;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.hbasesoft.framework.common.utils.PropertyHolder;

public class WechatThreadPoolExecutor {

    private ThreadPoolExecutor pool = null;

    private int corePoolSize;

    private int maximumPoolSize;

    private long keepAliveTime;

    public WechatThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime) {
        this.corePoolSize = corePoolSize;
        this.maximumPoolSize = maximumPoolSize;
        this.keepAliveTime = keepAliveTime;
        init();
    }

    public WechatThreadPoolExecutor() {
        this.corePoolSize = PropertyHolder.getIntProperty("wechat.thread.corePoolSize", 20); // 核心线程数
        this.maximumPoolSize = PropertyHolder.getIntProperty("wechat.thread.maximumPoolSize", 100); // 最大线程数
        this.keepAliveTime = PropertyHolder.getIntProperty("wechat.thread.keepAliveTime", 600);
        init();
    }

    public void init() {
        if (pool == null) {
            pool = new ThreadPoolExecutor(corePoolSize, // 核心线程数
                maximumPoolSize, // 最大线程数
                keepAliveTime, // 闲置线程存活时间
                TimeUnit.MILLISECONDS, // 时间单位
                new LinkedBlockingDeque<Runnable>());
        }
    }

    public void execute(Runnable runnable) {
        if (runnable == null) {
            return;
        }
        if (pool == null) {
            init();
        }
        pool.execute(runnable);
    }
}