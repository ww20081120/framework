package com.hbasesoft.framework.ai.agent.recorder.model;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

/**
 * 记录智能体在单个执行步骤中的思考和行动过程。 作为AgentExecutionRecord的子步骤存在，专注于记录思考和行动阶段的处理消息。
 * <p>
 * 数据结构简化为三个主要部分：
 * <p>
 * 1. 基本信息 (Basic Info) - id: 记录的唯一标识 - stepNumber: 步骤编号 - parentExecutionId:
 * 父执行记录ID
 * <p>
 * 2. 思考阶段 (Think Phase) - thinkStartTime: 思考开始时间 - thinkInput: 思考输入内容 -
 * thinkOutput: 思考输出结果 - thinkEndTime: 思考结束时间
 * <p>
 * 3. 行动阶段 (Act Phase) - actStartTime: 行动开始时间 - toolName: 使用的工具名称 -
 * toolParameters: 工具参数 - actionNeeded: 是否需要执行动作 - actionDescription: 行动描述 -
 * actionResult: 行动执行结果 - actEndTime: 行动结束时间 - status: 执行状态 - errorMessage:
 * 错误信息（如有）
 *
 * @see AgentExecutionRecord
 */
@Data
public class ThinkActRecord {

	// Unique identifier of the record
	private Long id;

	// Method to generate unique ID
	private Long generateId() {
		if (this.id == null) {
			long timestamp = System.currentTimeMillis();
			int random = (int) (Math.random() * 1000000);
			this.id = timestamp * 1000 + random;
		}
		return this.id;
	}

	// ID of parent execution record, linked to AgentExecutionRecord
	private Long parentExecutionId;

	// Timestamp when thinking started
	private LocalDateTime thinkStartTime;

	// Timestamp when thinking completed
	private LocalDateTime thinkEndTime;

	// Timestamp when action started
	private LocalDateTime actStartTime;

	// Timestamp when action completed
	private LocalDateTime actEndTime;

	// Input context for the thinking process
	private String thinkInput;

	// Output result of the thinking process
	private String thinkOutput;

	// Whether thinking determined that action is needed
	private boolean actionNeeded;

	// Description of the action to be taken
	private String actionDescription;

	// Result of action execution
	private String actionResult;

	// Status of this think-act cycle (success, failure, etc.)
	private ExecutionStatus status;

	// Error message if the cycle encountered problems
	private String errorMessage;

	// Tool name used for action (if applicable)
	private String toolName;

	// Tool parameters used for action (serialized, if applicable)
	private String toolParameters;

	// Sub-plan execution record for tool calls that create new execution plans
	private PlanExecutionRecord subPlanExecutionRecord;

	// Action tool information(When disabling parallel tool calls, there is always
	// only
	// one)
	private List<ActToolInfo> actToolInfoList;

	// Default constructor
	public ThinkActRecord() {
		this.id = generateId();
	}

	// Constructor with parent execution ID
	public ThinkActRecord(Long parentExecutionId) {
		this.parentExecutionId = parentExecutionId;
		this.thinkStartTime = LocalDateTime.now();
		this.id = generateId();
	}

	/**
	 * Record the start of thinking phase
	 */
	public void startThinking(String thinkInput) {

		this.thinkStartTime = LocalDateTime.now();
		this.thinkInput = thinkInput;
	}

	/**
	 * Record the end of thinking phase
	 */
	public void finishThinking(String thinkOutput) {
		this.thinkEndTime = LocalDateTime.now();
		this.thinkOutput = thinkOutput;
	}

	/**
	 * Record the start of action phase
	 */
	public void startAction(String actionDescription, String toolName, String toolParameters) {
		this.actStartTime = LocalDateTime.now();
		this.actionNeeded = true;
		this.actionDescription = actionDescription;
		this.toolName = toolName;
		this.toolParameters = toolParameters;
	}

	/**
	 * Record the end of action phase
	 */
	public void finishAction(String actionResult, ExecutionStatus status) {
		this.actEndTime = LocalDateTime.now();
		this.actionResult = actionResult;
		this.status = status;
	}

	/**
	 * Record error information
	 */
	public void recordError(String errorMessage) {
		this.errorMessage = errorMessage;
		this.status = ExecutionStatus.RUNNING;
	}

