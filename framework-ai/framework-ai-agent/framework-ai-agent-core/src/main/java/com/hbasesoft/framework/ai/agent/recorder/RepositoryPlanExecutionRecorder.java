/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.agent.recorder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hbasesoft.framework.ai.agent.agent.AgentState;
import com.hbasesoft.framework.ai.agent.planning.model.vo.ExecutionContext;
import com.hbasesoft.framework.ai.agent.planning.model.vo.ExecutionStep;
import com.hbasesoft.framework.ai.agent.recorder.model.AgentExecutionRecord;
import com.hbasesoft.framework.ai.agent.recorder.model.ExecutionStatus;
import com.hbasesoft.framework.ai.agent.recorder.model.PlanExecutionRecord;
import com.hbasesoft.framework.ai.agent.recorder.model.ThinkActRecord;
import com.hbasesoft.framework.ai.agent.recorder.model.vo.RecorderVo;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月21日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.agent.recorder <br>
 */

@Component
public class RepositoryPlanExecutionRecorder implements PlanExecutionRecorder {

    @Autowired
    private RecorderService recorderService;

    /**
     * Record think-act execution with PlanExecutionRecord parameter
     * 
     * @param planExecutionRecord Plan execution record
     * @param agentExecutionId Agent execution ID
     * @param thinkActRecord Think-act record
     */
    @Override
    public void setThinkActExecution(PlanExecutionRecord planExecutionRecord, Long agentExecutionId,
        ThinkActRecord thinkActRecord) {
        if (planExecutionRecord != null) {
            for (AgentExecutionRecord agentRecord : planExecutionRecord.getAgentExecutionSequence()) {
                if (agentExecutionId.equals(agentRecord.getId())) {
                    addThinkActStep(agentRecord, thinkActRecord);

                    updateThinkActRecord(planExecutionRecord, thinkActRecord);
                    break;
                }
            }
        }
    }

    /**
     * Record plan completion with PlanExecutionRecord parameter
     * 
     * @param planExecutionRecord Plan execution record
     * @param summary Execution summary
     */
    @Override
    public void setPlanCompletion(PlanExecutionRecord planExecutionRecord, String summary) {
        if (planExecutionRecord != null) {
            planExecutionRecord.complete(summary);
        }
    }

    /**
     * Save execution records of the specified plan ID to persistent storage. This method will recursively call save
     * methods of PlanExecutionRecord, AgentExecutionRecord and ThinkActRecord
     * 
     * @param rootPlanId Plan ID to save
     * @return Returns true if record is found and saved, false otherwise
     */
    @Override
    public boolean savePlanExecutionRecords(PlanExecutionRecord planExecutionRecord) {
        saveExecutionRecord(planExecutionRecord);
        return true;
    }

    /**
     * Delete execution record of the specified plan ID
     * 
     * @param planId Plan ID to delete
     */
    @Override
    public void removeExecutionRecord(String planId) {
        recorderService.deleteById(planId);
    }

    /**
     * Record the start of plan execution.
     */
    public void recordPlanExecutionStart(ExecutionContext context) {

        String rootPlanId = context.getPlan().getRootPlanId();
        PlanExecutionRecord rootPlan = getOrCreateRootPlanExecutionRecord(rootPlanId, true);
        PlanExecutionRecord recordToUpdate = getRecordToUpdate(context, rootPlan);

        recordToUpdate.setCurrentPlanId(context.getPlan().getCurrentPlanId());
        recordToUpdate.setStartTime(LocalDateTime.now());
        recordToUpdate.setTitle(context.getPlan().getTitle());
        recordToUpdate.setUserRequest(context.getUserRequest());
        retrieveExecutionSteps(context, recordToUpdate);

        // Save the correct plan (parent for sub-plan, self for root plan)
        if (rootPlan != null) {
            savePlanExecutionRecords(rootPlan);
        }
    }

