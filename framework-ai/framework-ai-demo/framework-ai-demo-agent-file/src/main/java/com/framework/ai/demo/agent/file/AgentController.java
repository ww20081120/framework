/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.framework.ai.demo.agent.file;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.hbasesoft.framework.ai.agent.dynamic.memory.service.MemoryService;
import com.hbasesoft.framework.ai.agent.dynamic.memory.vo.MemoryVo;
import com.hbasesoft.framework.ai.agent.planning.PlanningFactory;
import com.hbasesoft.framework.ai.agent.planning.coordinator.PlanIdDispatcher;
import com.hbasesoft.framework.ai.agent.planning.coordinator.PlanningCoordinator;
import com.hbasesoft.framework.ai.agent.planning.coordinator.StreamingPlanningCoordinator;
import com.hbasesoft.framework.ai.agent.planning.listener.ExecutionListener;
import com.hbasesoft.framework.ai.agent.planning.model.vo.ExecutionContext;
import com.hbasesoft.framework.ai.agent.planning.model.vo.ExecutionStep;
import com.hbasesoft.framework.ai.agent.planning.model.vo.PlanInterface;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;

import reactor.core.publisher.Flux;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月27日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.demo.jmanus.simple <br>
 */
@RequestMapping("/api/agent")
@RestController
public class AgentController {

    @Autowired
    @Lazy
    private PlanningFactory planningFactory;

    @Autowired
    private PlanIdDispatcher planIdDispatcher;

    @Autowired
    private MemoryService memoryService;

    private String memoryId;

    private String rootPlanId;

    @GetMapping("/solve")
    public String solveCodingTask(@RequestParam("task") String task) {
        if (StringUtils.isAllBlank(task)) {
            return "处理任务失败: Query content cannot be empty";
        }

        try {
            // 创建 ProcessOptions

            ExecutionContext context = new ExecutionContext();
            context.setUserRequest(task);

            // Use PlanIdDispatcher to generate a unique plan ID
            String planId = planIdDispatcher.generatePlanId();
            context.setCurrentPlanId(planId);

            context.setNeedSummary(true);
            if (rootPlanId == null) {
                rootPlanId = planId;
            }
            context.setRootPlanId(rootPlanId);

            if (StringUtils.isEmpty(memoryId)) {
                memoryId = RandomStringUtils.randomAlphabetic(8);
            }
            context.setMemoryId(memoryId);

            // Get or create planning flow
            PlanningCoordinator planningFlow = planningFactory.createPlanningCoordinator(context);

            // Asynchronous execution of task
            memoryService.saveMemory(new MemoryVo(context.getMemoryId(), task));

            ExecutionContext ctx = planningFlow.executePlan(context);

            // 返回结果
            return "任务处理完成:\n" + ctx.getResultSummary();
        }
        catch (Exception e) {
            return "处理任务时出错: " + e.getMessage();
        }
    }

    @GetMapping("/clean")
    public String clean() {
        memoryId = null;
        rootPlanId = null;
        return "清理成功";
    }

    /**
     * 通过反射创建StreamingPlanningCoordinator
     */
    private StreamingPlanningCoordinator createStreamingCoordinator(PlanningCoordinator originalCoordinator) {
        try {
            // 获取PlanningCoordinator的私有字段
            Field planCreatorField = PlanningCoordinator.class.getDeclaredField("planCreator");
            Field planExecutorFactoryField = PlanningCoordinator.class.getDeclaredField("planExecutorFactory");
            Field planFinalizerField = PlanningCoordinator.class.getDeclaredField("planFinalizer");
            
            planCreatorField.setAccessible(true);
            planExecutorFactoryField.setAccessible(true);
            planFinalizerField.setAccessible(true);
            
            // 获取组件实例
            Object planCreator = planCreatorField.get(originalCoordinator);
            Object planExecutorFactory = planExecutorFactoryField.get(originalCoordinator);
            Object planFinalizer = planFinalizerField.get(originalCoordinator);
            
            // 创建StreamingPlanningCoordinator
            return new StreamingPlanningCoordinator(
                (com.hbasesoft.framework.ai.agent.planning.creator.PlanCreator) planCreator,
                (com.hbasesoft.framework.ai.agent.planning.executor.factory.PlanExecutorFactory) planExecutorFactory,
                (com.hbasesoft.framework.ai.agent.planning.finalizer.PlanFinalizer) planFinalizer
            );
        } catch (Exception e) {
            LoggerUtil.error("创建StreamingPlanningCoordinator失败", e);
            throw new RuntimeException("无法创建流式计划协调器", e);
        }
    }

