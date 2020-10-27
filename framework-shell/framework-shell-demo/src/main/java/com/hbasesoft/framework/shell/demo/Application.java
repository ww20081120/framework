/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.shell.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import com.hbasesoft.framework.common.Bootstrap;
import com.hbasesoft.framework.common.utils.CommonUtil;
import com.hbasesoft.framework.common.utils.PropertyHolder;
import com.hbasesoft.framework.shell.core.RemoteShell;
import com.hbasesoft.framework.shell.core.Shell;

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
@ComponentScan(basePackages = "com.hbasesoft")
@SpringBootApplication
public class Application {
    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param args <br>
     * @throws Exception
     * @throws InterruptedException
     */
    public static void main(final String[] args) throws Exception {
        Bootstrap.before();
        ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
        Bootstrap.after(context);

        if (CommonUtil.isEmpty(args) && PropertyHolder.getBooleanProperty("server.remote", false)) {
            RemoteShell.run();
        }
        else {
            new Shell(System.in, System.out).run(args);
        }
    }
}
