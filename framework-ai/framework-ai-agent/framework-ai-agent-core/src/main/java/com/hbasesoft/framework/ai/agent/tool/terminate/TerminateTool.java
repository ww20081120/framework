/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.agent.tool.terminate;

import java.util.List;
import java.util.Map;

import com.hbasesoft.framework.ai.agent.tool.AbstractBaseTool;
import com.hbasesoft.framework.ai.agent.tool.ToolExecuteResult;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月19日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.agent.tool.terminate <br>
 */
public class TerminateTool extends AbstractBaseTool<Map<String, Object>> implements TerminableTool {

	public static final String name = "terminate";

	private final String expectedReturnInfo;

	private String lastTerminationMessage = "";

	private boolean isTerminated = false;

	private String terminationTimestamp = "";

	private static String getDescriptions(String expectedReturnInfo) {
		// Simple description to avoid generating overly long content
		return "Terminate the current execution step with structured data. "
				+ "Provide data in JSON format with 'message' field and optional 'fileList' array containing file information.";
	}

	private static String generateParametersJson(String expectedReturnInfo) {
		String template = """
				{
				  "type": "object",
				  "properties": {
				    "message": {
				      "type": "string",
				      "description": "Comprehensive termination message that should include all relevant facts, viewpoints, details, and conclusions from the execution step. This message should provide a complete summary of what was accomplished, any important observations, key findings, and final outcomes. The message must explicitly mention and describe the data corresponding to the expected return information: %s"
				    },
				    "fileList": {
				      "type": "array",
				      "items": {
				        "type": "object",
				        "properties": {
				          "fileName": {
				            "type": "string",
				            "description": "Name of the file"
				          },
				          "fileDescription": {
				            "type": "string",
				            "description": "Detailed description of what the file contains. This should include a comprehensive summary of all content generated during this agent execution cycle. Every file created during this execution must be listed here with complete and accurate information about its contents."
				          }
				        },
				        "required": ["fileName", "fileDescription"]
				      },
				      "description": "Complete list of all files generated during this agent execution cycle. Every file created must be included with its name and a detailed description of its contents. This is mandatory for full transparency and auditing purposes."
				    },
				    "folderList": {
				      "type": "array",
				      "items": {
				        "type": "object",
				        "properties": {
				          "folderName": {
				            "type": "string",
				            "description": "Name of the folder"
				          },
				          "folderDescription": {
				            "type": "string",
				            "description": "Detailed description of what the folder contains. This should include a comprehensive summary of all content within this folder generated during this agent execution cycle."
				          }
				        },
				        "required": ["folderName", "folderDescription"]
				      },
				      "description": "Complete list of all folders generated during this agent execution cycle. Every folder created must be included with its name and a detailed description of its contents."
				    }
				  },
				  "required": ["message"]
				}
				""";

		return String.format(template, expectedReturnInfo != null ? expectedReturnInfo : "N/A");
	}

	@Override
	public String getCurrentToolStateString() {
		return String.format("""
				Termination Tool Status:
				- Current State: %s
				- Last Termination: %s
				- Termination Message: %s
				- Timestamp: %s
				- Plan ID: %s
				- Expected Return Info: %s
				""", isTerminated ? "🛑 Terminated" : "⚡ Active",
				isTerminated ? "Process was terminated" : "No termination recorded",
				lastTerminationMessage.isEmpty() ? "N/A" : lastTerminationMessage,
				terminationTimestamp.isEmpty() ? "N/A" : terminationTimestamp,
				currentPlanId != null ? currentPlanId : "N/A", expectedReturnInfo != null ? expectedReturnInfo : "N/A");
	}

	public TerminateTool(String planId, String expectedReturnInfo) {
		this.currentPlanId = planId;
		// If expectedReturnInfo is null or empty, use "message" as default
		this.expectedReturnInfo = (expectedReturnInfo == null || expectedReturnInfo.isEmpty()) ? "message"
				: expectedReturnInfo;
	}

	@Override
	public ToolExecuteResult run(Map<String, Object> input) {
		LoggerUtil.info("Terminate with input: {0}", input);

		// Extract message from the structured data
		String message = formatStructuredData(input);
		this.lastTerminationMessage = message;
		this.isTerminated = true;
		this.terminationTimestamp = java.time.LocalDateTime.now().toString();

		return new ToolExecuteResult(message);
	}

	private String formatStructuredData(Map<String, Object> input) {
		StringBuilder sb = new StringBuilder();

		// Handle new format with message and fileList
		if (input.containsKey("message")) {
			sb.append("Message: ").append(input.get("message")).append("\n");
		}

		if (input.containsKey("fileList")) {
			@SuppressWarnings("unchecked")
			List<Map<String, String>> fileList = (List<Map<String, String>>) input.get("fileList");
			sb.append("Files:\n");
			for (Map<String, String> file : fileList) {
				sb.append("  - Name: ").append(file.get("fileName")).append("\n    Description: ")
						.append(file.get("fileDescription")).append("\n");
			}
		}

		if (input.containsKey("folderList")) {
			@SuppressWarnings("unchecked")
			List<Map<String, String>> folderList = (List<Map<String, String>>) input.get("folderList");
			sb.append("Folders:\n");
			for (Map<String, String> folder : folderList) {
				sb.append("  - Name: ").append(folder.get("folderName")).append("\n    Description: ")
						.append(folder.get("folderDescription")).append("\n");
			}
		}

		// If no recognized keys, just output the whole map
		if (!input.containsKey("message") && !input.containsKey("fileList") && !input.containsKey("folderList")) {
			sb.append(input.toString());
		}

		return sb.toString();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDescription() {
		return getDescriptions(this.expectedReturnInfo);
	}

	@Override
	public String getParameters() {
		return generateParametersJson(this.expectedReturnInfo);
	}

	@Override
	public Class<Map<String, Object>> getInputType() {
		@SuppressWarnings("unchecked")
		Class<Map<String, Object>> clazz = (Class<Map<String, Object>>) (Class<?>) Map.class;
		return clazz;
	}

	@Override
	public boolean isReturnDirect() {
		return true;
	}

	@Override
	public void cleanup(String planId) {
		// do nothing
	}

	@Override
	public String getServiceGroup() {
		return "default-service-group";
	}

	// ==================== TerminableTool interface implementation
	// ====================

	@Override
	public boolean canTerminate() {
		// TerminateTool can always be terminated as its purpose is to terminate
		// execution
		return true;
	}

}
