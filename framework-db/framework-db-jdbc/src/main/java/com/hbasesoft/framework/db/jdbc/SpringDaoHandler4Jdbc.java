/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.db.jdbc;

import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import com.hbasesoft.framework.common.utils.logger.Logger;
import com.hbasesoft.framework.db.core.annotation.handler.DaoHandler;

/**
 * <Description> <br>
 * 
 * @author 王伟 <br>
 * @version 1.0 <br>
 * @CreateDate 2014年10月23日 <br>
 * @see com.hbasesoft.framework.dao.annotation.handler <br>
 */
public class SpringDaoHandler4Jdbc implements MethodInterceptor {

    /**
     * logger
     */
    private static Logger logger = new Logger(SpringDaoHandler4Jdbc.class);

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param invocation <br>
     * @return <br>
     * @throws Throwable <br>
     */
    @Override
    public Object invoke(final MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();
        Object[] args = invocation.getArguments();

        try {
            DaoHandler handler = (DaoHandler) invocation.getThis();
            return handler.invoke(null, method, args);
        }
        catch (Exception e) {
            logger.error(e);
            throw e;
        }
    }

}
