/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.db.jdbc.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.RowMapper;

import com.hbasesoft.framework.db.core.config.DaoConfig;
import com.hbasesoft.framework.db.core.executor.ISqlExcutor;
import com.hbasesoft.framework.db.core.executor.ISqlExcutorFactory;
import com.hbasesoft.framework.db.core.spring.AutoProxyBeanFactory;
import com.hbasesoft.framework.db.core.spring.SpringDaoHandler;
import com.hbasesoft.framework.db.jdbc.BaseJdbcDao;
import com.hbasesoft.framework.db.jdbc.Dao4Jdbc;

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
    @ConditionalOnMissingBean(SpringDaoHandler.class)
    @Bean("springDaoHandler")
    public SpringDaoHandler registDaoHandler() {
        // dao处理类
        return new SpringDaoHandler();
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

        AutoProxyBeanFactory beanFactory = new AutoProxyBeanFactory(new ISqlExcutorFactory() {

            @Override
            public ISqlExcutor create() {
                return new BaseJdbcDao();
            }
        }, Dao4Jdbc.class);

        // dao的配置
        DaoConfig dataConfig = new DaoConfig();
        dataConfig.setDbType(dbType);
        dataConfig.setBaseDaoType(BaseJdbcDao.class);
        dataConfig.setCallBackType(RowMapper.class);
        beanFactory.setConfig(dataConfig);
        beanFactory.setInterceptors("springDaoHandler");

        return beanFactory;
    }
}
