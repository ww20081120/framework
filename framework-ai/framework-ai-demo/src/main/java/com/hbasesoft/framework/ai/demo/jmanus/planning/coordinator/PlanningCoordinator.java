package com.hbasesoft.framework.ai.demo.jmanus.planning.coordinator;

import com.hbasesoft.framework.ai.demo.jmanus.planning.coordinator.creator.PlanCreator;
import com.hbasesoft.framework.ai.demo.jmanus.planning.coordinator.executor.PlanExecutor;
import com.hbasesoft.framework.ai.demo.jmanus.planning.coordinator.finalizer.PlanFinalizer;
import com.hbasesoft.framework.ai.demo.jmanus.planning.model.vo.ExecutionContext;

/**
 * 计划流程的总协调器 负责协调计划的创建、执行和总结三个主要步骤
 */
public class PlanningCoordinator {

    /**
     * 计划创建者
     */
    private final PlanCreator planCreator;

    /**
     * 计划执行者
     */
    private final PlanExecutor planExecutor;

    /**
     * 计划总结者
     */
    private final PlanFinalizer planFinalizer;

    /**
     * 构造函数
     *
     * @param planCreator   计划创建者
     * @param planExecutor  计划执行者
     * @param planFinalizer 计划总结者
     */
    public PlanningCoordinator(PlanCreator planCreator, PlanExecutor planExecutor, PlanFinalizer planFinalizer) {
        this.planCreator = planCreator;
        this.planExecutor = planExecutor;
        this.planFinalizer = planFinalizer;
    }

    /**
     * 仅创建计划，不执行
     *
     * @param context 执行上下文
     * @return 执行上下文
     */
    public ExecutionContext createPlan(ExecutionContext context) {
        // 只执行创建计划步骤
        context.setUserMemory(false);
        planCreator.createPlan(context);
        return context;
    }

    /**
     * 执行完整的计划流程
     *
     * @param context 执行上下文
     * @return 执行总结
     */
    public ExecutionContext executePlan(ExecutionContext context) {
        context.setUserMemory(true);

        // 1.创建计划
        planCreator.createPlan(context);

        // 2.执行计划
        planExecutor.executeAllSteps(context);

        // 3.生成总结
        planFinalizer.generateSummary(context);

        return context;
    }

    /**
     * 执行已有计划（跳过创建计划步骤）
     *
     * @param context 包含现有计划的执行上下文
     * @return 执行总结
     */
    public ExecutionContext executeExistingPlan(ExecutionContext context) {
        // 1.执行计划
        planExecutor.executeAllSteps(context);

        // 2.生成总结
        planFinalizer.generateSummary(context);

        return context;
    }

}
