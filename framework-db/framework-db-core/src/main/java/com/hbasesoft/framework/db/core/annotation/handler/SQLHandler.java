/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.db.core.annotation.handler;

import java.lang.reflect.Method;

import org.apache.commons.lang3.ArrayUtils;

import com.hbasesoft.framework.common.InitializationException;
import com.hbasesoft.framework.db.core.config.DaoConfig;

/**
 * <Description> <br>
 * 
 * @author 王伟 <br>
 * @version 1.0 <br>
 * @CreateDate 2014年10月24日 <br>
 * @see com.hbasesoft.framework.dao.annotation.handler <br>
 */
public class SQLHandler extends AbstractAnnotationHandler {

    /** 
     * @param daoConfig 
     */ 
    public SQLHandler(final DaoConfig daoConfig) {
        super(daoConfig);
    }

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param clazz <br>
     * @throws InitializationException <br>
     */
    public void invoke(final Class<?> clazz) throws InitializationException {
        Method[] methods = clazz.getDeclaredMethods();
        if (ArrayUtils.isNotEmpty(methods)) {
            for (Method method : methods) {
                // Step1:判断是否是BaseDaoExcutor方法
                if (getBaseDaoExcutor(method) != null) {
                    continue;
                }

                // Step2:缓存SQL模板
                cacheSqlTemplate(method);

                // Step3:缓存Sql参数
                cacheSqlParamMetadata(method);
            }
        }
    }

}
