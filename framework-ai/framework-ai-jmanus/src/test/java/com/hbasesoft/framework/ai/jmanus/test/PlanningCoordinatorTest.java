package com.hbasesoft.framework.ai.jmanus.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import com.hbasesoft.framework.ai.jmanus.dynamic.agent.model.po.DynamicAgentPo;
import com.hbasesoft.framework.ai.jmanus.llm.LlmService;
import com.hbasesoft.framework.ai.jmanus.planning.coordinator.creator.PlanCreator;
import com.hbasesoft.framework.ai.jmanus.planning.model.vo.ExecutionContext;
import com.hbasesoft.framework.ai.jmanus.tool.PlanningTool;
import com.hbasesoft.framework.common.utils.CommonUtil;

@SpringBootTest(classes = TestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PlanningCoordinatorTest {

    @Autowired
    @Qualifier("dashscopeChatModel")
    private ChatModel dashscopeChatModel;

    private LlmService llmService;

    private PlanCreator planCreator;

    @BeforeEach
    public void init() {

        llmService = new LlmService(dashscopeChatModel);

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
        agent.setAgentName("产品经理");
        agent.setAgentDescription("负责设计需求，对客户对需求进行细化");
        agent.setNextStepPrompt("对客户的需求进行细化，与客户沟通，最终输出可供开发、测试使用的需求文档");
        agents.add(agent);

        agent = new DynamicAgentPo();
        agent.setId(3L);
        agent.setAgentName("后台开发");
        agent.setAgentDescription("负责接口开发, 技术栈java、spring boot、mysql、jpa等");
        agent.setNextStepPrompt("请根据需求，生成具体的开发计划，根据开发计划具体的执行");
        agents.add(agent);

        agent = new DynamicAgentPo();
        agent.setId(4L);
        agent.setAgentName("前端开发");
        agent.setAgentDescription("负责前端页面开发，技术栈js、react、ant design、ant design pro等");
        agent.setNextStepPrompt("请根据需求，开发页面，并且和后台开发进行联调");
        agents.add(agent);

        agent = new DynamicAgentPo();
        agent.setId(5L);
        agent.setAgentName("测试人员");
        agent.setAgentDescription("负责对需求的测试");
        agent.setNextStepPrompt("请根据产品提的需求，对开发出来的产品进行测试");
        agents.add(agent);

        planCreator = new PlanCreator(agents, llmService, new PlanningTool(), null);
    }

    @Test
    public void testCreatePlan_ShouldSetUserMemoryFalseAndCallPlanCreator() {
        // 准备测试数据
        ExecutionContext context = new ExecutionContext();
        context.setUserMemory(true); // 初始值设为 true
        context.setUserRequest("我需要开发一个ERP");
        context.setPlanId(CommonUtil.getTransactionID());
        planCreator.createPlan(context);

    }
}
