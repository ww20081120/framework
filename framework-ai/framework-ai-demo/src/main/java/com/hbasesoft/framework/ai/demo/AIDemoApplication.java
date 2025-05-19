package com.hbasesoft.framework.ai.demo;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import com.hbasesoft.framework.common.Bootstrap;

/**
 * @Author: fb
 * @Description
 * @Date: Create in 11:00 2018/2/5
 * @Modified By
 */
@EnableAutoConfiguration
@ComponentScan(basePackages = "com.hbasesoft.framework.ai.demo")
public class AIDemoApplication {

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param args <br>
     */
    public static void main(final String[] args) {
        Bootstrap.before();
        ConfigurableApplicationContext context = new SpringApplicationBuilder(AIDemoApplication.class).headless(false)
            .run(args);
        Bootstrap.after(context);
    }
}
