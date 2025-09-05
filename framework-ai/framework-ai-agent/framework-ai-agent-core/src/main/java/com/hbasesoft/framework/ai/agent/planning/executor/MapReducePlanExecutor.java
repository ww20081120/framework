/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.agent.planning.executor;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.collections4.CollectionUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hbasesoft.framework.ai.agent.agent.BaseAgent;
import com.hbasesoft.framework.ai.agent.config.IManusProperties;
import com.hbasesoft.framework.ai.agent.dynamic.agent.DynamicAgent;
import com.hbasesoft.framework.ai.agent.dynamic.agent.service.AgentService;
import com.hbasesoft.framework.ai.agent.llm.ILlmService;
import com.hbasesoft.framework.ai.agent.planning.IPlanningFactory.ToolCallBackContext;
import com.hbasesoft.framework.ai.agent.planning.model.vo.ExecutionContext;
import com.hbasesoft.framework.ai.agent.planning.model.vo.ExecutionPlan;
import com.hbasesoft.framework.ai.agent.planning.model.vo.ExecutionStep;
import com.hbasesoft.framework.ai.agent.planning.model.vo.PlanInterface;
import com.hbasesoft.framework.ai.agent.planning.model.vo.mapreduce.ExecutionNode;
import com.hbasesoft.framework.ai.agent.planning.model.vo.mapreduce.MapReduceExecutionPlan;
import com.hbasesoft.framework.ai.agent.planning.model.vo.mapreduce.MapReduceNode;
import com.hbasesoft.framework.ai.agent.planning.model.vo.mapreduce.SequentialNode;
import com.hbasesoft.framework.ai.agent.recorder.PlanExecutionRecorder;
import com.hbasesoft.framework.ai.agent.tool.ToolCallBiFunctionDef;
import com.hbasesoft.framework.ai.agent.tool.mapreduce.MapOutputTool;
import com.hbasesoft.framework.ai.agent.tool.mapreduce.ReduceOperationTool;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月20日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.agent.planning.executor <br>
 */

public class MapReducePlanExecutor extends AbstractPlanExecutor {

    private final ObjectMapper OBJECT_MAPPER;

    // ==================== Configuration Constants ====================

    /**
     * Default maximum character limit for Reduce phase batch processing, used to control total character count of Map
     * task results per batch, avoiding overly long context
     */
    private static final int DEFAULT_REDUCE_BATCH_MAX_CHARACTERS = 2500;

    /**
     * Maximum retry count for Map task execution, retry mechanism when task execution fails or is incomplete
     */
    private static final int MAX_TASK_RETRY_COUNT = 3;

    /**
     * Base time interval for retry waiting (milliseconds), actual wait time = BASE_RETRY_WAIT_MILLIS * current retry
     * count
     */
    private static final long BASE_RETRY_WAIT_MILLIS = 1000;

    /**
     * Default character count when task character count calculation fails, fallback value when unable to read task
     * output file, avoiding calculation errors
     */
    private static final int DEFAULT_TASK_CHARACTER_COUNT = 100;

    /**
     * Default thread pool thread count for Map task execution, used when configuration is not set
     */
    private static final int DEFAULT_MAP_TASK_THREAD_POOL_SIZE = 1;

    /**
     * Configuration check interval in milliseconds (10 seconds)
     */
    private static final long CONFIG_CHECK_INTERVAL_MILLIS = 10_000;

    // Thread pool for parallel execution
    private volatile ExecutorService executorService;

    // Thread pool configuration tracking
    private volatile int currentThreadPoolSize;

    private volatile long lastConfigCheckTime;

    public MapReducePlanExecutor(List<DynamicAgent> agents, PlanExecutionRecorder recorder, AgentService agentService,
        ILlmService llmService, IManusProperties manusProperties, ObjectMapper objectMapper) {
        super(agents, recorder, agentService, llmService, manusProperties);
        OBJECT_MAPPER = objectMapper;

        // Initialize thread pool with current configuration
        this.currentThreadPoolSize = getMapTaskThreadPoolSize();
        this.executorService = Executors.newFixedThreadPool(currentThreadPoolSize);
        this.lastConfigCheckTime = System.currentTimeMillis();

        LoggerUtil.info("MapReducePlanExecutor initialized with thread pool size: {0}", currentThreadPoolSize);
    }

