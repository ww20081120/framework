/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.jmanus.tool.innerStorage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.springframework.ai.openai.api.OpenAiApi;

import com.hbasesoft.framework.ai.jmanus.recorder.PlanExecutionRecorder;
import com.hbasesoft.framework.ai.jmanus.tool.AbstractBaseTool;
import com.hbasesoft.framework.ai.jmanus.tool.code.ToolExecuteResult;
import com.hbasesoft.framework.ai.jmanus.tool.filesystem.UnifiedDirectoryManager;
import com.hbasesoft.framework.ai.jmanus.tool.workflow.ISummaryWorkflow;
import com.hbasesoft.framework.ai.jmanus.tool.workflow.SummaryWorkflow;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月20日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.jmanus.tool.innerStorage <br>
 */
public class InnerStorageContentTool extends AbstractBaseTool<InnerStorageContentTool.InnerStorageContentInput> {

	/**
	 * Internal storage content retrieval input class
	 */
	public static class InnerStorageContentInput {

		private String action;

		@com.fasterxml.jackson.annotation.JsonProperty("file_name")
		private String fileName;

		@com.fasterxml.jackson.annotation.JsonProperty("folder_name")
		private String folderName;

		@com.fasterxml.jackson.annotation.JsonProperty("query_key")
		private String queryKey;

		@com.fasterxml.jackson.annotation.JsonProperty("outputFormatSpecification")
		private String outputFormatSpecification;

		@com.fasterxml.jackson.annotation.JsonProperty("start_line")
		private Integer startLine;

		@com.fasterxml.jackson.annotation.JsonProperty("end_line")
		private Integer endLine;

		public InnerStorageContentInput() {
		}

		public String getAction() {
			return action;
		}

		public void setAction(String action) {
			this.action = action;
		}

		public String getFileName() {
			return fileName;
		}

		public void setFileName(String fileName) {
			this.fileName = fileName;
		}

		public String getFolderName() {
			return folderName;
		}

		public void setFolderName(String folderName) {
			this.folderName = folderName;
		}

		public String getQueryKey() {
			return queryKey;
		}

		public void setQueryKey(String queryKey) {
			this.queryKey = queryKey;
		}

		public String getOutputFormatSpecification() {
			return outputFormatSpecification;
		}

		public void setOutputFormatSpecification(String outputFormatSpecification) {
			this.outputFormatSpecification = outputFormatSpecification;
		}

		public Integer getStartLine() {
			return startLine;
		}

		public void setStartLine(Integer startLine) {
			this.startLine = startLine;
		}

		public Integer getEndLine() {
			return endLine;
		}

		public void setEndLine(Integer endLine) {
			this.endLine = endLine;
		}

	}

	private final UnifiedDirectoryManager directoryManager;

	private final ISummaryWorkflow summaryWorkflow;

	private final PlanExecutionRecorder planExecutionRecorder;

	public InnerStorageContentTool(UnifiedDirectoryManager directoryManager, SummaryWorkflow summaryWorkflow,
			PlanExecutionRecorder planExecutionRecorder) {
		this.directoryManager = directoryManager;
		this.summaryWorkflow = summaryWorkflow;
		this.planExecutionRecorder = planExecutionRecorder;
	}

	private static final String TOOL_NAME = "inner_storage_content_tool";

	private static final String TOOL_DESCRIPTION = """
			Internal storage content retrieval tool specialized for intelligent content extraction and structured output.
			Intelligent content extraction mode: Get detailed content based on file name, **must provide** query_key and outputFormatSpecification parameters for intelligent extraction and structured output

			Supports two operation modes:
			1. extract_relevant_content: Get content from single file (exact filename match or relative path)
			2. get_folder_content: Get content from all files in specified folder
			""";

	private static final String PARAMETERS = """
			{
				"oneOf": [
					{
						"type": "object",
						"properties": {
							"action": {
								"type": "string",
								"const": "extract_relevant_content",
								"description": "Get content from single file"
							},
							"file_name": {
								"type": "string",
								"description": "Filename (with extension) or relative path, supports exact matching"
							},
							"query_key": {
								"type": "string",
								"description": "Related questions or content keywords to extract, must be provided"
							},
							"outputFormatSpecification": {
								"type": "string",
								"description": "Provide a string to specify the structure in which you expect the data for query_key to be returned. If you want the result to consist of multiple fields as a whole, you can input a comma-separated string to define the fields."
							}
						},
						"required": ["action", "file_name", "query_key", "outputFormatSpecification"],
						"additionalProperties": false
					},
					{
						"type": "object",
						"properties": {
							"action": {
								"type": "string",
								"const": "get_folder_content",
								"description": "Get content from all files in specified folder"
							},
							"folder_name": {
								"type": "string",
								"description": "Folder name or relative path"
							},
							"query_key": {
								"type": "string",
								"description": "Related questions or content keywords to extract, must be provided"
							},
							"outputFormatSpecification": {
								"type": "string",
							"description": "Provide a string to specify the structure in which you expect the data for query_key to be returned. If you want the result to consist of multiple fields as a whole, you can input a comma-separated string to define the fields."
							}
						},
						"required": ["action", "folder_name", "query_key", "outputFormatSpecification"],
						"additionalProperties": false
					}
				]
			}
			""";

