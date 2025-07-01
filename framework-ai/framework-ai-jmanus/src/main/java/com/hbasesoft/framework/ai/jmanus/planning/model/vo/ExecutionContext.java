package com.hbasesoft.framework.ai.jmanus.planning.model.vo;

import lombok.Data;

@Data
public class ExecutionContext {

    /**
     * 计划的唯一标识符
     */
    private String planId;

    /**
     * 执行计划实体， 包含计划的详细信息和执行步骤
     */
    private ExecutionPlan plan;

    /**
     * 用户原始的请求内容
     */
    private String userRequest;

    /**
     * 是否使用记忆， 场景是如果只构建计划， 那么不应该用记忆，否则记忆无法删除
     */
    private boolean userMemory = false;

}
