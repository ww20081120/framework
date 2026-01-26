/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.demo.agentscope;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.hbasesoft.framework.common.Bootstrap;
import com.hbasesoft.framework.common.utils.PropertyHolder;

import io.agentscope.core.studio.StudioManager;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2018年1月24日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.vcc.sgp.bootstrap.plat <br>
 */
@EnableAutoConfiguration
@ComponentScan(basePackages = "com.hbasesoft")
@Configuration
@SpringBootApplication
public class Application {
    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param args <br>
     */
    public static void main(final String[] args) {
        // 初始化 Studio 连接
        StudioManager.init().studioUrl("http://localhost:3000").project(PropertyHolder.getProjectName())
            .runName("demo_" + System.currentTimeMillis()).initialize().block();
        Bootstrap.before();
        ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
        Bootstrap.after(context);
    }
}
