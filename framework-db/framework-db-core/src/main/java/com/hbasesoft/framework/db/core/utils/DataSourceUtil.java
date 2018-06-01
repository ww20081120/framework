/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.db.core.utils;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import org.apache.commons.collections.CollectionUtils;

import com.alibaba.druid.pool.DruidDataSource;
import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.InitializationException;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;
import com.hbasesoft.framework.db.core.DataSourceRegister;
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

    private static Map<String, DataSource> dataSourceMap = new ConcurrentHashMap<String, DataSource>();

    private static boolean initFlag = false;

    private DataSourceUtil() {
    }

    public static DataSource getDataSource() {
        return getDataSource(DynamicDataSourceManager.getDataSourceCode());
    }

    public static DataSource getDataSource(String name) {
        return getDataSourceMap().get(name);
    }

    public static DataSource registDataSource(String name, DbParam dbParam) {
        synchronized (dataSourceMap) {
            return regist(name, dbParam);
        }
    }

    private static Map<String, DataSource> getDataSourceMap() {
        synchronized (dataSourceMap) {
            if (!initFlag) {
                ServiceLoader<DataSourceRegister> registerLoader = ServiceLoader.load(DataSourceRegister.class);
                if (registerLoader != null) {
                    registerLoader.forEach(register -> {
                        Set<DbParam> dbParams = register.getDbParam();
                        if (CollectionUtils.isNotEmpty(dbParams)) {
                            dbParams.forEach(dbParam -> {
                                regist(dbParam.getCode(), dbParam);
                            });
                        }
                    });
                }
                initFlag = true;
            }
            return dataSourceMap;
        }
    }

    private static DataSource regist(String name, DbParam dbParam) {
        DataSource dataSource = dataSourceMap.get(name);
        if (dataSource == null) {
            DruidDataSource ds = new DruidDataSource();
            ds.setDbType(dbParam.getDbType());

            ds.setUrl(dbParam.getUrl());
            ds.setUsername(dbParam.getUsername());
            ds.setPassword(dbParam.getPassword());
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
                throw new InitializationException(ErrorCodeDef.DB_DATASOURCE_NOT_SET, e);
            }
        }
        return dataSource;
    }

}
