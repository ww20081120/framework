package com.hbasesoft.framework.db.demo.mysql;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import com.hbasesoft.framework.common.Bootstrap;

@TestConfiguration
public class GlobalConfig implements ApplicationListener<ContextRefreshedEvent> {

    static {
        Bootstrap.before();
    }

    @Override
    public void onApplicationEvent(final ContextRefreshedEvent event) {
        if (event.getApplicationContext().getParent() == null) {
            Bootstrap.after(event.getApplicationContext());
        }
    }
}
