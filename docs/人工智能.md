### 简介

Framework AI模块是基于AgentScope和Spring AI构建的AI功能集成模块。它提供了强大的Agent执行框架，支持多种大模型API，并提供了灵活的工具调用和配置能力。

> 相关路径：`framework-ai/pom.xml`

### 模块结构

Framework AI包含以下子模块：

| 模块名称 | 说明 |
|---------|------|
| `framework-ai-core` | 核心模块，定义Agent注解和基础接口 |
| `framework-ai-agentscope` | 基于AgentScope实现的AI Agent框架 |
| `framework-ai-spring` | 基于Spring AI Alibaba实现的AI Agent框架 |
| `framework-ai-demo` | 示例代码，包含agentscope和openai两个实现示例 |

### 特性

1. **多框架支持**：集成AgentScope和Spring AI Alibaba两大框架
2. **注解驱动**：通过`@Agent`注解快速创建AI Agent
3. **工具调用**：支持自定义工具和方法级别的工具注册
4. **灵活配置**：提供`AgentConfig`接口进行详细配置
5. **多模型支持**：支持OpenAI兼容的多种大模型API
6. **记忆管理**：支持对话历史记忆和长期记忆
7. **Hook机制**：提供钩子机制用于监控和拦截Agent执行

### 快速上手

#### 方式一：使用AgentScope

1. 在pom.xml中引入依赖：

```xml
<dependency>
    <groupId>com.hbasesoft.framework</groupId>
    <artifactId>framework-ai-agentscope</artifactId>
    <version>${framework.version}</version>
</dependency>
```

2. 定义Agent类：

```java
import com.hbasesoft.framework.ai.agentscope.AgentConfig;
import com.hbasesoft.framework.ai.core.Agent;
import io.agentscope.core.tool.Tool;
import io.agentscope.core.tool.ToolParam;

@Agent(name = "WeatherAgent", tools = "shellCommandTool")
public class WeatherAgent implements AgentConfig {

    @Override
    public String systemPrompt() {
        return "你是天气查询助手，可以使用weather方法查询天气预报。";
    }

    @Tool(description = "查询天气的工具")
    public String weather(@ToolParam(description = "城市名称") String city) {
        return "今天是晴天";
    }
}
```

3. 配置模型Bean：

```java
@Configuration
public class AiConfig {

    @Bean
    public Model defaultModel() {
        return OpenAIChatModel.builder()
            .baseUrl("http://127.0.0.1:11434")
            .modelName("qwen3-coder:30b-a3b-fp16")
            .build();
    }

    @Bean
    public ShellCommandTool shellCommandTool() {
        return new ShellCommandTool();
    }
}
```

#### 方式二：使用Spring AI

1. 在pom.xml中引入依赖：

```xml
<dependency>
    <groupId>com.hbasesoft.framework</groupId>
    <artifactId>framework-ai-spring</artifactId>
    <version>${framework.version}</version>
</dependency>
```

2. 定义Agent类：

```java
import com.hbasesoft.framework.ai.core.Agent;
import com.hbasesoft.framework.ai.spring.AgentConfig;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

@Agent(name = "WeatherAgent")
public class WeatherAgent implements AgentConfig {

    @Override
    public String systemPrompt() {
        return "你是天气查询助手";
    }

    @Override
    public ChatModel model() {
        OpenAiApi openAiApi = OpenAiApi.builder()
            .baseUrl("http://127.0.0.1:11434")
            .apiKey("your-api-key")
            .build();

        OpenAiChatOptions options = OpenAiChatOptions.builder()
            .model("qwen3-coder:30b-a3b-fp16")
            .temperature(0.7)
            .maxTokens(2000)
            .build();

        return OpenAiChatModel.builder()
            .openAiApi(openAiApi)
            .defaultOptions(options)
            .build();
    }

    @Tool(description = "查询天气的工具")
    public String weather(@ToolParam(description = "城市名称") String city) {
        return "今天是晴天";
    }
}
```

### Agent执行框架

AI模块提供了强大的Agent执行框架，可用于执行复杂的AI任务。该框架基于注解驱动，开发者可以通过简单的类定义和方法注解快速创建功能强大的AI Agent。

#### 核心注解

##### @Agent注解

用于标记和配置智能代理类。

> 完整路径：`com.hbasesoft.framework.ai.core.Agent`

**参数说明**：

| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| `name` | String | 是 | 无 | Agent名称，必须唯一 |
| `description` | String | 否 | "" | Agent描述信息 |
| `systemPrompt` | String | 否 | "" | 系统提示词，定义Agent的角色和行为准则 |
| `model` | String | 否 | "" | 使用的模型名称或Bean名称 |
| `memery` | String | 否 | "" | 记忆配置（注：拼写为memery） |
| `tools` | String[] | 否 | {} | 工具列表，指定Agent可以使用的工具Bean名称 |
| `hooks` | String[] | 否 | {} | 钩子列表，用于监控和拦截Agent执行 |

##### @Tool注解

用于标记Agent类中的工具方法。不同框架有不同的实现：

- **AgentScope**: `io.agentscope.core.tool.Tool`
- **Spring AI**: `org.springframework.ai.tool.annotation.Tool`

**参数说明**：

| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| `description` | String | 是 | 无 | 工具功能描述 |
| `serviceGroup` | String | 否 | 无 | 服务分组名称（仅部分框架支持） |

##### @ToolParam注解

用于标记工具方法的参数，提供参数说明。

- **AgentScope**: `io.agentscope.core.tool.ToolParam`
- **Spring AI**: `org.springframework.ai.tool.annotation.ToolParam`

**参数说明**：

| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| `description` | String | 是 | 无 | 参数说明 |
| `required` | boolean | 否 | true | 是否必填 |
| `name` | String | 否 | 无 | 参数名称（仅AgentScope支持） |

#### AgentConfig接口

提供详细的Agent配置能力，支持两个实现：

1. **AgentScope实现**：`com.hbasesoft.framework.ai.agentscope.AgentConfig`
2. **Spring AI实现**：`com.hbasesoft.framework.ai.spring.AgentConfig`

> 完整路径：
> - `framework-ai-agentscope/src/main/java/com/hbasesoft/framework/ai/agentscope/AgentConfig.java`
> - `framework-ai-spring/src/main/java/com/hbasesoft/framework/ai/spring/AgentConfig.java`

**主要配置方法**（以AgentScope为例）：

| 方法 | 返回类型 | 说明 |
|------|---------|------|
| `systemPrompt()` | String | 系统提示词 |
| `description()` | String | Agent描述 |
| `model()` | Model | 使用的语言模型 |
| `memory()` | Memory | 对话历史记忆 |
| `maxIters()` | int | 最大迭代次数，默认10 |
| `hooks()` | List&lt;Hook&gt; | 钩子列表 |
| `enableMetaTool()` | boolean | 是否启用元工具 |
| `toolExecutionConfig()` | ExecutionConfig | 工具执行配置 |

#### 完整示例

##### 示例1：简单的天气查询Agent

```java
import com.hbasesoft.framework.ai.agentscope.AgentConfig;
import com.hbasesoft.framework.ai.core.Agent;
import io.agentscope.core.tool.Tool;
import io.agentscope.core.tool.ToolParam;

/**
 * 天气查询Agent
 */
@Agent(name = "WeatherAgent", tools = "shellCommandTool")
public class WeatherAgent implements AgentConfig {

    @Override
    public String systemPrompt() {
        return "你是天气查询助手，可以使用weather方法查询天气预报。";
    }

    /**
     * 查询天气的工具方法
     *
     * @param city 城市名称
     * @return 天气信息
     */
    @Tool(description = "查询天气的工具")
    public String weather(@ToolParam(description = "城市名称") String city) {
        // 实际应用中可以调用真实的天气API
        return "今天" + city + "天气晴朗，温度20°C";
    }
}
```

##### 示例2：多功能计算Agent

```java
import com.hbasesoft.framework.ai.agentscope.AgentConfig;
import com.hbasesoft.framework.ai.core.Agent;
import io.agentscope.core.tool.Tool;
import io.agentscope.core.tool.ToolParam;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * 多功能计算Agent
 */
@Agent(name = "CalculatorAgent",
    description = "能进行计算、查询时间和打招呼",
    tools = "shellCommandTool")
public class CalculatorAgent implements AgentConfig {

    @Override
    public String systemPrompt() {
        return "你是一个多功能助手，可以计算、查询时间和打招呼。";
    }

    /**
     * 获取当前日期时间
     */
    @Tool(description = "获取当前日期和时间")
    public String getCurrentDateTime() {
        return LocalDateTime.now().atZone(ZoneId.systemDefault()).toString();
    }

    /**
     * 两数相加
     */
    @Tool(description = "将两个数字相加")
    public double add(
        @ToolParam(description = "第一个数字", required = true) double a,
        @ToolParam(description = "第二个数字", required = true) double b) {
        return a + b;
    }

    /**
     * 打招呼
     */
    @Tool(description = "向指定的人打招呼")
    public String greet(
        @ToolParam(description = "人名", required = true) String name,
        @ToolParam(description = "语言(zh/en)", required = false) String language) {
        if ("zh".equals(language)) {
            return "你好，" + name + "！";
        }
        return "Hello, " + name + "!";
    }
}
```

