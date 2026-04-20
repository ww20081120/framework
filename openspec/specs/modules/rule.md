### 特性

* 规则配置简单，上手容易
* 屏蔽复杂的数据库，打造一套轻量级的流程引擎
* 支持基于状态机的工作流
* 扩展性高，插件丰富

### 快速上手

1、 项目的pom.xml中引入maven配置

```
<dependency>
	<groupId>com.hbasesoft.framework</groupId>
	<artifactId>framework-rule-file</artifactId>
	<version>${project.parent.version}</version>
</dependency>
```

2、编写规则文件,并存放在src/resources/META-INF/rules/目录下

> demo.json

```
{
    "name": "demo", 
    "version": "1.0",
    "children": [
        {
            "component": "Child01Component"
        },
        {
            "component": "Child02Component"
        },
        {
            "component": "Child03Component"
        }
    ]
}
```

3、流程组件继承 `com.hbasesoft.framework.rule.core.FlowComponent<T>`，并注入Spring容器

> Child01Component.java

```java
import com.hbasesoft.framework.rule.core.FlowComponent;
import com.hbasesoft.framework.rule.core.FlowContext;
import org.springframework.stereotype.Component;

@Component("Child01Component")
public class Child01Component implements FlowComponent<FlowBean> {

    @Override
    public boolean process(final FlowBean flowBean, final FlowContext flowContext) throws Exception {
        System.out.println("---------Child01Component----------");
        return true;
    }

}
```

**说明**：
- `FlowComponent<T>` 是泛型接口，`T` 是流程Bean的类型，必须实现 `java.io.Serializable` 接口
- `process()` 方法返回 `true` 表示继续执行子流程，返回 `false` 表示跳过子流程

4、调用 `com.hbasesoft.framework.rule.core.FlowHelper.flowStart()` 方法启动流程

> TestFlow.java

```java
import com.hbasesoft.framework.rule.core.FlowBean;
import com.hbasesoft.framework.rule.core.FlowHelper;
import org.junit.Test;

@Test
public void flowStart() {
    FlowBean flowBean = new FlowBean();
    FlowHelper.flowStart(flowBean, "demo");
}
```

**其他启动方式**：
```java
// 从JSONObject对象启动（适用于规则存储在数据库）
ErrorCode result = FlowHelper.flowStart(flowBean, jsonObject);

// 带异常抛出的启动
ErrorCode result = FlowHelper.flowStart(flowBean, "demo", true);
```
### 插件使用

规则流程可以自定义各种插件，`framework-rule-core` 包中已经包含 3 个内置插件：

- **CodeMatchInterceptor**：匹配 code 的插件，匹配到继续执行，匹配不到跳过
- **ConditionInterceptor**：条件判断插件，使用 Spring 的 `SpelExpressionParser` 实现
- **ToolsInterceptor**：在规则文件中可以使用各种工具类插件

#### 1. 注入插件到 Spring 容器

> ConfigBean.java

```java
import com.hbasesoft.framework.rule.core.plugin.CodeMatchInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigBean {

    /** 注入Code Match插件 */
    @Bean
    public CodeMatchInterceptor codeMatchInterceptor() {
        return new CodeMatchInterceptor("code", "type");
    }

    // 可以注入更多插件...
}
```

#### 2. 在规则文件中使用插件

> demo.json

```
{
    "name": "demo", 
    "version": "1.0",
    "children": [
        {
            "code": "1001"
            "component": "Child01Component"
        },
        {
            "code": "1002",
            "type": "A",
            "component": "Child02Component"
        },
        {
            "component": "Child03Component"
        }
    ]
}
```

另外框架中还提供了framework-rule-plugin-event、framework-rule-plugin-transaction、framework-rule-plugin-stateMachine插件模块

### 插件的编写

插件需要实现 `com.hbasesoft.framework.rule.core.FlowComponentInterceptor` 接口

> FlowComponentInterceptor.java
> 路径：framework-rule/framework-rule-core/src/main/java/com/hbasesoft/framework/rule/core/FlowComponentInterceptor.java

```java
import java.io.Serializable;

public interface FlowComponentInterceptor extends Comparable<FlowComponentInterceptor> {

    /**
     * 流程执行前扩展
     * @param flowBean 流程Bean
     * @param flowContext 流程上下文
     * @return true表示继续执行，false表示跳过当前流程
     */
    default boolean before(Serializable flowBean, FlowContext flowContext) {
        return true;
    }

    /**
     * 流程执行后扩展
     * @param flowBean 流程Bean
     * @param flowContext 流程上下文
     */
    default void after(Serializable flowBean, FlowContext flowContext) {
    }

    /**
     * 流程执行失败的扩展
     * @param e 异常对象
     * @param flowBean 流程Bean
     * @param flowContext 流程上下文
     */
    default void error(Exception e, Serializable flowBean, FlowContext flowContext) {
    }

}
```

**实现建议**：
- 建议继承 `com.hbasesoft.framework.rule.core.AbstractFlowCompnentInterceptor` 抽象类
- 通过 `setOrder()` 方法设置拦截器执行顺序（值越小越先执行）
- 拦截器需要注册为Spring Bean才能生效

### 基于状态机的工作流

1、引入framework-rule-plugin-stateMachine 状态机模块

```
<dependency>
	<groupId>com.hbasesoft.framework</groupId>
	<artifactId>framework-rule-plugin-stateMachine</artifactId>
	<version>${project.parent.version}</version>
</dependency>
```

2、在规则文件上增加stateMachine属性的配置，注意stateMachine可以放在整个规则文件的任何节点上，并不一定要至于最顶层。

> stateMachine.json