	@Override
	public String getName() {
		return TOOL_NAME;
	}

	@Override
	public String getDescription() {
		return TOOL_DESCRIPTION;
	}

	@Override
	public String getParameters() {
		return PARAMETERS;
	}

	@Override
	public Class<InnerStorageContentInput> getInputType() {
		return InnerStorageContentInput.class;
	}

	@Override
	public String getServiceGroup() {
		return "default-service-group";
	}

	public OpenAiApi.FunctionTool getToolDefinition() {
		String description = getDescription();
		String parameters = getParameters();
		OpenAiApi.FunctionTool.Function function = new OpenAiApi.FunctionTool.Function(description, TOOL_NAME,
				parameters);
		return new OpenAiApi.FunctionTool(function);
	}

	/**
	 * Execute internal storage content retrieval operation
	 */
	@Override
	public ToolExecuteResult run(InnerStorageContentInput input) {
		LoggerUtil.info(
				"InnerStorageContentTool input: action={0}, fileName={1}, folderName={2}, queryKey={3}, outputFormatSpecification={4}",
				input.getAction(), input.getFileName(), input.getFolderName(), input.getQueryKey(),
				input.getOutputFormatSpecification());
		try {
			String action = input.getAction();
			if (action == null) {
				return new ToolExecuteResult("Error: action parameter is required");
			}

			return switch (action) {
			case "extract_relevant_content" ->
				getStoredContent(input.getFileName(), input.getQueryKey(), input.getOutputFormatSpecification());
			case "get_folder_content" ->
				getFolderContent(input.getFolderName(), input.getQueryKey(), input.getOutputFormatSpecification());
			default -> new ToolExecuteResult("Error: Unsupported operation type '" + action
					+ "'. Supported operations: extract_relevant_content, get_folder_content");
			};
		} catch (Exception e) {
			LoggerUtil.error("InnerStorageContentTool execution failed", e);
			return new ToolExecuteResult("Tool execution failed: " + e.getMessage());
		}
	}

	/**
	 * Get stored content by filename, supports AI intelligent extraction and
	 * structured output
	 */
	private ToolExecuteResult getStoredContent(String fileName, String queryKey, String outputFormatSpecification) {
		if (fileName == null || fileName.trim().isEmpty()) {
			return new ToolExecuteResult("Error: file_name parameter is required");
		}
		if (queryKey == null || queryKey.trim().isEmpty()) {
			return new ToolExecuteResult(
					"Error: query_key parameter is required to specify content keywords to extract");
		}
		if (outputFormatSpecification == null || outputFormatSpecification.isEmpty()) {
			return new ToolExecuteResult(
					"Error: outputFormatSpecification parameter is required to specify structured column names for return results");
		}
		try {
			Path planDir = directoryManager.getRootPlanDirectory(rootPlanId);
			Path targetFile = null;

			// First try exact relative path matching
			if (fileName.contains("/")) {
				Path exactPath = planDir.resolve(fileName);
				if (Files.exists(exactPath) && Files.isRegularFile(exactPath)) {
					targetFile = exactPath;
				}
			} else {
				// If no path separator, exact match filename in root directory
				List<Path> files = Files.list(planDir).filter(Files::isRegularFile).toList();
				for (Path filePath : files) {
					if (filePath.getFileName().toString().equals(fileName)) {
						targetFile = filePath;
						break;
					}
				}
			}

			if (targetFile == null) {
				return new ToolExecuteResult(
						"File '" + fileName + "' not found. Please provide exact filename or relative path.");
			}

			String fileContent = Files.readString(targetFile);
			String actualFileName = planDir.relativize(targetFile).toString();

			LoggerUtil.info("Delegating to SummaryWorkflow for file content extraction: file={0}, query keywords={1}",
					actualFileName, queryKey);
			Long thinkActRecordId = getCurrentThinkActRecordId();
			String result = summaryWorkflow.executeSummaryWorkflow(rootPlanId, actualFileName, fileContent, queryKey,
					thinkActRecordId, outputFormatSpecification).get();
			return new ToolExecuteResult(result);
		} catch (IOException e) {
			LoggerUtil.error("Failed to get storage content", e);
			return new ToolExecuteResult("Failed to get content: " + e.getMessage());
		} catch (Exception e) {
			LoggerUtil.error("SummaryWorkflow execution failed", e);
			return new ToolExecuteResult("Content processing failed: " + e.getMessage());
		}
	}

