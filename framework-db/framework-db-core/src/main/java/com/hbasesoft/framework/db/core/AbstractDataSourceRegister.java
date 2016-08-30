/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.db.core;

import java.sql.SQLException;
import java.util.Arrays;

import javax.sql.DataSource;

import com.alibaba.druid.pool.DruidDataSource;
import com.hbasesoft.framework.common.utils.logger.Logger;
import com.hbasesoft.framework.db.core.config.DbParam;
import com.hbasesoft.framework.db.core.utils.SqlLogFilter;

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
public abstract class AbstractDataSourceRegister implements DataSourceRegister {

    protected Logger logger = new Logger(AbstractDataSourceRegister.class);

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    @Override
    public DataSource regist() {
        DbParam dbParam = new DbParam(getTypeName());
        DruidDataSource ds = new DruidDataSource();
        ds.setDbType(dbParam.getDbType());
        ds.setDriverClassName(dbParam.getDriverClassName());
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
        }
        catch (SQLException e) {
            logger.error(e);
        }
        return ds;
    }
}
