/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.db.mongo.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.hbasesoft.framework.common.utils.PropertyHolder;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;
import com.hbasesoft.framework.db.core.DynamicDataSourceManager;
import com.hbasesoft.framework.db.core.config.DaoTypeDef;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.connection.ConnectionPoolSettings;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;


/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2016年8月28日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.db.core <br>
 */
public final class MongodbSourceUtil {

    /** 前缀长度 */
    private static final int PREFIX_LENGTH = 6;
    /**
     * 初始化连接大小
     */
    private static final int INITIAL_SIZE = 5;

    /**
     * 连接池最大使用连接数量
     */
    private static final int MAX_ACTIVE = 100;

    /**
     * 连接池最小空闲
     */
    private static final int MIN_IDLE = 5;

    /**
     * 获取连接最大等待时间
     */
    private static final long MAX_WAIT_TIME = 60000;

    /**
     * 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
     */
    private static final long TIME_BETWEEN_EVICTION_RUNS_MILLIS = 60000;

    /**
     * 配置一个连接在池中最小生存的时间，单位是毫秒
     */
    private static final long MIN_EVICTABLE_IDLE_TIME_MILLIS = 1800000;

    /**
     * 1800秒，也就是30分钟
     */
    private static final int REMOVE_ABANDONED_TIMEOUT = 60;

    /** datasource map */
    private static Map<String, String> dataSourceMap = new ConcurrentHashMap<>();

    /** datasource map */
    private static Map<String, String>  dateSourceNames = new ConcurrentHashMap<>();
    /** datasource map */
    private static Map<String, MongoClient> mongoTemplateHolder = new ConcurrentHashMap<>();

    private MongodbSourceUtil() {
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    public static void init() {
        synchronized (dataSourceMap) {
            for (String key : PropertyHolder.getProperties().keySet()) {
                if (key.endsWith("mongodb.uri")) {
                    String name = key.substring(0, key.indexOf(".mongodb.") + PREFIX_LENGTH);
                    LoggerUtil.info("开始注册{0}数据源", name);
                    regist(name, PropertyHolder.getProperties().get(key));
                    LoggerUtil.info("注册{0}数据源成功", name);
                }
            }
        }
    }

    public static MongoClient getDataSource() {
        return getDataSource(DynamicDataSourceManager.getInstance(DaoTypeDef.mongodb).getDataSourceCode());
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param name
     * @return <br>
     */
    public static MongoClient getDataSource(final String name) {
        synchronized (dataSourceMap) {
            String uid = dataSourceMap.get(name);
            return uid == null ? null : mongoTemplateHolder.get(uid);
        }
    }

    /**
     *
     * @param name
     * @return String
     */
    public static String getDatabaseName(final String name) {
        synchronized (dataSourceMap) {
            String dataSourceName = dateSourceNames.get(name);
            return dataSourceName;
        }
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param name <br>
     */
    public static void close(final String name) {
        synchronized (dataSourceMap) {
            String uid = dataSourceMap.remove(name);
            dateSourceNames.remove(name);
            if (uid != null && !dataSourceMap.containsValue(uid)) {
                MongoClient mongoDatabase = mongoTemplateHolder.remove(uid);
                if (mongoDatabase != null && mongoDatabase instanceof DruidDataSource) {
                    ((DruidDataSource) mongoDatabase).close();
                }
            }
        }
    }

    private static MongoClient regist(final String name, final String dbParam) {
        String uid = new StringBuilder().append(dbParam).toString();
        dataSourceMap.put(name, uid);
        MongoClient mongoDatabase = mongoTemplateHolder.get(uid);
        if (mongoDatabase == null) {
            ConnectionString connectionString = new ConnectionString(dbParam);
            String database = connectionString.getDatabase();
            dateSourceNames.put(name, database);
            // 如果你需要自定义连接池设置，可以像下面这样创建：
            ConnectionPoolSettings poolSettings = ConnectionPoolSettings.builder()
                    .maxSize(INITIAL_SIZE) // 最大连接数
                    .minSize(MIN_IDLE) // 最小空闲连接数
                    .maxWaitTime(MAX_WAIT_TIME, TimeUnit.MILLISECONDS) // 最大等待时间
                    .maxConnectionLifeTime(MIN_EVICTABLE_IDLE_TIME_MILLIS, TimeUnit.MILLISECONDS) // 连接的最大生命周期
                    .maxConnectionIdleTime(TIME_BETWEEN_EVICTION_RUNS_MILLIS, TimeUnit.MILLISECONDS) // 连接的最大空闲时间
                    // 维护任务的初始延迟  设置了连接池维护任务开始前的初始延迟时间（毫秒），比如清理不再需要的空闲连接。
                    .maintenanceInitialDelay(TIME_BETWEEN_EVICTION_RUNS_MILLIS, TimeUnit.MILLISECONDS)
                    // 维护任务的频率 指定执行连接池维护任务的频率（毫秒），如定期检查和移除超时的连接。
                    .maintenanceFrequency(TIME_BETWEEN_EVICTION_RUNS_MILLIS, TimeUnit.MILLISECONDS)
                    .maxConnecting(MAX_ACTIVE)//建立的最大连接数
                    .build();

            MongoClientSettings settings = MongoClientSettings.builder()
                    .applyConnectionString(connectionString)
                    .applyToConnectionPoolSettings(builder -> builder.applySettings(poolSettings))
                    .build();
            mongoDatabase = MongoClients.create(settings);
            mongoTemplateHolder.put(uid, mongoDatabase);
        }
        return mongoDatabase;
    }

    public static Collection<String> getAllDatasourceCode() {
        return dataSourceMap.keySet();
    }

}
