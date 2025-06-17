package com.hbasesoft.framework.test.ai.demo;

import com.hbasesoft.framework.ai.demo.AIDemoApplication;
import com.hbasesoft.framework.ai.demo.jmanus.dynamic.agent.model.po.DynamicAgentPo;
import com.hbasesoft.framework.ai.demo.jmanus.llm.LlmService;
import com.hbasesoft.framework.ai.demo.jmanus.planning.coordinator.creator.PlanCreator;
import com.hbasesoft.framework.ai.demo.jmanus.planning.model.vo.ExecutionContext;
import com.hbasesoft.framework.ai.demo.jmanus.tool.PlanningTool;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest(classes = AIDemoApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PlanningCoordinatorTest {

    @Autowired
    private LlmService llmService;

    private PlanCreator planCreator;

    @BeforeEach
    public void init() {
        List<DynamicAgentPo> agents = new ArrayList<>();

        // 创建动态代理
        DynamicAgentPo agent = new DynamicAgentPo();
        agent.setId(1L);
        agent.setAgentName("项目经理");
        agent.setAgentDescription("负责项目计划、进度、成本控制");
        agent.setNextStepPrompt("请根据项目计划，生成下一步的步骤");
        agents.add(agent);

        agent = new DynamicAgentPo();
        agent.setId(2L);
        agent.setAgentName("开发人员");
        agent.setAgentDescription("负责项目开发, java web开发");
        agent.setNextStepPrompt("请根据项目计划，生成下一步的步骤");
        agents.add(agent);

        planCreator = new PlanCreator(agents, llmService, new PlanningTool(), null);

    }

    @Test
    public void testCreatePlan_ShouldSetUserMemoryFalseAndCallPlanCreator() {
        // 准备测试数据
        ExecutionContext context = new ExecutionContext();
        context.setUserMemory(true);  // 初始值设为 true
        context.setUserRequest("我需要开发一个ERP");
        planCreator.createPlan(context);

    }
}
