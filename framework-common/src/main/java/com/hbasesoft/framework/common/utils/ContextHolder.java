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

    /** context */
    private static ApplicationContext context;

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public static ApplicationContext getContext() {
        return ContextHolder.context;
    }

    /**
     * Description: context<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param c <br>
     */
    public static void setContext(final ApplicationContext c) {
        ContextHolder.context = c;
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
         * @param c
         * @throws FrameworkException <br>
         */
        @Override
        public void complete(final ApplicationContext c) throws FrameworkException {
            ContextHolder.context = c;
        }
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param c
     * @throws BeansException <br>
     */
    @Override
    public void setApplicationContext(final ApplicationContext c) throws BeansException {
        if (ContextHolder.context == null) {
            ContextHolder.context = c;
        }
    }
}
