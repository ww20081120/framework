/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.agent.tool.mapreduce;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

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
public class ReduceOperationTool extends AbstractBaseTool<ReduceOperationTool.ReduceOperationInput>
    implements TerminableTool {

    // ==================== Configuration Constants ====================

    /**
     * Fixed file name for reduce operations
     */
    private static final String REDUCE_FILE_NAME = "reduce_output.md";

    /**
     * Internal input class for defining Reduce operation tool input parameters
     */
    public static class ReduceOperationInput {

        private String data;

        @com.fasterxml.jackson.annotation.JsonProperty("has_value")
        private boolean hasValue;

        public ReduceOperationInput() {
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

    private static final String TOOL_NAME = "reduce_operation_tool";

    private static String getToolDescription() {
        return """
            Reduce operation tool for MapReduce workflow file manipulation.
            Aggregates and merges data from multiple Map tasks and generates final consolidated output.

            **Important Parameter Description:**
            - has_value: Boolean value indicating whether there is valid data to write
              - If no valid data is found, set to false
              - If there is data to output, set to true
            - data: Must provide data when has_value is true. This should be a string containing the final output content.

            **IMPORTANT**: Tool will automatically terminate after operation completion.
            Please complete all content output in a single call.
            """;
    }

    /**
     * Generate parameters JSON for ReduceOperationTool with predefined columns format
     * 
     * @param terminateColumns the columns specification (e.g., "url,description")
     * @return JSON string for parameters schema
     */
    private static String generateParametersJson() {
        return """
            {
                "type": "object",
                "properties": {
                    "has_value": {
                        "type": "boolean",
                        "description": "Whether there is valid data to write. Set to false if no valid data is found, set to true when there is data"
                    },
                    "data": {
                        "type": "string",
                        "description": "The final output content as a string (only required when has_value is true)"
                    }
                },
                "required": ["has_value"],
                "additionalProperties": false
            }
            """;
    }

    private UnifiedDirectoryManager unifiedDirectoryManager;

    // Shared state manager for managing shared state between multiple Agent
    // instances
    private final MapReduceSharedStateManager sharedStateManager;

    // ==================== TerminableTool Related Fields ====================

    // Thread-safe lock to protect append operations and termination state
    private final ReentrantLock operationLock = new ReentrantLock();

    // Termination state related fields
    private volatile boolean isTerminated = false;

    private String lastTerminationMessage = "";

    private String terminationTimestamp = "";

    /**
     * Constructor for ReduceOperationTool
     * 
     * @param manusProperties the manus properties
     * @param sharedStateManager the shared state manager
     * @param unifiedDirectoryManager the unified directory manager
     */
    public ReduceOperationTool(final IManusProperties manusProperties,
        final MapReduceSharedStateManager sharedStateManager, final UnifiedDirectoryManager unifiedDirectoryManager) {
        this.unifiedDirectoryManager = unifiedDirectoryManager;
        this.sharedStateManager = sharedStateManager;
    }

    @Override
    public String getName() {
        return TOOL_NAME;
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

    @Override
    public String getDescription() {
        return getToolDescription();
    }

    @Override
    public String getParameters() {
        return generateParametersJson();
    }

    @Override
    public Class<ReduceOperationInput> getInputType() {
        return ReduceOperationInput.class;
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
     * Execute Reduce operation
     * 
     * @param input the reduce operation input parameters
     * @return the tool execution result
     */
    @Override
    public ToolExecuteResult run(final ReduceOperationInput input) {
        LoggerUtil.info("ReduceOperationTool input: hasValue={0}", input.isHasValue());
        try {
            String data = input.getData();
            boolean hasValue = input.isHasValue();

            // Check hasValue logic
            if (hasValue) {
                // When hasValue is true, data must be provided
                if (data == null || data.isEmpty()) {
                    return new ToolExecuteResult("Error: data parameter is required when has_value is true");
                }

                // Append the data directly to file
                return appendToFile(REDUCE_FILE_NAME, data);
            }
            else {
                // When hasValue is false, do not append anything but still mark as
                // terminated
                this.isTerminated = true;
                this.lastTerminationMessage = "No data to append, operation completed";
                this.terminationTimestamp = java.time.LocalDateTime.now().toString();
                LoggerUtil.info("Tool marked as terminated (no data to append) for planId: {0}", currentPlanId);
                return new ToolExecuteResult("No data to append, operation completed successfully");
            }

        }
        catch (Exception e) {
            LoggerUtil.error("ReduceOperationTool execution failed", e);
            // Mark as terminated even on failure
            this.isTerminated = true;
            this.lastTerminationMessage = "Operation failed: " + e.getMessage();
            this.terminationTimestamp = java.time.LocalDateTime.now().toString();
            return new ToolExecuteResult("Tool execution failed: " + e.getMessage());
        }
    }

    /**
     * Append content to file in root plan directory Similar to InnerStorageTool.appendToFile() but operates on root
     * plan directory This method is thread-safe and will set termination status after execution
     * 
     * @param fileName the name of the file to append to
     * @param content the content to append
     * @return the tool execution result
     */
    private ToolExecuteResult appendToFile(final String fileName, final String content) {
        operationLock.lock();
        try {
            String contentToWrite = content;
            if (contentToWrite == null) {
                contentToWrite = "";
            }

            // Get file from plan directory with hierarchical structure
            Path planDir = getPlanDirectory();
            ensureDirectoryExists(planDir);

            // Get file path and append content
            Path filePath = planDir.resolve(fileName);

            String resultMessage;
            // If file doesn't exist, create new file
            if (!Files.exists(filePath)) {
                Files.writeString(filePath, contentToWrite);
                LoggerUtil.info("File created and content added: {0}", fileName);
                resultMessage = String.format("File created successfully and content added: %s", fileName);
            }
            else {
                // Append content (add newline)
                Files.writeString(filePath, "\n" + contentToWrite, StandardOpenOption.APPEND);
                LoggerUtil.info("Content appended to file: {0}", fileName);
                resultMessage = String.format("Content appended successfully: %s", fileName);
            }

            // Number of lines to display at the end of the file
            final int linesToDisplay = 3;

            // Width of the separator line
            final int separatorWidth = 30;

            // Read the file and get last 3 lines with line numbers
            List<String> lines = Files.readAllLines(filePath);
            StringBuilder result = new StringBuilder();
            result.append(resultMessage).append("\n\n");
            result.append("Last ").append(linesToDisplay).append(" lines of file:\n");
            result.append("-".repeat(separatorWidth)).append("\n");

            int totalLines = lines.size();
            int startLine = Math.max(0, totalLines - linesToDisplay);

            for (int i = startLine; i < totalLines; i++) {
                result.append(String.format("%4d: %s\n", i + 1, lines.get(i)));
            }

            String resultStr = result.toString();
            if (sharedStateManager != null) {
                sharedStateManager.setLastOperationResult(currentPlanId, resultStr);
            }

            // Set termination status
            this.isTerminated = true;
            this.lastTerminationMessage = "Append operation completed successfully";
            this.terminationTimestamp = java.time.LocalDateTime.now().toString();
            LoggerUtil.info("Tool marked as terminated after append operation for planId: {0}", currentPlanId);

            return new ToolExecuteResult(resultStr);

        }
        catch (IOException e) {
            LoggerUtil.error("Failed to append to file", e);
            // Set termination status even if failed
            this.isTerminated = true;
            this.lastTerminationMessage = "Append operation failed: " + e.getMessage();
            this.terminationTimestamp = java.time.LocalDateTime.now().toString();
            return new ToolExecuteResult("Failed to append to file: " + e.getMessage());
        }
        finally {
            operationLock.unlock();
        }
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
        LoggerUtil.info("ReduceOperationTool cleanup completed for planId: {0}", planId);
    }

    /**
     * Apply the tool with the given input and tool context
     * 
     * @param input the reduce operation input parameters
     * @param toolContext the tool context
     * @return the tool execution result
     */
    @Override
    public ToolExecuteResult apply(final ReduceOperationInput input, final ToolContext toolContext) {
        return run(input);
    }

    /**
     * Get inner storage root directory path
     */
    private Path getInnerStorageRoot() {
        return unifiedDirectoryManager.getInnerStorageRoot();
    }

    /**
     * Get plan directory path with hierarchical structure support
     */
    private Path getPlanDirectory() {
        Path innerStorageRoot = getInnerStorageRoot();
        if (rootPlanId != null && !rootPlanId.equals(currentPlanId)) {
            // Use hierarchical structure: inner_storage/{rootPlanId}/{currentPlanId}
            return innerStorageRoot.resolve(rootPlanId).resolve(currentPlanId);
        }
        else {
            // Use flat structure: inner_storage/{planId}
            return innerStorageRoot.resolve(currentPlanId);
        }
    }

    /**
     * Ensure directory exists
     * 
     * @param directory the directory to ensure exists
     * @throws IOException if an I/O error occurs
     */
    private void ensureDirectoryExists(final Path directory) throws IOException {
        unifiedDirectoryManager.ensureDirectoryExists(directory);
    }

    /**
     * Get class-level expected return info configuration
     * 
     * @return expected return info (always null as it's no longer used)
     */
    public String getTerminateColumns() {
        return null;
    }

    /**
     * Get class-level expected return info configuration
     * 
     * @return expected return info (always null as it's no longer used)
     */
    public String getTerminateColumnsList() {
        return null;
    }

    // ==================== TerminableTool interface implementation
    // ====================

    @Override
    public boolean canTerminate() {
        // Check if append operation has been executed, if so then can terminate
        return isTerminated;
    }

    /**
     * Get termination status information, including original status and termination-related status
     */
    @Override
    public String getCurrentToolStateString() {
        StringBuilder sb = new StringBuilder();

        // Original shared state information
        if (sharedStateManager != null && currentPlanId != null) {
            sb.append(sharedStateManager.getCurrentToolStateString(currentPlanId));
            sb.append("\n\n");
        }

        // Simplified termination status information
        sb.append(String.format("ReduceOperationTool: %s", isTerminated ? "🛑 Terminated" : "⚡ Active"));

        return sb.toString();
    }

    /**
     * Check if tool has already terminated
     */
    public boolean isTerminated() {
        return isTerminated;
    }

    /**
     * Get last termination message
     */
    public String getLastTerminationMessage() {
        return lastTerminationMessage;
    }

    /**
     * Get termination timestamp
     */
    public String getTerminationTimestamp() {
        return terminationTimestamp;
    }

}
