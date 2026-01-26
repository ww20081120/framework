# @Agent 注解使用指南

## 功能说明

`@Agent` 注解提供了一种声明式的方式在 Spring Boot 项目中定义和注册 AI Agent。使用该注解后，Spring 容器会自动将标注的类转换为 AgentScope 的 `ReActAgent` 实例并注册到容器中。

## 快速开始

### 1. 定义 Agent

```java
package com.example.app.agent;

import com.hbasesoft.framework.ai.agentscope.AgentConfig;
import com.hbasesoft.framework.ai.core.Agent;
import io.agentscope.core.memory.Memory;
import io.agentscope.core.memory.TemporaryMemory;
import io.agentscope.core.model.Model;
import io.agentscope.core.model.OllamaChatModel;

@Agent(
    name = "assistant",
    description = "智能助手",
    systemPrompt = "你是一个有用的AI助手"
)
public class MyAssistant implements AgentConfig {

    @Override
    public Model model() {
        return OllamaChatModel.builder()
            .baseUrl("http://127.0.0.1:11434")
            .modelName("qwen3-coder:30b-a3b-fp16")
            .build();
    }

    @Override
    public Memory memory() {
        return TemporaryMemory.builder().build();
    }

    @Override
    public int maxIters() {
        return 5;
    }
}
```

### 2. 使用 Agent

```java
package com.example.app.service;

import io.agentscope.core.ReActAgent;
import io.agentscope.core.message.Msg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ChatService {

    // 通过名称注入（推荐）
    @Autowired
    @Qualifier("assistant")
    private ReActAgent assistant;

    // 或者通过类名注入
    @Autowired
    @Qualifier("myAssistant")
    private ReActAgent myAssistant2;

    public String chat(String userInput) {
        Msg message = Msg.builder()
            .name("user")
            .textContent(userInput)
            .build();

        Mono<Msg> response = assistant.call(message);
        return response.block().getTextContent();
    }
}
```

## 注解属性说明

### @Agent 注解属性

| 属性 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| name | String | 是 | - | Agent名称，也是Spring Bean的名称 |
| description | String | 是 | - | Agent描述信息 |
| systemPrompt | String | 否 | "" | 系统提示词 |
| model | String | 否 | "" | 模型配置（字符串形式，优先级低于AgentConfig.model()） |
| memery | String | 否 | - | 记忆配置（注意拼写：memory） |
| hooks | String | 否 | - | 钩子配置 |
| tools | String[] | 否 | {} | 工具列表 |

### AgentConfig 接口方法

AgentConfig 接口提供了丰富的配置选项，以下是所有可配置的方法：

#### 基本配置

| 方法 | 返回类型 | 必填 | 说明 |
|------|----------|------|------|
| name() | String | 否 | Agent名称（注解值优先） |
| description() | String | 否 | Agent描述（注解值优先） |
| systemPrompt() | String | 否 | 系统提示词（注解值优先） |
| model() | Model | 是 | 语言模型配置 |
| memory() | Memory | 是 | 记忆组件 |
| maxIters() | int | 否 | 最大迭代次数，默认10 |

#### 执行配置

| 方法 | 返回类型 | 说明 |
|------|----------|------|
| hooks() | List\<Hook\> | 钩子列表 |
| enableMetaTool() | boolean | 是否启用元工具 |
| modelExecutionConfig() | ExecutionConfig | 模型执行配置 |
| toolExecutionConfig() | ExecutionConfig | 工具执行配置 |
| structuredOutputReminder() | StructuredOutputReminder | 结构化输出提醒器 |

#### 高级功能

| 方法 | 返回类型 | 说明 |
|------|----------|------|
| planNotebook() | PlanNotebook | 计划笔记本 |
| skillBox() | SkillBox | 技能箱 |
| longTermMemory() | LongTermMemory | 长期记忆 |
| longTermMemoryMode() | LongTermMemoryMode | 长期记忆模式 |
| statePersistence() | StatePersistence | 状态持久化配置 |
| enablePlan() | boolean | 是否启用计划功能 |

#### RAG 功能

| 方法 | 返回类型 | 说明 |
|------|----------|------|
| knowledges() | List\<Knowledge\> | 知识库列表 |
| ragMode() | RAGMode | RAG模式 |
| retrieveConfig() | RetrieveConfig | 检索配置 |

#### 上下文配置

| 方法 | 返回类型 | 说明 |
|------|----------|------|
| toolExecutionContext() | ToolExecutionContext | 工具执行上下文 |

## 配置优先级

配置值的应用优先级从高到低：

