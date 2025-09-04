/****************************************************************************************
 * Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or    *
 * transmission in whole or in part, in any form or by any means, electronic, mechanical*
 * or otherwise, is prohibited without the prior written consent of the copyright owner.*
 ****************************************************************************************/
package com.hbasesoft.framework.ai.agent.tool.mapreduce;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.stereotype.Component;

import com.hbasesoft.framework.ai.agent.config.IManusProperties;
import com.hbasesoft.framework.ai.agent.tool.AbstractBaseTool;
import com.hbasesoft.framework.ai.agent.tool.ToolExecuteResult;
import com.hbasesoft.framework.ai.agent.tool.filesystem.UnifiedDirectoryManager;
import com.hbasesoft.framework.ai.agent.tool.terminate.TerminableTool;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;

/**
 * FinalizeTool is responsible for finalizing MapReduce workflow output
 * processing. It supports operations such as exporting the reduce output file
 * to a new file with a user-specified name.
 * 
 * <p>
 * This tool is specifically designed for finalizing MapReduce workflow results
 * by:
 * <ul>
 * <li>Creating a final output file with a meaningful name</li>
 * <li>Preserving the original reduce output file</li>
 * <li>Ensuring the final result is properly named and accessible</li>
 * </ul>
 * 
 * @author 王伟
 * @version 1.0
 * @taskId
 * @CreateDate 2025年8月20日
 * @since V1.0
 * @see com.hbasesoft.framework.ai.agent.tool.mapreduce
 */
@Component
public class FinalizeTool extends AbstractBaseTool<FinalizeTool.FinalizeInput> implements TerminableTool {

	// ==================== Configuration Constants ====================

	/** Supported operation type: export reduce output to new file */
	private static final String ACTION_EXPORT = "export";

	/** Source file name for finalize operations (same as ReduceOperationTool) */
	private static final String REDUCE_FILE_NAME = "reduce_output.md";

	/** Default context size for infinite context tasks */
	private static final int DEFAULT_CONTEXT_SIZE = 20000;

	/**
	 * Internal input class for defining Finalize tool input parameters
	 */
	public static class FinalizeInput {

		/** Action to perform (e.g., "export") */
		private String action;

		/** New file name for export operation */
		@com.fasterxml.jackson.annotation.JsonProperty("new_file_name")
		private String newFileName;

		/**
		 * Constructor for FinalizeInput
		 */
		public FinalizeInput() {
		}

		/**
		 * Get action
		 * 
		 * @return the action
		 */
		public String getAction() {
			return action;
		}

		/**
		 * Set action
		 * 
		 * @param action the action to set
		 */
		public void setAction(final String action) {
			this.action = action;
		}

		/**
		 * Get new file name
		 * 
		 * @return the new file name
		 */
		public String getNewFileName() {
			return newFileName;
		}

		/**
		 * Set new file name
		 * 
		 * @param newFileName the new file name to set
		 */
		public void setNewFileName(final String newFileName) {
			this.newFileName = newFileName;
		}

	}

	private static final String TOOL_NAME = "mapreduce_finalize_tool";

	private static final String TOOL_DESCRIPTION = """
			Finalize tool for MapReduce workflow output processing.
			Supports copying the reduce output file to a new file with user-specified name.
			Source file: %s
			Supported operations:
			- export: Copy the reduce output file to a new file with the specified name in
			  the same directory.

			This tool is specifically designed for finalizing MapReduce workflow results by:
			- Creating a final output file with a meaningful name
			- Preserving the original reduce output file
			- Ensuring the final result is properly named and accessible
			""".formatted(REDUCE_FILE_NAME);

	private static final String PARAMETERS_JSON = """
			{
			    "type": "object",
			    "properties": {
			        "action": {
			            "type": "string",
			            "const": "export"
			        },
			        "new_file_name": {
			            "type": "string",
			            "description": "New file name (with extension), used to save the final output result"
			        }
			    },
			    "required": ["action", "new_file_name"],
			    "additionalProperties": false
			}
			""";

	private UnifiedDirectoryManager unifiedDirectoryManager;

	private IManusProperties manusProperties;

	// Shared state manager for managing shared state between multiple Agent
	// instances
	private MapReduceSharedStateManager sharedStateManager;

	// Track if any operation has completed, allowing termination
	private volatile boolean operationCompleted = false;

