/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.jmanus.recorder.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.hbasesoft.framework.ai.jmanus.planning.model.vo.UserInputWaitState;
import com.hbasesoft.framework.common.utils.date.DateUtil;

import lombok.Data;

/**
 * <Description> <br>
 * 计划执行记录类，用于跟踪和记录规划流程执行过程的详细信息。 数据结构分为四个主要部分： 1. 基本信息 - id: 记录的唯一标识符 - planId: 计划的唯一标识符 - title: 计划标题 - startTime:
 * 执行开始时间 - endTime: 执行结束时间 - userRequest: 用户的原始请求 2. 计划结构 - steps: 计划步骤列表 - stepStatuses: 步骤状态列表 - stepNotes: 步骤备注列表 -
 * stepAgents: 与每个步骤关联的智能代理 3. 执行数据 - currentStepIndex: 当前正在执行的步骤索引 - agentExecutionRecords: 每个智能代理的执行记录 - executorKeys:
 * 执行器键列表 - resultState: 共享结果状态 4. 执行结果 - completed: 是否完成 - progress: 执行进度（百分比） - summary: 执行总结
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年7月1日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.jmanus.recorder.entity <br>
 */
@Data
public class PlanExecutionRecord {

    /** 记录的唯一标识符 */
    private Long id;

    /** 计划的唯一标识符 */
    private String planId;

    /** 计划标题 */
    private String title;

    /** 用户的原始请求 */
    private String userRequest;

    /** 执行开始的时间戳 */
    private Date startTime;

    /** 执行结束的时间戳 */
    private Date endTime;

    /** 计划步骤列表 */
    private List<String> steps;

    /** 当前正在执行的步骤索引 */
    private Integer currentStepIndex;

    /** 是否完成 */
    private boolean completed;

    /** 执行总结 */
    private String summary;

    /** 用于维护智能代理执行顺序的列表 */
    private List<AgentExecutionRecord> agentExecutionSequence;

    /** 用于存储用户输入等待状态的字段 */
    private UserInputWaitState userInputWaitState;

    /**
     * Constructor for creating a new execution record
     * 
     * @param planId The unique identifier for the plan.
     */
    public PlanExecutionRecord(String planId) {
        this.planId = planId;
        this.steps = new ArrayList<>();
        this.startTime = DateUtil.getCurrentDate();
        this.completed = false;
        this.agentExecutionSequence = new ArrayList<>();
    }

    /**
     * Add an execution step
     * 
     * @param step Step description
     * @param agentName Executing agent name
     */
    public void addStep(String step, String agentName) {
        this.steps.add(step);
    }

    /**
     * Add agent execution record
     * 
     * @param record Execution record
     */
    public void addAgentExecutionRecord(AgentExecutionRecord record) {
        this.agentExecutionSequence.add(record);
    }

    /**
     * Complete execution and set end time
     */
    public void complete(String summary) {
        this.endTime = DateUtil.getCurrentDate();
        this.completed = true;
        this.summary = summary;
    }

    /**
     * Save record to persistent storage. Empty implementation, to be overridden by specific storage implementations.
     * Also recursively saves all AgentExecutionRecord
     * 
     * @return Record ID after saving
     */
    public Long save() {
        // If ID is empty, generate a random ID
        if (this.id == null) {
            // Use combination of timestamp and random number to generate ID
            long timestamp = System.currentTimeMillis();
            int random = (int) (Math.random() * 1000000);
            this.id = timestamp * 1000 + random;
        }

        // Save all AgentExecutionRecords
        if (agentExecutionSequence != null) {
            for (AgentExecutionRecord agentRecord : agentExecutionSequence) {
                agentRecord.save();
            }
        }
        return this.id;
    }
}
