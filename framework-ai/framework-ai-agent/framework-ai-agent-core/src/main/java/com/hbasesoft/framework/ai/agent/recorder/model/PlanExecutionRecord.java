/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.agent.recorder.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.hbasesoft.framework.ai.agent.planning.model.vo.UserInputWaitState;

/**
 * <Description> <br>
 * 计划执行记录类，用于跟踪和记录规划流程执行过程的详细信息。 数据结构分为四个主要部分： 1. 基本信息 - id: 记录的唯一标识符 - planId:
 * 计划的唯一标识符 - title: 计划标题 - startTime: 执行开始时间 - endTime: 执行结束时间 - userRequest:
 * 用户的原始请求 2. 计划结构 - steps: 计划步骤列表 - stepStatuses: 步骤状态列表 - stepNotes: 步骤备注列表 -
 * stepAgents: 与每个步骤关联的智能代理 3. 执行数据 - currentStepIndex: 当前正在执行的步骤索引 -
 * agentExecutionRecords: 每个智能代理的执行记录 - executorKeys: 执行器键列表 - resultState:
 * 共享结果状态 4. 执行结果 - completed: 是否完成 - progress: 执行进度（百分比） - summary: 执行总结
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年7月1日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.agent.recorder.model <br>
 */
public class PlanExecutionRecord {

	// Unique identifier for the record
	private Long id;

	// Unique identifier for the plan
	private String currentPlanId;

	// Parent plan ID for sub-plans (null for main plans)
	private String rootPlanId;

	// Think-act record ID that triggered this sub-plan (null for main plans)
	private Long thinkActRecordId;

	// Plan title
	private String title;

	// User's original request
	private String userRequest;

	// Timestamp when execution started
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	private LocalDateTime startTime;

	// Timestamp when execution ended
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	private LocalDateTime endTime;

	// List of plan steps
	private List<String> steps;

	// Current step index being executed
	private Integer currentStepIndex;

	// Whether completed
	private boolean completed;

	// Execution summary
	private String summary;

	// List to maintain the sequence of agent executions
	private List<AgentExecutionRecord> agentExecutionSequence;

	// Field to store user input wait state
	private UserInputWaitState userInputWaitState;

	// Actual calling model
	private String modelName;

	/**
	 * Default constructor for Jackson and other frameworks.
	 */
	public PlanExecutionRecord() {
		this.steps = new ArrayList<>();
		// It's generally better to initialize time-sensitive fields like startTime
		// when the actual event occurs, or in the specific constructor that signifies
		// creation.
		// However, if a default non-null startTime is always expected, this is one way.
		// this.startTime = LocalDateTime.now(); // Consider if this is appropriate for
		// a
		// default constructor
		this.completed = false;
		this.agentExecutionSequence = new ArrayList<>();
		// Ensure ID is generated during initialization
		this.id = generateId();
	}

	/**
	 * Constructor for creating a new execution record
	 * 
	 * @param planId The unique identifier for the plan.
	 */
	public PlanExecutionRecord(String currentPlanId, String rootPlanId) {
		this.currentPlanId = currentPlanId;
		this.rootPlanId = rootPlanId;
		this.steps = new ArrayList<>();
		this.startTime = LocalDateTime.now();
		this.completed = false;
		this.agentExecutionSequence = new ArrayList<>();
		// Ensure ID is generated during initialization
		this.id = generateId();
	}

	/**
	 * Add an execution step
	 * 
	 * @param step      Step description
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
	 * Get agent execution records sorted by execution order
	 * 
	 * @return List of execution records
	 */
	public List<AgentExecutionRecord> getAgentExecutionSequence() {
		return agentExecutionSequence;
	}

	public void setAgentExecutionSequence(List<AgentExecutionRecord> agentExecutionSequence) {
		this.agentExecutionSequence = agentExecutionSequence;
	}

	/**
	 * Complete execution and set end time
	 */
	public void complete(String summary) {
		this.endTime = LocalDateTime.now();
		this.completed = true;
		this.summary = summary;
	}

	/**
	 * Generate unique ID if not already set
	 * 
	 * @return Generated or existing ID
	 */
	private Long generateId() {
		if (this.id == null) {
			// Use combination of timestamp and random number to generate ID
			long timestamp = System.currentTimeMillis();
			int random = (int) (Math.random() * 1000000);
			this.id = timestamp * 1000 + random;
		}
		return this.id;
	}

	/**
	 * Save record to persistent storage. Empty implementation, to be overridden by
	 * specific storage implementations. Also recursively saves all
	 * AgentExecutionRecord
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

	// Getters and Setters

	public Long getId() {
		// Ensure ID is generated when accessing
		if (this.id == null) {
			this.id = generateId();
		}
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCurrentPlanId() {
		return currentPlanId;
	}

	public void setCurrentPlanId(String planId) {
		this.currentPlanId = planId;
	}

	public String getRootPlanId() {
		return rootPlanId;
	}

	public void setRootPlanId(String rootPlanId) {
		this.rootPlanId = rootPlanId;
	}

	public Long getThinkActRecordId() {
		return thinkActRecordId;
	}

	public void setThinkActRecordId(Long thinkActRecordId) {
		this.thinkActRecordId = thinkActRecordId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUserRequest() {
		return userRequest;
	}

	public void setUserRequest(String userRequest) {
		this.userRequest = userRequest;
	}

	public LocalDateTime getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
	}

	public UserInputWaitState getUserInputWaitState() {
		return userInputWaitState;
	}

	public void setUserInputWaitState(UserInputWaitState userInputWaitState) {
		this.userInputWaitState = userInputWaitState;
	}

	public LocalDateTime getEndTime() {
		return endTime;
	}

	public void setEndTime(LocalDateTime endTime) {
		this.endTime = endTime;
	}

	public List<String> getSteps() {
		return steps;
	}

	public void setSteps(List<String> steps) {
		this.steps = steps;
	}

	public Integer getCurrentStepIndex() {
		return currentStepIndex;
	}

	public void setCurrentStepIndex(Integer currentStepIndex) {
		this.currentStepIndex = currentStepIndex;
	}

	public boolean isCompleted() {
		return completed;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getModelName() {
		return modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	/**
	 * Return string representation of this record, containing key field information
	 * 
	 * @return String containing key information of the record
	 */
	@Override
	public String toString() {
		return String.format(
				"PlanExecutionRecord{id=%d, planId='%s',planId='%s', title='%s', steps=%d, currentStep=%d/%d, completed=%b}",
				id, currentPlanId, rootPlanId, title, steps.size(), currentStepIndex != null ? currentStepIndex + 1 : 0,
				steps.size(), completed);
	}

}