	/**
	 * Constructor for FinalizeTool
	 * 
	 * @param manusProperties         the manus properties
	 * @param sharedStateManager      the shared state manager
	 * @param unifiedDirectoryManager the unified directory manager
	 */
	public FinalizeTool(final IManusProperties manusProperties, final MapReduceSharedStateManager sharedStateManager,
			final UnifiedDirectoryManager unifiedDirectoryManager) {
		this.manusProperties = manusProperties;
		this.unifiedDirectoryManager = unifiedDirectoryManager;
		this.sharedStateManager = sharedStateManager;
	}

	/**
	 * Set shared state manager
	 * 
	 * @param sharedStateManager the shared state manager to set
	 */
	public void setSharedStateManager(final MapReduceSharedStateManager sharedStateManager) {
		this.sharedStateManager = sharedStateManager;
	}

	@Override
	public String getName() {
		return TOOL_NAME;
	}

	/**
	 * Get task directory list
	 * 
	 * @return the list of task directories
	 */
	public List<String> getSplitResults() {
		if (sharedStateManager != null && currentPlanId != null) {
			return sharedStateManager.getSplitResults(currentPlanId);
		}
		return new ArrayList<>();
	}

	@Override
	public String getDescription() {
		return TOOL_DESCRIPTION;
	}

	@Override
	public String getParameters() {
		return PARAMETERS_JSON;
	}

	@Override
	public Class<FinalizeInput> getInputType() {
		return FinalizeInput.class;
	}

	@Override
	public String getServiceGroup() {
		return "data-processing";
	}

	/**
	 * Get tool definition
	 * 
	 * @return the tool definition
	 */
	public static OpenAiApi.FunctionTool getToolDefinition() {
		OpenAiApi.FunctionTool.Function function = new OpenAiApi.FunctionTool.Function(TOOL_DESCRIPTION, TOOL_NAME,
				PARAMETERS_JSON);
		return new OpenAiApi.FunctionTool(function);
	}

	/**
	 * Execute Finalize operation
	 * 
	 * @param input the finalize input
	 * @return the tool execution result
	 */
	@Override
	public ToolExecuteResult run(final FinalizeInput input) {
		LoggerUtil.info("FinalizeTool input: action={0}, newFileName={1}", input.getAction(), input.getNewFileName());
		try {
			String action = input.getAction();
			if (action == null) {
				return new ToolExecuteResult("Error: action parameter is required");
			}

			ToolExecuteResult result = switch (action) {
			case ACTION_EXPORT -> {
				String newFileName = input.getNewFileName();
				if (newFileName == null || newFileName.trim().isEmpty()) {
					yield new ToolExecuteResult("Error: new_file_name parameter is required");
				}
				yield exportFile(newFileName);
			}
			default -> new ToolExecuteResult(
					"Unknown operation: " + action + ". Supported operations: " + ACTION_EXPORT);
			};

			// Mark operation as completed for termination capability
			this.operationCompleted = true;
			return result;

		} catch (Exception e) {
			LoggerUtil.error("FinalizeTool execution failed", e);
			return new ToolExecuteResult("Tool execution failed: " + e.getMessage());
		}
	}

