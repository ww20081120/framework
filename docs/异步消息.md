### 特性

* API简单学习成本低
* 支持多种消息中间件，适应性好
* 支持Topic级别的线程隔离
* 支持点对点消息，也支持广播消息
* 支持延迟消息（测试版本）

### 快速上手

1、在pom.xml中引入framework-message-redis模块

```
<dependency>
	<groupId>com.hbasesoft.framework</groupId>
	<artifactId>framework-message-redis</artifactId>
	<version>${project.parent.version}</version>
</dependency>
```

2、在application.yml中配置消息中间件

```
message: #消息服务
  model: REDIS
  redis:
   address: 127.0.0.1:6379
```

### Event消息

Event消息是一种基于发布-订阅模式的事件通知机制，支持类型安全的事件传递。

#### 核心接口

##### EventListener<T>
> 完整路径：`com.hbasesoft.framework.message.core.event.EventListener`

事件监听器接口，用于接收和处理事件消息。

```java
/**
 * 事件监听器接口
 *
 * @param <T> 事件数据类型
 */
public interface EventListener<T> extends MessageSubscriber {

    /**
     * 返回监听的事件名称数组
     * @return 事件名称数组
     */
    String[] events();

    /**
     * 接收到事件后的处理方法
     * @param event 事件名称
     * @param data 事件数据
     */
    void onEmmit(String event, T data);
}
```

##### EventData<T>
> 完整路径：`com.hbasesoft.framework.message.core.event.EventData`

事件数据封装类，包含追踪ID和消息ID。

```java
/**
 * 事件数据封装类
 *
 * @param <T> 数据类型
 */
@Data
public class EventData<T> implements Serializable {
    private final String tracerId;  // 追踪ID
    private final String msgId;     // 消息ID
    private T data;                 // 实际数据

    public EventData() {
        this.msgId = CommonUtil.getTransactionID();
        this.tracerId = TraceLogUtil.getTraceId();
    }

    public EventData(final T data) {
        this.msgId = CommonUtil.getTransactionID();
        this.tracerId = TraceLogUtil.getTraceId();
        this.data = data;
    }
}
```

##### EventEmmiter
> 完整路径：`com.hbasesoft.framework.message.core.event.EventEmmiter`

事件发送器，提供静态方法发送事件消息。

**方法列表**：

| 方法名 | 参数 | 说明 |
|-------|------|------|
| emmit(String event) | event - 事件名称 | 发送无数据事件 |
| emmit(String event, Object data) | event - 事件名称, data - 事件数据 | 发送带数据事件 |
| emmit(String event, Object data, int seconds) | event - 事件名称, data - 事件数据, seconds - 延迟秒数 | 发送延迟事件 |
| emmit(String event, Object data, String produceModel) | event - 事件名称, data - 事件数据, produceModel - 生产模式 | 发送指定模式事件 |

#### 使用示例

##### 1、消费者实现

> 文件路径：`framework-message/framework-message-demo/src/main/java/com/hbasesoft/framework/message/demo/event/TestEventHandler.java:24`

```java
import org.springframework.stereotype.Service;
import com.hbasesoft.framework.message.core.event.EventListener;

/**
 * 测试事件处理器
 * 监听 String 类型的事件数据
 */
@Service
public class TestEventHandler implements EventListener<String> {

    @Override
    public String[] events() {
        // 监听的Topic
        return new String[] {
            "testEvent"
        };
    }

    @Override
    public void onEmmit(final String event, final String data) {
        // 接收到消息后的业务处理
        System.out.println("接收到事件: " + event + ", 数据: " + data);
    }
}
```

**关键点**：
- 实现 `EventListener<T>` 接口，泛型 `T` 指定事件数据类型
- 使用 `@Service` 注解将监听器注入到 Spring 容器
- `events()` 方法返回监听的事件名称数组
- `onEmmit()` 方法处理接收到的事件

##### 2、生产者发送事件

> 文件路径：`framework-message/framework-message-demo/src/main/java/com/hbasesoft/framework/message/demo/event/TestEventHandler.java:72`

```java
import com.hbasesoft.framework.message.core.event.EventEmmiter;

public class EventProducer {
    private static final int NUM_10000 = 10000;

    public static void main(final String[] args) {
        // 发送 String 类型的事件数据
        for (int i = 0; i < NUM_10000; i++) {
            EventEmmiter.emmit("testEvent", "value" + i);
        }
    }
}
```

**关键点**：
- 使用 `EventEmmiter.emmit()` 静态方法发送事件
- 第一个参数是事件名称，对应监听器的 `events()` 返回值
- 第二个参数是事件数据，类型需与监听器的泛型类型一致

##### 3、发送自定义对象事件

```java
import com.hbasesoft.framework.message.core.event.EventEmmiter;
import com.hbasesoft.framework.message.core.event.EventListener;
import org.springframework.stereotype.Service;

// 定义事件数据类
class UserData {
    private String userId;
    private String userName;
    // getter/setter 省略
}

// 消费者：监听 UserData 类型事件
@Service
public class UserEventHandler implements EventListener<UserData> {

    @Override
    public String[] events() {
        return new String[] { "userCreated" };
    }

    @Override
    public void onEmmit(String event, UserData data) {
        System.out.println("用户创建: " + data.getUserName());
    }
}

// 生产者：发送 UserData 对象
public class UserService {
    public void createUser(UserData user) {
        // 保存用户到数据库...
        // 发送用户创建事件
        EventEmmiter.emmit("userCreated", user);
    }
}
```

### 消息的API

1. 使用com.hbasesoft.framework.message.core.MessageHelper.createMessageSubcriberFactory()来注册消费者， 消费者继承MessageSubscriber
2. 使用com.hbasesoft.framework.message.core.MessageHelper.createMessagePublisher()来创建生产者

### 配置参数说明
