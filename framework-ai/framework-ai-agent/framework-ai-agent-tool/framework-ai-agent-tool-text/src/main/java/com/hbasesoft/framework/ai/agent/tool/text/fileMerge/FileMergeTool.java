/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.agent.tool.text.fileMerge;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.springframework.stereotype.Component;

import com.hbasesoft.framework.ai.agent.tool.AbstractBaseTool;
import com.hbasesoft.framework.ai.agent.tool.ToolExecuteResult;
import com.hbasesoft.framework.ai.agent.tool.filesystem.UnifiedDirectoryManager;
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
@Component
public class FileMergeTool extends AbstractBaseTool<FileMergeTool.FileMergeInput> {

	/**
	 * File merge input class
	 */
	public static class FileMergeInput {

		private String action;

		@com.fasterxml.jackson.annotation.JsonProperty("file_name")
		private String fileName;

		@com.fasterxml.jackson.annotation.JsonProperty("target_folder")
		private String targetFolder;

		public FileMergeInput() {
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

		public String getTargetFolder() {
			return targetFolder;
		}

		public void setTargetFolder(String targetFolder) {
			this.targetFolder = targetFolder;
		}

	}

	private final UnifiedDirectoryManager directoryManager;

	public FileMergeTool(UnifiedDirectoryManager directoryManager) {
		this.directoryManager = directoryManager;
	}

	private static final String TOOL_NAME = "file_merge_tool";

	@Override
	public String getName() {
		return TOOL_NAME;
	}

	@Override
	public String getDescription() {
		return """
				Merge multiple files into a single file. This tool can combine content from multiple source files and create a merged output file.
				""";
	}

	@Override
	public String getParameters() {
		return """
				{
				    "type": "object",
				    "properties": {
				        "source_files": {
				            "type": "array",
				            "items": {
				                "type": "string"
				            },
				            "description": "List of source file paths to merge"
				        },
				        "output_file": {
				            "type": "string",
				            "description": "Output file path for the merged content"
				        },
				        "merge_strategy": {
				            "type": "string",
				            "enum": ["concatenate", "interleave"],
				            "description": "Strategy for merging files: concatenate (append all files) or interleave (alternate between files)"
				        }
				    },
				    "required": ["source_files", "output_file"]
				}
				""";
	}

	@Override
	public Class<FileMergeInput> getInputType() {
		return FileMergeInput.class;
	}

	@Override
	public String getServiceGroup() {
		return "default-service-group";
	}

	/**
	 * Execute file merge operation
	 */
	@Override
	public ToolExecuteResult run(FileMergeInput input) {
		LoggerUtil.info("FileMergeTool input: action={0}, fileName={1}, targetFolder={2}", input.getAction(),
				input.getFileName(), input.getTargetFolder());
		try {
			return mergeFile(input.getFileName(), input.getTargetFolder());
		} catch (Exception e) {
			LoggerUtil.error("FileMergeTool execution failed", e);
			return new ToolExecuteResult("Tool execution failed: " + e.getMessage());
		}
	}

	/**
	 * Merge single file to specified folder
	 */
	private ToolExecuteResult mergeFile(String fileName, String targetFolder) {
		if (fileName == null || fileName.trim().isEmpty()) {
			return new ToolExecuteResult("Error: file_name parameter is required");
		}
		if (targetFolder == null || targetFolder.trim().isEmpty()) {
			return new ToolExecuteResult("Error: target_folder parameter is required");
		}

		try {
			Path planDir = directoryManager.getRootPlanDirectory(rootPlanId);
			Path targetDir = planDir.resolve(targetFolder);

			// Ensure target folder exists
			Files.createDirectories(targetDir);

			// Find matching files
			String actualFileName = null;
			Path sourceFile = null;
			List<Path> files = Files.list(planDir).filter(Files::isRegularFile).toList();

			for (Path filePath : files) {
				if (filePath.getFileName().toString().contains(fileName)) {
					sourceFile = filePath;
					actualFileName = filePath.getFileName().toString();
					break;
				}
			}

			if (sourceFile == null) {
				return new ToolExecuteResult("File with name '" + fileName
						+ "' not found. Please use part of the filename to search for files.");
			}

			// Copy file to target folder
			Path targetFile = targetDir.resolve(actualFileName);
			Files.copy(sourceFile, targetFile, StandardCopyOption.REPLACE_EXISTING);

			LoggerUtil.info("File merge completed: {0} -> {1}", actualFileName, targetFolder);

			StringBuilder result = new StringBuilder();
			result.append("File merge successful\n");
			result.append("Source file: ").append(actualFileName).append("\n");
			result.append("Target folder: ").append(targetFolder).append("\n");
			result.append("Target file path: ").append(targetFile.toString()).append("\n");

			return new ToolExecuteResult(result.toString());

		} catch (IOException e) {
			LoggerUtil.error("File merge failed", e);
			return new ToolExecuteResult("File merge failed: " + e.getMessage());
		} catch (Exception e) {
			LoggerUtil.error("File merge operation failed", e);
			return new ToolExecuteResult("File merge operation failed: " + e.getMessage());
		}
	}

	@Override
	public String getCurrentToolStateString() {
		try {
			StringBuilder sb = new StringBuilder();
			sb.append("FileMerge current status:\n");
			sb.append("- Storage root directory: ").append(directoryManager.getRootPlanDirectory(rootPlanId))
					.append("\n");
			Path planDir = directoryManager.getRootPlanDirectory(rootPlanId);
			List<Path> files = Files.exists(planDir) ? Files.list(planDir).filter(Files::isRegularFile).toList()
					: List.of();
			if (files.isEmpty()) {
				sb.append("- Available files: None\n");
			} else {
				sb.append("- Available files (").append(files.size()).append(" files): ");
				for (int i = 0; i < Math.min(files.size(), 5); i++) {
					sb.append(files.get(i).getFileName().toString());
					if (i < Math.min(files.size(), 5) - 1) {
						sb.append(", ");
					}
				}
				if (files.size() > 5) {
					sb.append("...");
				}
				sb.append("\n");
			}
			return sb.toString();
		} catch (Exception e) {
			LoggerUtil.error("Failed to get tool status", e);
			return "FileMerge status retrieval failed: " + e.getMessage();
		}
	}

	@Override
	public void cleanup(String planId) {
		// File merge tool does not need to perform cleanup operations
		LoggerUtil.info("FileMergeTool cleanup for plan: {0}", planId);
	}

}
