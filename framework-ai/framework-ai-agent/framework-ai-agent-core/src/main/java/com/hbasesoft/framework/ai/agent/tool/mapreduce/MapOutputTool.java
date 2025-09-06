/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.agent.tool.mapreduce;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hbasesoft.framework.ai.agent.config.IManusProperties;
import com.hbasesoft.framework.ai.agent.tool.AbstractBaseTool;
import com.hbasesoft.framework.ai.agent.tool.ToolExecuteResult;
import com.hbasesoft.framework.ai.agent.tool.filesystem.UnifiedDirectoryManager;
import com.hbasesoft.framework.ai.agent.tool.terminate.TerminableTool;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月20日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.agent.tool.mapreduce <br>
 */

@Component
public class MapOutputTool extends AbstractBaseTool<MapOutputTool.MapOutputInput> implements TerminableTool {

    // ==================== Configuration Constants ====================

    /**
     * Task directory name. All tasks are stored under this directory.
     */
    private static final String TASKS_DIRECTORY_NAME = "tasks";

    /**
     * Task status file name. Stores task execution status information.
     */
    private static final String TASK_STATUS_FILE_NAME = "status.json";

    /**
     * Task output file name. Stores results after Map stage processing completion.
     */
    private static final String TASK_OUTPUT_FILE_NAME = "output.md";

    /**
     * Task input file name. Stores document fragment content after splitting.
     */
    private static final String TASK_INPUT_FILE_NAME = "input.md";

    /**
     * Task status: completed
     */
    /**
     * Internal input class for defining Map output tool input parameters
     */
    public static class MapOutputInput {

        @com.fasterxml.jackson.annotation.JsonProperty("task_id")
        private String taskId;

        private String data;

        @com.fasterxml.jackson.annotation.JsonProperty("has_value")
        private boolean hasValue;

        public MapOutputInput() {
        }

        public String getTaskId() {
            return taskId;
        }

        public void setTaskId(String taskId) {
            this.taskId = taskId;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }

        public boolean isHasValue() {
            return hasValue;
        }

        public void setHasValue(boolean hasValue) {
            this.hasValue = hasValue;
        }

    }

    private static final String TOOL_NAME = "map_output_tool";

    private static String getToolDescription() {
        String baseDescription = """
            Map output recording tool for MapReduce workflow.
            Accepts content after Map stage processing is completed, automatically generates file names and creates output files.
            Records task status and manages structured data output.

            **Important parameter description:**
            - task_id: String, task ID identifier, used to identify the currently processing Map task (required)
            - has_value: Boolean, indicates whether there is valid data
              - Set to false if no valid data is found
              - Set to true if there is data to output
            - data: Data must be provided when has_value is true. You need to output information in a structured manner as required by the return data, in markdown format.

            """;

        return baseDescription;
    }

    /**
     * Generate parameters JSON for MapOutputTool with predefined columns format
     * 
     * @param terminateColumns the columns specification (e.g., "url")
     * @return JSON string for parameters schema
     */
    private static String generateParametersJson() {
        String columnsDesc = "data row list";

        return """
            {
                "type": "object",
                "properties": {
                    "task_id": {
                        "type": "string",
                        "description": "Task ID identifier for identifying the currently processing Map task"
                    },
                    "has_value": {
                        "type": "boolean",
                        "description": "Whether there is valid data. Set to false if no valid data is found, set to true when there is data"
                    },
                    "data": {
                        "type": "string",
                        "description": "%s (only required when has_value is true)"
                    }
                },
                "required": ["task_id", "has_value"],
                "additionalProperties": false
            }
            """
            .formatted(columnsDesc);
    }

    private UnifiedDirectoryManager unifiedDirectoryManager;

    // Shared state manager for managing shared state between multiple Agent
    // instances
    private IMapReduceSharedStateManager sharedStateManager;

    // Track if map output recording has completed, allowing termination
    private volatile boolean mapOutputRecorded = false;

    private final ObjectMapper objectMapper;

    // Main constructor
    public MapOutputTool(final IManusProperties manusProperties, final IMapReduceSharedStateManager sharedStateManager,
        final UnifiedDirectoryManager unifiedDirectoryManager, final ObjectMapper objectMapper) {
        this.unifiedDirectoryManager = unifiedDirectoryManager;
        this.sharedStateManager = sharedStateManager;
        this.objectMapper = objectMapper;
    }

    /**
     * Get task directory list
     */
    public List<String> getSplitResults() {
        if (sharedStateManager != null && currentPlanId != null) {
            return sharedStateManager.getSplitResults(currentPlanId);
        }
        return new ArrayList<>();
    }

    /**
     * Set shared state manager
     * 
     * @param sharedStateManager the shared state manager to set
     */
    public void setSharedStateManager(final IMapReduceSharedStateManager sharedStateManager) {
        this.sharedStateManager = sharedStateManager;
    }

    @Override
    public String getName() {
        return TOOL_NAME;
    }

