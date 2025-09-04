/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.db.core.utils;

import java.util.ServiceLoader;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.hbasesoft.framework.common.utils.ContextHolder;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;
import com.hbasesoft.framework.db.core.DaoException;
import com.hbasesoft.framework.db.core.ITransactionManager;

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

    /** transactionManager */
    private static ITransactionManager transactionManager;

    private static ITransactionManager getTransactionManager() {
        if (transactionManager == null) {
            ServiceLoader<ITransactionManager> loader = ServiceLoader.load(ITransactionManager.class);
            if (loader.iterator().hasNext()) {
                transactionManager = loader.iterator().next();
            }
            else {
                transactionManager = ContextHolder.getContext().getBean(ITransactionManager.class);
            }
        }
        return transactionManager;
    }

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
        PlatformTransactionManager tm = getTransactionManager().getTransactionManager();
        TransactionStatus ts = null;
        if (tm != null) {
            ts = tm.getTransaction(new DefaultTransactionDefinition());
        }
        try {
            T data = invoker.invoke(tm, ts);
            if (tm != null && ts != null && !ts.isCompleted()) {
                tm.commit(ts);
            }
            return data;
        }
        catch (Throwable e) {
            if (tm != null && ts != null) {
                tm.rollback(ts);
            }
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
        PlatformTransactionManager tm = getTransactionManager().getTransactionManager();
        TransactionStatus ts = null;
        if (tm != null) {
            ts = tm.getTransaction(new DefaultTransactionDefinition());
        }
        try {
            invoker.invoke(tm, ts);
            if (tm != null && ts != null && !ts.isCompleted()) {
                tm.commit(ts);
            }
        }
        catch (Throwable e) {
            if (tm != null && ts != null) {
                tm.rollback(ts);
            }
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
