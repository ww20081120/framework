/****************************************************************************************
 * Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or    *
 * transmission in whole or in part, in any form or by any means, electronic, mechanical*
 * or otherwise, is prohibited without the prior written consent of the copyright owner.*
 ****************************************************************************************/
package com.hbasesoft.framework.ai.agent.tool.mapreduce;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
public class DataSplitTool extends AbstractBaseTool<DataSplitTool.DataSplitInput> implements TerminableTool {

    // ==================== Configuration Constants ====================

    /**
     * Default plan ID prefix When planId is empty, use this prefix + timestamp to generate default ID
     */
    private static final String DEFAULT_PLAN_ID_PREFIX = "plan-";

    /**
     * Task directory name All tasks are stored under this directory
     */
    private static final String TASKS_DIRECTORY_NAME = "tasks";

    /**
     * Task ID format template Used to generate incremental task IDs like task_001, task_002
     */
    private static final String TASK_ID_FORMAT = "task_%03d";

    /**
     * Task input file name Stores document fragment content after splitting
     */
    private static final String TASK_INPUT_FILE_NAME = "input.md";

    /**
     * Task status file name Stores task execution status information
     */
    private static final String TASK_STATUS_FILE_NAME = "status.json";

    /**
     * Task status: pending
     */
    private static final String TASK_STATUS_PENDING = "pending";

    /**
     * Internal input class for defining data split tool input parameters
     */
    public static class DataSplitInput {

        /** Input file to split */
        @com.fasterxml.jackson.annotation.JsonProperty("input_file_to_split")
        private String inputFileToSplit;

        /** Expected output fields */
        @com.fasterxml.jackson.annotation.JsonProperty("expected_output_fields")
        private java.util.List<String> expectedOutputFields;

        /**
         * Default constructor
         */
        public DataSplitInput() {
        }

        /**
         * Get input file to split
         * 
         * @return input file to split
         */
        public String getInputFileToSplit() {
            return inputFileToSplit;
        }

        /**
         * Set input file to split
         * 
         * @param inputFileToSplit input file to split
         */
        public void setInputFileToSplit(final String inputFileToSplit) {
            this.inputFileToSplit = inputFileToSplit;
        }

        /**
         * Get expected output fields
         * 
         * @return expected output fields
         */
        public java.util.List<String> getExpectedOutputFields() {
            return expectedOutputFields;
        }

        /**
         * Set expected output fields
         * 
         * @param expectedOutputFields expected output fields
         */
        public void setExpectedOutputFields(final java.util.List<String> expectedOutputFields) {
            this.expectedOutputFields = expectedOutputFields;
        }

    }

    private static final String TOOL_NAME = "data_split_tool";

    private static final String TOOL_DESCRIPTION = """
        Data split tool for MapReduce workflow data preparation phase.
        Automatically validates file existence and performs data split processing.
        Supports CSV, TSV, TXT and other text format data files.

        Key features:
        - File and directory existence validation
        - Automatic text file detection and processing
        - Task directory structure creation with metadata
        - Support for both single files and directories
        """;

    private static final String PARAMETERS_JSON = """
        {
            "type": "object",
            "properties": {
                "input_file_to_split": {
                    "type": "string",
                    "description": "Input file or folder path to be split"
                },
                "expected_output_fields": {
                    "type": "array",
                    "description": "List of expected output field names",
                    "items": {
                        "type": "string"
                    }
                }
            },
            "required": ["input_file_to_split"],
            "additionalProperties": false
        }
        """;

    private UnifiedDirectoryManager unifiedDirectoryManager;

    private IManusProperties manusProperties;

    // Shared state manager for managing shared state between multiple Agent
    // instances
    private MapReduceSharedStateManager sharedStateManager;

    // Track if split operation has completed, allowing termination
    private volatile boolean splitCompleted = false;

    private final ObjectMapper objectMapper;

