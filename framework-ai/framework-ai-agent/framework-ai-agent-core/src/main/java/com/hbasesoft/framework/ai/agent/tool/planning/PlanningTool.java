package com.hbasesoft.framework.ai.agent.tool.planning;

import java.util.List;

import org.springframework.ai.openai.api.OpenAiApi.FunctionTool;
import org.springframework.ai.tool.function.FunctionToolCallback;
import org.springframework.ai.tool.metadata.ToolMetadata;
import org.springframework.stereotype.Component;

import com.hbasesoft.framework.ai.agent.planning.model.vo.ExecutionPlan;
import com.hbasesoft.framework.ai.agent.planning.model.vo.ExecutionStep;
import com.hbasesoft.framework.ai.agent.tool.AbstractBaseTool;
import com.hbasesoft.framework.ai.agent.tool.ToolExecuteResult;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;

/**
 * <Description> 计划工具类 <br>
 *
 * @author wangwei<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年6月16日 <br>
 * @see com.hbasesoft.framework.ai.agent.tool <br>
 * @since V1.0<br>
 */
@Component
public class PlanningTool extends AbstractBaseTool<PlanningTool.PlanningInput> implements PlanningToolInterface {

	private ExecutionPlan currentPlan;

	public PlanningTool() {
	}

	/**
	 * Internal input class for defining planning tool input parameters
	 */
	public static class PlanningInput {

		private String command;

		private String planId;

		private String title;

		private List<String> steps;

		private String terminateColumns;

		private boolean directResponse = false;

		public PlanningInput() {
		}

		public PlanningInput(String command, String planId, String title, List<String> steps, boolean directResponse) {
			this.command = command;
			this.planId = planId;
			this.title = title;
			this.steps = steps;
			this.terminateColumns = null;
			this.directResponse = directResponse;
		}

		public String getCommand() {
			return command;
		}

		public void setCommand(String command) {
			this.command = command;
		}

		public String getPlanId() {
			return planId;
		}

		public void setPlanId(String planId) {
			this.planId = planId;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public List<String> getSteps() {
			return steps;
		}

		public void setSteps(List<String> steps) {
			this.steps = steps;
		}

		public String getTerminateColumns() {
			return terminateColumns;
		}

		public void setTerminateColumns(String terminateColumns) {
			this.terminateColumns = terminateColumns;
		}

		public boolean isDirectResponse() {
			return directResponse;
		}

		public void setDirectResponse(boolean directResponse) {
			this.directResponse = directResponse;
		}

	}

	public String getCurrentPlanId() {
		return currentPlan != null ? currentPlan.getCurrentPlanId() : null;
	}

	public ExecutionPlan getCurrentPlan() {
		return currentPlan;
	}

	private static final String PARAMETERS = """
			{
			 "type": "object",
			 "properties": {
			  "command": {
			   "description": "create a execution plan , Available commands: create",
			   "enum": [
				   "create"
			   ],
			   "type": "string"
			  },
			  "title": {
			   "description": "Title for the plan",
			   "type": "string"
			  },
			  "steps": {
			   "description": "List of plan steps",
			   "type": "array",
			   "items": {
				   "type": "string"
			   }
			  },
			  "terminateColumns": {
				   "description": "Terminate structure output columns for all steps (optional, will be applied to every step)",
				   "type": "string"
			  },
			  "directResponse": {
				   "description": "Whether to use direct response mode (skip planning and respond directly)",
				   "type": "boolean"
			  }
			 },
			 "required": [
			"command",
			"title",
			"steps"
			 ]
			}
			""";

	private static final String name = "planning";

	private static final String description = "Planning tool for managing tasks ";

	public FunctionTool getToolDefinition() {
		return new FunctionTool(new FunctionTool.Function(description, name, PARAMETERS));
	}

