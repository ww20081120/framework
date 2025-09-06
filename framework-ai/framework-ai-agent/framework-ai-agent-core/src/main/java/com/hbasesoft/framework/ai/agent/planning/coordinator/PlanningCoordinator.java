/*
 * Copyright 2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hbasesoft.framework.ai.agent.planning.coordinator;

import com.hbasesoft.framework.ai.agent.planning.creator.PlanCreator;
import com.hbasesoft.framework.ai.agent.planning.executor.PlanExecutorInterface;
import com.hbasesoft.framework.ai.agent.planning.executor.factory.PlanExecutorFactory;
import com.hbasesoft.framework.ai.agent.planning.finalizer.PlanFinalizer;
import com.hbasesoft.framework.ai.agent.planning.model.vo.ExecutionContext;
import com.hbasesoft.framework.ai.agent.planning.model.vo.PlanInterface;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;

/**
 * Enhanced Planning Coordinator that uses PlanExecutorFactory to dynamically select the appropriate executor based on
 * plan type
 */
public class PlanningCoordinator {

    private final PlanCreator planCreator;

    private final PlanExecutorFactory planExecutorFactory;

    private PlanFinalizer planFinalizer;

    public PlanningCoordinator(PlanCreator planCreator, PlanExecutorFactory planExecutorFactory,
        PlanFinalizer planFinalizer) {
        this.planCreator = planCreator;
        this.planExecutorFactory = planExecutorFactory;
        this.planFinalizer = planFinalizer;
    }

    /**
     * Create a plan only, without executing it
     * 
     * @param context execution context
     * @return execution context
     */
    public ExecutionContext createPlan(ExecutionContext context) {
        LoggerUtil.info("Creating plan for planId: {0}", context.getCurrentPlanId());
        // Only execute the create plan step
        context.setUseMemory(false);
        planCreator.createPlan(context);

        PlanInterface plan = context.getPlan();
        if (plan != null) {
            LoggerUtil.info("Plan created successfully with type: {0} for planId: {1}", plan.getPlanType(),
                context.getCurrentPlanId());
        }

        return context;
    }

    /**
     * Execute the complete plan process with dynamic executor selection
     * 
     * @param context execution context
     * @return execution summary
     */
    public ExecutionContext executePlan(ExecutionContext context) {
        LoggerUtil.info("Executing complete plan process for planId: {0}", context.getCurrentPlanId());
        context.setUseMemory(true);

        // 1. Create a plan (normal flow)
        planCreator.createPlan(context);

        // 2. Select appropriate executor based on plan type and execute
        PlanInterface plan = context.getPlan();

        if (plan != null) {
            // Check if this is a direct response plan
            boolean isDirectResponse = plan.isDirectResponse();
            if (isDirectResponse) {
                // For direct response plans, use DirectResponseExecutor but handle
                // generation in coordinator
                PlanExecutorInterface executor = planExecutorFactory.createExecutor(plan);
                LoggerUtil.info("Selected executor: {0} for direct response plan (planId: {1})",
                    executor.getClass().getSimpleName(), context.getCurrentPlanId());
                executor.executeAllSteps(context);

                // Generate direct response using PlanFinalizer
                planFinalizer.generateDirectResponse(context);
                LoggerUtil.info("Direct response completed successfully for planId: {0}", context.getCurrentPlanId());
                return context;
            }
            else {
                // Normal plan execution
                PlanExecutorInterface executor = planExecutorFactory.createExecutor(plan);
                LoggerUtil.info("Selected executor: {0} for plan type: {1} (planId: {2})",
                    executor.getClass().getSimpleName(), plan.getPlanType(), context.getCurrentPlanId());
                executor.executeAllSteps(context);
            }
        }
        else {
            LoggerUtil.error("No plan found in context for planId: {0}", context.getCurrentPlanId());
            throw new IllegalStateException("Plan creation failed, no plan found in execution context");
        }

        // 3. Generate a summary
        planFinalizer.generateSummary(context);

        LoggerUtil.info("Plan execution completed successfully for planId: {0}", context.getCurrentPlanId());
        return context;
    }

    /**
     * Execute an existing plan (skip the create plan step) with dynamic executor selection
     * 
     * @param context execution context containing the existing plan
     * @return execution summary
     */
    public ExecutionContext executeExistingPlan(ExecutionContext context) {
        LoggerUtil.info("Executing existing plan for planId: {0}", context.getCurrentPlanId());

        PlanInterface plan = context.getPlan();
        if (plan == null) {
            LoggerUtil.error("No existing plan found in context for planId: {0}", context.getCurrentPlanId());
            throw new IllegalArgumentException("No existing plan found in execution context");
        }

        // 1. Select appropriate executor based on plan type and execute
        PlanExecutorInterface executor = planExecutorFactory.createExecutor(plan);
        LoggerUtil.info("Selected executor: {0} for existing plan type: {1} (planId: {2})",
            executor.getClass().getSimpleName(), plan.getPlanType(), context.getCurrentPlanId());
        executor.executeAllSteps(context);

        // 2. Generate a summary
        planFinalizer.generateSummary(context);

        LoggerUtil.info("Existing plan execution completed successfully for planId: {0}", context.getCurrentPlanId());
        return context;
    }

    /**
     * Execute plan with explicit executor type (useful for testing or override scenarios)
     * 
     * @param context execution context
     * @param executorType explicit executor type to use
     * @return execution summary
     */
    public ExecutionContext executeWithExplicitExecutor(ExecutionContext context, String executorType) {
        LoggerUtil.info("Executing plan with explicit executor type: {0} for planId: {1}", executorType,
            context.getCurrentPlanId());

        PlanInterface plan = context.getPlan();
        if (plan == null) {
            LoggerUtil.error("No plan found in context for planId: {0}", context.getCurrentPlanId());
            throw new IllegalArgumentException("No plan found in execution context");
        }

        // Select executor based on explicit type
        PlanExecutorInterface executor = planExecutorFactory.createExecutorByType(executorType,
            context.getCurrentPlanId());
        LoggerUtil.info("Using explicit executor: {0} for planId: {1}", executor.getClass().getSimpleName(),
            context.getCurrentPlanId());

        executor.executeAllSteps(context);
        planFinalizer.generateSummary(context);

        LoggerUtil.info("Plan execution with explicit executor completed successfully for planId: {0}",
            context.getCurrentPlanId());
        return context;
    }

    /**
     * Get information about supported plan types
     * 
     * @return Array of supported plan types
     */
    public String[] getSupportedPlanTypes() {
        return planExecutorFactory.getSupportedPlanTypes();
    }

    /**
     * Check if a plan type is supported
     * 
     * @param planType The plan type to check
     * @return true if supported, false otherwise
     */
    public boolean isPlanTypeSupported(String planType) {
        return planExecutorFactory.isPlanTypeSupported(planType);
    }

    public void setPlanFinalizer(PlanFinalizer planFinalizer) {
        this.planFinalizer = planFinalizer;
    }

}