    /**
     * Constructor for DataSplitTool
     * 
     * @param manusProperties the manus properties
     * @param sharedStateManager the shared state manager
     * @param unifiedDirectoryManager the unified directory manager
     * @param objectMapper the object mapper
     */
    public DataSplitTool(final IManusProperties manusProperties, final MapReduceSharedStateManager sharedStateManager,
        final UnifiedDirectoryManager unifiedDirectoryManager, final ObjectMapper objectMapper) {
        this.manusProperties = manusProperties;
        this.unifiedDirectoryManager = unifiedDirectoryManager;
        this.sharedStateManager = sharedStateManager;
        this.objectMapper = objectMapper;
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

    @Override
    public String getDescription() {
        return TOOL_DESCRIPTION;
    }

    @Override
    public String getParameters() {
        return PARAMETERS_JSON;
    }

    @Override
    public Class<DataSplitInput> getInputType() {
        return DataSplitInput.class;
    }

    @Override
    public String getServiceGroup() {
        return "data-processing";
    }

    public static OpenAiApi.FunctionTool getToolDefinition() {
        OpenAiApi.FunctionTool.Function function = new OpenAiApi.FunctionTool.Function(TOOL_DESCRIPTION, TOOL_NAME,
            PARAMETERS_JSON);
        return new OpenAiApi.FunctionTool(function);
    }

    /**
     * Execute data split operation
     * 
     * @param input the data split input
     * @return the tool execution result
     */
    @Override
    public ToolExecuteResult run(final DataSplitInput input) {
        LoggerUtil.info("DataSplitTool input: inputFileToSplit={0}, expectedOutputFields={1}",
            input.getInputFileToSplit(), input.getExpectedOutputFields());
        try {
            String inputFileToSplit = input.getInputFileToSplit();
            if (inputFileToSplit == null) {
                return new ToolExecuteResult("Error: input_file_to_split parameter is required");
            }

            return processFileOrDirectory(inputFileToSplit, input.getExpectedOutputFields());

        }
        catch (Exception e) {
            LoggerUtil.error("DataSplitTool execution failed", e);
            return new ToolExecuteResult("Tool execution failed: " + e.getMessage());
        }
    }

    /**
     * Process complete workflow for file or directory: validate existence -> split data
     * 
     * @param inputFileToSplit the input file or directory to split
     * @param expectedOutputFields the expected output fields
     * @return the tool execution result
     */
    private ToolExecuteResult processFileOrDirectory(final String inputFileToSplit,
        final java.util.List<String> expectedOutputFields) {
        try {
            // Ensure planId exists, use default if empty
            if (currentPlanId == null || currentPlanId.trim().isEmpty()) {
                currentPlanId = DEFAULT_PLAN_ID_PREFIX + System.currentTimeMillis();
                LoggerUtil.info("currentPlanId is empty, using default value: {0}", currentPlanId);
            }

            // Validate file or folder existence
            // Use UnifiedDirectoryManager to get working directory path
            String workingDirectoryPath = unifiedDirectoryManager.getWorkingDirectoryPath();
            // Process based on path type
            Path path = null;
            boolean foundInInnerStorage = false;

            // First, try to find file in inner storage directory
            if (!Paths.get(inputFileToSplit).isAbsolute()) {
                // Check in inner storage first
                Path planDir = getPlanDirectory(rootPlanId);
                Path innerStoragePath = planDir.resolve(inputFileToSplit);

                if (Files.exists(innerStoragePath)) {
                    path = innerStoragePath;
                    foundInInnerStorage = true;
                    LoggerUtil.info("Found file in inner storage: {0}", path.toAbsolutePath());
                }
            }

            // If not found in inner storage, try working directory
            if (path == null) {
                if (Paths.get(inputFileToSplit).isAbsolute()) {
                    // If absolute path, use directly
                    path = Paths.get(inputFileToSplit);
                }
                else {
                    // If relative path, resolve based on working directory
                    path = Paths.get(workingDirectoryPath).resolve(inputFileToSplit);
                }
                LoggerUtil.info("Checking file in working directory: {0}", path.toAbsolutePath());
            }
            if (!Files.exists(path)) {
                String errorMsg = "Error: File or directory does not exist: " + path.toAbsolutePath().toString();
                if (!foundInInnerStorage) {
                    // Also check if file exists in inner storage and provide helpful
                    // message
                    Path planDir = getPlanDirectory(currentPlanId);
                    Path innerStoragePath = planDir.resolve(inputFileToSplit);
                    if (Files.exists(innerStoragePath)) {
                        errorMsg += "\nNote: File exists in inner storage at: "
                            + innerStoragePath.toAbsolutePath().toString();
                    }
                    else {
                        errorMsg += "\nSearched in: working directory and inner storage ("
                            + planDir.toAbsolutePath().toString() + ")";
                    }
                }
                return new ToolExecuteResult(errorMsg);
            }

            boolean isFile = Files.isRegularFile(path);
            boolean isDirectory = Files.isDirectory(path);

            // Determine output directory - store to
            // inner_storage/{rootPlanId}/{currentPlanId}/tasks directory
            // This creates a hierarchical structure where sub-plan data is stored
            // under the root plan
            Path rootPlanDir = getPlanDirectory(rootPlanId);
            Path currentPlanDir = rootPlanDir.resolve(currentPlanId);
            Path tasksPath = currentPlanDir.resolve(TASKS_DIRECTORY_NAME);
            ensureDirectoryExists(tasksPath);

            List<String> allTaskDirs = new ArrayList<>();

            if (isFile && isTextFile(path.toString())) {
                // Process single file - use infinite context task context size
                int splitSize = getInfiniteContextTaskContextSize();
                SplitResult result = splitSingleFileToTasks(path, null, splitSize, tasksPath, null);
                allTaskDirs.addAll(result.taskDirs);

            }
            else if (isDirectory) {
                // Process all text files in directory - use infinite context task context
                // size
                int splitSize = getInfiniteContextTaskContextSize();
                List<Path> textFiles = Files.list(path).filter(Files::isRegularFile)
                    .filter(p -> isTextFile(p.toString())).collect(Collectors.toList());

                for (Path file : textFiles) {
                    SplitResult result = splitSingleFileToTasks(file, null, splitSize, tasksPath, null);
                    allTaskDirs.addAll(result.taskDirs);
                }
            }

            // Update split results
            if (sharedStateManager != null) {
                sharedStateManager.setSplitResults(currentPlanId, allTaskDirs);
            }

            // Generate concise return result with table header information if it's a
            // table file
            StringBuilder result = new StringBuilder();
            result.append("Split successful");

            // If expectedOutputFields is provided, include information about it
            if (expectedOutputFields != null && !expectedOutputFields.isEmpty()) {
                result.append(", expected output fields: ").append(expectedOutputFields);
            }

            result.append(", created ").append(allTaskDirs.size()).append(" task directories");

            String resultStr = result.toString();
            if (sharedStateManager != null) {
                sharedStateManager.setLastOperationResult(currentPlanId, resultStr);
            }

            // Mark that split operation has completed, allowing termination
            this.splitCompleted = true;

            return new ToolExecuteResult(resultStr);

        }
        catch (Exception e) {
            String error = "Processing failed: " + e.getMessage();
            LoggerUtil.error(error, e);
            return new ToolExecuteResult(error);
        }
    }

    /**
     * Split results class
     */
    private static class SplitResult {

        private List<String> taskDirs;

        SplitResult(final List<String> taskDirs) {
            this.taskDirs = taskDirs;
        }

        /**
         * Get task directories
         * 
         * @return task directories
         */
        public List<String> getTaskDirs() {
            return taskDirs;
        }

    }

    /**
     * Split single file into task directory structure
     * 
     * @param filePath the file path to split
     * @param headers the headers
     * @param splitSize the split size
     * @param tasksPath the tasks path
     * @param delimiter the delimiter
     * @return the split result
     * @throws IOException if an I/O error occurs
     */
    private SplitResult splitSingleFileToTasks(final Path filePath, final String headers, final int splitSize,
        final Path tasksPath, final String delimiter) throws IOException {
        List<String> taskDirs = new ArrayList<>();
        String fileName = filePath.getFileName().toString();

        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String line;
            StringBuilder currentContent = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                // Add line content and newline character
                String lineWithNewline = line + "\n";

                // Check if adding this line would exceed character limit
                if (currentContent.length() + lineWithNewline.length() > splitSize && currentContent.length() > 0) {
                    // If would exceed limit and current content is not empty, save
                    // current content first
                    String taskDir = createTaskDirectory(tasksPath, currentContent.toString(), fileName);
                    taskDirs.add(taskDir);
                    currentContent = new StringBuilder();
                }

                // Handle oversized single line when currentContent is empty
                if (currentContent.length() == 0 && lineWithNewline.length() > splitSize) {
                    // Split the oversized line into multiple chunks
                    String lineContent = lineWithNewline;
                    int startIndex = 0;
                    int chunkCount = 0;

                    LoggerUtil.warn(
                        "Line exceeds split size limit ({0} chars > {1} chars), " + "splitting into chunks: file={}",
                        lineWithNewline.length(), splitSize, fileName);

                    while (startIndex < lineContent.length()) {
                        int endIndex = Math.min(startIndex + splitSize, lineContent.length());
                        String chunk = lineContent.substring(startIndex, endIndex);
                        chunkCount++;

                        // Create task directory for each chunk
                        String taskDir = createTaskDirectory(tasksPath, chunk, fileName);
                        taskDirs.add(taskDir);

                        startIndex = endIndex;
                    }

                    LoggerUtil.info("Oversized line split into {0} chunks for file: {1}", chunkCount, fileName);
                }
                else {
                    // Add current line normally
                    currentContent.append(lineWithNewline);
                }
            }

            // Process remaining content
            if (currentContent.length() > 0) {
                String taskDir = createTaskDirectory(tasksPath, currentContent.toString(), fileName);
                taskDirs.add(taskDir);
            }
        }