    /**
     * Execute all steps of the entire MapReduce plan
     * 
     * @param context Execution context containing user request and execution process information
     */
    @Override
    public void executeAllSteps(ExecutionContext context) {
        BaseAgent lastExecutor = null;
        PlanInterface plan = context.getPlan();

        if (!(plan instanceof MapReduceExecutionPlan)) {
            LoggerUtil.error("MapReducePlanExecutor can only execute MapReduceExecutionPlan, but got: {0}",
                plan.getClass().getSimpleName());
            throw new IllegalArgumentException("MapReducePlanExecutor can only execute MapReduceExecutionPlan");
        }

        MapReduceExecutionPlan mapReducePlan = (MapReduceExecutionPlan) plan;
        plan.updateStepIndices();

        try {
            recorder.recordPlanExecutionStart(context);
            List<ExecutionNode> steps = mapReducePlan.getSteps();

            if (CollectionUtils.isNotEmpty(steps)) {
                for (Object stepNode : steps) {
                    if (stepNode instanceof SequentialNode) {
                        lastExecutor = executeSequentialNode((SequentialNode) stepNode, context, lastExecutor);
                    }
                    else if (stepNode instanceof MapReduceNode) {
                        lastExecutor = executeMapReduceNode((MapReduceNode) stepNode, context, lastExecutor);
                    }
                }
            }

            context.setSuccess(true);
        }
        finally {
            performCleanup(context, lastExecutor);
        }
    }

    /**
     * Execute sequential node
     */
    private BaseAgent executeSequentialNode(SequentialNode seqNode, ExecutionContext context, BaseAgent lastExecutor) {
        LoggerUtil.info("Executing sequential node with {0} steps", seqNode.getStepCount());

        BaseAgent executor = lastExecutor;
        List<ExecutionStep> steps = seqNode.getSteps();

        if (CollectionUtils.isNotEmpty(steps)) {
            for (ExecutionStep step : steps) {
                BaseAgent stepExecutor = executeStep(step, context);
                if (stepExecutor != null) {
                    executor = stepExecutor;
                }
            }
        }

        return executor;
    }

    /**
     * Execute MapReduce node
     */
    private BaseAgent executeMapReduceNode(MapReduceNode mrNode, ExecutionContext context, BaseAgent lastExecutor) {
        LoggerUtil.info(
            "Executing MapReduce node, Data Prepared steps: {0}, Map steps: {1}, Reduce steps: {2}, Post Process steps: {3}",
            mrNode.getDataPreparedStepCount(), mrNode.getMapStepCount(), mrNode.getReduceStepCount(),
            mrNode.getPostProcessStepCount());

        BaseAgent executor = lastExecutor;

        // 1. Serial execution of Data Prepared phase
        if (CollectionUtils.isNotEmpty(mrNode.getDataPreparedSteps())) {
            executor = executeDataPreparedPhase(mrNode.getDataPreparedSteps(), context, executor);
        }

        List<ExecutionStep> mapSteps = mrNode.getMapSteps();
        // 2. Parallel execution of Map phase
        if (CollectionUtils.isNotEmpty(mapSteps)) {
            // Get MapReduceTool's ToolCallBackContext
            ToolCallBackContext toolCallBackContext = null;
            if (executor != null) {
                LoggerUtil.debug("Attempting to get ToolCallBackContext for map_output_tool, current executor: {0}",
                    executor.getClass().getSimpleName());
                toolCallBackContext = executor.getToolCallBackContext("map_output_tool");
                if (toolCallBackContext == null) {
                    LoggerUtil.warn(
                        "Unable to get ToolCallBackContext for map_output_tool, tool may not be properly registered or name mismatch");
                }
            }
            else {
                LoggerUtil.error("Executor is null, unable to get ToolCallBackContext for MapOutputTool");
            }
            executor = executeMapPhase(mapSteps, context, toolCallBackContext);
        }

        // 3. Execute Reduce phase in parallel (sharing thread pool with Map phase)
        if (CollectionUtils.isNotEmpty(mrNode.getReduceSteps())) {
            executor = executeReducePhaseParallel(mrNode.getReduceSteps(), context, executor);
        }

        // 4. Execute Post Process phase serially (post-processing phase)
        if (CollectionUtils.isNotEmpty(mrNode.getPostProcessSteps())) {
            executor = executePostProcessPhase(mrNode.getPostProcessSteps(), context, executor);
        }

        return executor;
    }

    /**
     * Execute Data Prepared phase serially
     */
    private BaseAgent executeDataPreparedPhase(List<ExecutionStep> dataPreparedSteps, ExecutionContext context,
        BaseAgent lastExecutor) {
        LoggerUtil.info("Executing Data Prepared phase serially, total {0} steps", dataPreparedSteps.size());

        BaseAgent executor = lastExecutor;

        for (ExecutionStep step : dataPreparedSteps) {
            BaseAgent stepExecutor = executeStep(step, context);
            if (stepExecutor != null) {
                executor = stepExecutor;
            }
        }

        LoggerUtil.info("Data Prepared phase execution completed");
        return executor;
    }

    /**
     * Execute Post Process phase serially (post-processing phase) Similar to Data Prepared phase, supports single agent
     * execution, specifically for final processing tasks after MapReduce workflow completion
     */
    private BaseAgent executePostProcessPhase(List<ExecutionStep> postProcessSteps, ExecutionContext context,
        BaseAgent lastExecutor) {
        LoggerUtil.info("Executing Post Process phase serially, total {0} steps", postProcessSteps.size());

        BaseAgent executor = lastExecutor;

        for (ExecutionStep step : postProcessSteps) {
            BaseAgent stepExecutor = executeStep(step, context);
            if (stepExecutor != null) {
                executor = stepExecutor;
            }
        }

        // Record Post Process phase completion status - record completion status for
        // each
        // Post Process step
        for (ExecutionStep step : postProcessSteps) {
            step.setAgent(executor);
            recorder.recordStepEnd(step, context);
        }
        LoggerUtil.info("Post Process phase execution completed");
        return executor;
    }