    @Override
    public String getDescription() {
        return getToolDescription();
    }

    @Override
    public String getParameters() {
        return generateParametersJson();
    }

    @Override
    public Class<MapOutputInput> getInputType() {
        return MapOutputInput.class;
    }

    @Override
    public String getServiceGroup() {
        return "data-processing";
    }

    public static OpenAiApi.FunctionTool getToolDefinition() {
        String parameters = generateParametersJson();
        String description = getToolDescription();
        OpenAiApi.FunctionTool.Function function = new OpenAiApi.FunctionTool.Function(description, TOOL_NAME,
            parameters);
        return new OpenAiApi.FunctionTool(function);
    }

    /**
     * Execute Map output recording operation
     * 
     * @param input the map output input parameters
     * @return the tool execution result
     */
    @Override
    public ToolExecuteResult run(final MapOutputInput input) {
        LoggerUtil.info("MapOutputTool input: taskId={0}, hasValue={1}", input.getTaskId(), input.isHasValue());
        try {
            String taskId = input.getTaskId();
            String data = input.getData();
            boolean hasValue = input.isHasValue();

            // Validate taskId
            if (taskId == null || taskId.trim().isEmpty()) {
                return new ToolExecuteResult("Error: task_id parameter is required");
            }

            // Check hasValue logic
            if (hasValue) {
                // When hasValue is true, data must be provided
                if (data == null || data.isEmpty()) {
                    return new ToolExecuteResult("Error: data parameter is required when has_value is true");
                }
                // Convert structured data to content string
                String content = formatStructuredData(data);
                return recordMapTaskOutput(content, taskId);
            }
            else {
                // When hasValue is false, create empty output
                return recordMapTaskOutput("", taskId);
            }

        }
        catch (Exception e) {
            LoggerUtil.error("MapOutputTool execution failed", e);
            return new ToolExecuteResult("Tool execution failed: " + e.getMessage());
        }
    }

    /**
     * Format structured data similar to TerminateTool
     * 
     * @param data the data rows
     * @return formatted string representation of the structured data
     */
    private String formatStructuredData(final String data) {
        if (data == null || data.isEmpty()) {
            return "";
        }
        return data;
    }