    /**
     * Record the start of step execution.
     */
    public void recordStepStart(ExecutionStep step, ExecutionContext context) {
        String rootPlanId = context.getPlan().getRootPlanId();
        PlanExecutionRecord rootPlan = getOrCreateRootPlanExecutionRecord(rootPlanId, true);
        PlanExecutionRecord recordToUpdate = getRecordToUpdate(context, rootPlan);
        if (recordToUpdate != null) {
            int currentStepIndex = step.getStepIndex();
            recordToUpdate.setCurrentStepIndex(currentStepIndex);
            retrieveExecutionSteps(context, recordToUpdate);
            List<AgentExecutionRecord> agentExecutionSequence = recordToUpdate.getAgentExecutionSequence();
            AgentExecutionRecord agentExecutionRecord;
            if (agentExecutionSequence.size() > currentStepIndex) {
                agentExecutionRecord = agentExecutionSequence.get(currentStepIndex);
            }
            else {
                // create and add new AgentExecutionRecord
                agentExecutionRecord = new AgentExecutionRecord(recordToUpdate.getCurrentPlanId(), null, null);
                // Fill up to currentStepIndex
                while (agentExecutionSequence.size() < currentStepIndex) {
                    agentExecutionSequence.add(new AgentExecutionRecord());
                }
                agentExecutionSequence.add(agentExecutionRecord);
            }
            agentExecutionRecord.setStatus(ExecutionStatus.RUNNING);
            // Save the correct plan (parent for sub-plan, self for root plan)
            if (rootPlan != null) {
                savePlanExecutionRecords(rootPlan);
            }
        }
    }

    /**
     * Record the end of step execution.
     */
    public void recordStepEnd(ExecutionStep step, ExecutionContext context) {
        String rootPlanId = context.getPlan().getRootPlanId();
        PlanExecutionRecord rootPlan = getOrCreateRootPlanExecutionRecord(rootPlanId, true);
        PlanExecutionRecord recordToUpdate = getRecordToUpdate(context, rootPlan);

        if (recordToUpdate != null) {
            int currentStepIndex = step.getStepIndex();
            recordToUpdate.setCurrentStepIndex(currentStepIndex);
            retrieveExecutionSteps(context, recordToUpdate);

            List<AgentExecutionRecord> agentExecutionSequence = recordToUpdate.getAgentExecutionSequence();
            // Check boundaries to ensure agentExecutionSequence has enough elements
            if (agentExecutionSequence.size() > currentStepIndex) {
                AgentExecutionRecord agentExecutionRecord = agentExecutionSequence.get(currentStepIndex);
                agentExecutionRecord.setStatus(
                    step.getStatus() == AgentState.COMPLETED ? ExecutionStatus.FINISHED : ExecutionStatus.RUNNING);
            }
            else {
                // If there is no corresponding AgentExecutionRecord, create a new one
                AgentExecutionRecord agentExecutionRecord = new AgentExecutionRecord(recordToUpdate.getCurrentPlanId(),
                    null, null);
                agentExecutionRecord.setStatus(
                    step.getStatus() == AgentState.COMPLETED ? ExecutionStatus.FINISHED : ExecutionStatus.RUNNING);

                // Fill up to currentStepIndex
                while (agentExecutionSequence.size() < currentStepIndex) {
                    agentExecutionSequence.add(new AgentExecutionRecord());
                }
                agentExecutionSequence.add(agentExecutionRecord);
            }

            // Save the correct plan (parent for sub-plan, self for root plan)
            if (rootPlan != null) {
                savePlanExecutionRecords(rootPlan);
            }
        }
    }

    /**
     * Get the plan execution record to update in memory. Returns the record that should be updated with current
     * execution data.
     */
    private PlanExecutionRecord getRecordToUpdate(ExecutionContext context, PlanExecutionRecord rootRecord) {
        Long thinkActRecordId = context.getThinkActRecordId();

        // For sub-plan execution, we want to save to parent plan, so return the root
        // plan
        // record
        if (thinkActRecordId != null) {
            // This is a sub-plan execution - save data to parent plan
            String currentPlanId = context.getPlan().getCurrentPlanId();
            if (currentPlanId.startsWith("sub")) {
                // If currentPlanId starts with "sub-", it indicates a sub-plan execution
                // Use the root record to create or get the sub-plan execution record
                return getOrCreateSubPlanExecutionRecord(rootRecord, currentPlanId, thinkActRecordId, true);
            }
            else {
                throw new IllegalArgumentException("Current plan ID is not a sub-plan: " + currentPlanId);
            }

        }
        else {
            // This is a parent/root plan execution - update the plan itself (the record
            // we already have)
            return rootRecord;
        }
    }