    /**
     * Execute Map phase in parallel
     */
    private BaseAgent executeMapPhase(List<ExecutionStep> mapSteps, ExecutionContext context,
        ToolCallBackContext toolCallBackContext) {
        LoggerUtil.info("Executing Map phase in parallel, total {0} steps", mapSteps.size());

        // Record Map phase start status - record start status for each Map step
        for (ExecutionStep step : mapSteps) {
            recorder.recordStepStart(step, context);
        }

        // Add null pointer check
        if (toolCallBackContext == null) {
            LoggerUtil.error(
                "ToolCallBackContext is null, cannot execute Map phase. Please ensure MapReduceTool context is properly obtained before executing Map phase.");
            throw new RuntimeException("ToolCallBackContext is null, cannot execute Map phase");
        }

        ToolCallBiFunctionDef<?> callFunc = toolCallBackContext.getFunctionInstance();
        if (callFunc == null) {
            LoggerUtil.error("ToolCallBiFunctionDef is null, cannot execute Map phase");
            return null;
        }
        if (!(callFunc instanceof MapOutputTool)) {
            LoggerUtil.error("ToolCallBiFunctionDef is not MapOutputTool, cannot execute Map phase. Actual type: {0}",
                callFunc.getClass().getSimpleName());
            return null;
        }
        MapOutputTool splitTool = (MapOutputTool) callFunc;

        List<CompletableFuture<BaseAgent>> futures = new ArrayList<>();
        BaseAgent lastExecutor = null;

        try {
            // 2. Get task directory list (new MapReduceTool returns task directory paths)
            List<String> taskDirectories = splitTool.getSplitResults();

            if (taskDirectories.isEmpty()) {
                LoggerUtil.error("No task directories found, Map phase execution failed");
                throw new RuntimeException("No task directories found, Map phase cannot execute");
            }
            else {
                LoggerUtil.info("Found {0} task directories, will execute Map steps for each task",
                    taskDirectories.size());

                // 3. Create and execute mapSteps copies for each task directory
                for (String taskDirectory : taskDirectories) {
                    // 4. Copy a new mapSteps list
                    List<ExecutionStep> copiedMapSteps = copyMapSteps(mapSteps, taskDirectory);

                    // 5. Use CompletableFuture to execute new mapSteps list for each task
                    // directory
                    CompletableFuture<BaseAgent> future = CompletableFuture.supplyAsync(() -> {
                        BaseAgent fileExecutor = null;
                        LoggerUtil.info("Starting to process task directory: {0}", taskDirectory);

                        // Execute steps with task context parameter injection
                        fileExecutor = executeStepsWithTaskContext(copiedMapSteps, context, taskDirectory);

                        LoggerUtil.info("Completed processing task directory: {0}", taskDirectory);
                        return fileExecutor;
                    }, getUpdatedExecutorService());

                    futures.add(future);
                }
            }

            // Wait for all Map steps to complete
            for (CompletableFuture<BaseAgent> future : futures) {
                try {
                    BaseAgent executor = future.get();
                    if (executor != null) {
                        lastExecutor = executor;
                    }
                }
                catch (Exception e) {
                    LoggerUtil.error("Map phase step execution failed", e);
                }
            }

        }
        catch (Exception e) {
            LoggerUtil.error("Error occurred while executing Map phase", e);
            throw new RuntimeException("Map phase execution failed", e);
        }

        // Record Map phase completion status - record completion status for each Map
        // step
        for (ExecutionStep step : mapSteps) {
            step.setAgent(lastExecutor);
            step.setResult("Successfully executed all Map tasks");
            recorder.recordStepEnd(step, context);
        }

        LoggerUtil.info("Map phase execution completed");
        return lastExecutor;
    }

    /**
     * Copy mapSteps list
     */
    private List<ExecutionStep> copyMapSteps(List<ExecutionStep> originalSteps, String taskDirectory) {
        List<ExecutionStep> copiedSteps = new ArrayList<>();

        for (ExecutionStep originalStep : originalSteps) {
            ExecutionStep copiedStep = new ExecutionStep();
            copiedStep.setStepIndex(originalStep.getStepIndex());
            copiedStep.setStepRequirement(originalStep.getStepRequirement());
            copiedStep.setTerminateColumns(originalStep.getTerminateColumns());

            copiedSteps.add(copiedStep);
        }

        return copiedSteps;
    }

