package com.hbasesoft.framework.ai.jmanus.recorder.entity;

import com.hbasesoft.framework.ai.jmanus.recorder.entity.AgentExecutionRecord;
import com.hbasesoft.framework.common.utils.date.DateUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 记录智能体在单个执行步骤中的思考和行动过程。 作为AgentExecutionRecord的子步骤存在，专注于记录思考和行动阶段的处理消息。
 * <p>
 * 数据结构简化为三个主要部分：
 * <p>
 * 1. 基本信息 (Basic Info) - id: 记录的唯一标识 - stepNumber: 步骤编号 - parentExecutionId: 父执行记录ID
 * <p>
 * 2. 思考阶段 (Think Phase) - thinkStartTime: 思考开始时间 - thinkInput: 思考输入内容 - thinkOutput:
 * 思考输出结果 - thinkEndTime: 思考结束时间
 * <p>
 * 3. 行动阶段 (Act Phase) - actStartTime: 行动开始时间 - toolName: 使用的工具名称 - toolParameters: 工具参数 -
 * actionNeeded: 是否需要执行动作 - actionDescription: 行动描述 - actionResult: 行动执行结果 - actEndTime:
 * 行动结束时间 - status: 执行状态 - errorMessage: 错误信息（如有）
 *
 * @see AgentExecutionRecord
 */
@NoArgsConstructor
@Data
public class ThinkActRecord {


    // 记录的唯一标识符
    private Long id;

    // 父执行记录的ID，关联到AgentExecutionRecord
    private Long parentExecutionId;

    // 思考开始的时间戳
    private Date thinkStartTime;

    // 思考完成的时间戳
    private Date thinkEndTime;

    // 行动开始的时间戳
    private Date actStartTime;

    // 行动完成的时间戳
    private Date actEndTime;

    // 思考过程的输入上下文
    private String thinkInput;

    // 思考过程的输出结果
    private String thinkOutput;

    // 思考是否确定需要采取行动
    private boolean actionNeeded;

    // 将要采取的行动描述
    private String actionDescription;

    // 行动执行的结果
    private String actionResult;

    // 此思考-行动周期的状态（成功、失败等）
    private String status;

    // 如果周期遇到问题的错误消息
    private String errorMessage;

    // 用于行动的工具名称（如适用）
    private String toolName;

    // 用于行动的工具参数（序列化，如适用）
    private String toolParameters;

    // 带父执行ID的构造函数
    public ThinkActRecord(Long parentExecutionId) {
        this.parentExecutionId = parentExecutionId;
        this.thinkStartTime = DateUtil.getCurrentDate();
    }


    /**
     * 记录思考阶段开始
     */
    public void startThinking(String thinkInput) {
        this.thinkStartTime = DateUtil.getCurrentDate();
        this.thinkInput = thinkInput;
    }


    /**
     * 记录思考阶段结束
     */
    public void finishThinking(String thinkOutput) {
        this.thinkEndTime = DateUtil.getCurrentDate();
        this.thinkOutput = thinkOutput;
    }


    /**
     * 记录行动阶段开始
     */
    public void startAction(String actionDescription, String toolName, String toolParameters) {
        this.actStartTime = DateUtil.getCurrentDate();
        this.actionNeeded = true;
        this.actionDescription = actionDescription;
        this.toolName = toolName;
        this.toolParameters = toolParameters;
    }


    /**
     * 记录行动阶段结束
     */
    public void finishAction(String actionResult, String status) {
        this.actEndTime = DateUtil.getCurrentDate();
        this.actionResult = actionResult;
        this.status = status;
    }

    /**
     * 记录错误信息
     */
    public void recordError(String errorMessage) {
        this.errorMessage = errorMessage;
        this.status = "ERROR";
    }

    /**
     * 保存记录到持久化存储 空实现，由具体的存储实现来覆盖
     * @return 保存后的记录ID
     */
    public Long save() {
        // 如果ID为空，生成一个随机ID
        if (this.id == null) {
            // 使用时间戳和随机数组合生成ID
            long timestamp = System.currentTimeMillis();
            int random = (int) (Math.random() * 1000000);
            this.id = timestamp * 1000 + random;
        }
        return this.id;
    }

}
