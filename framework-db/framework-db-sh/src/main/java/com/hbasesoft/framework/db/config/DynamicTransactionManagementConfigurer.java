/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.db.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;

import com.hbasesoft.framework.db.TransactionManagerHolder;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2018年5月31日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.db.config <br>
 */
@Configuration
@EnableTransactionManagement(order = 2)
public class DynamicTransactionManagementConfigurer implements TransactionManagementConfigurer {

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    @Override
    public PlatformTransactionManager annotationDrivenTransactionManager() {
        return new ProxyPlatformTransactionManager();
    }

    private static class ProxyPlatformTransactionManager implements PlatformTransactionManager {

        /**
         * Description: <br>
         * 
         * @author 王伟<br>
         * @taskId <br>
         * @param definition
         * @return
         * @throws TransactionException <br>
         */
        @Override
        public TransactionStatus getTransaction(final TransactionDefinition definition) throws TransactionException {
            return TransactionManagerHolder.getTransactionManager().getTransaction(definition);
        }

        /**
         * Description: <br>
         * 
         * @author 王伟<br>
         * @taskId <br>
         * @param status
         * @throws TransactionException <br>
         */
        @Override
        public void commit(final TransactionStatus status) throws TransactionException {
            if (!status.isRollbackOnly()) {
                TransactionManagerHolder.getTransactionManager().commit(status);
            }
        }

        /**
         * Description: <br>
         * 
         * @author 王伟<br>
         * @taskId <br>
         * @param status
         * @throws TransactionException <br>
         */
        @Override
        public void rollback(final TransactionStatus status) throws TransactionException {
            TransactionManagerHolder.getTransactionManager().rollback(status);
        }

    }

}