    /**
     * Execute Reduce phase in parallel, sharing thread pool with Map phase Supports batch processing of Map task
     * outputs, controlling task count per batch based on character count
     */
    private BaseAgent executeReducePhaseParallel(List<ExecutionStep> reduceSteps, ExecutionContext context,
        BaseAgent lastExecutor) {
        LoggerUtil.info("Executing Reduce phase in parallel, total {0} steps", reduceSteps.size());

        // Record Reduce phase start status - record start status for each Reduce step
        for (ExecutionStep step : reduceSteps) {
            recorder.recordStepStart(step, context);
        }

        BaseAgent executor = lastExecutor;

        // Get ReduceOperationTool instance to obtain Map task results
        ToolCallBackContext reduceToolContext = null;
        if (executor != null) {
            reduceToolContext = executor.getToolCallBackContext("reduce_operation_tool");
        }

        if (reduceToolContext == null) {
            LoggerUtil.error("Unable to get ReduceOperationTool context, Reduce phase cannot obtain Map task results");
            throw new RuntimeException("ReduceOperationTool context is null, cannot execute Reduce phase");
        }

        ToolCallBiFunctionDef<?> reduceToolFunc = reduceToolContext.getFunctionInstance();
        if (!(reduceToolFunc instanceof ReduceOperationTool)) {
            LoggerUtil.error("Retrieved tool is not ReduceOperationTool instance, cannot execute Reduce phase");
            throw new RuntimeException("Tool type error, cannot execute Reduce phase");
        }

        ReduceOperationTool reduceTool = (ReduceOperationTool) reduceToolFunc;

        List<String> taskDirectories = reduceTool.getSplitResults();
        if (taskDirectories.isEmpty()) {
            LoggerUtil.warn("No Map task results found, skipping Reduce phase");
            return executor;
        }

        // Configure character limit per batch processing (configurable, mainly limited
        // by
        // context length)
        int maxBatchCharacters = getMaxBatchCharacters(context);
        LoggerUtil.info(
            "Starting Reduce phase parallel processing, total {0} Map tasks, character limit per batch {1} characters",
            taskDirectories.size(), maxBatchCharacters);

        // Group Map task results into batches based on character count
        List<List<String>> batches = groupTasksByCharacterCount(taskDirectories, maxBatchCharacters);

        // Execute batches in parallel
        List<CompletableFuture<BaseAgent>> futures = new ArrayList<>();

        for (int batchIndex = 0; batchIndex < batches.size(); batchIndex++) {
            final int batchCounter = batchIndex + 1;
            final List<String> batchTaskDirectories = batches.get(batchIndex);

            LoggerUtil.info("Preparing to process batch {0} in parallel, containing {1} tasks", batchCounter,
                batchTaskDirectories.size());

            // Create parallel task for each batch
            CompletableFuture<BaseAgent> future = CompletableFuture.supplyAsync(() -> {
                BaseAgent batchExecutor = null;
                LoggerUtil.info("Starting to process Reduce batch {0}", batchCounter);

                // Execute Reduce steps for current batch
                for (ExecutionStep step : reduceSteps) {
                    BaseAgent stepExecutor = executeReduceStepWithBatch(step, context, batchTaskDirectories,
                        batchCounter);
                    if (stepExecutor != null) {
                        batchExecutor = stepExecutor;
                    }
                }

                LoggerUtil.info("Completed processing Reduce batch {0}", batchCounter);
                return batchExecutor;
            }, getUpdatedExecutorService());

            futures.add(future);
        }

        // Wait for all Reduce batches to complete
        for (CompletableFuture<BaseAgent> future : futures) {
            try {
                BaseAgent batchExecutor = future.get();
                if (batchExecutor != null) {
                    executor = batchExecutor;
                }
            }
            catch (Exception e) {
                LoggerUtil.error("Reduce phase batch execution failed", e);
            }
        }

        // Record Reduce phase completion status - record completion status for each
        // Reduce step
        for (ExecutionStep step : reduceSteps) {
            recorder.recordStepEnd(step, context);
        }

        LoggerUtil.info("Reduce phase parallel execution completed, processed {0} batches total", batches.size());
        return executor;
    }

    /**
     * Get maximum character count per batch processing Can be dynamically adjusted based on context length limits and
     * configuration
     */
    private int getMaxBatchCharacters(ExecutionContext context) {
        // Can get configured batch character limit from ExecutionParams
        // String executionParams = context.getPlan().getExecutionParams();
        // if (executionParams != null &&
        // executionParams.contains(REDUCE_BATCH_CHARACTERS_CONFIG_KEY)) {
        // try {
        // String[] lines = executionParams.split("\n");
        // for (String line : lines) {
        // if (line.trim().startsWith(REDUCE_BATCH_CHARACTERS_CONFIG_KEY)) {
        // String charactersStr = line.split(":")[1].trim();
        // int configuredCharacters = Integer.parseInt(charactersStr);
        // if (configuredCharacters > 0 && configuredCharacters <=
        // MAX_REDUCE_BATCH_CHARACTERS_LIMIT) {
        // logger.info("Using configured Reduce batch character limit: {}",
        // configuredCharacters);
        // return configuredCharacters;
        // }
        // }
        // }
        // } catch (Exception e) {
        // logger.warn("Failed to parse reduce_batch_characters configuration, using
        // default value: {}",
        // DEFAULT_REDUCE_BATCH_MAX_CHARACTERS, e);
        // }
        // }

        LoggerUtil.info("Using default Reduce batch character limit: {0}", DEFAULT_REDUCE_BATCH_MAX_CHARACTERS);
        return DEFAULT_REDUCE_BATCH_MAX_CHARACTERS;
    }

