package com.hbasesoft.framework.ai.jmanus.dynamic.agent.vo;

import lombok.Data;

/**
 * Tool类代表一个工具的配置信息
 * 它包含了工具的关键字、名称、描述、启用状态以及服务组等信息
 */
@Data
public class Tool {

    /**
     * 工具的关键字，用于唯一标识一个工具
     */
    private String key;

    /**
     * 工具的名称，用于显示工具的全名
     */
    private String name;

    /**
     * 工具的描述，提供了工具功能的详细说明
     */
    private String description;

    /**
     * 工具的启用状态，表示工具是否可用
     * 当enabled为true时，表示工具是可用的；当为false时，表示工具不可用
     */
    private boolean enabled;

    /**
     * 工具所属的服务组，用于将工具分类管理
     */
    private String serviceGroup;
}
