/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.db;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.hbasesoft.framework.common.utils.logger.LoggerUtil;
import com.hbasesoft.framework.db.core.DaoException;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2024年12月13日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.db <br>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TransactionUtil {

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param <T>
     * @param invoker
     * @return <br>
     */
    public static <T> T withSession(final SessionInvoker<T> invoker) {
        PlatformTransactionManager tm = TransactionManagerHolder.getTransactionManager();
        TransactionStatus ts = tm.getTransaction(new DefaultTransactionDefinition());
        try {
            T data = invoker.invoke(tm, ts);
            if (!ts.isCompleted()) {
                tm.commit(ts);
            }
            return data;
        }
        catch (Throwable e) {
            tm.rollback(ts);
            LoggerUtil.error(e);
            throw new DaoException(e);
        }
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param invoker
     */
    public static void withSession(final SessionInvokerWithNull invoker) {
        PlatformTransactionManager tm = TransactionManagerHolder.getTransactionManager();
        TransactionStatus ts = tm.getTransaction(new DefaultTransactionDefinition());
        try {
            invoker.invoke(tm, ts);
            if (!ts.isCompleted()) {
                tm.commit(ts);
            }
        }
        catch (Throwable e) {
            tm.rollback(ts);
            LoggerUtil.error(e);
            throw new DaoException(e);
        }
    }

    /**
     * <Description> <br>
     * 
     * @author 王伟<br>
     * @version 1.0<br>
     * @taskId <br>
     * @CreateDate 2024年12月13日 <br>
     * @since V1.0<br>
     * @see com.hbasesoft.framework.db <br>
     */
    @FunctionalInterface
    public interface SessionInvokerWithNull {

        /**
         * Description: <br>
         * 
         * @author 王伟<br>
         * @taskId <br>
         * @param tm
         * @param ts
         */
        void invoke(PlatformTransactionManager tm, TransactionStatus ts) throws Throwable;
    }

    /**
     * <Description> <br>
     * 
     * @param <T> T
     * @author 王伟<br>
     * @version 1.0<br>
     * @taskId <br>
     * @CreateDate 2024年12月13日 <br>
     * @since V1.0<br>
     * @see com.hbasesoft.framework.db <br>
     */
    @FunctionalInterface
    public interface SessionInvoker<T> {

        /**
         * Description: <br>
         * 
         * @author 王伟<br>
         * @taskId <br>
         * @param tm
         * @param ts
         * @return <br>
         */
        T invoke(PlatformTransactionManager tm, TransactionStatus ts) throws Throwable;
    }
}
