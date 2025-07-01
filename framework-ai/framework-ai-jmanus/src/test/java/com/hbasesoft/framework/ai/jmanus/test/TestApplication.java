/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.jmanus.test;

import com.hbasesoft.framework.common.Bootstrap;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

/**
 * <Description> <br>
 *
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2018年9月13日 <br>
 * @see com.hbasesoft.framework.db.demo <br>
 * @since V1.0<br>
 */
@ComponentScan(basePackages = "com.hbasesoft.framework.ai.jmanus")
@SpringBootApplication
public class TestApplication {

    /**
     * Description: <br>
     *
     * @param args <br>
     * @author 王伟<br>
     * @taskId <br>
     */
    public static void main(final String[] args) {
        Bootstrap.before();
        ConfigurableApplicationContext context = SpringApplication.run(TestApplication.class, args);
        Bootstrap.after(context);
    }

}