```
{
    "name": "stateMachine",
    "version": "1.0",
    "begin": "B",      // 状态机启动时会分配一个初使状态  （必填）
    "end": "D,E",     // 当流程Bean状态为D、E两种时，状态机就会停止，不在执行 （必填）
    "error": "E",      // 当流程里面未定义错误状态，统一状态为E （必填）
    "stateMachine": {
        "A": [{          // 当状态为A时    （必填）
                "event": "e1",    // 当进行e1操作时   （必填）
                "end": "C",       // 成功后状态为C     （必填）
                "children": [{
                        "component": "Child02Component",
                        "children": [{
                            "component": "DemoComponent"
                        }]
                    },
                    {
                        "component": "Child01Component",
                        "children": [{
                            "component": "Child03Component"
                        }]
                    }
                ]
            },
            {
                "event": "e2",     // 当进行e2操作时 （必填）
                "end": "D",         // 成功后状态为D     （必填）
                "error": "1001=C, 1002=D, *=E",    // 错误时，对应1001错误码，状态为转向C；1002错误码，状态会转向D，其他错误转向为E      （选填）
                "component": "Child02Component"
            }
        ],
        "B": [{            // 当状态为B时     （必填）
            "event": "e1",     // 可以执行e1操作     （必填）
            "end": "A",       // 成功后状态为A        （必填）
            "component": "Child02Component"
        }], 
        "C": [{        // 当状态为C时     （必填）
            "event": "e1",     //可以执行e1操作     （必填）
            "end": "D",    //  成功后状态为D       （必填）
            "component": "Child01Component"
        }]
    }
}

```

3、流程Bean必须实现 `com.hbasesoft.rule.plugin.statemachine.StateMachineFlowBean` 接口

> StateMachineFlowBean.java
> 路径：framework-rule/framework-rule-plugin-stateMachine/src/main/java/com/hbasesoft/rule/plugin/statemachine/StateMachineFlowBean.java

```java
import java.io.Serializable;

public interface StateMachineFlowBean extends Serializable {
    /**
     * 获取操作事件
     * @return 事件名称，如 "e1", "e2"
     */
    String getEvent();

    /**
     * 获取当前状态
     * @return 当前状态，如 "A", "B", "C"
     */
    String getState();

    /**
     * 设置状态（执行完成后调用）
     * @param state 新状态
     */
    void setState(String state);
}
```

案例：

> TestFlow.java

```
@Test
public void testStateMachineBean() {
    TestStateMachineBean bean = new TestStateMachineBean();
    bean.setEvent("e1");
    System.out.println(JSONObject.toJSONString(bean));
    int result = FlowHelper.flowStart(bean, "stateMachine");
    System.out.println(result);
    System.out.println(JSONObject.toJSONString(bean));
    result = FlowHelper.flowStart(bean, "stateMachine");
    System.out.println(result);
    System.out.println(JSONObject.toJSONString(bean));
    result = FlowHelper.flowStart(bean, "stateMachine");
    System.out.println(result);
    System.out.println(JSONObject.toJSONString(bean));
}
```

### 核心API参考

#### FlowComponent<T>
> 路径：framework-rule/framework-rule-core/src/main/java/com/hbasesoft/framework/rule/core/FlowComponent.java

```java
public interface FlowComponent<T> {
    /**
     * 处理流程逻辑
     * @param flowBean 流程Bean
     * @param flowContext 流程上下文
     * @return true继续执行子流程，false跳过子流程
     */
    boolean process(T flowBean, FlowContext flowContext) throws Exception;

    /**
     * 流程完成后回调（无论成功或失败）
     * @param flowBean 流程Bean
     * @param flowContext 流程上下文
     * @param e 异常对象（成功时为null）
     */
    default void afterProcess(T flowBean, FlowContext flowContext, Exception e) throws Exception {
    }
}
```

#### FlowContext
流程上下文对象，用于在组件间传递数据和获取配置信息。

**主要方法**：
- `setAttribute(String key, Object value)` - 设置属性
- `<T> T getAttribute(String key)` - 获取属性
- `getFlowConfig()` - 获取流程配置
- `getParamMap()` - 获取参数Map
- `getExtendUtils()` - 获取扩展工具Map

#### FlowHelper
> 路径：framework-rule/framework-rule-core/src/main/java/com/hbasesoft/framework/rule/core/FlowHelper.java

**静态方法**：
```java
// 从文件启动流程
ErrorCode flowStart(Serializable bean, String flowName);

// 从JSONObject启动流程
ErrorCode flowStart(Serializable bean, JSONObject flowConfig);

// 带异常处理的启动
ErrorCode flowStart(Serializable bean, String flowName, boolean throwable);
```

### 其他说明

* `FlowHelper` 里面提供多种启动方式，规则可以从文件中读取，也可以传入一个 JSON 对象，这样就可以把规则存储在数据库中
* `stateMachine` 并未管理状态的存储，实际场景下大家根据项目需要自己存储状态
* `FlowComponent` 的 `process` 方法包含了一个 `FlowContext` 上下文，里面可以获取规则配置里面的各种属性，为了防止过程参数污染 `flowBean`，多个组件间的消息传递可以通过 `FlowContext` 的 `paramMap` 来实现

### 最佳实践

1. **流程Bean设计**
   - 实现 `Serializable` 接口
   - 保持简单，避免复杂嵌套
   - 包含流程状态和业务数据

2. **组件设计**
   - 单一职责，每个组件只做一件事
   - 通过 `process()` 返回值控制流程
   - 使用 `afterProcess()` 进行资源清理

3. **拦截器使用**
   - 继承 `AbstractFlowCompnentInterceptor` 简化开发
   - 合理设置执行顺序
   - 避免在拦截器中执行耗时操作

4. **状态机配置**
   - 明确定义初始状态和结束状态
   - 合理规划状态转换路径
   - 提供错误处理机制
