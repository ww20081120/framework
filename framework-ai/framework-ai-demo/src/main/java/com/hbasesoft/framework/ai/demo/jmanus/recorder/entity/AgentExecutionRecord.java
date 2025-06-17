package com.hbasesoft.framework.ai.demo.jmanus.recorder.entity;

import com.hbasesoft.framework.ai.demo.jmanus.agent.BaseAgent;
import com.hbasesoft.framework.common.utils.date.DateUtil;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 智能体执行记录类，用于跟踪和记录BaseAgent执行过程的详细信息。
 * <p>
 * 数据结构分为三个主要部分：
 * <p>
 * 1. 基本信息 (Basic Info) - id: 记录的唯一标识 - conversationId: 对话唯一标识 - agentName: 智能体名称 -
 * agentDescription: 智能体描述 - startTime: 执行开始时间 - endTime: 执行结束时间
 * <p>
 * 2. 执行过程数据 (Execution Data) - maxSteps: 最大执行步骤数 - currentStep: 当前执行步骤 - status:
 * 执行状态（IDLE, RUNNING, FINISHED） - thinkActSteps: 思考-行动步骤记录列表，每个元素是ThinkActRecord对象 -
 * agentRequest: 输入提示模板
 * <p>
 * 3. 执行结果 (Execution Result) - isCompleted: 是否完成 - isStuck: 是否卡住 - result: 执行结果 -
 * errorMessage: 错误信息（如有）
 *
 * @see BaseAgent
 * @see ThinkActRecord
 */
@Data
public class AgentExecutionRecord {

    // 记录的唯一标识符
    private Long id;

    // 此记录所属的对话ID
    private String conversationId;

    // 创建此记录的智能体名称
    private String agentName;

    // 智能体的描述信息
    private String agentDescription;

    // 执行开始的时间戳
    private Date startTime;

    // 执行结束的时间戳
    private Date endTime;

    // 最大允许的步骤数
    private int maxSteps;

    // 当前执行的步骤编号
    private int currentStep;

    // 执行状态（IDLE, RUNNING, FINISHED）
    private String status;

    // 是否执行完成
    private boolean isCompleted;

    // 是否卡住
    private boolean isStuck;

    // 思考-行动步骤的记录列表，作为子步骤存在
    private List<ThinkActRecord> thinkActSteps;

    // 用于智能体执行的请求内容
    private String agentRequest;

    // 执行结果
    private String result;

    // 如果执行遇到问题的错误消息
    private String errorMessage;

    // 默认构造函数
    public AgentExecutionRecord() {
        this.thinkActSteps = new ArrayList<>();
    }

    // 带参数的构造函数
    public AgentExecutionRecord(String conversationId, String agentName, String agentDescription) {
        this.conversationId = conversationId;
        this.agentName = agentName;
        this.agentDescription = agentDescription;
        this.startTime = DateUtil.getCurrentDate();
        this.status = "IDLE";
        this.isCompleted = false;
        this.isStuck = false;
        this.currentStep = 0;
        this.thinkActSteps = new ArrayList<>();
    }

    /**
     * 添加一个ThinkActRecord作为执行步骤
     * @param record ThinkActRecord实例
     */
    public void addThinkActStep(ThinkActRecord record) {
        if (this.thinkActSteps == null) {
            this.thinkActSteps = new ArrayList<>();
        }
        this.thinkActSteps.add(record);
        this.currentStep = this.thinkActSteps.size();
    }

    public void setThinkActSteps(List<ThinkActRecord> thinkActSteps) {
        this.thinkActSteps = thinkActSteps;
        this.currentStep = thinkActSteps != null ? thinkActSteps.size() : 0;
    }

    /**
     * 保存记录到持久化存储 空实现，由具体的存储实现来覆盖 同时会递归保存所有ThinkActRecord
     * @return 保存后的记录ID
     */
    public long save(){
        // 如果ID为空，生成一个随机ID
        if(this.id == null){
            // 使用时间戳和随机数组合生成ID
            long timestamp = System.currentTimeMillis();
            int random = (int) (Math.random() * 1000000);
            this.id = timestamp * 1000 + random;
        }

        // Save all ThinkActRecords
        if(this.thinkActSteps != null){
            for(ThinkActRecord thinkActRecord : this.thinkActSteps){
                thinkActRecord.save();
            }
        }
        return this.id;
    }
}
