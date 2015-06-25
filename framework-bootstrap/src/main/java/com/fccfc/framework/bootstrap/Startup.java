/**************************************************************************************** 
 Copyright © 2003-2012 ZTEsoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.fccfc.framework.bootstrap;

import java.io.IOException;
import java.lang.management.ManagementFactory;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.fccfc.framework.cache.core.CacheConstant;
import com.fccfc.framework.common.utils.logger.Logger;
import com.fccfc.framework.config.core.Configuration;

/**
 * <Description> <br>
 * 
 * @author 王伟 <br>
 * @version 1.0 <br>
 * @CreateDate 2015年6月23日 <br>
 * @see com.fccfc.framework.bootstrap <br>
 */
public class Startup {

    private static Logger logger = new Logger(Startup.class);

    private static ApplicationContext context;

    /**
     * Description: <br>
     * 
     * @author 王伟 <br>
     * @param args <br>
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        logger.info("====================>准备加载Spring配置文件<====================");
        ClassPathXmlApplicationContext ac = new ClassPathXmlApplicationContext("classpath:/META-INF/spring/*.xml");
        context = ac;
        ac.start();
        logger.info("====================>Spring配置文件加载完毕<====================");
        System.out.println(new StringBuilder().append("\n***************************************").append('\n')
            .append("*         ").append(ManagementFactory.getRuntimeMXBean().getName()).append("        *")
            .append('\n').append("*            ").append(Configuration.get(CacheConstant.LOCAL_MODULE_CODE))
            .append("模块启动成功！").append("                                   *").append('\n')
            .append("***************************************"));
        System.in.read();
        ac.close();
        logger.info("====================>系统正常停止运行<====================");
    }

    public static ApplicationContext getContext() {
        return context;
    }
}