    /**
     * Group tasks into different batches based on character count Ensures total character count per batch doesn't
     * exceed specified limit while maintaining document integrity
     */
    private List<List<String>> groupTasksByCharacterCount(List<String> taskDirectories, int maxBatchCharacters) {
        List<List<String>> batches = new ArrayList<>();
        List<String> currentBatch = new ArrayList<>();
        int currentBatchCharacterCount = 0;

        for (String taskDirectory : taskDirectories) {
            // Calculate character count for current task
            int taskCharacterCount = getTaskCharacterCount(taskDirectory);

            // If single task character count exceeds limit, make it a separate batch
            if (taskCharacterCount > maxBatchCharacters) {
                // Save current batch first (if not empty)
                if (!currentBatch.isEmpty()) {
                    batches.add(new ArrayList<>(currentBatch));
                    currentBatch.clear();
                    currentBatchCharacterCount = 0;
                }

                // Single oversized task as independent batch
                List<String> singleTaskBatch = new ArrayList<>();
                singleTaskBatch.add(taskDirectory);
                batches.add(singleTaskBatch);
                LoggerUtil.warn("Task {0} character count {1} exceeds batch limit {2}, making it a separate batch",
                    taskDirectory, taskCharacterCount, maxBatchCharacters);
                continue;
            }

            // Check if adding current task would exceed limit
            if (currentBatchCharacterCount + taskCharacterCount > maxBatchCharacters && !currentBatch.isEmpty()) {
                // Save current batch and start new batch
                batches.add(new ArrayList<>(currentBatch));
                currentBatch.clear();
                currentBatchCharacterCount = 0;
            }

            // Add task to current batch
            currentBatch.add(taskDirectory);
            currentBatchCharacterCount += taskCharacterCount;

            LoggerUtil.debug("Task {0} ({1} characters) added to batch, current batch character count: {2}",
                taskDirectory, taskCharacterCount, currentBatchCharacterCount);
        }

        // Add last batch (if not empty)
        if (!currentBatch.isEmpty()) {
            batches.add(currentBatch);
        }

        // Log batch grouping results
        for (int i = 0; i < batches.size(); i++) {
            List<String> batch = batches.get(i);
            int batchTotalCharacters = batch.stream().mapToInt(this::getTaskCharacterCount).sum();
            LoggerUtil.info("Batch {0} contains {1} tasks, total character count: {2}", i + 1, batch.size(),
                batchTotalCharacters);
        }

        return batches;
    }

    /**
     * Get character count for single task Read output.md file in task directory and calculate character count
     */
    private int getTaskCharacterCount(String taskDirectory) {
        try {
            Path taskPath = Paths.get(taskDirectory);
            Path outputFile = taskPath.resolve("output.md");

            if (Files.exists(outputFile)) {
                String content = Files.readString(outputFile);
                return content.length();
            }
            else {
                LoggerUtil.warn(
                    "output.md file does not exist in task directory {0}, returning default character count",
                    taskDirectory);
                return DEFAULT_TASK_CHARACTER_COUNT;
            }
        }
        catch (Exception e) {
            LoggerUtil.error("Failed to read character count for task {0}", taskDirectory, e);
            return DEFAULT_TASK_CHARACTER_COUNT;
        }
    }

