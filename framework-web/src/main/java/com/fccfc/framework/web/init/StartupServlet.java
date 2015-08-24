/**************************************************************************************** 
 Copyright © 2003-2012 fccfc Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.fccfc.framework.web.init;

import java.lang.management.ManagementFactory;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.springframework.context.ApplicationContext;

import com.fccfc.framework.cache.core.CacheConstant;
import com.fccfc.framework.config.core.Configuration;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年7月25日 <br>
 * @since V1.0<br>
 * @see com.fccfc.framework.web.init <br>
 */
public class StartupServlet extends org.springframework.web.servlet.DispatcherServlet {

    /**
     * serialVersionUID <br>
     */
    private static final long serialVersionUID = 6364903462514241142L;

    /**
     * context
     */
    private static ServletContext context;

    /**
     * applicationContext
     */
    private static ApplicationContext applicationContext;

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param config <br>
     * @throws ServletException <br>
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        context = config.getServletContext();
        super.init(config);
        applicationContext = super.getWebApplicationContext();
        System.out.println(
            new StringBuilder().append("\n***************************************").append('\n').append("*            ")
                .append(ManagementFactory.getRuntimeMXBean().getName()).append("               *").append('\n')
                .append("*            ").append(Configuration.get(CacheConstant.LOCAL_MODULE_CODE)).append("模块启动成功！")
                .append("                  *").append('\n').append("***************************************"));

    }

    public static ServletContext getContext() {
        return context;
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }
}