	// Parameterized FunctionToolCallback with appropriate types.
	public static FunctionToolCallback<PlanningInput, ToolExecuteResult> getFunctionToolCallback(
			PlanningTool toolInstance) {
		return FunctionToolCallback.builder(name, toolInstance).description(description).inputSchema(PARAMETERS)
				.inputType(PlanningInput.class).toolMetadata(ToolMetadata.builder().returnDirect(true).build()).build();
	}

	@Override
	public ToolExecuteResult run(PlanningInput input) {
		String command = input.getCommand();
		String planId = input.getPlanId();
		String title = input.getTitle();
		List<String> steps = input.getSteps();
		boolean directResponse = input.isDirectResponse();

		// Support directResponse mode
		if (directResponse) {
			LoggerUtil.info("Direct response mode enabled for planId: {0}", planId);
			ExecutionPlan plan = new ExecutionPlan(planId, planId, title);
			plan.setDirectResponse(true);
			plan.setUserRequest(title); // Here title is the user request content
			this.currentPlan = plan;
			return new ToolExecuteResult("Direct response mode: plan created for " + planId);
		}

		return switch (command) {
		case "create" -> createPlan(planId, title, steps, input.getTerminateColumns());
		// case "update" -> updatePlan(planId, title, steps);
		// case "get" -> getPlan(planId);
		// case "mark_step" -> markStep(planId, stepIndex, stepStatus, stepNotes);
		// case "delete" -> deletePlan(planId);
		default -> {
			LoggerUtil.info("Received invalid command: {0}", command);
			throw new IllegalArgumentException("Invalid command: " + command);
		}
		};
	}

	/**
	 * Create a single execution step
	 * 
	 * @param step  step description
	 * @param index step index
	 * @return created ExecutionStep instance
	 */
	private ExecutionStep createExecutionStep(String step, int index) {
		ExecutionStep executionStep = new ExecutionStep();
		executionStep.setStepRequirement(step);
		return executionStep;
	}

	public ToolExecuteResult createPlan(String planId, String title, List<String> steps, String terminateColumns) {
		if (title == null || steps == null || steps.isEmpty()) {
			LoggerUtil.info("Missing required parameters when creating plan: planId={0}, title={1}, steps={2}", planId,
					title, steps);
			return new ToolExecuteResult("Required parameters missing");
		}

		ExecutionPlan plan = new ExecutionPlan(planId, planId, title);

		int index = 0;
		for (String step : steps) {
			ExecutionStep execStep = createExecutionStep(step, index);
			if (terminateColumns != null && !terminateColumns.isEmpty()) {
				execStep.setTerminateColumns(terminateColumns);
			}
			plan.addStep(execStep);
			index++;
		}

		this.currentPlan = plan;
		return new ToolExecuteResult("Plan created: " + planId + "\n" + plan.getPlanExecutionStateStringFormat(false));
	}

	// ToolCallBiFunctionDef interface methods
	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public String getParameters() {
		return PARAMETERS;
	}

	@Override
	public Class<PlanningInput> getInputType() {
		return PlanningInput.class;
	}

	@Override
	public boolean isReturnDirect() {
		return true;
	}

	@Override
	public String getCurrentToolStateString() {
		if (currentPlan != null) {
			return "Current plan: " + currentPlan.getPlanExecutionStateStringFormat(false);
		}
		return "No active plan";
	}

	@Override
	public void cleanup(String planId) {
		// Implementation can be added if needed
	}

	@Override
	public String getServiceGroup() {
		return "default-service-group";
	}

	// PlanningToolInterface methods
	@Override
	public FunctionToolCallback<PlanningInput, ToolExecuteResult> getFunctionToolCallback() {
		return FunctionToolCallback.<PlanningInput, ToolExecuteResult>builder(name, this::run).description(description)
				.inputSchema(PARAMETERS).inputType(PlanningInput.class)
				.toolMetadata(ToolMetadata.builder().returnDirect(true).build()).build();
	}

}