#### 使用Agent

创建好Agent后，可以通过依赖注入使用：

```java
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;
import io.agentscope.core.ReActAgent;
import io.agentscope.core.message.Msg;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/agent")
public class AgentController {

    @Resource(name = "WeatherAgent")
    private ReActAgent weatherAgent;

    /**
     * 与Agent对话
     */
    @PostMapping("/chat")
    public String chat(@RequestBody String message) {
        try {
            Mono<Msg> response = weatherAgent.call(
                Msg.builder()
                    .name("user")
                    .textContent(message)
                    .build()
            );
            return response.block().getTextContent();
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}
```

### 高级配置

#### 模型配置

支持OpenAI兼容的各种大模型，包括：

- **OpenAI**: GPT-4、GPT-3.5等
- **通义千问**: Qwen系列
- **本地模型**: 通过Ollama等部署的本地模型

**配置示例**：

```java
@Bean
public Model defaultModel() {
    return OpenAIChatModel.builder()
        .baseUrl("http://127.0.0.1:11434")  // 模型服务地址
        .modelName("qwen3-coder:30b-a3b-fp16")  // 模型名称
        .build();
}
```

#### Hook机制

Hook用于监控和拦截Agent的执行过程，可以用于日志记录、性能监控、权限控制等。

```java
import io.agentscope.core.hook.Hook;
import io.agentscope.core.message.Msg;
import java.util.Arrays;
import java.util.List;

@Agent(name = "MonitoredAgent", tools = "shellCommandTool")
public class MonitoredAgent implements AgentConfig {

    @Override
    public String systemPrompt() {
        return "你是一个被监控的Agent";
    }

    @Override
    public List<Hook> hooks() {
        return Arrays.asList(
            new LoggingHook(),  // 自定义日志Hook
            new PerformanceHook()  // 自定义性能Hook
        );
    }

    @Tool(description = "示例工具")
    public String exampleTool(String input) {
        return "处理结果: " + input;
    }
}
```

#### Studio集成

AgentScope支持Studio可视化界面，方便调试和监控Agent执行过程。

```java
import io.agentscope.core.studio.StudioManager;
import io.agentscope.core.studio.StudioMessageHook;
import io.agentscope.core.hook.Hook;
import java.util.Arrays;
import java.util.List;

@Agent(name = "StudioAgent")
public class StudioAgent implements AgentConfig {

    @Override
    public List<Hook> hooks() {
        return Arrays.asList(
            new StudioMessageHook(StudioManager.getClient())
        );
    }
}
```

启动应用后，访问 http://localhost:3000 可以与Agent进行交互。

### 配置参数说明

#### application.yml配置

根据使用的模型和框架，可能需要以下配置：

```yaml
# OpenAI兼容模型配置
spring:
  ai:
    openai:
      base-url: http://127.0.0.1:11434
      api-key: your-api-key

# AgentScope Studio配置（可选）
agentscope:
  studio:
    enabled: true
    port: 3000
```

#### 依赖版本

确保在父pom.xml中配置正确的版本：

```xml
<properties>
    <framework.version>4.2.0</framework.version>
</properties>
```

### 示例项目

完整的示例代码位于：

- **AgentScope示例**: `framework-ai-demo/framework-ai-demo-agentscope`
- **Spring AI示例**: `framework-ai-demo/framework-ai-demo-openai`

包含以下示例：

1. 基本的Agent定义和使用
2. 工具方法的定义和调用
3. 模型配置
4. Studio集成
5. REST API接口

### 最佳实践

1. **命名规范**：Agent名称应简洁明了，反映其功能，如`WeatherAgent`、`CalculatorAgent`
2. **工具设计**：每个工具方法应专注于单一功能
3. **错误处理**：工具方法中应添加适当的异常处理
4. **提示词优化**：systemPrompt应清晰定义Agent的角色和约束
5. **工具粒度**：避免创建过于复杂的工具，保持简单和可测试性
6. **记忆管理**：对于需要上下文的Agent，配置适当的memory组件

### 注意事项

1. **模型兼容性**：确保使用的模型与选择的框架兼容
2. **性能考虑**：大模型API调用可能有延迟，考虑异步处理
3. **安全性**：不要在代码中硬编码API密钥，使用配置文件或环境变量
4. **成本控制**：注意监控API调用量和成本
5. **测试**：在生产环境部署前充分测试Agent的行为