	/**
	 * Export (copy) the reduce output file to a new file with the specified name
	 * 
	 * @param newFileName the name of the new file to create
	 * @return the tool execution result
	 */
	private ToolExecuteResult exportFile(final String newFileName) {
		try {
			// Get plan directory with hierarchical structure
			Path planDir = getPlanDirectory();
			Path sourceFilePath = planDir.resolve(REDUCE_FILE_NAME);

			// Target file path - export to parent directory or root storage directory
			Path targetFilePath;
			if (rootPlanId != null && !rootPlanId.equals(currentPlanId)) {
				// If hierarchical structure exists, export to parent directory
				// (rootPlanId level)
				targetFilePath = getInnerStorageRoot().resolve(rootPlanId).resolve(newFileName);
			} else {
				// If no hierarchy, throw an exception
				return new ToolExecuteResult("Error: Cannot export file - no hierarchical structure found. "
						+ "The tool requires a root plan ID different from the current plan ID.");
			}

			// Check if source file exists
			if (!Files.exists(sourceFilePath)) {
				return new ToolExecuteResult("Error: Source file does not exist: " + REDUCE_FILE_NAME);
			}

			// Check if target file already exists
			if (Files.exists(targetFilePath)) {
				return new ToolExecuteResult("Error: Target file already exists: " + newFileName);
			}

			// Copy the file
			Files.copy(sourceFilePath, targetFilePath, StandardCopyOption.COPY_ATTRIBUTES);

			LoggerUtil.info("File exported successfully: {0} -> {1}", REDUCE_FILE_NAME, newFileName);

			// Read the target file and check its size
			List<String> lines = Files.readAllLines(targetFilePath);
			StringBuilder fileContent = new StringBuilder();
			for (String line : lines) {
				fileContent.append(line).append("\n");
			}

			int charLimit = getInfiniteContextTaskContextSize();
			int contentLength = fileContent.length();

			// If content size is less than required content size, include the entire
			// content in the result
			if (contentLength <= charLimit) {
				StringBuilder result = new StringBuilder();
				result.append(
						String.format("The function call was successful. The content has been saved to the file(%s). "
								+ "the file content is :\n", newFileName));
				result.append(fileContent.toString());
				return new ToolExecuteResult(result.toString());
			}
			// If the file content size exceeds the required context size, indicate that
			// the content is still too large
			else {
				StringBuilder result = new StringBuilder();
				result.append("The function call was successful. The content has been saved to the file(");
				result.append(newFileName);
				result.append("). The file content size is ");
				result.append(contentLength);
				result.append(" characters, which exceeds the limit of ");
				result.append(charLimit);
				result.append(" characters. The content is still too large and has been saved to the new file. ");
				result.append("The user can read the file directly or use other functions to further ");
				result.append("process the oversized file.");
				return new ToolExecuteResult(result.toString());
			}
		} catch (IOException e) {
			LoggerUtil.error("Failed to export file", e);
			return new ToolExecuteResult("Failed to export file: " + e.getMessage());
		}
	}

	@Override
	public String getCurrentToolStateString() {
		if (sharedStateManager != null && currentPlanId != null) {
			return sharedStateManager.getCurrentToolStateString(currentPlanId);
		}

		// Fallback solution
		StringBuilder sb = new StringBuilder();
		return sb.toString();
	}

	/**
	 * Clean up resources for the given plan ID
	 * 
	 * @param planId the plan ID to clean up
	 */
	@Override
	public void cleanup(final String planId) {
		// Clean up shared state
		if (sharedStateManager != null && planId != null) {
			sharedStateManager.cleanupPlanState(planId);
		}
		LoggerUtil.info("FinalizeTool cleanup completed for planId: {0}", planId);
	}

	/**
	 * Apply the tool with the given input and tool context
	 * 
	 * @param input       the finalize input
	 * @param toolContext the tool context
	 * @return the tool execution result
	 */
	@Override
	public ToolExecuteResult apply(final FinalizeInput input, final ToolContext toolContext) {
		return run(input);
	}

	/**
	 * Get inner storage root directory path
	 * 
	 * @return the inner storage root directory path
	 */
	private Path getInnerStorageRoot() {
		return unifiedDirectoryManager.getInnerStorageRoot();
	}

	/**
	 * Get plan directory path with hierarchical structure support
	 * 
	 * @return the plan directory path
	 */
	private Path getPlanDirectory() {
		Path innerStorageRoot = getInnerStorageRoot();
		if (rootPlanId != null && !rootPlanId.equals(currentPlanId)) {
			// Use hierarchical structure: inner_storage/{rootPlanId}/{currentPlanId}
			return innerStorageRoot.resolve(rootPlanId).resolve(currentPlanId);
		} else {
			// Use flat structure: inner_storage/{planId}
			return innerStorageRoot.resolve(currentPlanId);
		}
	}

	/**
	 * Get infinite context task context size
	 * 
	 * @return Context size for infinite context tasks
	 */
	private Integer getInfiniteContextTaskContextSize() {
		if (manusProperties != null) {
			Integer contextSize = manusProperties.getInfiniteContextTaskContextSize();
			return contextSize != null ? contextSize : DEFAULT_CONTEXT_SIZE; // Default 20000 characters
		}
		return DEFAULT_CONTEXT_SIZE; // Default 20000 characters
	}

	// ==================== TerminableTool interface implementation
	// ====================

	@Override
	public boolean canTerminate() {
		// FinalizeTool can be terminated after the export operation has completed
		// This marks the end of the MapReduce workflow
		return operationCompleted;
	}

}