    /**
     * 流式输出接口 - 使用SSE (Server-Sent Events)
     * 思考过程和最终输出分离，类似思考大模型
     */
    @GetMapping(value = "/solve-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter solveCodingTaskStream(@RequestParam("task") String task) {
        if (StringUtils.isAllBlank(task)) {
            SseEmitter errorEmitter = new SseEmitter();
            try {
                errorEmitter.send(SseEmitter.event().name("error").data("处理任务失败: Query content cannot be empty"));
                errorEmitter.complete();
            } catch (IOException e) {
                errorEmitter.completeWithError(e);
            }
            return errorEmitter;
        }

        // 创建SSE发射器，设置超时时间
        SseEmitter emitter = new SseEmitter(60000L); // 60秒超时
        
        try {
            // 创建执行上下文
            ExecutionContext context = new ExecutionContext();
            context.setUserRequest(task);

            String planId = planIdDispatcher.generatePlanId();
            context.setCurrentPlanId(planId);
            context.setNeedSummary(true);
            
            if (rootPlanId == null) {
                rootPlanId = planId;
            }
            context.setRootPlanId(rootPlanId);

            if (StringUtils.isEmpty(memoryId)) {
                memoryId = RandomStringUtils.randomAlphabetic(8);
            }
            context.setMemoryId(memoryId);

            // 保存内存
            memoryService.saveMemory(new MemoryVo(context.getMemoryId(), task));

            // 获取原有的PlanningCoordinator来获取组件
            PlanningCoordinator originalCoordinator = planningFactory.createPlanningCoordinator(context);
            
            // 创建流式计划协调器（通过反射获取私有组件）
            StreamingPlanningCoordinator streamingCoordinator = createStreamingCoordinator(originalCoordinator);

            // 创建思考过程监听器
            StringBuilder thinkBuilder = new StringBuilder();
            AtomicBoolean isThinking = new AtomicBoolean(true);
            
            ExecutionListener thinkingListener = new ExecutionListener() {
                @Override
                public void onStatusChange(ExecutionContext context, String status) {
                    try {
                        if (isThinking.get()) {
                            emitter.send(SseEmitter.event()
                                .name("thinking")
                                .data("🔄 " + status));
                        }
                    } catch (IOException e) {
                        LoggerUtil.error("发送状态更新失败", e);
                        emitter.completeWithError(e);
                    }
                }

                @Override
                public void onPlanCreated(PlanInterface plan) {
                    try {
                        if (isThinking.get()) {
                            StringBuilder planInfo = new StringBuilder();
                            planInfo.append("📋 计划已创建\n");
                            planInfo.append("┌─────────────────────────────────\n");
                            planInfo.append(String.format("│ 📝 计划类型: %s\n", plan.getPlanType()));
                            
                            if (plan.getTitle() != null && !plan.getTitle().trim().isEmpty()) {
                                planInfo.append(String.format("│ 🏷️  计划标题: %s\n", plan.getTitle()));
                            }
                            
                            if (plan.getPlanningThinking() != null && !plan.getPlanningThinking().trim().isEmpty()) {
                                planInfo.append("│ 🤔 思考过程:\n");
                                String[] thinkingLines = plan.getPlanningThinking().split("\n");
                                for (String line : thinkingLines) {
                                    if (line.trim().length() > 0) {
                                        planInfo.append(String.format("│   %s\n", line.trim()));
                                    }
                                }
                            }
                            
                            if (plan.getAllSteps() != null && !plan.getAllSteps().isEmpty()) {
                                planInfo.append(String.format("│ 📊 执行步骤: 共 %d 步\n", plan.getAllSteps().size()));
                                for (int i = 0; i < plan.getAllSteps().size(); i++) {
                                    ExecutionStep step = plan.getAllSteps().get(i);
                                    planInfo.append(String.format("│   %d. %s\n", i + 1, step.getStepRequirement()));
                                }
                            }
                            
                            if (plan.getExecutionParams() != null && !plan.getExecutionParams().trim().isEmpty()) {
                                planInfo.append("│ ⚙️  执行参数: ").append(plan.getExecutionParams()).append("\n");
                            }
                            
                            planInfo.append("└─────────────────────────────────\n\n");
                            
                            thinkBuilder.append(planInfo.toString());
                            emitter.send(SseEmitter.event()
                                .name("thinking")
                                .data(planInfo.toString()));
                        }
                    } catch (IOException e) {
                        LoggerUtil.error("发送计划创建信息失败", e);
                        emitter.completeWithError(e);
                    }
                }

                @Override
                public void onStepStart(ExecutionStep step) {
                    try {
                        if (isThinking.get()) {
                            String stepInfo = String.format("▶️ 开始执行: %s\n", step.getStepRequirement());
                            thinkBuilder.append(stepInfo);
                            emitter.send(SseEmitter.event()
                                .name("thinking")
                                .data(stepInfo));
                        }
                    } catch (IOException e) {
                        LoggerUtil.error("发送步骤开始信息失败", e);
                        emitter.completeWithError(e);
                    }
                }

                @Override
                public void onStepProgress(ExecutionStep step, String progress) {
                    try {
                        if (isThinking.get()) {
                            String progressInfo = String.format("📊 进度: %s - %s\n", step.getStepRequirement(), progress);
                            thinkBuilder.append(progressInfo);
                            emitter.send(SseEmitter.event()
                                .name("thinking")
                                .data(progressInfo));
                        }
                    } catch (IOException e) {
                        LoggerUtil.error("发送进度信息失败", e);
                        emitter.completeWithError(e);
                    }
                }

                @Override
                public void onStepComplete(ExecutionStep step, String result) {
                    try {
                        if (isThinking.get()) {
                            String completeInfo = String.format("✅ 完成: %s\n", step.getStepRequirement());
                            thinkBuilder.append(completeInfo);
                            emitter.send(SseEmitter.event()
                                .name("thinking")
                                .data(completeInfo));
                        }
                    } catch (IOException e) {
                        LoggerUtil.error("发送步骤完成信息失败", e);
                        emitter.completeWithError(e);
                    }
                }

                @Override
                public void onError(Exception error) {
                    try {
                        emitter.send(SseEmitter.event()
                            .name("error")
                            .data("❌ 执行出错: " + error.getMessage()));
                        emitter.complete();
                    } catch (IOException e) {
                        LoggerUtil.error("发送错误信息失败", e);
                        emitter.completeWithError(e);
                    }
                }
            };

            // 添加监听器
            context.addListener(thinkingListener);

            // 异步执行
            new Thread(() -> {
                try {
                    // 发送开始信号
                    emitter.send(SseEmitter.event().name("start").data("开始处理任务..."));
                    
                    // 执行计划并获取流式总结
                    Flux<String> summaryStream = streamingCoordinator.executePlanWithSummaryStream(context);
                    
                    // 发送思考完成信号
                    isThinking.set(false);
                    emitter.send(SseEmitter.event().name("thinking_complete").data("思考完成，开始生成最终答案..."));
                    
                    // 发送最终答案
                    StringBuilder finalAnswer = new StringBuilder();
                    summaryStream.subscribe(
                        chunk -> {
                            try {
                                finalAnswer.append(chunk);
                                emitter.send(SseEmitter.event()
                                    .name("answer")
                                    .data(chunk));
                            } catch (IllegalStateException e) {
                                LoggerUtil.warn("SseEmitter已关闭，无法发送答案片段: {0}", e.getMessage());
                                // 发送完成信号以避免客户端继续等待
                                try {
                                    emitter.complete();
                                } catch (Exception ex) {
                                    LoggerUtil.warn("完成SseEmitter时出错: {0}", ex.getMessage());
                                }
                            } catch (IOException e) {
                                LoggerUtil.error("发送答案片段失败", e);
                                emitter.completeWithError(e);
                            }
                        },
                        error -> {
                            try {
                                LoggerUtil.error("流式执行出错", error);
                                emitter.send(SseEmitter.event()
                                    .name("error")
                                    .data("❌ 生成答案时出错: " + error.getMessage()));
                                emitter.complete();
                            } catch (IOException e) {
                                emitter.completeWithError(e);
                            }
                        },
                        () -> {
                            try {
                                // 发送完成信号
                                emitter.send(SseEmitter.event().name("complete").data("✅ 任务完成"));
                                emitter.complete();
                            } catch (IOException e) {
                                LoggerUtil.error("发送完成信号失败", e);
                                emitter.completeWithError(e);
                            }
                        }
                    );
                    
                } catch (Exception e) {
                    LoggerUtil.error("流式执行异常", e);
                    try {
                        emitter.send(SseEmitter.event()
                            .name("error")
                            .data("❌ 执行异常: " + e.getMessage()));
                        emitter.complete();
                    } catch (IOException ioException) {
                        emitter.completeWithError(ioException);
                    }
                }
            }).start();

        } catch (Exception e) {
            LoggerUtil.error("创建流式执行失败", e);
            try {
                emitter.send(SseEmitter.event()
                    .name("error")
                    .data("❌ 创建流式执行失败: " + e.getMessage()));
                emitter.complete();
            } catch (IOException ioException) {
                emitter.completeWithError(ioException);
            }
        }

        return emitter;
    }

