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

import reactor.core.publisher.Flux;

/**
 * 流式计划协调器，提供流式执行和监听功能
 * 
 * @author 王伟
 * @version 1.0
 * @since 2025年10月14日
 */
public class StreamingPlanningCoordinator extends PlanningCoordinator {

    public StreamingPlanningCoordinator(PlanCreator planCreator, PlanExecutorFactory planExecutorFactory,
        PlanFinalizer planFinalizer) {
        super(planCreator, planExecutorFactory, planFinalizer);
    }

    /**
     * 执行计划并返回总结的流式输出
     * 
     * @param context 执行上下文
     * @return 总结的流式Flux
     */
    public Flux<String> executePlanWithSummaryStream(ExecutionContext context) {
        LoggerUtil.info("Executing plan with summary stream for planId: {0}", context.getCurrentPlanId());
        context.setUseMemory(true);
        
        // 通知监听器开始执行
        context.notifyStatusChange("开始执行流式计划流程");

        try {
            // 1. 创建计划
            context.notifyStatusChange("开始创建计划");
            getPlanCreator().createPlan(context);

            PlanInterface plan = context.getPlan();
            if (plan == null) {
                LoggerUtil.error("No plan found in context for planId: {0}", context.getCurrentPlanId());
                context.notifyError(new IllegalStateException("Plan creation failed, no plan found in execution context"));
                return Flux.error(new IllegalStateException("Plan creation failed, no plan found in execution context"));
            }

            // 通知监听器计划创建完成
            context.notifyPlanCreated(plan);
            context.notifyStatusChange("计划创建完成，开始执行步骤");

            // 2. 执行计划步骤
            if (plan.isDirectResponse()) {
                // 直接响应模式
                context.notifyStatusChange("使用直接响应模式");
                PlanExecutorInterface executor = getPlanExecutorFactory().createExecutor(plan);
                executor.executeAllSteps(context);

                // 生成直接响应
                context.notifyStatusChange("生成直接响应");
                getPlanFinalizer().generateDirectResponse(context);
                
                // 通知执行完成
                context.notifyExecutionComplete();
                
                // 返回结果作为流
                String result = context.getResultSummary();
                return result != null ? Flux.just(result) : Flux.empty();
            } else {
                // 正常计划执行
                context.notifyStatusChange("开始执行计划步骤");
                PlanExecutorInterface executor = getPlanExecutorFactory().createExecutor(plan);
                executor.executeAllSteps(context);

                // 3. 生成流式总结
                context.notifyStatusChange("开始生成流式总结");
                return getPlanFinalizer().generateSummaryStreaming(context)
                    .doOnComplete(() -> {
                        LoggerUtil.info("Plan execution with streaming completed successfully for planId: {0}", 
                            context.getCurrentPlanId());
                        context.notifyExecutionComplete();
                    })
                    .doOnError(error -> {
                        LoggerUtil.error("Error in streaming execution for planId: {0}", 
                            context.getCurrentPlanId(), error);
                        context.notifyError(error instanceof Exception ? (Exception) error : new RuntimeException(error));
                    });
            }
        } catch (Exception e) {
            LoggerUtil.error("Error executing plan with stream for planId: {0}", context.getCurrentPlanId(), e);
            context.notifyError(e);
            return Flux.error(new RuntimeException("Failed to execute plan with stream", e));
        }
    }