    /**
     * Record complete agent execution at the end. This method handles all agent execution record management logic
     * without exposing internal record objects.
     */
    @Override
    public void recordCompleteAgentExecution(PlanExecutionParams params) {
        if (params == null || params.getCurrentPlanId() == null) {
            return;
        }

        // Create agent execution record with all the final data
        AgentExecutionRecord agentRecord = new AgentExecutionRecord(params.getCurrentPlanId(), params.getAgentName(),
            params.getAgentDescription());
        agentRecord.setMaxSteps(params.getMaxSteps());
        agentRecord.setCurrentStep(params.getActualSteps());
        agentRecord.setErrorMessage(params.getErrorMessage());
        agentRecord.setResult(params.getResult());
        agentRecord.setStartTime(params.getStartTime());
        agentRecord.setEndTime(params.getEndTime());
        agentRecord.setStatus(params.getStatus());

        PlanExecutionRecord planRecord = null;
        PlanExecutionRecord rootPlan = getOrCreateRootPlanExecutionRecord(params.getRootPlanId(), true);
        // Handle both root plan and sub-plan execution cases
        if (params.getThinkActRecordId() != null) {
            // For sub-plan execution, we need the parent plan first
            if (rootPlan != null) {
                planRecord = getOrCreateSubPlanExecutionRecord(rootPlan, params.getCurrentPlanId(),
                    params.getThinkActRecordId(), true);
                // For sub-plan, set execution to sub-plan but save parent plan
                if (planRecord != null) {
                    setAgentExecution(planRecord, agentRecord);
                }
            }
        }
        else {
            // For root plan execution
            planRecord = getOrCreateRootPlanExecutionRecord(params.getCurrentPlanId(), true);
            if (planRecord != null) {
                setAgentExecution(planRecord, agentRecord);
            }
        }

        // Save the correct plan (parent for sub-plan, self for root plan)
        if (rootPlan != null) {
            savePlanExecutionRecords(rootPlan);
        }
    }

