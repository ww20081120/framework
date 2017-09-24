/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.common.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.hbasesoft.framework.common.FrameworkException;
import com.hbasesoft.framework.common.StartupListener;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2016年3月17日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.common.utils <br>
 */
@Component
public class ContextHolder implements ApplicationContextAware {
    private static ApplicationContext context;

    public static ApplicationContext getContext() {
        return ContextHolder.context;
    }

    public static void setContext(ApplicationContext context) {
        ContextHolder.context = context;
    }

    public static class ContextHolderListener implements StartupListener {

        /**
         * Description: <br>
         * 
         * @author 王伟<br>
         * @taskId <br>
         * @return <br>
         */
        @Override
        public LoadOrder getOrder() {
            return LoadOrder.FIRST;
        }

        /**
         * Description: <br>
         * 
         * @author 王伟<br>
         * @taskId <br>
         * @param context
         * @throws FrameworkException <br>
         */
        @Override
        public void complete(ApplicationContext context) throws FrameworkException {
            ContextHolder.context = context;
        }
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param arg0
     * @throws BeansException <br>
     */
    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        if (ContextHolder.context == null) {
            ContextHolder.context = context;
        }
    }
}
