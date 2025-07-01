package com.hbasesoft.framework.ai.jmanus.config;

import com.hbasesoft.framework.common.utils.PropertyHolder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * ManusProperties 类用于绑定和管理 `manus` 前缀的配置属性。
 * 该类通过 Spring 的 `@ConfigurationProperties` 注解，从配置文件中读取相应的配置项，
 * 并提供对应的 getter 和 setter 方法来访问和修改这些配置。同时，使用 `@ConfigProperty` 注解对配置项进行元数据描述。
 * 配置项涉及浏览器设置、智能体执行参数、交互模式等多个方面。
 */
@Component
@ConfigurationProperties(prefix = "manus")
public class ManusProperties {

    /**
     * 浏览器模式 debug
     */
    private volatile Boolean browserDebug;

    /**
     * 智能体执行最大步数
     */
    private volatile Integer maxSteps;

    public Boolean getBrowserDebug() {
        String configPath = "manus.browser.debug";
        String value = PropertyHolder.getProperty(configPath);
        if (value != null) {
            browserDebug = Boolean.valueOf(value);
        }
        return browserDebug;
    }

    public void setBrowserDebug(Boolean browserDebug) {
        this.browserDebug = browserDebug;
    }

    public Integer getMaxSteps() {
        String configPath = "manus.maxSteps";
        String value = PropertyHolder.getProperty(configPath);
        if (value != null) {
            maxSteps = Integer.valueOf(value);
        }
        return maxSteps;
    }

    public void setMaxSteps(Integer maxSteps) {
        this.maxSteps = maxSteps;
    }
}
