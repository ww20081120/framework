/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.db.core.config;

import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.utils.Assert;
import com.hbasesoft.framework.common.utils.CommonUtil;
import com.hbasesoft.framework.common.utils.PropertyHolder;
import com.hbasesoft.framework.common.utils.security.DataUtil;
import com.hbasesoft.framework.db.core.BaseEntity;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2016年8月28日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.db.core.config <br>
 */
public class DbParam extends BaseEntity {
    /**
     * serialVersionUID <br>
     */
    private static final long serialVersionUID = -3873382206326039466L;

    /**
     * 数据库链接
     */
    private String url;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 数据库类型
     */
    private String dbType = "mysql";

    /**
     * 初始化连接大小
     */
    private int initialSize = 5;

    /**
     * 连接池最大使用连接数量
     */
    private int maxActive = 100;

    /**
     * 连接池最小空闲
     */
    private int minIdle = 0;

    /**
     * 获取连接最大等待时间
     */
    private long maxWait = 6000000;

    /**
     * 
     */
    private String validationQuery = "SELECT 1";

    private boolean testOnBorrow = true;

    private boolean testOnReturn = false;

    private boolean testWhileIdle = true;

    /**
     * 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
     */
    private long timeBetweenEvictionRunsMillis = 600000;

    /**
     * 配置一个连接在池中最小生存的时间，单位是毫秒
     */
    private long minEvictableIdleTimeMillis = 25200000;

    /**
     * 打开removeAbandoned功能
     */
    private boolean removeAbandoned = true;

    /**
     * 1800秒，也就是30分钟
     */
    private int removeAbandonedTimeout = 1800;

    /**
     * 关闭abanded连接时输出错误日志
     */
    private boolean logAbandoned;

    private String filters = "stat";

    public DbParam() {
    }

    public DbParam(String prefix) {
        this.url = PropertyHolder.getProperty(prefix + ".db.url");
        Assert.notEmpty(this.url, ErrorCodeDef.DB_URL_NOT_SET);
        this.username = PropertyHolder.getProperty(prefix + ".db.username");
        Assert.notEmpty(this.username, ErrorCodeDef.DB_USERNAME_NOT_SET, prefix);
        String password = PropertyHolder.getProperty(prefix + ".db.password");
        Assert.notEmpty(password, ErrorCodeDef.DB_PASSWORD_NOT_SET, prefix);
        if (CommonUtil.isNotEmpty(password) && password.startsWith("ENC(") && password.endsWith(")")) {
            password = DataUtil.decrypt(password.substring(4, password.length() - 1));
        }
        this.password = password;

        this.dbType = PropertyHolder.getProperty(prefix + ".db.type", "mysql");
        this.initialSize = PropertyHolder.getIntProperty(prefix + ".db.initialSize", 5);
        this.maxActive = PropertyHolder.getIntProperty(prefix + ".db.maxActive", 100);
        this.minIdle = PropertyHolder.getIntProperty(prefix + ".db.minIdle", 0);
        this.maxWait = PropertyHolder.getLongProperty(prefix + ".db.maxWait", 6000000L);
        this.validationQuery = PropertyHolder.getProperty(prefix + ".db.validationQuery", "SELECT 1");
        this.testOnBorrow = PropertyHolder.getBooleanProperty(prefix + ".db.testOnBorrow", true);
        this.testOnReturn = PropertyHolder.getBooleanProperty(prefix + ".db.testOnReturn", false);
        this.testWhileIdle = PropertyHolder.getBooleanProperty(prefix + ".db.testWhileIdle", true);
        this.timeBetweenEvictionRunsMillis = PropertyHolder
            .getLongProperty(prefix + ".db.timeBetweenEvictionRunsMillis", 600000L);
        this.minEvictableIdleTimeMillis = PropertyHolder.getLongProperty(prefix + ".db.timeBetweenEvictionRunsMillis",
            25200000L);
        this.removeAbandoned = PropertyHolder.getBooleanProperty(prefix + ".db.removeAbandoned", true);
        this.removeAbandonedTimeout = PropertyHolder.getIntProperty(prefix + ".db.removeAbandonedTimeout", 1800);
        this.logAbandoned = PropertyHolder.getBooleanProperty(prefix + ".db.logAbandoned", true);
        this.filters = PropertyHolder.getProperty(prefix + ".db.filters", "stat");
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        if (CommonUtil.isNotEmpty(password) && password.startsWith("ENC(") && password.endsWith(")")) {
            password = DataUtil.decrypt(password.substring(4, password.length() - 1));
        }
        this.password = password;
    }

    public String getDbType() {
        return dbType;
    }

    public void setDbType(String dbType) {
        this.dbType = dbType;
    }

    public int getInitialSize() {
        return initialSize;
    }

    public void setInitialSize(int initialSize) {
        this.initialSize = initialSize;
    }

    public int getMaxActive() {
        return maxActive;
    }

    public void setMaxActive(int maxActive) {
        this.maxActive = maxActive;
    }

    public int getMinIdle() {
        return minIdle;
    }

    public void setMinIdle(int minIdle) {
        this.minIdle = minIdle;
    }

    public long getMaxWait() {
        return maxWait;
    }

    public void setMaxWait(long maxWait) {
        this.maxWait = maxWait;
    }

    public String getValidationQuery() {
        return validationQuery;
    }

    public void setValidationQuery(String validationQuery) {
        this.validationQuery = validationQuery;
    }

    public boolean isTestOnBorrow() {
        return testOnBorrow;
    }

    public void setTestOnBorrow(boolean testOnBorrow) {
        this.testOnBorrow = testOnBorrow;
    }

    public boolean isTestOnReturn() {
        return testOnReturn;
    }

    public void setTestOnReturn(boolean testOnReturn) {
        this.testOnReturn = testOnReturn;
    }

    public boolean isTestWhileIdle() {
        return testWhileIdle;
    }

    public void setTestWhileIdle(boolean testWhileIdle) {
        this.testWhileIdle = testWhileIdle;
    }

    public long getTimeBetweenEvictionRunsMillis() {
        return timeBetweenEvictionRunsMillis;
    }

    public void setTimeBetweenEvictionRunsMillis(long timeBetweenEvictionRunsMillis) {
        this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
    }

    public long getMinEvictableIdleTimeMillis() {
        return minEvictableIdleTimeMillis;
    }

    public void setMinEvictableIdleTimeMillis(long minEvictableIdleTimeMillis) {
        this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
    }

    public boolean isRemoveAbandoned() {
        return removeAbandoned;
    }

    public void setRemoveAbandoned(boolean removeAbandoned) {
        this.removeAbandoned = removeAbandoned;
    }

    public int getRemoveAbandonedTimeout() {
        return removeAbandonedTimeout;
    }

    public void setRemoveAbandonedTimeout(int removeAbandonedTimeout) {
        this.removeAbandonedTimeout = removeAbandonedTimeout;
    }

    public boolean isLogAbandoned() {
        return logAbandoned;
    }

    public void setLogAbandoned(boolean logAbandoned) {
        this.logAbandoned = logAbandoned;
    }

    public String getFilters() {
        return filters;
    }

    public void setFilters(String filters) {
        this.filters = filters;
    }

}