    /**
     * Execute Reduce step with batch Map results Use InnerStorageTool to aggregate and store batch context to unified
     * file, avoiding overly long context
     */
    private BaseAgent executeReduceStepWithBatch(ExecutionStep step, ExecutionContext context,
        List<String> batchTaskDirectories, int batchCounter) {

        // Save original ExecutionParams and temporarily modify

        // Save original ExecutionParams and temporarily modify
        String originalExecutionParams = context.getPlan().getExecutionParams();
        StringBuilder enhancedParams = new StringBuilder();
        if (originalExecutionParams != null && !originalExecutionParams.trim().isEmpty()) {
            enhancedParams.append(originalExecutionParams).append("\n\n");
        }

        // Add simplified batch context information
        enhancedParams.append("=== Reduce Batch ").append(String.format("%03d", batchCounter)).append(" Context : \n");

        // Only include output.md content, not status data
        for (String taskDirectory : batchTaskDirectories) {
            try {
                Path taskPath = Paths.get(taskDirectory);
                String taskId = taskPath.getFileName().toString();

                // Read task's output.md file (Map phase output)
                Path outputFile = taskPath.resolve("output.md");
                if (Files.exists(outputFile)) {
                    String outputContent = Files.readString(outputFile);
                    enhancedParams.append("=== Task ID: ").append(taskId).append(" ===\n");
                    enhancedParams.append(outputContent).append("\n");
                    enhancedParams.append("=== Task ID: ").append(taskId).append(" End ===\n\n");
                }
            }
            catch (Exception e) {
                LoggerUtil.error("Failed to read Map task output: {0}", taskDirectory, e);
            }
        }

        // Create modified step
        // ExecutionStep enhancedStep = new ExecutionStep();
        // enhancedStep.setStepIndex(step.getStepIndex());
        // enhancedStep.setStepRequirement(step.getStepRequirement());

        try {
            // Temporarily set enhanced ExecutionParams
            context.getPlan().setExecutionParams(enhancedParams.toString());

            // Execute step
            BaseAgent stepExecutor = executeStep(step, context);

            LoggerUtil.info("Completed processing Reduce batch {0}, containing {1} Map tasks", batchCounter,
                batchTaskDirectories.size());
            return stepExecutor;

        }
        finally {
            // Restore original ExecutionParams
            context.getPlan().setExecutionParams(originalExecutionParams);
        }
    }

    /**
     * Shutdown executor, release thread pool resources
     */
    public void shutdown() {
        ExecutorService currentExecutor = executorService;
        if (currentExecutor != null && !currentExecutor.isShutdown()) {
            shutdownExecutorGracefully(currentExecutor);
        }
    }

    /**
     * Override parent class executeStep method, temporarily add task information to ExecutionParams during map task
     * execution
     */
    @Override
    protected BaseAgent executeStep(ExecutionStep step, ExecutionContext context) {
        // Directly call parent method, as task context parameter injection is already
        // handled in executeStepsWithTaskContext
        return super.executeStep(step, context);
    }

