package com.hbasesoft.framework.ai.demo;

import com.hbasesoft.framework.common.Bootstrap;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

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
     * @param args <br>
     * @author 王伟<br>
     * @taskId <br>
     */
    public static void main(final String[] args) {
        Bootstrap.before();
        ConfigurableApplicationContext context = new SpringApplicationBuilder(AIDemoApplication.class).run(args);
        Bootstrap.after(context);
    }
}