	/**
	 * Save record to persistent storage. Empty implementation, to be overridden by
	 * specific storage implementations
	 * 
	 * @return Record ID after saving
	 */
	public Long save() {

		// Save sub-plan execution record if exists
		if (subPlanExecutionRecord != null) {
			subPlanExecutionRecord.save();
		}

		return this.id;
	}

	// Getters and setters

	public Long getId() {
		// Ensure ID is generated when accessing
		if (this.id == null) {
			this.id = generateId();
		}
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getParentExecutionId() {
		return parentExecutionId;
	}

	public void setParentExecutionId(Long parentExecutionId) {
		this.parentExecutionId = parentExecutionId;
	}

	public LocalDateTime getThinkStartTime() {
		return thinkStartTime;
	}

	public void setThinkStartTime(LocalDateTime thinkStartTime) {
		this.thinkStartTime = thinkStartTime;
	}

	public LocalDateTime getThinkEndTime() {
		return thinkEndTime;
	}

	public void setThinkEndTime(LocalDateTime thinkEndTime) {
		this.thinkEndTime = thinkEndTime;
	}

	public LocalDateTime getActStartTime() {
		return actStartTime;
	}

	public void setActStartTime(LocalDateTime actStartTime) {
		this.actStartTime = actStartTime;
	}

	public LocalDateTime getActEndTime() {
		return actEndTime;
	}

	public void setActEndTime(LocalDateTime actEndTime) {
		this.actEndTime = actEndTime;
	}

	public String getThinkInput() {
		return thinkInput;
	}

	public void setThinkInput(String thinkInput) {
		this.thinkInput = thinkInput;
	}

	public String getThinkOutput() {
		return thinkOutput;
	}

	public void setThinkOutput(String thinkOutput) {
		this.thinkOutput = thinkOutput;
	}

	public boolean isActionNeeded() {
		return actionNeeded;
	}

	public void setActionNeeded(boolean actionNeeded) {
		this.actionNeeded = actionNeeded;
	}

	public String getActionDescription() {
		return actionDescription;
	}

	public void setActionDescription(String actionDescription) {
		this.actionDescription = actionDescription;
	}

	public String getActionResult() {
		return actionResult;
	}

	public void setActionResult(String actionResult) {
		this.actionResult = actionResult;
	}

	public ExecutionStatus getStatus() {
		return status;
	}

	public void setStatus(ExecutionStatus status) {
		this.status = status;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getToolName() {
		return toolName;
	}

	public void setToolName(String toolName) {
		this.toolName = toolName;
	}

	public String getToolParameters() {
		return toolParameters;
	}

	public void setToolParameters(String toolParameters) {
		this.toolParameters = toolParameters;
	}

	public PlanExecutionRecord getSubPlanExecutionRecord() {
		return subPlanExecutionRecord;
	}

	public void setSubPlanExecutionRecord(PlanExecutionRecord subPlanExecutionRecord) {
		this.subPlanExecutionRecord = subPlanExecutionRecord;
	}

	/**
	 * Record a sub-plan execution triggered by tool call
	 * 
	 * @param subPlanRecord Sub-plan execution record
	 */
	public void recordSubPlanExecution(PlanExecutionRecord subPlanRecord) {
		this.subPlanExecutionRecord = subPlanRecord;
	}

	/**
	 * Check if this think-act record has a sub-plan execution
	 * 
	 * @return true if sub-plan exists, false otherwise
	 */
	public boolean hasSubPlanExecution() {
		return this.subPlanExecutionRecord != null;
	}

	public List<ActToolInfo> getActToolInfoList() {
		return actToolInfoList;
	}

	public void setActToolInfoList(List<ActToolInfo> actToolInfoList) {
		this.actToolInfoList = actToolInfoList;
	}

	@Override
	public String toString() {
		return "ThinkActRecord{" + "id='" + id + '\'' + ", parentExecutionId='" + parentExecutionId + '\''
				+ ", actionNeeded=" + actionNeeded + ", status='" + status + '\'' + '}';
	}

	public static class ActToolInfo {

		private String name;

		private String parameters;

		private String result;

		private String id;

		// Default constructor for Jackson deserialization
		public ActToolInfo() {
		}

		public ActToolInfo(String name, String arguments, String id) {
			this.name = name;
			this.parameters = arguments;
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public String getParameters() {
			return parameters;
		}

		public String getResult() {
			return result;
		}

		public void setResult(String result) {
			this.result = result;
		}

		public String getId() {
			return id;
		}

	}

}