    /**
     * Interface 1: Record thinking and action execution process. This method handles ThinkActRecord creation and
     * thinking process without exposing internal record objects.
     */
    @Override
    public Long recordThinkingAndAction(PlanExecutionParams params) {
        if (params.getCurrentPlanId() == null) {
            return null;
        }

        PlanExecutionRecord planExecutionRecord = null;
        PlanExecutionRecord planToSave = null; // Track which plan should be saved

        // Handle both root plan and sub-plan execution cases based on thinkActRecordId
        if (params.getThinkActRecordId() != null) {
            PlanExecutionRecord parentPlan = getOrCreateRootPlanExecutionRecord(params.getRootPlanId(), true);
            if (parentPlan != null) {
                planExecutionRecord = getOrCreateSubPlanExecutionRecord(parentPlan, params.getCurrentPlanId(),
                    params.getThinkActRecordId(), true);
                planToSave = parentPlan; // Save parent plan for sub-plan execution
            }
        }
        else {
            planExecutionRecord = getOrCreateRootPlanExecutionRecord(params.getCurrentPlanId(), true);
            planToSave = planExecutionRecord; // Save the root plan record itself
        }

        AgentExecutionRecord agentExecutionRecord = getCurrentAgentExecutionRecord(planExecutionRecord);

        if (agentExecutionRecord == null && planExecutionRecord != null) {
            agentExecutionRecord = new AgentExecutionRecord(params.getCurrentPlanId(), params.getAgentName(),
                params.getAgentDescription());
            setAgentExecution(planExecutionRecord, agentExecutionRecord);
        }

        if (agentExecutionRecord == null) {
            LoggerUtil.error("Failed to create or retrieve AgentExecutionRecord for plan: {0}",
                params.getCurrentPlanId());
            return null;
        }

        ThinkActRecord thinkActRecord = new ThinkActRecord(agentExecutionRecord.getId());
        thinkActRecord.setActStartTime(LocalDateTime.now());

        if (params.getModelName() != null) {
            if (planExecutionRecord != null) {
                planExecutionRecord.setModelName(params.getModelName());
            }
            agentExecutionRecord.setModelName(params.getModelName());
        }

        if (params.getThinkInput() != null) {
            thinkActRecord.startThinking(params.getThinkInput());
        }
        if (params.getThinkOutput() != null) {
            thinkActRecord.finishThinking(params.getThinkOutput());
        }

        if (params.isActionNeeded() && params.getToolName() != null) {
            thinkActRecord.setActionNeeded(true);
            thinkActRecord.setToolName(params.getToolName());
            thinkActRecord.setToolParameters(params.getToolParameters());
            thinkActRecord.setStatus(ExecutionStatus.RUNNING);
        }

        if (params.getErrorMessage() != null) {
            thinkActRecord.recordError(params.getErrorMessage());
        }

        if (planExecutionRecord != null) {
            setThinkActExecution(planExecutionRecord, agentExecutionRecord.getId(), thinkActRecord);
            if (planToSave != null) {
                savePlanExecutionRecords(planToSave);
            }
        }

        return thinkActRecord.getId();
    }

    /**
     * Interface 2: Record action execution result. This method updates the ThinkActRecord with action results without
     * exposing internal record objects.
     */
    @Override
    public void recordActionResult(PlanExecutionParams params) {
        if (params.getCurrentPlanId() == null || params.getCreatedThinkActRecordId() == null) {
            return;
        }

        PlanExecutionRecord planExecutionRecord = null;
        PlanExecutionRecord planToSave = null; // Track which plan should be saved

        // Handle both root plan and sub-plan execution cases based on thinkActRecordId
        if (params.getThinkActRecordId() != null) {
            // Sub-plan execution: thinkActRecordId indicates this is triggered by a tool
            // call
            PlanExecutionRecord parentPlan = getOrCreateRootPlanExecutionRecord(params.getRootPlanId(), true);
            if (parentPlan != null) {
                planExecutionRecord = getOrCreateSubPlanExecutionRecord(parentPlan, params.getCurrentPlanId(),
                    params.getThinkActRecordId(), true);
                planToSave = parentPlan; // Save parent plan for sub-plan execution
            }
        }
        else {
            // Root plan execution: no thinkActRecordId means this is a main plan
            planExecutionRecord = getOrCreateRootPlanExecutionRecord(params.getCurrentPlanId(), true);
            planToSave = planExecutionRecord; // Save the root plan record itself
        }

        AgentExecutionRecord agentExecutionRecord = getCurrentAgentExecutionRecord(planExecutionRecord);

        // Additional safety check
        if (agentExecutionRecord == null) {
            LoggerUtil.error("Failed to retrieve AgentExecutionRecord for plan: {0} in recordActionResult",
                params.getCurrentPlanId());
            return;
        }

        // Find the ThinkActRecord by ID and update it with action results
        ThinkActRecord thinkActRecord = findThinkActRecordInPlan(planExecutionRecord,
            params.getCreatedThinkActRecordId());

        if (thinkActRecord != null) {
            // Record action start if not already recorded
            if (params.getActionDescription() != null && params.getToolName() != null) {
                thinkActRecord.startAction(params.getActionDescription(), params.getToolName(),
                    params.getToolParameters());
            }

            // Record action completion
            if (params.getActionResult() != null && params.getStatus() != null) {
                thinkActRecord.finishAction(params.getActionResult(), params.getStatus());
            }

            // Record error if any
            if (params.getErrorMessage() != null) {
                thinkActRecord.recordError(params.getErrorMessage());
            }

            // Set actToolInfoList if available
            if (params.getActToolInfoList() != null) {
                thinkActRecord.setActToolInfoList(params.getActToolInfoList());
            }

            // Set think-act execution to update the record
            setThinkActExecution(planExecutionRecord, agentExecutionRecord.getId(), thinkActRecord);

            // Save the execution records
            if (planToSave != null) {
                savePlanExecutionRecords(planToSave);
            }
        }
        else {
            LoggerUtil.warn("ThinkActRecord not found with ID: {0} for plan: {1}", params.getCreatedThinkActRecordId(),
                params.getCurrentPlanId());
        }
    }

