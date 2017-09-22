/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.common;

import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;

/**
 * <Description> <br>
 * 
 * @author 王伟 <br>
 * @version 1.0 <br>
 * @CreateDate 2015年6月23日 <br>
 * @see com.hbasesoft.framework.bootstrap <br>
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.hbasesoft")
@ImportResource("classpath*:META-INF/spring/*.xml")
@EnableAutoConfiguration(exclude = {
    DataSourceAutoConfiguration.class
})
public class Application {

    /**
     * Description: <br>
     * 
     * @author 王伟 <br>
     * @param args <br>
     * @throws IOException <br>
     */
    public static void main(String[] args) throws Exception {
        Bootstrap.before();
        ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
        Bootstrap.after(context);
    }
}
