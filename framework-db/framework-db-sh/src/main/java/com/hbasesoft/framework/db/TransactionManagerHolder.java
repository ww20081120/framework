/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.db;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.InitializationException;
import com.hbasesoft.framework.common.utils.Assert;
import com.hbasesoft.framework.common.utils.PropertyHolder;
import com.hbasesoft.framework.db.core.DynamicDataSourceManager;
import com.hbasesoft.framework.db.core.utils.DataSourceUtil;

import lombok.NoArgsConstructor;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2018年5月31日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.db <br>
 */
@NoArgsConstructor
public final class TransactionManagerHolder {

    private static Map<String, PlatformTransactionManager> transactionManagerHolder = new ConcurrentHashMap<>();

    private static Map<String, SessionFactory> sessionFactoryHolder = new ConcurrentHashMap<>();

    /**
     * Description: 获取Spring trascation <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public static PlatformTransactionManager getTransactionManager() {

        synchronized (transactionManagerHolder) {
            String dbCode = DynamicDataSourceManager.getDataSourceCode();
            PlatformTransactionManager manager = transactionManagerHolder.get(dbCode);

            if (manager == null) {
                HibernateTransactionManager hibernateTransactionManager = new HibernateTransactionManager();
                hibernateTransactionManager.setSessionFactory(getSessionFactory());
                manager = hibernateTransactionManager;
                transactionManagerHolder.put(dbCode, manager);
            }

            return manager;
        }
    }

    /**
     * Description: 获取session 工厂 <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     * @throws IOException
     */
    public static SessionFactory getSessionFactory() {
        synchronized (sessionFactoryHolder) {
            String dbCode = DynamicDataSourceManager.getDataSourceCode();

            SessionFactory sessionFactory = sessionFactoryHolder.get(dbCode);

            if (sessionFactory == null) {
                DataSource dataSource = DataSourceUtil.getDataSource(dbCode);
                Assert.notNull(dataSource, ErrorCodeDef.DB_DATASOURCE_NOT_SET);

                LocalSessionFactoryBean bean = new LocalSessionFactoryBean();
                bean.setDataSource(dataSource);
                Map<String, String> map = PropertyHolder.getProperties();
                Properties properties = new Properties();
                for (Entry<String, String> entry : map.entrySet()) {
                    if (entry.getKey().startsWith("db.hibernate")) {
                        properties.setProperty(entry.getKey().substring(3, entry.getKey().length()), entry.getValue());
                    }
                }
                bean.setHibernateProperties(properties);
                bean.setPackagesToScan(getBasePackage());
                try {
                    bean.afterPropertiesSet();
                }
                catch (IOException e) {
                    throw new InitializationException(e);
                }
                sessionFactory = bean.getObject();
                sessionFactoryHolder.put(dbCode, sessionFactory);
            }

            return sessionFactory;
        }
    }

    public static String getBasePackage() {
        return "com.hbasesoft.*";
    }
}