    /**
     * 执行计划并使用监听器模式（阻塞但支持流式通知）
     * 
     * @param context 执行上下文
     * @return 执行上下文
     */
    public ExecutionContext executePlanWithListener(ExecutionContext context) {
        LoggerUtil.info("Executing plan with listener support for planId: {0}", context.getCurrentPlanId());
        context.setUseMemory(true);
        
        // 通知监听器开始执行
        context.notifyStatusChange("开始执行带监听器的计划流程");

        try {
            // 1. 创建计划
            context.notifyStatusChange("开始创建计划");
            getPlanCreator().createPlan(context);

            PlanInterface plan = context.getPlan();
            if (plan == null) {
                LoggerUtil.error("No plan found in context for planId: {0}", context.getCurrentPlanId());
                context.notifyError(new IllegalStateException("Plan creation failed, no plan found in execution context"));
                throw new IllegalStateException("Plan creation failed, no plan found in execution context");
            }

            // 通知监听器计划创建完成
            context.notifyPlanCreated(plan);
            context.notifyStatusChange("计划创建完成，开始执行步骤");

            // 2. 执行计划步骤
            if (plan.isDirectResponse()) {
                // 直接响应模式
                context.notifyStatusChange("使用直接响应模式");
                PlanExecutorInterface executor = getPlanExecutorFactory().createExecutor(plan);
                executor.executeAllSteps(context);

                // 生成直接响应
                context.notifyStatusChange("生成直接响应");
                getPlanFinalizer().generateDirectResponse(context);
            } else {
                // 正常计划执行
                context.notifyStatusChange("开始执行计划步骤");
                PlanExecutorInterface executor = getPlanExecutorFactory().createExecutor(plan);
                executor.executeAllSteps(context);

                // 3. 生成带监听器的总结
                context.notifyStatusChange("开始生成带监听器的总结");
                getPlanFinalizer().generateSummaryWithListener(context);
            }

            LoggerUtil.info("Plan execution with listener completed successfully for planId: {0}", 
                context.getCurrentPlanId());
            
            // 通知监听器执行完成
            context.notifyExecutionComplete();
            
        } catch (Exception e) {
            LoggerUtil.error("Error executing plan with listener for planId: {0}", context.getCurrentPlanId(), e);
            context.notifyError(e);
            throw e;
        }
        
        return context;
    }

    /**
     * 仅生成流式总结（用于已有计划的情况）
     * 
     * @param context 执行上下文（必须包含已执行的计划）
     * @return 总结的流式Flux
     */
    public Flux<String> generateSummaryStreamOnly(ExecutionContext context) {
        LoggerUtil.info("Generating summary stream only for planId: {0}", context.getCurrentPlanId());
        
        if (context.getPlan() == null) {
            LoggerUtil.error("No plan found in context for planId: {0}", context.getCurrentPlanId());
            return Flux.error(new IllegalArgumentException("No plan found in execution context"));
        }

        context.notifyStatusChange("开始生成流式总结");
        return getPlanFinalizer().generateSummaryStreaming(context)
            .doOnComplete(() -> {
                LoggerUtil.info("Summary stream generation completed for planId: {0}", context.getCurrentPlanId());
                context.notifyExecutionComplete();
            })
            .doOnError(error -> {
                LoggerUtil.error("Error in summary stream generation for planId: {0}", 
                    context.getCurrentPlanId(), error);
                context.notifyError(error instanceof Exception ? (Exception) error : new RuntimeException(error));
            });
    }

    // 获取私有成员的辅助方法
    private PlanCreator getPlanCreator() {
        try {
            java.lang.reflect.Field field = PlanningCoordinator.class.getDeclaredField("planCreator");
            field.setAccessible(true);
            return (PlanCreator) field.get(this);
        } catch (Exception e) {
            throw new RuntimeException("Failed to access planCreator", e);
        }
    }

    private PlanExecutorFactory getPlanExecutorFactory() {
        try {
            java.lang.reflect.Field field = PlanningCoordinator.class.getDeclaredField("planExecutorFactory");
            field.setAccessible(true);
            return (PlanExecutorFactory) field.get(this);
        } catch (Exception e) {
            throw new RuntimeException("Failed to access planExecutorFactory", e);
        }
    }

    private PlanFinalizer getPlanFinalizer() {
        try {
            java.lang.reflect.Field field = PlanningCoordinator.class.getDeclaredField("planFinalizer");
            field.setAccessible(true);
            return (PlanFinalizer) field.get(this);
        } catch (Exception e) {
            throw new RuntimeException("Failed to access planFinalizer", e);
        }
    }
}