    /**
     * 简化的流式输出接口 - 使用Flux直接返回
     */
    @GetMapping(value = "/solve-flux", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> solveCodingTaskFlux(@RequestParam("task") String task) {
        if (StringUtils.isAllBlank(task)) {
            return Flux.just("错误: 任务内容不能为空");
        }

        try {
            // 创建执行上下文
            ExecutionContext context = new ExecutionContext();
            context.setUserRequest(task);

            String planId = planIdDispatcher.generatePlanId();
            context.setCurrentPlanId(planId);
            context.setNeedSummary(true);
            
            if (rootPlanId == null) {
                rootPlanId = planId;
            }
            context.setRootPlanId(rootPlanId);

            if (StringUtils.isEmpty(memoryId)) {
                memoryId = RandomStringUtils.randomAlphabetic(8);
            }
            context.setMemoryId(memoryId);

            // 保存内存
            memoryService.saveMemory(new MemoryVo(context.getMemoryId(), task));

            // 获取原有的PlanningCoordinator来获取组件
            PlanningCoordinator originalCoordinator = planningFactory.createPlanningCoordinator(context);
            
            // 创建流式计划协调器（通过反射获取私有组件）
            StreamingPlanningCoordinator streamingCoordinator = createStreamingCoordinator(originalCoordinator);

            // 添加简单的监听器来记录思考过程
            context.addListener(new ExecutionListener() {
                @Override
                public void onStatusChange(ExecutionContext context, String status) {
                    LoggerUtil.info("状态: {0}", status);
                }

                @Override
                public void onStepStart(ExecutionStep step) {
                    LoggerUtil.info("开始步骤: {0}", step.getStepRequirement());
                }

                @Override
                public void onStepComplete(ExecutionStep step, String result) {
                    LoggerUtil.info("完成步骤: {0}", step.getStepRequirement());
                }
            });

            // 执行并返回流式总结
            return streamingCoordinator.executePlanWithSummaryStream(context)
                .startWith("🤖 开始处理任务...\n")
                .concatWithValues("\n\n✅ 任务完成!");

        } catch (Exception e) {
            LoggerUtil.error("Flux流式执行失败", e);
            return Flux.just("❌ 执行失败: " + e.getMessage());
        }
    }
}
