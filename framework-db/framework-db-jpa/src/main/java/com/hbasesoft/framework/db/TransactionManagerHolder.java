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
import com.hbasesoft.framework.common.utils.bean.BasePackagesUtil;
import com.hbasesoft.framework.db.core.DynamicDataSourceManager;
import com.hbasesoft.framework.db.core.config.DaoTypeDef;
import com.hbasesoft.framework.db.orm.util.DataSourceUtil;

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

    /** transaction */
    private static Map<String, PlatformTransactionManager> transactionManagerHolder = new ConcurrentHashMap<>();

    /** session factory */
    private static Map<String, SessionFactory> sessionFactoryHolder = new ConcurrentHashMap<>();

    /**
     * Description: 获取Spring trascation <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public static PlatformTransactionManager getTransactionManager() {
        String key = DynamicDataSourceManager.getInstance(DaoTypeDef.db).getDataSourceCode();
        return transactionManagerHolder.computeIfAbsent(key, dbCode -> {
            HibernateTransactionManager hibernateTransactionManager = new HibernateTransactionManager();
            hibernateTransactionManager.setSessionFactory(getSessionFactory(dbCode));
            return hibernateTransactionManager;
        });
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param key
     * @return <br>
     */
    public static SessionFactory getSessionFactory(final String key) {
        return sessionFactoryHolder.computeIfAbsent(key, dbCode -> {
            DataSource dataSource = DataSourceUtil.getDataSource(dbCode);
            Assert.notNull(dataSource, ErrorCodeDef.DB_DATASOURCE_NOT_SET, dbCode);

            LocalSessionFactoryBean bean = new LocalSessionFactoryBean();
            bean.setDataSource(dataSource);
            Map<String, String> map = PropertyHolder.getProperties();
            Properties properties = new Properties();
            int prefixLength = dbCode.length() + 1;
            String prefix = dbCode + ".hibernate";
            for (Entry<String, String> entry : map.entrySet()) {
                if (entry.getKey().startsWith(prefix)) {
                    properties.setProperty(entry.getKey().substring(prefixLength, entry.getKey().length()),
                        entry.getValue());
                }
            }
            bean.setHibernateProperties(properties);
            bean.setPackagesToScan(BasePackagesUtil.getBasePackages().toArray(new String[0]));
            try {
                bean.afterPropertiesSet();
            }
            catch (IOException e) {
                throw new InitializationException(e);
            }
            return bean.getObject();
        });

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
        String dbCode = DynamicDataSourceManager.getInstance(DaoTypeDef.db).getDataSourceCode();
        return getSessionFactory(dbCode);
    }
}
