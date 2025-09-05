/****************************************************************************************
 * Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or    *
 * transmission in whole or in part, in any form or by any means, electronic, mechanical*
 * or otherwise, is prohibited without the prior written consent of the copyright owner.*
 ****************************************************************************************/
package com.hbasesoft.framework.ai.agent.tool.mapreduce;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import com.hbasesoft.framework.common.utils.logger.LoggerUtil;

/**
 * MapReduceSharedStateManager manages shared state information for MapReduce workflows. It maintains state across
 * multiple tasks and plans, including task statuses, split results, and operation results.
 * <p>
 * This class is responsible for:
 * <ul>
 * <li>Managing plan state information with thread-safe operations</li>
 * <li>Tracking task statuses and results</li>
 * <li>Maintaining split results for map tasks</li>
 * <li>Providing status overview of all active plans</li>
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
public class MapReduceSharedStateManager implements IMapReduceSharedStateManager {

    /**
     * Plan state information Key: planId, Value: PlanState
     */
    private final Map<String, PlanState> planStates = new ConcurrentHashMap<>();

    /**
     * Get or create plan state
     * 
     * @param planId Plan ID
     * @return Plan state
     */
    public PlanState getOrCreatePlanState(final String planId) {
        if (planId == null || planId.trim().isEmpty()) {
            throw new IllegalArgumentException("planId cannot be empty");
        }

        return planStates.computeIfAbsent(planId, id -> {
            LoggerUtil.info("Creating new shared state for plan {0}", id);
            return new PlanState();
        });
    }

    /**
     * Get plan state (return null if not exists)
     * 
     * @param planId Plan ID
     * @return Plan state, return null if not exists
     */
    public PlanState getPlanState(final String planId) {
        return planStates.get(planId);
    }

    /**
     * Clean up plan state
     * 
     * @param planId Plan ID
     */
    public void cleanupPlanState(final String planId) {
        PlanState removed = planStates.remove(planId);
        if (removed != null) {
            LoggerUtil.info("Cleaned up shared state for plan {0}", planId);
        }
    }

    /**
     * Get next task ID
     * 
     * @param planId Plan ID
     * @return Task ID
     */
    public String getNextTaskId(final String planId) {
        PlanState planState = getOrCreatePlanState(planId);
        int taskNumber = planState.getTaskCounter().getAndIncrement();
        return String.format("task_%03d", taskNumber);
    }

    /**
     * Add split result
     * 
     * @param planId Plan ID
     * @param taskDirectory Task directory
     */
    public void addSplitResult(final String planId, final String taskDirectory) {
        PlanState planState = getOrCreatePlanState(planId);
        planState.getSplitResults().add(taskDirectory);
        LoggerUtil.debug("Added split result for plan {0}: {1}", planId, taskDirectory);
    }

    /**
     * Get split result list
     * 
     * @param planId Plan ID
     * @return Copy of split result list
     */
    public List<String> getSplitResults(final String planId) {
        PlanState planState = getPlanState(planId);
        if (planState == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(planState.getSplitResults());
    }

    /**
     * Set split result list
     * 
     * @param planId Plan ID
     * @param splitResults Split result list
     */
    public void setSplitResults(final String planId, final List<String> splitResults) {
        PlanState planState = getOrCreatePlanState(planId);
        planState.getSplitResults().clear();
        planState.getSplitResults().addAll(splitResults);
        LoggerUtil.info("Set split results for plan {0}, total {1} tasks", planId, splitResults.size());
    }

    /**
     * Record Map task status
     * 
     * @param planId Plan ID
     * @param taskId Task ID
     * @param taskStatus Task status
     */
    public void recordMapTaskStatus(final String planId, final String taskId, final TaskStatus taskStatus) {
        PlanState planState = getOrCreatePlanState(planId);
        planState.getMapTaskStatuses().put(taskId, taskStatus);
        LoggerUtil.debug("Recorded task {0} status for plan {1}: {2}", taskId, planId, taskStatus.getStatus());
    }

    /**
     * Get Map task status
     * 
     * @param planId Plan ID
     * @param taskId Task ID
     * @return Task status
     */
    public TaskStatus getMapTaskStatus(final String planId, final String taskId) {
        PlanState planState = getPlanState(planId);
        if (planState == null) {
            return null;
        }
        return planState.getMapTaskStatuses().get(taskId);
    }

    /**
     * Get all Map task statuses
     * 
     * @param planId Plan ID
     * @return Copy of task status mapping
     */
    public Map<String, TaskStatus> getAllMapTaskStatuses(final String planId) {
        PlanState planState = getPlanState(planId);
        if (planState == null) {
            return new HashMap<>();
        }
        return new HashMap<>(planState.getMapTaskStatuses());
    }

    /**
     * Set last operation result
     * 
     * @param planId Plan ID
     * @param result Operation result
     */
    public void setLastOperationResult(final String planId, final String result) {
        PlanState planState = getOrCreatePlanState(planId);
        planState.setLastOperationResult(result);
    }

    /**
     * Get last operation result
     * 
     * @param planId Plan ID
     * @return Last operation result
     */
    public String getLastOperationResult(final String planId) {
        PlanState planState = getPlanState(planId);
        if (planState == null) {
            return "";
        }
        return planState.getLastOperationResult();
    }

    /**
     * Set last processed file
     * 
     * @param planId Plan ID
     * @param filePath File path
     */
    public void setLastProcessedFile(final String planId, final String filePath) {
        PlanState planState = getOrCreatePlanState(planId);
        planState.setLastProcessedFile(filePath);
    }

    /**
     * Get last processed file
     * 
     * @param planId Plan ID
     * @return Last processed file path
     */
    public String getLastProcessedFile(final String planId) {
        PlanState planState = getPlanState(planId);
        if (planState == null) {
            return "";
        }
        return planState.getLastProcessedFile();
    }

    /**
     * Get current tool status string
     * 
     * @param planId Plan ID
     * @return Status string
     */
    public String getCurrentToolStateString(final String planId) {
        PlanState planState = getPlanState(planId);
        if (planState == null) {
            return "reduce_operation_tool current status:\n- Plan ID: " + planId + " (status does not exist)\n";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("reduce_operation_tool current status:\n");
        sb.append("- Plan ID: ").append(planId).append("\n");
        sb.append("- Last processed file: ")
            .append(planState.getLastProcessedFile().isEmpty() ? "None" : planState.getLastProcessedFile())
            .append("\n");
        sb.append("- Last operation result: ").append(
            planState.getLastOperationResult().isEmpty() ? "None" : "Completed: " + planState.getLastOperationResult())
            .append("\n");
        return sb.toString();
    }

    /**
     * Get status overview of all plans
     * 
     * @return Status overview string
     */
    public String getAllPlansOverview() {
        StringBuilder sb = new StringBuilder();
        sb.append("MapReduce Shared State Manager Overview:\n");
        sb.append("- Active plan count: ").append(planStates.size()).append("\n");

        for (Map.Entry<String, PlanState> entry : planStates.entrySet()) {
            String planId = entry.getKey();
            PlanState planState = entry.getValue();
            sb.append("  - Plan ").append(planId).append(": ");
            sb.append("Task count=").append(planState.getSplitResults().size());
            sb.append(", Status count=").append(planState.getMapTaskStatuses().size());
            sb.append(", Counter=").append(planState.getTaskCounter().get());
            sb.append("\n");
        }

        return sb.toString();
    }

    /**
     * Clean up all plan states
     */
    public void cleanupAllPlanStates() {
        int count = planStates.size();
        planStates.clear();
        LoggerUtil.info("Cleaned up all plan states, total {0} plans", count);
    }
}