        return new SplitResult(taskDirs);
    }

    /**
     * Create task directory structure
     * 
     * @param tasksPath the tasks path
     * @param content the content
     * @param originalFileName the original file name
     * @return the task directory path
     * @throws IOException if an I/O error occurs
     */
    private String createTaskDirectory(final Path tasksPath, final String content, final String originalFileName)
        throws IOException {
        // Generate task ID
        String taskId = null;
        if (sharedStateManager != null) {
            taskId = sharedStateManager.getNextTaskId(currentPlanId);
        }
        else {
            // Fallback solution: use default format
            taskId = String.format(TASK_ID_FORMAT, 1);
        }

        Path taskDir = tasksPath.resolve(taskId);
        ensureDirectoryExists(taskDir);

        // Create input.md file
        Path inputFile = taskDir.resolve(TASK_INPUT_FILE_NAME);
        StringBuilder inputContent = new StringBuilder();
        // inputContent.append("# Document Fragment\n\n");
        // inputContent.append("**Original File:**
        // ").append(originalFileName).append("\n\n");
        inputContent.append("**Task ID:** ").append(taskId).append("\n\n");
        inputContent.append("## Content\n\n");
        inputContent.append("```\n");
        inputContent.append(content);
        inputContent.append("```\n");

        Files.write(inputFile, inputContent.toString().getBytes());

        // Create initial status file status.json
        Path statusFile = taskDir.resolve(TASK_STATUS_FILE_NAME);
        TaskStatus initialStatus = new TaskStatus();
        initialStatus.taskId = taskId;
        initialStatus.status = TASK_STATUS_PENDING;
        initialStatus.timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        initialStatus.inputFile = inputFile.toAbsolutePath().toString();

        String statusJson = objectMapper.writeValueAsString(initialStatus);
        Files.write(statusFile, statusJson.getBytes());

        return taskDir.toAbsolutePath().toString();
    }

    /**
     * Check if file is a text file
     * 
     * @param fileName the file name
     * @return true if the file is a text file, false otherwise
     */
    private boolean isTextFile(final String fileName) {
        String lowercaseFileName = fileName.toLowerCase();
        return lowercaseFileName.endsWith(".csv") || lowercaseFileName.endsWith(".tsv")
            || lowercaseFileName.endsWith(".txt") || lowercaseFileName.endsWith(".dat")
            || lowercaseFileName.endsWith(".log") || lowercaseFileName.endsWith(".json")
            || lowercaseFileName.endsWith(".xml") || lowercaseFileName.endsWith(".yaml")
            || lowercaseFileName.endsWith(".yml") || lowercaseFileName.endsWith(".md");
    }

    /**
     * Check if file is a table file
     * 
     * @param fileName the file name
     * @return true if the file is a table file, false otherwise
     */
    private boolean isTableFile(final String fileName) {
        String lowercaseFileName = fileName.toLowerCase();
        return lowercaseFileName.endsWith(".csv") || lowercaseFileName.endsWith(".tsv")
            || lowercaseFileName.endsWith(".xls") || lowercaseFileName.endsWith(".xlsx");
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

    @Override
    public void cleanup(final String planId) {
        // Clean up shared state
        if (sharedStateManager != null && planId != null) {
            sharedStateManager.cleanupPlanState(planId);
        }
        LoggerUtil.info("DataSplitTool cleanup completed for planId: {0}", planId);
    }

    @Override
    public ToolExecuteResult apply(final DataSplitInput input, final ToolContext toolContext) {
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
     * Get plan directory path
     * 
     * @param planId the plan ID
     * @return the plan directory path
     */
    private Path getPlanDirectory(final String planId) {
        return getInnerStorageRoot().resolve(planId);
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

    /** Default context size for infinite context tasks */
    private static final int DEFAULT_CONTEXT_SIZE = 20000;

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
        public void setTaskId(final String taskId) {
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
        public void setInputFile(final String inputFile) {
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
        public void setOutputFilePath(final String outputFilePath) {
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
        public void setStatus(final String status) {
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
        public void setTimestamp(final String timestamp) {
            this.timestamp = timestamp;
        }

    }

    // ==================== TerminableTool interface implementation
    // ====================

    @Override
    public boolean canTerminate() {
        // DataSplitTool can be terminated only after split operation has completed
        return splitCompleted;
    }

}
