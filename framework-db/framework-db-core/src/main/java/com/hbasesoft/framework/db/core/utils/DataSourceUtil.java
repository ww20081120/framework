/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.db.core.utils;

import java.sql.Driver;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;

import com.alibaba.druid.pool.DruidDataSource;
import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.InitializationException;
import com.hbasesoft.framework.common.utils.PropertyHolder;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;
import com.hbasesoft.framework.db.core.DynamicDataSourceManager;
import com.hbasesoft.framework.db.core.config.DbParam;

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
public final class DataSourceUtil {

    /** datasource map */
    private static Map<String, DataSource> dataSourceMap = new ConcurrentHashMap<String, DataSource>();

    private DataSourceUtil() {
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
                if (key.endsWith(".db.url")) {
                    String name = key.substring(0, key.indexOf("."));
                    LoggerUtil.info("开始注册{0}数据源", name);
                    regist(name, new DbParam(name));
                    LoggerUtil.info("注册{0}数据源成功", name);
                }
            }
        }
    }

    public static DataSource getDataSource() {
        return getDataSource(DynamicDataSourceManager.getDataSourceCode());
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param name
     * @return <br>
     */
    public static DataSource getDataSource(String name) {
        synchronized (dataSourceMap) {
            return dataSourceMap.get(name);
        }
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param name
     * @param dbParam
     * @return <br>
     */
    public static DataSource registDataSource(String name, DbParam dbParam) {
        synchronized (dataSourceMap) {
            return regist(name, dbParam);
        }
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param name <br>
     */
    public static void close(String name) {
        synchronized (dataSourceMap) {
            DataSource dataSource = dataSourceMap.remove(name);
            if (dataSource != null && dataSource instanceof DruidDataSource) {
                ((DruidDataSource) dataSource).close();
            }
        }
    }

    private static DataSource regist(String name, DbParam dbParam) {
        DataSource dataSource = dataSourceMap.get(name);
        if (dataSource == null) {
            DruidDataSource ds = new DruidDataSource();
            ds.setDbType(dbParam.getDbType());

            ds.setUrl(dbParam.getUrl());
            if (StringUtils.isNotEmpty(dbParam.getUsername())) {
                ds.setUsername(dbParam.getUsername());
                ds.setPassword(dbParam.getPassword());
            }
            if (StringUtils.isNotEmpty(dbParam.getDriverClass())) {
                ds.setDriverClassName(dbParam.getDriverClass());
            }
            else {
                ServiceLoader<Driver> drivers = ServiceLoader.load(Driver.class);
                if (drivers != null) {
                    String dbType = new StringBuilder().append('.').append(dbParam.getDbType().toLowerCase())
                        .append('.').toString();
                    for (Driver driver : drivers) {
                        if (driver.getClass().getName().indexOf(dbType) != -1) {
                            ds.setDriverClassName(driver.getClass().getName());
                            break;
                        }
                    }
                }
            }
            ds.setInitialSize(dbParam.getInitialSize());
            ds.setMaxActive(dbParam.getMaxActive());
            ds.setMinIdle(dbParam.getMinIdle());
            ds.setMaxWait(dbParam.getMaxWait());
            ds.setValidationQuery(dbParam.getValidationQuery());
            ds.setTestOnBorrow(dbParam.isTestOnBorrow());
            ds.setTestOnReturn(dbParam.isTestOnReturn());
            ds.setTestWhileIdle(dbParam.isTestWhileIdle());
            ds.setTimeBetweenEvictionRunsMillis(dbParam.getTimeBetweenEvictionRunsMillis());
            ds.setMinEvictableIdleTimeMillis(dbParam.getMinEvictableIdleTimeMillis());
            ds.setRemoveAbandonedTimeout(dbParam.getRemoveAbandonedTimeout());
            ds.setRemoveAbandoned(dbParam.isRemoveAbandoned());
            ds.setProxyFilters(Arrays.asList(new SqlLogFilter()));
            try {
                ds.setFilters(dbParam.getFilters());
                ds.init();
                dataSourceMap.put(name, ds);
                dataSource = ds;
            }
            catch (SQLException e) {
                LoggerUtil.error(e);
                if (ds != null) {
                    ds.close();
                }
                throw new InitializationException(ErrorCodeDef.DB_DATASOURCE_NOT_SET, e, name);
            }
        }
        return dataSource;
    }

}
