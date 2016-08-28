/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.db.core;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.List;
import java.util.logging.Logger;

import javax.sql.DataSource;

import com.hbasesoft.framework.common.utils.CommonUtil;
import com.hbasesoft.framework.db.core.utils.DataSourceUtil;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年12月11日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.db.core <br>
 */
public class ClusterDataSource implements DataSource {

    private String name;

    public ClusterDataSource() {
        this.name = "master";
    }

    public ClusterDataSource(String name) {
        this.name = name;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return
     * @throws SQLException <br>
     */
    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return DataSourceUtil.getDataSource(this.name).getLogWriter();
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param out
     * @throws SQLException <br>
     */
    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        List<DataSource> dsList = DataSourceUtil.getDataSources(name);
        if (CommonUtil.isNotEmpty(dsList)) {
            for (DataSource ds : dsList) {
                ds.setLogWriter(out);
            }
        }
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param seconds
     * @throws SQLException <br>
     */
    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        List<DataSource> dsList = DataSourceUtil.getDataSources(name);
        if (CommonUtil.isNotEmpty(dsList)) {
            for (DataSource ds : dsList) {
                ds.setLoginTimeout(seconds);
            }
        }
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return
     * @throws SQLException <br>
     */
    @Override
    public int getLoginTimeout() throws SQLException {
        return DataSourceUtil.getDataSource(this.name).getLoginTimeout();
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return
     * @throws SQLFeatureNotSupportedException <br>
     */
    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return DataSourceUtil.getDataSource(this.name).getParentLogger();
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param iface
     * @return
     * @throws SQLException <br>
     */
    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return DataSourceUtil.getDataSource(this.name).unwrap(iface);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param iface
     * @return
     * @throws SQLException <br>
     */
    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return DataSourceUtil.getDataSource(this.name).isWrapperFor(iface);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return
     * @throws SQLException <br>
     */
    @Override
    public Connection getConnection() throws SQLException {
        return DataSourceUtil.getDataSource(this.name).getConnection();
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param username
     * @param password
     * @return
     * @throws SQLException <br>
     */
    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return DataSourceUtil.getDataSource(this.name).getConnection(username, password);
    }
}
