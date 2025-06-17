package com.hbasesoft.framework.ai.demo.jmanus.recorder;

import com.hbasesoft.framework.ai.demo.jmanus.recorder.entity.AgentExecutionRecord;
import com.hbasesoft.framework.ai.demo.jmanus.recorder.entity.ThinkActRecord;

/**
 * 计划执行记录器接口， 定义了记录和检索计划执行详情的方法
 */
public interface PlanExecutionRecorder {

    /**
     * 记录一个计划实例， 返回其唯一标识符
     *
     * @param stepRecord 计划执行记录
     * @return 计划ID
     */
    String recordPlanExecution(PlanExecutionRecorder stepRecord);

    /**
     * 记录智能体执行实例， 关联到特定的计划
     *
     * @param planId      计划ID
     * @param agentRecord 智能体执行记录
     * @return 智能体执行ID
     */
    Long recordAgentExecution(String planId, AgentExecutionRecord agentRecord);

    /**
     * 记录思考-行动执行实例， 关联到特定的智能体执行
     *
     * @param planId
     * @param agentExecutionId
     * @param thinkActRecord
     */
    void recordThinkActExecution(String planId, Long agentExecutionId, ThinkActRecord thinkActRecord);

    /**
     * 标记计划执行完成
     *
     * @param planId  计划ID
     * @param summary 计划总结
     */
    void recordPlanCompletion(String planId, String summary);

    /**
     * 获取计划执行记录
     *
     * @param planId 计划ID
     * @return 计划执行记录
     */
    PlanExecutionRecorder getExecutionRecorder(String planId);

    /**
     * 将制定计划ID的执行记录保存到持久化存储 此方法会递归调用 PlanExecutionRecord、 AgentExecutionRecord、 ThinkActRecord 的 save 方法
     *
     * @param planId 计划ID
     * @return 是否保存成功
     */
    boolean savePlanExecutionRecorders(String planId);

    /**
     * 保存所有执行记录 此方法会递归调用 PlanExecutionRecord、 AgentExecutionRecord、 ThinkActRecord 的 save 方法
     */
    void saveAllExecutionRecorders();

    /**
     * 获取指定计划的当前智能体执行记录
     *
     * @param planId 计划ID
     * @return 智能体执行记录， 如果没有则返回 null
     */
    AgentExecutionRecord getCurrentAgentExecutionRecord(String planId);

    /**
     * 删除指定计划执行记录
     *
     * @param planId 计划ID
     */
    void removeExecutionRecord(String planId);
}
