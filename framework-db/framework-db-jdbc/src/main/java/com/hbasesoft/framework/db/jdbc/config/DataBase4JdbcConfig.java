/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.db.jdbc.config;

import org.aopalliance.intercept.MethodInterceptor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.RowMapper;

import com.hbasesoft.framework.common.GlobalConstants;
import com.hbasesoft.framework.common.utils.PropertyHolder;
import com.hbasesoft.framework.db.core.config.DaoConfig;
import com.hbasesoft.framework.db.jdbc.AutoProxyBeanFactory;
import com.hbasesoft.framework.db.jdbc.BaseJdbcDao;
import com.hbasesoft.framework.db.jdbc.SpringDaoHandler4Jdbc;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2016年3月23日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.db.config <br>
 */
@Configuration
public class DataBase4JdbcConfig {

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    @Bean(name = "springDaoHandler4Jdbc")
    public MethodInterceptor registDaoHandler() {
        // dao处理类
        return new SpringDaoHandler4Jdbc();
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param dbType
     * @return <br>
     */
    @Bean(name = "autoProxyBeanFactory4Jdbc")
    public AutoProxyBeanFactory registAutoProxyBeanFactory(final @Value("${master.db.type}") String dbType) {

        AutoProxyBeanFactory beanFactory = new AutoProxyBeanFactory();

        // dao的配置
        DaoConfig dataConfig = new DaoConfig();
        dataConfig.setDbType(dbType);
        dataConfig.setBaseDaoType(BaseJdbcDao.class);
        dataConfig.setCallBackType(RowMapper.class);
        beanFactory.setConfig(dataConfig);
        beanFactory.setInterceptors("springDaoHandler4Jdbc");
        beanFactory.setPackagesToScan(getBasePackage());

        return beanFactory;
    }

    public static String[] getBasePackage() {
        return StringUtils.split(PropertyHolder.getProperty("db.basepackage", "com.hbasesoft.*"),
            GlobalConstants.SPLITOR);
    }

}
