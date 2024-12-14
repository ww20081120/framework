/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.db.core.config;

import org.apache.commons.lang3.StringUtils;

import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.utils.Assert;
import com.hbasesoft.framework.common.utils.PropertyHolder;
import com.hbasesoft.framework.common.utils.security.DataUtil;
import com.hbasesoft.framework.db.core.BaseEntity;

import lombok.Getter;
import lombok.Setter;

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
@Getter
@Setter
public class DbParam extends BaseEntity {

    /**
     * serialVersionUID <br>
     */
    private static final long serialVersionUID = -3873382206326039466L;

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
    private static final int MIN_IDLE = 10;

    /**
     * 获取连接最大等待时间
     */
    private static final long MAX_WAIT = 6000000;

    /**
     * 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
     */
    private static final long TIME_BETWEEN_EVICTION_RUNS_MILLIS = 600000;

    /**
     * 配置一个连接在池中最小生存的时间，单位是毫秒
     */
    private static final long MIN_EVICTABLE_IDLE_TIME_MILLIS = 25200000;

    /**
     * 1800秒，也就是30分钟
     */
    private static final int REMOVE_ABANDONED_TIMEOUT = 1800;

    /** ENC_LENGTH */
    private static final int ENC_LENGTH = "ENC(".length();

    /** code */
    private String code;

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
    private int initialSize = INITIAL_SIZE;

    /**
     * 连接池最大使用连接数量
     */
    private int maxActive = MAX_ACTIVE;

    /**
     * 连接池最小空闲
     */
    private int minIdle = MIN_IDLE;

    /**
     * 获取连接最大等待时间
     */
    private long maxWait = MAX_WAIT;

    /**
     * validationQuery
     */
    private String validationQuery = "SELECT 1";

    /** testOnBorrow */
    private boolean testOnBorrow = true;

    /** testOnReturn */
    private boolean testOnReturn = false;

    /** testWhileIdle */
    private boolean testWhileIdle = true;

    /**
     * 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
     */
    private long timeBetweenEvictionRunsMillis = TIME_BETWEEN_EVICTION_RUNS_MILLIS;

    /**
     * 配置一个连接在池中最小生存的时间，单位是毫秒
     */
    private long minEvictableIdleTimeMillis = MIN_EVICTABLE_IDLE_TIME_MILLIS;

    /**
     * 打开removeAbandoned功能
     */
    private boolean removeAbandoned = true;

    /**
     * 1800秒，也就是30分钟
     */
    private int removeAbandonedTimeout = REMOVE_ABANDONED_TIMEOUT;

    /**
     * 关闭abanded连接时输出错误日志
     */
    private boolean logAbandoned;

    /** filters */
    private String filters = "stat";

    /** driverClass */
    private String driverClass;

    /**
     * @param prefix
     * @param u
     * @param un
     * @param pw
     */
    public DbParam(final String prefix, final String u, final String un, final String pw) {
        setUrl(u);
        setUsername(un);
        setPassword(pw);
        init(prefix);
    }

    /**
     * @param prefix
     */
    public DbParam(final String prefix) {
        this.url = PropertyHolder.getProperty(prefix + ".url");
        Assert.notEmpty(this.url, ErrorCodeDef.DB_URL_NOT_SET);
        this.username = PropertyHolder.getProperty(prefix + ".username");
        // Assert.notEmpty(this.username, ErrorCodeDef.DB_USERNAME_NOT_SET, prefix);
        String pw = PropertyHolder.getProperty(prefix + ".password");
        // Assert.notEmpty(password, ErrorCodeDef.DB_PASSWORD_NOT_SET, prefix);
        setPassword(pw);
        init(prefix);
    }

    private void init(final String prefix) {
        this.code = prefix;
        this.dbType = PropertyHolder.getProperty(prefix + ".type", "mysql");
        this.initialSize = PropertyHolder.getIntProperty(prefix + ".initialSize", INITIAL_SIZE);
        this.maxActive = PropertyHolder.getIntProperty(prefix + ".maxActive", MAX_ACTIVE);
        this.minIdle = PropertyHolder.getIntProperty(prefix + ".minIdle", MIN_IDLE);
        this.maxWait = PropertyHolder.getLongProperty(prefix + ".maxWait", MAX_WAIT);
        this.validationQuery = PropertyHolder.getProperty(prefix + ".validationQuery", "SELECT 1");
        this.testOnBorrow = PropertyHolder.getBooleanProperty(prefix + ".testOnBorrow", true);
        this.testOnReturn = PropertyHolder.getBooleanProperty(prefix + ".testOnReturn", false);
        this.testWhileIdle = PropertyHolder.getBooleanProperty(prefix + ".testWhileIdle", true);
        this.timeBetweenEvictionRunsMillis = PropertyHolder.getLongProperty(prefix + ".timeBetweenEvictionRunsMillis",
            timeBetweenEvictionRunsMillis);
        this.minEvictableIdleTimeMillis = PropertyHolder.getLongProperty(prefix + ".timeBetweenEvictionRunsMillis",
            TIME_BETWEEN_EVICTION_RUNS_MILLIS);
        this.removeAbandoned = PropertyHolder.getBooleanProperty(prefix + ".removeAbandoned", true);
        this.removeAbandonedTimeout = PropertyHolder.getIntProperty(prefix + ".removeAbandonedTimeout",
            REMOVE_ABANDONED_TIMEOUT);
        this.logAbandoned = PropertyHolder.getBooleanProperty(prefix + ".logAbandoned", true);
        this.driverClass = PropertyHolder.getProperty(prefix + ".driverClass");
        this.filters = PropertyHolder.getProperty(prefix + ".filters", "stat");
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param pw <br>
     */
    public void setPassword(final String pw) {
        String tempPw = pw;
        if (StringUtils.isNotEmpty(tempPw) && tempPw.startsWith("ENC(") && tempPw.endsWith(")")) {
            tempPw = DataUtil.decrypt(tempPw.substring(ENC_LENGTH, tempPw.length() - 1));
        }
        this.password = tempPw;
    }

}