    /**
     * Interface 3: Record plan completion. This method handles plan completion recording logic without exposing
     * internal record objects.
     */
    @Override
    public void recordPlanCompletion(String currentPlanId, String rootPlanId, Long thinkActRecordId, String summary) {
        if (currentPlanId == null) {
            return;
        }

        PlanExecutionRecord planExecutionRecord = null;
        PlanExecutionRecord planToSave = null; // Track which plan should be saved

        // Handle both root plan and sub-plan execution cases based on thinkActRecordId
        if (thinkActRecordId != null) {
            // Sub-plan execution: thinkActRecordId indicates this is triggered by a tool
            // call
            PlanExecutionRecord parentPlan = getOrCreateRootPlanExecutionRecord(rootPlanId, false);
            if (parentPlan != null) {
                planExecutionRecord = getOrCreateSubPlanExecutionRecord(parentPlan, currentPlanId, thinkActRecordId,
                    false);
                planToSave = parentPlan; // Save parent plan for sub-plan execution
            }
        }
        else {
            // Root plan execution: no thinkActRecordId means this is a main plan
            planExecutionRecord = getOrCreateRootPlanExecutionRecord(currentPlanId, false);
            planToSave = planExecutionRecord; // Save the root plan record itself
        }

        if (planExecutionRecord != null) {
            setPlanCompletion(planExecutionRecord, summary);
            // Save the correct plan (parent for sub-plan, self for root plan)
            if (planToSave != null) {
                savePlanExecutionRecords(planToSave);
            }
        }

        LoggerUtil.info("Plan completed with ID: {0} (thinkActRecordId: {1}) and summary: {2}", currentPlanId,
            thinkActRecordId, summary);
    }

    @Override
    public PlanExecutionRecord getRootPlanExecutionRecord(String rootPlanId) {
        if (rootPlanId == null) {
            LoggerUtil.warn("rootPlanId is null, cannot retrieve plan execution record");
            return null;
        }
        return getOrCreateRootPlanExecutionRecord(rootPlanId, false);
    }

    /**
     * Gets or creates root plan execution record
     * 
     * @param rootPlanId Root plan ID
     * @param createIfNotExists Whether to create if not exists
     * @return Root plan execution record, or null if not found and createIfNotExists is false
     */
    private PlanExecutionRecord getOrCreateRootPlanExecutionRecord(String rootPlanId, boolean createIfNotExists) {
        LoggerUtil.debug("Enter getOrCreateRootPlanExecutionRecord with rootPlanId: {0}, createIfNotExists: {1}",
            rootPlanId, createIfNotExists);

        if (rootPlanId == null) {
            LoggerUtil.error("rootPlanId is null");
            return null;
        }

        // Get existing root plan record
        PlanExecutionRecord rootRecord = getExecutionRecord(rootPlanId);

        // Create if not exists and createIfNotExists is true
        if (rootRecord == null && createIfNotExists) {
            LoggerUtil.info("Creating root plan with ID: {0}", rootPlanId);
            rootRecord = new PlanExecutionRecord(rootPlanId, rootPlanId);
            // Note: No explicit save here as per requirement
        }

        return rootRecord;
    }