    /**
     * Execute step list with task context parameter injection, supporting task completion status check and retry Use
     * copied ExecutionContext to avoid modifying original context
     * 
     * @param steps Step list to execute
     * @param context Execution context
     * @param taskDirectory Task directory path
     * @return Last executed Agent
     */
    private BaseAgent executeStepsWithTaskContext(List<ExecutionStep> steps, ExecutionContext context,
        String taskDirectory) {
        BaseAgent fileExecutor = null;

        // 2. Find input.md fixed file from corresponding directory based on
        // taskDirectory
        String taskId = "";
        String fileContent = "";

        try {
            // Extract task ID (get last directory name from taskDirectory path)
            Path taskPath = Paths.get(taskDirectory);
            taskId = taskPath.getFileName().toString();

            // Read input.md file content
            Path inputFile = taskPath.resolve("input.md");
            if (Files.exists(inputFile)) {
                fileContent = Files.readString(inputFile);
                LoggerUtil.debug("Successfully read task file content, task ID: {0}, content length: {1} characters",
                    taskId, fileContent.length());
            }
            else {
                LoggerUtil.warn("input.md file does not exist in task directory: {0}", inputFile);
                fileContent = "Task file does not exist";
            }
        }
        catch (Exception e) {
            LoggerUtil.error("Failed to read task file: {0}", taskDirectory, e);
            fileContent = "Error occurred while reading task file: " + e.getMessage();
        }

        // 3. Create ExecutionContext copy with enhanced parameters
        ExecutionContext copiedContext = createContextCopyWithEnhancedParams(context, taskId, fileContent);

        // Execute task with retry mechanism support
        int maxRetries = MAX_TASK_RETRY_COUNT;
        int currentRetry = 0;
        boolean taskCompleted = false;

        while (currentRetry <= maxRetries && !taskCompleted) {
            if (currentRetry > 0) {
                LoggerUtil.warn("Task {0} retry execution attempt {1}", taskId, currentRetry);
            }

            try {
                // 4. Execute steps using copied context
                for (ExecutionStep step : steps) {
                    BaseAgent stepExecutor = executeStep(step, copiedContext);
                    if (stepExecutor != null) {
                        fileExecutor = stepExecutor;
                    }
                }

                // 5. Check if task is completed
                taskCompleted = checkTaskCompletion(taskDirectory, taskId);

                if (taskCompleted) {
                    LoggerUtil.info("Task {0} executed successfully", taskId);
                    break;
                }
                else {
                    LoggerUtil.warn(
                        "Task {0} execution incomplete, detected task status is not completed or missing output file",
                        taskId);
                    currentRetry++;

                    if (currentRetry <= maxRetries) {
                        // Wait for some time before retry
                        try {
                            Thread.sleep(BASE_RETRY_WAIT_MILLIS * currentRetry); // Incremental
                                                                                 // wait
                                                                                 // time
                        }
                        catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            LoggerUtil.error("Retry wait interrupted", ie);
                            break;
                        }
                    }
                }

            }
            catch (Exception e) {
                LoggerUtil.error(e, "Exception occurred while executing task {0}", taskId);
                currentRetry++;

                if (currentRetry <= maxRetries) {
                    try {
                        Thread.sleep(BASE_RETRY_WAIT_MILLIS * currentRetry);
                    }
                    catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        LoggerUtil.error("Retry wait interrupted", ie);
                        break;
                    }
                }
            }
        }

        // 6. Final task status check
        if (!taskCompleted) {
            LoggerUtil.error("Task {0} still incomplete after {1} retries", taskId, maxRetries);
            throw new RuntimeException("Task " + taskId + " execution failed, maximum retry count reached");
        }

        return fileExecutor;
    }

    /**
     * Create ExecutionContext copy and enhance ExecutionParams
     * 
     * @param originalContext Original execution context
     * @param taskId Task ID
     * @param fileContent File content
     * @return Enhanced ExecutionContext copy
     */
    private ExecutionContext createContextCopyWithEnhancedParams(ExecutionContext originalContext, String taskId,
        String fileContent) {
        // Create ExecutionContext copy
        ExecutionContext copiedContext = new ExecutionContext();

        // Copy basic properties
        copiedContext.setCurrentPlanId(originalContext.getCurrentPlanId());
        copiedContext.setRootPlanId(originalContext.getRootPlanId());
        copiedContext.setUserRequest(originalContext.getUserRequest());
        copiedContext.setResultSummary(originalContext.getResultSummary());
        copiedContext.setNeedSummary(originalContext.isNeedSummary());
        copiedContext.setSuccess(originalContext.isSuccess());
        copiedContext.setUseMemory(originalContext.isUseMemory());
        copiedContext.setThinkActRecordId(originalContext.getThinkActRecordId());

        // Copy tool context
        if (originalContext.getToolsContext() != null) {
            Map<String, String> copiedToolsContext = new HashMap<>(originalContext.getToolsContext());
            copiedContext.setToolsContext(copiedToolsContext);
        }

        // Create Plan copy and enhance ExecutionParams
        PlanInterface copiedPlan = createPlanCopyWithEnhancedParams(originalContext.getPlan(), taskId, fileContent);
        copiedContext.setPlan(copiedPlan);

        return copiedContext;
    }

    /**
     * Create Plan copy and enhance ExecutionParams
     * 
     * @param originalPlan Original plan
     * @param taskId Task ID
     * @param fileContent File content
     * @return Enhanced Plan copy
     */
    private PlanInterface createPlanCopyWithEnhancedParams(PlanInterface originalPlan, String taskId,
        String fileContent) {
        // Create copy based on actual Plan type
        PlanInterface copiedPlan;

        if (originalPlan instanceof MapReduceExecutionPlan) {
            MapReduceExecutionPlan originalMapReducePlan = (MapReduceExecutionPlan) originalPlan;
            MapReduceExecutionPlan copiedMapReducePlan = new MapReduceExecutionPlan();

            // Copy all properties of MapReduceExecutionPlan
            copiedMapReducePlan.setCurrentPlanId(originalMapReducePlan.getCurrentPlanId());
            copiedMapReducePlan.setRootPlanId(originalMapReducePlan.getRootPlanId());
            copiedMapReducePlan.setTitle(originalMapReducePlan.getTitle());
            copiedMapReducePlan.setPlanningThinking(originalMapReducePlan.getPlanningThinking());
            copiedMapReducePlan.setUserRequest(originalMapReducePlan.getUserRequest());
            // Copy step structure (Note: copying references here, as steps themselves
            // won't be modified during execution)
            copiedMapReducePlan.setSteps(originalMapReducePlan.getSteps());

            copiedPlan = copiedMapReducePlan;
        }
        else {
            // Handle other Plan types, such as ExecutionPlan
            ExecutionPlan originalExecutionPlan = (ExecutionPlan) originalPlan;
            ExecutionPlan copiedExecutionPlan = new ExecutionPlan();

            // Copy all properties of ExecutionPlan
            copiedExecutionPlan.setCurrentPlanId(originalExecutionPlan.getCurrentPlanId());
            copiedExecutionPlan.setTitle(originalExecutionPlan.getTitle());
            copiedExecutionPlan.setPlanningThinking(originalExecutionPlan.getPlanningThinking());
            copiedExecutionPlan.setUserRequest(originalExecutionPlan.getUserRequest());

            // Copy step list (Note: copying references here, as steps themselves won't be
            // modified during execution)
            copiedExecutionPlan.setSteps(originalExecutionPlan.getSteps());

            copiedPlan = copiedExecutionPlan;
        }

        // Create enhanced ExecutionParams
        String originalExecutionParams = originalPlan.getExecutionParams();
        StringBuilder enhancedParams = new StringBuilder();
        if (originalExecutionParams != null && !originalExecutionParams.trim().isEmpty()) {
            enhancedParams.append(originalExecutionParams).append("\n\n");
        }
        enhancedParams.append("=== Current Task Context ===\n");
        enhancedParams.append("Task ID: ").append(taskId).append("\n");
        enhancedParams.append("File Content: ").append(fileContent).append("\n");
        enhancedParams.append("=== Task Context End ===");

        // Set enhanced ExecutionParams
        copiedPlan.setExecutionParams(enhancedParams.toString());

        return copiedPlan;
    }

    /**
     * Get Map task thread pool size from configuration
     * 
     * @return configured thread pool size or default value if not configured
     */
    private int getMapTaskThreadPoolSize() {
        if (manusProperties != null) {
            Integer configuredThreads = manusProperties.getInfiniteContextParallelThreads();
            if (configuredThreads != null && configuredThreads > 0) {
                LoggerUtil.debug("Using configured Map task thread pool size: {0}", configuredThreads);
                return configuredThreads;
            }
        }

        LoggerUtil.debug("Using default Map task thread pool size: {0}", DEFAULT_MAP_TASK_THREAD_POOL_SIZE);
        return DEFAULT_MAP_TASK_THREAD_POOL_SIZE;
    }

    /**
     * Check and update thread pool configuration if needed. This method is called before each executor service usage to
     * ensure configuration changes are picked up without using timers or background threads.
     * 
     * @return the current (potentially updated) executor service
     */
    private ExecutorService getUpdatedExecutorService() {
        long currentTime = System.currentTimeMillis();

        // Check if enough time has passed since last configuration check
        if (currentTime - lastConfigCheckTime >= CONFIG_CHECK_INTERVAL_MILLIS) {
            lastConfigCheckTime = currentTime;

            // Get current configuration
            int newThreadPoolSize = getMapTaskThreadPoolSize();

            // Check if configuration has changed
            if (newThreadPoolSize != currentThreadPoolSize) {
                LoggerUtil.info("Thread pool size configuration changed from {0} to {1}, rebuilding thread pool",
                    currentThreadPoolSize, newThreadPoolSize);

                // Gracefully shutdown old executor service
                ExecutorService oldExecutorService = executorService;

                // Create new executor service with updated configuration
                ExecutorService newExecutorService = Executors.newFixedThreadPool(newThreadPoolSize);

                // Update current state atomically
                this.executorService = newExecutorService;
                this.currentThreadPoolSize = newThreadPoolSize;

                // Gracefully shutdown old executor service in background
                // This ensures existing tasks can complete
                shutdownExecutorGracefully(oldExecutorService);

                LoggerUtil.info("Thread pool successfully updated to size: {0}", newThreadPoolSize);
            }
        }

        return executorService;
    }

    /**
     * Gracefully shutdown an executor service
     * 
     * @param executor the executor service to shutdown
     */
    private void shutdownExecutorGracefully(ExecutorService executor) {
        if (executor != null && !executor.isShutdown()) {
            try {
                // Initiate graceful shutdown
                executor.shutdown();
                LoggerUtil.debug("Old thread pool shutdown initiated");
            }
            catch (Exception e) {
                LoggerUtil.warn("Error during graceful shutdown of old thread pool", e);
                // Force shutdown if graceful shutdown fails
                executor.shutdownNow();
            }
        }
    }

    /**
     * Check if task is completed
     * 
     * @param taskDirectory Task directory path
     * @param taskId Task ID
     * @return Whether task is completed
     */
    private boolean checkTaskCompletion(String taskDirectory, String taskId) {
        try {
            Path taskPath = Paths.get(taskDirectory);

            // Check status.json file
            Path statusFile = taskPath.resolve("status.json");
            if (Files.exists(statusFile)) {
                String statusContent = Files.readString(statusFile);
                LoggerUtil.debug("Task {0} status file content: {1}", taskId, statusContent);

                // Use ObjectMapper to parse statusContent as Map then check status field
                try {
                    Map<?, ?> statusMap = OBJECT_MAPPER.readValue(statusContent, Map.class);
                    Object statusValue = statusMap.get("status");
                    if ("completed".equals(statusValue)) {
                        // Also check if output.md file exists
                        Path outputFile = taskPath.resolve("output.md");
                        if (Files.exists(outputFile)) {
                            LoggerUtil.debug("Task {0} completed, status file and output file exist", taskId);
                            return true;
                        }
                        else {
                            LoggerUtil.warn("Task {0} status is completed but missing output.md file", taskId);
                            return false;
                        }
                    }
                    else {
                        LoggerUtil.debug("Task {0} status is not completed", taskId);
                        return false;
                    }
                }
                catch (Exception jsonEx) {
                    LoggerUtil.error("Failed to parse status.json as Map", jsonEx);
                    return false;
                }
            }
            else {
                LoggerUtil.warn("Task {0} missing status.json file", taskId);
                return false;
            }

        }
        catch (Exception e) {
            LoggerUtil.error("Error occurred while checking task {0} completion status", taskId, e);
            return false;
        }
    }

}