1. **@Agent 注解值**（仅适用于 name, description, systemPrompt）
2. **AgentConfig 接口实现**
3. **ReActAgent 默认值**

## 完整示例

### 带钩子和工具的 Agent

```java
@Agent(name = "coder", description = "代码助手")
public class CoderAgent implements AgentConfig {

    @Override
    public Model model() {
        return OllamaChatModel.builder()
            .baseUrl("http://127.0.0.1:11434")
            .modelName("qwen3-coder:30b-a3b-fp16")
            .build();
    }

    @Override
    public Memory memory() {
        return TemporaryMemory.builder().build();
    }

    @Override
    public List<Hook> hooks() {
        return Arrays.asList(
            ModelCallLimitHook.builder()
                .runLimit(100)
                .build()
        );
    }

    @Override
    public boolean enableMetaTool() {
        return true;
    }

    @Override
    public int maxIters() {
        return 8;
    }
}
```

### 带 RAG 功能的 Agent

```java
@Agent(name = "knowledgeAgent", description = "知识库助手")
public class KnowledgeAgent implements AgentConfig {

    @Override
    public Model model() {
        return OllamaChatModel.builder()
            .baseUrl("http://127.0.0.1:11434")
            .modelName("qwen3-coder:30b-a3b-fp16")
            .build();
    }

    @Override
    public Memory memory() {
        return TemporaryMemory.builder().build();
    }

    @Override
    public List<Knowledge> knowledges() {
        // 配置知识库
        return Arrays.asList(
            Knowledge.builder()
                .knowledgeType(KnowledgeType.KNOWLEDGE_DB)
                .config(...)
                .build()
        );
    }

    @Override
    public RAGMode ragMode() {
        return RAGMode.COMPONENT;
    }

    @Override
    public RetrieveConfig retrieveConfig() {
        return RetrieveConfig.builder()
            .topK(5)
            .scoreThreshold(0.7)
            .build();
    }
}
```

## Bean 命名规则

Agent 在 Spring 容器中的 Bean 名称按以下规则确定：

1. **优先使用** `@Agent.name()` 指定的名称
2. **如果未指定**，使用类名首字母小写（如 `MyAssistant` → `myAssistant`）

### 注入示例

```java
// 方式1：使用 @Agent.name() 指定的名称（推荐）
@Autowired
@Qualifier("assistant")
private ReActAgent assistant;

// 方式2：使用类名首字母小写
@Autowired
@Qualifier("myAssistant")
private ReActAgent myAssistant;
```

## 注意事项

1. **必须实现 AgentConfig 接口**：使用 `@Agent` 注解的类必须实现 `AgentConfig` 接口，否则会被忽略并记录警告日志。

2. **避免循环依赖**：在 `AgentConfig` 实现中不要注入其他 Agent，避免循环依赖问题。

3. **Bean 作用域**：Agent 默认为单例，创建过程较为重量级，请确保配置正确。

4. **模型配置**：`model()` 方法必须返回有效的 Model 实例，建议通过 `@Bean` 方法配置并注入。

5. **启动日志**：应用启动时会输出详细的 Agent 扫描和注册日志，请关注日志以确认配置正确。

### 日志示例

```
**************************************************************

***********************Agent扫描开始***************************

**************************************************************

扫描包路径: [com.example.app]

发现Agent类: com.example.app.agent.MyAssistant

成功注册Agent: Bean名称=assistant, 类=MyAssistant

**************************************************************

***********************Agent扫描完成***************************

**************************************************************

开始构建Agent: com.example.app.agent.MyAssistant

设置Agent名称: assistant

设置模型: OllamaChatModel

Agent构建完成: 类型=ReActAgent

```

## 故障排查

### Agent 未注册到容器

**原因**：类未实现 `AgentConfig` 接口

**解决方案**：确保类实现了 `AgentConfig` 接口

### Agent 构建失败

**原因**：配置方法返回 null 或抛出异常

**解决方案**：检查日志中的错误堆栈，确保所有必需的配置方法返回有效值

### Bean 注入失败

**原因**：Bean 名称不匹配

**解决方案**：使用 `@Qualifier` 指定正确的 Bean 名称，或检查 `@Agent.name()` 的值

## 技术架构

实现基于 Spring Boot 自动配置机制：

- **AgentDefinitionProcessor**：扫描 @Agent 注解并注册 Bean 定义
- **AgentFactoryBean**：负责创建 ReActAgent 实例
- **AgentBuilder**：合并注解和接口配置，构建 Agent
- **AgentAutoConfiguration**：自动配置入口

更多信息请参考源码：
- `com.hbasesoft.framework.ai.agentscope.spring`