    /**
     * Gets or creates sub-plan execution record from parent plan
     * 
     * @param parentPlan Parent plan execution record
     * @param subPlanId Sub-plan ID
     * @param thinkActRecordId Think-act record ID that contains the sub-plan
     * @param createIfNotExists Whether to create if not exists
     * @return Sub-plan execution record, or null if thinkActRecordId is null or not found and createIfNotExists is
     *         false
     */
    private PlanExecutionRecord getOrCreateSubPlanExecutionRecord(PlanExecutionRecord parentPlan, String subPlanId,
        Long thinkActRecordId, boolean createIfNotExists) {
        LoggerUtil.info(
            "Enter getOrCreateSubPlanExecutionRecord with subPlanId: {0}, thinkActRecordId: {1}, createIfNotExists: {2}",
            subPlanId, thinkActRecordId, createIfNotExists);

        // Return null if thinkActRecordId is null as per requirement
        if (thinkActRecordId == null) {
            LoggerUtil.warn("thinkActRecordId is null, returning null");
            return null;
        }

        if (parentPlan == null) {
            LoggerUtil.warn("parentPlan is null");
            return null;
        }

        // Find ThinkActRecord in parent plan
        ThinkActRecord thinkActRecord = findThinkActRecordInPlan(parentPlan, thinkActRecordId);
        if (thinkActRecord == null) {
            LoggerUtil.warn("ThinkActRecord not found with ID: {0}", thinkActRecordId);
            return null;
        }

        // Check if subPlanExecutionRecord exists
        PlanExecutionRecord subPlan = thinkActRecord.getSubPlanExecutionRecord();
        if (subPlan == null && createIfNotExists) {
            // Create new sub-plan with subPlanId and rootPlanId from parent
            LoggerUtil.info("Creating sub-plan with ID: {0}", subPlanId);
            subPlan = new PlanExecutionRecord(subPlanId, parentPlan.getRootPlanId());
            subPlan.setThinkActRecordId(thinkActRecordId);
            thinkActRecord.recordSubPlanExecution(subPlan);
            // Note: No explicit save here as per requirement
        }

        return subPlan;
    }

    /**
     * Helper method to find ThinkActRecord in a plan
     * 
     * @param plan Plan execution record
     * @param thinkActRecordId Think-act record ID
     * @return ThinkActRecord if found, null otherwise
     */
    private ThinkActRecord findThinkActRecordInPlan(PlanExecutionRecord plan, Long thinkActRecordId) {
        if (plan == null || thinkActRecordId == null) {
            return null;
        }

        for (AgentExecutionRecord agentRecord : plan.getAgentExecutionSequence()) {
            for (ThinkActRecord thinkActRecord : agentRecord.getThinkActSteps()) {
                if (thinkActRecordId.equals(thinkActRecord.getId())) {
                    return thinkActRecord;
                }
            }
        }
        return null;
    }

    /**
     * Get current agent execution record for a specific plan execution record
     * 
     * @param planExecutionRecord Plan execution record
     * @return Current active agent execution record, or null if none exists
     */
    private AgentExecutionRecord getCurrentAgentExecutionRecord(PlanExecutionRecord planExecutionRecord) {
        if (planExecutionRecord != null) {
            List<AgentExecutionRecord> agentExecutionSequence = planExecutionRecord.getAgentExecutionSequence();
            Integer currentIndex = planExecutionRecord.getCurrentStepIndex();
            if (!agentExecutionSequence.isEmpty() && currentIndex != null
                && currentIndex < agentExecutionSequence.size()) {
                return agentExecutionSequence.get(currentIndex);
            }
        }
        return null;
    }

    private PlanExecutionRecord getExecutionRecord(String rootPlanId) {
        RecorderVo entity = recorderService.getByRootPlanId(rootPlanId);
        return entity != null ? entity.getPlanExecutionRecord() : null;
    }

    private void saveExecutionRecord(PlanExecutionRecord planExecutionRecord) {
        RecorderVo entity = recorderService.getByRootPlanId(planExecutionRecord.getRootPlanId());
        if (entity == null) {
            entity = new RecorderVo();
            entity.setPlanId(planExecutionRecord.getRootPlanId());
            entity.setGmtCreate(new Date());
        }

        entity.setPlanExecutionRecord(planExecutionRecord);
        entity.setGmtModified(new Date());

        recorderService.save(entity);
    }

