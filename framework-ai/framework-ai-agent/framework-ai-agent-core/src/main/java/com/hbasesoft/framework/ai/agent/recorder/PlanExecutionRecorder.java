package com.hbasesoft.framework.ai.agent.recorder;

import java.util.List;

import com.hbasesoft.framework.ai.agent.planning.model.vo.ExecutionContext;
import com.hbasesoft.framework.ai.agent.planning.model.vo.ExecutionStep;
import com.hbasesoft.framework.ai.agent.recorder.model.ExecutionStatus;
import com.hbasesoft.framework.ai.agent.recorder.model.PlanExecutionRecord;
import com.hbasesoft.framework.ai.agent.recorder.model.ThinkActRecord;

import lombok.Data;

/**
 * 计划执行记录器接口， 定义了记录和检索计划执行详情的方法
 */
public interface PlanExecutionRecorder {

	/**
	 * Removes the execution records for the specified plan ID
	 * @param planId The plan ID to remove
	 */
	void removeExecutionRecord(String planId);

	PlanExecutionRecord getRootPlanExecutionRecord(String rootPlanId);

	/**
	 * Record the start of step execution.
	 * @param step Execution step
	 * @param context Execution context
	 */
	void recordStepStart(ExecutionStep step, ExecutionContext context);

	/**
	 * Record the end of step execution.
	 * @param step Execution step
	 * @param context Execution context
	 */
	void recordStepEnd(ExecutionStep step, ExecutionContext context);

	/**
	 * Record the start of plan execution.
	 * @param context Execution context containing user request and execution process
	 * information
	 */
	void recordPlanExecutionStart(ExecutionContext context);

	/**
	 * Record think-act execution
	 * @param planExecutionRecord Plan execution record
	 * @param agentExecutionId Agent execution ID
	 * @param thinkActRecord Think-act record
	 */
	void setThinkActExecution(PlanExecutionRecord planExecutionRecord, Long agentExecutionId,
			ThinkActRecord thinkActRecord);

	/**
	 * Record plan completion
	 * @param planExecutionRecord Plan execution record
	 * @param summary Execution summary
	 */
	void setPlanCompletion(PlanExecutionRecord planExecutionRecord, String summary);

	/**
	 * Save execution records to persistent storage
	 * @param planExecutionRecord Plan execution record to save
	 * @return true if save was successful
	 */
	boolean savePlanExecutionRecords(PlanExecutionRecord planExecutionRecord);

	/**
	 * Record complete agent execution at the end. This method handles all agent execution
	 * record management logic without exposing internal record objects.
	 * @param currentPlanId Current plan ID
	 * @param rootPlanId Root plan ID
	 * @param thinkActRecordId Think-act record ID for sub-plan executions (null for root
	 * plans)
	 * @param agentName Agent name
	 * @param agentDescription Agent description
	 * @param maxSteps Maximum execution steps
	 * @param actualSteps Actual steps executed
	 * @param completed Whether execution completed successfully
	 * @param stuck Whether agent got stuck
	 * @param errorMessage Error message if any
	 * @param result Final execution result
	 * @param startTime Execution start time
	 * @param endTime Execution end time
	 */
	void recordCompleteAgentExecution(PlanExecutionParams params);

	/**
	 * Interface 1: Record thinking and action execution process. This method handles
	 * ThinkActRecord creation and thinking process without exposing internal record
	 * objects.
	 * @param params Encapsulated parameters for plan execution
	 * @return ThinkActRecord ID for subsequent action recording
	 */
	Long recordThinkingAndAction(PlanExecutionParams params);

	/**
	 * Interface 2: Record action execution result. This method updates the ThinkActRecord
	 * with action results without exposing internal record objects.
	 * @param currentPlanId Current plan ID
	 * @param rootPlanId Root plan ID
	 * @param thinkActRecordId Think-act record ID for sub-plan executions (null for root
	 * plans)
	 * @param createdThinkActRecordId The ThinkActRecord ID returned from
	 * recordThinkingAndAction
	 * @param actionDescription Description of the action to be taken
	 * @param actionResult Result of action execution
	 * @param status Execution status (SUCCESS, FAILED, etc.)
	 * @param errorMessage Error message if action execution failed
	 * @param toolName Tool name used for action
	 * @param subPlanCreated Whether this action created a sub-plan execution
	 */
	void recordActionResult(PlanExecutionParams params);

	/**
	 * Interface 3: Record plan completion. This method handles plan completion recording
	 * logic without exposing internal record objects.
	 * @param currentPlanId Current plan ID
	 * @param rootPlanId Root plan ID
	 * @param thinkActRecordId Think-act record ID for sub-plan executions (null for root
	 * plans)
	 * @param summary The summary of the plan execution
	 */
	void recordPlanCompletion(String currentPlanId, String rootPlanId, Long thinkActRecordId, String summary);

	public Long getCurrentThinkActRecordId(String currentPlanId, String rootPlanId);

	/**
	 * Parameter encapsulation class for recording all relevant information about plan
	 * execution
	 */
	@Data
	public class PlanExecutionParams {

		/** Current plan ID */
		String currentPlanId;

		/** Root plan ID */
		String rootPlanId;

		/** Think-act record ID */
		Long thinkActRecordId;

		/** Agent name */
		String agentName;

		/** Agent description */
		String agentDescription;

		/** Thinking input */
		String thinkInput;

		/** Thinking output */
		String thinkOutput;

		/** Whether action is needed */
		boolean actionNeeded;

		/** Tool name */
		String toolName;

		/** Tool parameters */
		String toolParameters;

		/** Model name */
		String modelName;

		/** Error message */
		String errorMessage;

		/** Created Think-act record ID */
		Long createdThinkActRecordId;

		/** Action description */
		String actionDescription;

		/** Action result */
		String actionResult;

		/** Execution status */
		ExecutionStatus status;

		/** Whether a sub-plan was created */
		boolean subPlanCreated;

		/** Action tool information list */
		List<ThinkActRecord.ActToolInfo> actToolInfoList;

		/** Execution summary */
		String summary;

		/** Maximum execution steps */
		int maxSteps;

		/** Actual steps executed */
		int actualSteps;

		/** Final execution result */
		String result;

		/** Execution start time */
		java.time.LocalDateTime startTime;

		/** Execution end time */
		java.time.LocalDateTime endTime;


		public List<ThinkActRecord.ActToolInfo> getActToolInfoList() {
			return actToolInfoList;
		}

		public void setActToolInfoList(
				List<ThinkActRecord.ActToolInfo> actToolInfoList) {
			this.actToolInfoList = actToolInfoList;
		}

	}
}