    /**
     * Record Map task output result with completed status by default. Task ID is provided as parameter instead of being
     * obtained from the current execution context.
     * 
     * @param content the content to be recorded
     * @param taskId the task ID
     * @return the tool execution result
     */
    private ToolExecuteResult recordMapTaskOutput(final String content, final String taskId) {
        try {
            // Get timeout configuration for this operation
            Integer timeout = getMapReduceTimeout();
            LoggerUtil.debug("Recording Map task output with timeout: {0} seconds", timeout);

            // Ensure planId exists
            if (currentPlanId == null || currentPlanId.trim().isEmpty()) {
                return new ToolExecuteResult("Error: currentPlanId not set, cannot record task status");
            }
            // Validate taskId parameter
            if (taskId == null || taskId.trim().isEmpty()) {
                return new ToolExecuteResult("Error: taskId parameter is required for recording output");
            }

            // Locate task directory - use hierarchical structure:
            // inner_storage/{rootPlanId}/{currentPlanId}/tasks/{taskId}
            Path rootPlanDir = getPlanDirectory(rootPlanId);
            Path currentPlanDir = rootPlanDir.resolve(currentPlanId);
            Path taskDir = currentPlanDir.resolve(TASKS_DIRECTORY_NAME).resolve(taskId);

            if (!Files.exists(taskDir)) {
                return new ToolExecuteResult("Error: Task directory does not exist: " + taskId);
            }

            // Create output.md file
            Path outputFile = taskDir.resolve(TASK_OUTPUT_FILE_NAME);
            // Write processing content directly without adding extra metadata information
            Files.write(outputFile, content.getBytes());
            String outputFilePath = outputFile.toAbsolutePath().toString();
            // Update task status file
            Path statusFile = taskDir.resolve(TASK_STATUS_FILE_NAME);
            TaskStatus taskStatus;

            if (Files.exists(statusFile)) {
                // Read existing status
                String existingStatusJson = new String(Files.readAllBytes(statusFile));
                taskStatus = objectMapper.readValue(existingStatusJson, TaskStatus.class);
            }
            else {
                // Create new status
                taskStatus = new TaskStatus();
                taskStatus.taskId = taskId;
                taskStatus.inputFile = taskDir.resolve(TASK_INPUT_FILE_NAME).toAbsolutePath().toString();
            }

            // Update status information
            taskStatus.outputFilePath = outputFilePath;
            taskStatus.status = "completed";
            taskStatus.timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

            // Store in shared state manager
            if (sharedStateManager != null) {
                IMapReduceSharedStateManager.TaskStatus sharedTaskStatus = new MapReduceSharedStateManager.TaskStatus();
                sharedTaskStatus.setTaskId(taskStatus.getTaskId());
                sharedTaskStatus.setInputFile(taskStatus.getInputFile());
                sharedTaskStatus.setOutputFilePath(taskStatus.getOutputFilePath());
                sharedTaskStatus.setStatus(taskStatus.getStatus());
                sharedTaskStatus.setTimestamp(taskStatus.getTimestamp());
                sharedStateManager.recordMapTaskStatus(currentPlanId, taskId, sharedTaskStatus);
            }

            // Write updated status file
            String statusJson = objectMapper.writeValueAsString(taskStatus);
            Files.write(statusFile, statusJson.getBytes());

            // Mark that map output has been recorded, allowing termination
            this.mapOutputRecorded = true;

            String result = String.format("Task %s status recorded: %s, output file: %s", taskId, "completed",
                TASK_OUTPUT_FILE_NAME);

            String resultStr = result.toString();
            if (sharedStateManager != null) {
                sharedStateManager.setLastOperationResult(currentPlanId, resultStr);
            }
            LoggerUtil.info(result);
            return new ToolExecuteResult(result);

        }
        catch (Exception e) {
            String error = "Recording Map task status failed: " + e.getMessage();
            LoggerUtil.error(error, e);
            return new ToolExecuteResult(error);
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
        LoggerUtil.info("MapOutputTool cleanup completed for planId: {0}", planId);
    }

    /**
     * Apply the tool with the given input and tool context
     * 
     * @param input the map output input parameters
     * @param toolContext the tool context
     * @return the tool execution result
     */
    @Override
    public ToolExecuteResult apply(final MapOutputInput input, final ToolContext toolContext) {
        return run(input);
    }

    /**
     * Get inner storage root directory path
     */
    private Path getInnerStorageRoot() {
        return unifiedDirectoryManager.getInnerStorageRoot();
    }

    /**
     * Get plan directory path
     * 
     * @param planId the plan ID
     * @return the plan directory path
     */
    private Path getPlanDirectory(final String planId) {
        return getInnerStorageRoot().resolve(planId);
    }

    /**
     * Get class-level terminate columns configuration
     * 
     * @return terminate columns as comma-separated string
     */
    public String getTerminateColumns() {
        // Since terminateColumns is removed, always return null
        return null;
    }

    /**
     * Get class-level terminate columns configuration as List
     * 
     * @return terminate columns as List<String>
     */
    public List<String> getTerminateColumnsList() {
        // Since terminateColumns is removed, always return null
        return null;
    }

    /**
     * Task status internal class
     */
    @SuppressWarnings("unused")
    private static class TaskStatus {

        /** Task ID */
        private String taskId;

        /** Input file path */
        private String inputFile;

        /** Output file path */
        private String outputFilePath;

        /** Task status */
        private String status;

        /** Timestamp */
        private String timestamp;

        /**
         * Get task ID
         * 
         * @return task ID
         */
        public String getTaskId() {
            return taskId;
        }

        /**
         * Set task ID
         * 
         * @param taskId task ID
         */
        public void setTaskId(String taskId) {
            this.taskId = taskId;
        }

        /**
         * Get input file path
         * 
         * @return input file path
         */
        public String getInputFile() {
            return inputFile;
        }

        /**
         * Set input file path
         * 
         * @param inputFile input file path
         */
        public void setInputFile(String inputFile) {
            this.inputFile = inputFile;
        }

        /**
         * Get output file path
         * 
         * @return output file path
         */
        public String getOutputFilePath() {
            return outputFilePath;
        }

        /**
         * Set output file path
         * 
         * @param outputFilePath output file path
         */
        public void setOutputFilePath(String outputFilePath) {
            this.outputFilePath = outputFilePath;
        }

        /**
         * Get task status
         * 
         * @return task status
         */
        public String getStatus() {
            return status;
        }

        /**
         * Set task status
         * 
         * @param status task status
         */
        public void setStatus(String status) {
            this.status = status;
        }

        /**
         * Get timestamp
         * 
         * @return timestamp
         */
        public String getTimestamp() {
            return timestamp;
        }

        /**
         * Set timestamp
         * 
         * @param timestamp timestamp
         */
        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

    }

    /** Default timeout for MapReduce operations in seconds */
    private static final int DEFAULT_MAPREDUCE_TIMEOUT = 300;

    /**
     * Get MapReduce operation timeout configuration
     * 
     * @return Timeout in seconds, returns default value of 300 seconds if not configured
     */
    private Integer getMapReduceTimeout() {
        // For now, use default timeout until the configuration is added to
        // ManusProperties
        return DEFAULT_MAPREDUCE_TIMEOUT; // Default timeout is 5 minutes
    }

    // ==================== TerminableTool interface implementation
    // ====================

    @Override
    public boolean canTerminate() {
        // MapOutputTool can be terminated only after map output has been recorded
        // This ensures that the tool completes its processing cycle before termination
        return mapOutputRecorded;
    }

}