    private void updateThinkActRecord(PlanExecutionRecord parentPlan, ThinkActRecord record) {
        if (parentPlan != null && record != null) {
            ThinkActRecord existingRecord = findThinkActRecordInPlan(parentPlan, record.getId());
            if (existingRecord != null) {
                BeanUtils.copyProperties(record, existingRecord);
            }
        }
    }

    private void addThinkActStep(AgentExecutionRecord agentRecord, ThinkActRecord thinkActRecord) {
        if (agentRecord.getThinkActSteps() == null) {
            agentRecord.addThinkActStep(thinkActRecord);
            return;
        }
        // Will be called multiple times, so need to modify based on id
        ThinkActRecord exist = agentRecord.getThinkActSteps().stream()
            .filter(r -> r.getId().equals(thinkActRecord.getId())).findFirst().orElse(null);
        if (exist == null) {
            agentRecord.getThinkActSteps().add(thinkActRecord);
        }
        else {
            BeanUtils.copyProperties(thinkActRecord, exist);
        }
    }

    /**
     * Retrieve execution step information and set it to the record.
     */
    private void retrieveExecutionSteps(ExecutionContext context, PlanExecutionRecord record) {
        List<String> steps = new ArrayList<>();
        for (ExecutionStep step : context.getPlan().getAllSteps()) {
            steps.add(step.getStepInStr());
        }
        record.setSteps(steps);
    }

    /**
     * Record agent execution with PlanExecutionRecord parameter
     * 
     * @param planExecutionRecord Plan execution record
     * @param agentRecord Agent execution record
     * @return Agent execution ID
     */
    private Long setAgentExecution(PlanExecutionRecord planExecutionRecord, AgentExecutionRecord agentRecord) {
        if (planExecutionRecord != null) {
            planExecutionRecord.addAgentExecutionRecord(agentRecord);
        }
        return agentRecord.getId();
    }

    /**
     * get current think-act record ID
     * 
     * @param currentPlanId Current plan ID
     * @param rootPlanId Root plan ID
     * @return Current think-act record ID, returns null if none exists
     */
    public Long getCurrentThinkActRecordId(String currentPlanId, String rootPlanId) {
        try {
            PlanExecutionRecord planExecutionRecord = null;

            if (rootPlanId != null && !rootPlanId.equals(currentPlanId)) {
                PlanExecutionRecord parentPlan = getOrCreateRootPlanExecutionRecord(rootPlanId, false);
                if (parentPlan != null) {
                    AgentExecutionRecord currentAgentRecord = getCurrentAgentExecutionRecord(parentPlan);
                    if (currentAgentRecord != null && currentAgentRecord.getThinkActSteps() != null
                        && !currentAgentRecord.getThinkActSteps().isEmpty()) {
                        List<ThinkActRecord> steps = currentAgentRecord.getThinkActSteps();
                        ThinkActRecord lastStep = steps.get(steps.size() - 1);
                        return lastStep.getId();
                    }
                }
            }
            else {
                planExecutionRecord = getOrCreateRootPlanExecutionRecord(currentPlanId, false);
                if (planExecutionRecord != null) {
                    AgentExecutionRecord currentAgentRecord = getCurrentAgentExecutionRecord(planExecutionRecord);
                    if (currentAgentRecord != null && currentAgentRecord.getThinkActSteps() != null
                        && !currentAgentRecord.getThinkActSteps().isEmpty()) {
                        List<ThinkActRecord> steps = currentAgentRecord.getThinkActSteps();
                        ThinkActRecord lastStep = steps.get(steps.size() - 1);
                        return lastStep.getId();
                    }
                }
            }
        }
        catch (Exception e) {
            LoggerUtil.warn("Failed to get current think-act record ID: {0}", e.getMessage());
        }

        return null;
    }

}