	/**
	 * Get information from all files in specified folder
	 */
	private ToolExecuteResult getFolderContent(String folderName, String queryKey, String outputFormatSpecification) {
		if (folderName == null || folderName.trim().isEmpty()) {
			return new ToolExecuteResult("Error: folder_name parameter is required");
		}
		if (queryKey == null || queryKey.trim().isEmpty()) {
			return new ToolExecuteResult(
					"Error: query_key parameter is required to specify content keywords to extract");
		}
		if (outputFormatSpecification == null || outputFormatSpecification.isEmpty()) {
			return new ToolExecuteResult(
					"Error: outputFormatSpecification parameter is required to specify structured column names for return results");
		}
		try {
			Path planDir = directoryManager.getRootPlanDirectory(rootPlanId);
			Path targetFolder = planDir.resolve(folderName);

			if (!Files.exists(targetFolder)) {
				return new ToolExecuteResult("Folder '" + folderName + "' does not exist.");
			}

			if (!Files.isDirectory(targetFolder)) {
				return new ToolExecuteResult("'" + folderName + "' is not a folder.");
			}

			// Get all files in the folder
			List<Path> files = Files.list(targetFolder).filter(Files::isRegularFile).toList();

			if (files.isEmpty()) {
				return new ToolExecuteResult("No files in folder '" + folderName + "'.");
			}

			// Combine all file contents
			StringBuilder combinedContent = new StringBuilder();
			for (Path file : files) {
				String relativePath = planDir.relativize(file).toString();
				combinedContent.append("=== File: ").append(relativePath).append(" ===\n");
				combinedContent.append(Files.readString(file));
				combinedContent.append("\n\n");
			}

			LoggerUtil.info(
					"Delegating to SummaryWorkflow for folder content extraction: folder={0}, file count={1}, query keywords={2}",
					folderName, files.size(), queryKey);

			Long thinkActRecordId = getCurrentThinkActRecordId();
			String result = summaryWorkflow.executeSummaryWorkflow(rootPlanId, folderName, combinedContent.toString(),
					queryKey, thinkActRecordId, outputFormatSpecification).get();
			return new ToolExecuteResult(result);

		} catch (IOException e) {
			LoggerUtil.error("Failed to get folder content", e);
			return new ToolExecuteResult("Failed to get folder content: " + e.getMessage());
		} catch (Exception e) {
			LoggerUtil.error("SummaryWorkflow execution failed", e);
			return new ToolExecuteResult("Content processing failed: " + e.getMessage());
		}
	}

	/**
	 * Get current think-act record ID
	 * 
	 * @return Current think-act record ID, return null if none
	 */
	private Long getCurrentThinkActRecordId() {
		try {
			Long thinkActRecordId = planExecutionRecorder.getCurrentThinkActRecordId(currentPlanId, rootPlanId);
			if (thinkActRecordId != null) {
				LoggerUtil.info("Current think-act record ID: {0}", thinkActRecordId);
				return thinkActRecordId;
			} else {
				LoggerUtil.warn("No current think-act record ID");
			}
		} catch (Exception e) {
			LoggerUtil.warn("Failed to get current think-act record ID: {0}", e.getMessage());
		}

		return null;
	}

	@Override
	public String getCurrentToolStateString() {
		try {
			StringBuilder sb = new StringBuilder();
			sb.append("InnerStorageContent current status:\n");
			sb.append("- Storage root directory: ").append(directoryManager.getRootPlanDirectory(rootPlanId))
					.append("\n");
			Path planDir = directoryManager.getRootPlanDirectory(rootPlanId);
			List<Path> files = Files.exists(planDir) ? Files.list(planDir).filter(Files::isRegularFile).toList()
					: List.of();
			if (files.isEmpty()) {
				sb.append("- Internal files: None\n");
			} else {
				sb.append("- Internal files (").append(files.size()).append(" files)\n");
			}
			return sb.toString();
		} catch (Exception e) {
			LoggerUtil.error("Failed to get tool status", e);
			return "InnerStorageContent status retrieval failed: " + e.getMessage();
		}
	}

	@Override
	public void cleanup(String planId) {
		// Content retrieval tool does not need to perform cleanup operations
		LoggerUtil.info("InnerStorageContentTool cleanup for plan: {0}", planId);
	}

}
