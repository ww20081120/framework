### 简介

实际生产过程中因为硬件故障、网络故障、依赖第三方系统故障给我们带来了很多麻烦。原来的重试补偿功能都是写在各业务模块，增加了很多额外的开发工作量，在加上开发人员水平也不一致也很难全面的考虑各种稳定性问题，所以研发出该模块用于解决微服务业务模块不稳定问题。因业务要求，不允许出现失败回滚场景，该模块只实现了事务补偿。 原理：通过N次重试，跳过执行成功的部分，一直重试失败部分，来达到业务最终执行完成。（N次失败后可以通知人工来进行解决）。

实际场景举例： 用户购买了商品，当微信支付成功后，突然订单模块数据库宕机了。 当数据库修复后，之前丢失的订单能正确处理。

### 特性

1. 支持同步消息与异步消息  
2. 提供注解方法，使用简单，学习成本低 
3. 任何需要重试的内容都可以使用，适应性强。  

### 快速上手

分布式事务算是框架里面比较重的一个模块了，新的模式需要引用一个客户端，然后配置job和存储。

1、在pom.xml中引入依赖模块

```
<!-- 客户端事务集成模块-->
<dependency>
	<groupId>com.hbasesoft.framework</groupId>
	<artifactId>framework-tx-integration</artifactId>
	<version>${project.parent.version}</version>
</dependency>

<!-- 客户端消息的存储-->
<dependency>
	<groupId>com.hbasesoft.framework</groupId>
	<artifactId>framework-tx-server-storage-db</artifactId>
	<version>${project.parent.version}</version>
</dependency>
```

2、修改application.yml中配置

```
project: #项目信息
 name: tx-demo-client
    
server.port: 8939
 
spring: #应用配置
  application:
    name: ${project.name} 
  datasource: #使用数据库作为事务消息的存储
    url: jdbc:mysql://localhost:3306/tx_storage?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai
    username: root
    password: root
    driver-class-name: com.mysql.cj.jdbc.Driver
    
job:
  register:
    url: localhost:2181
    namespace: ${project.name}
  event:
    enable: false
```

3、在服务的入口增加 `@Tx` 注解，调用远程接口的地方用 `TxInvokerProxy.invoke(String, TxInvoker<String>)` 方法进行包裹，这样就可以保证重试的时候跳过已经执行的代码

> 文件路径：`framework-tx/framework-tx-demo/framework-tx-demo-client/src/main/java/com/hbasesoft/framework/tx/demo/client/TestProducter.java:55`

```java
import com.hbasesoft.framework.tx.core.TxInvokerProxy;
import com.hbasesoft.framework.tx.core.annotation.Tx;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@GetMapping
@Tx  // 分布式事务注解，支持maxRetryTimes重试次数（默认5次）和retryConfigs重试配置
public synchronized String test(final @RequestParam("id") String id) {

   // feClient2Consumer.test为远程方法，如果client2已经执行，下次重试会跳过该方法
    String value1 = TxInvokerProxy.invoke("client2", () -> {
        return feClient2Consumer.test(id);
    });
    System.out.println(value1);

   // feClient3Consumer.test为远程方法
    String value2 = TxInvokerProxy.invoke("client3", () -> {
        return feClient3Consumer.test(id);
    });
    System.out.println(value2);

 // 创造随机失败场景
    if (new Random().nextInt(NUM_5) == 1) {
        throw new RuntimeException();
    }
    System.out.println(i++ + ":" + id);

    return new StringBuilder().append(i).append("client1").append(id).append(':').append(value1).append(':')
        .append(value2).toString();
}
```

**关键点**：
- 使用 `@Tx` 注解标记方法支持分布式事务
- 每个远程调用使用 `TxInvokerProxy.invoke()` 包裹，传入唯一标识（如 "client2"）
- 重试时会自动跳过已经成功执行的远程调用

### 异步消息场景

针对异步消息framework-message模块，我们增加一些api来支撑这个场景，使用起来很方便

1、在pom.xml中引入额外的依赖模块

```
<dependency>
	<groupId>com.hbasesoft.framework</groupId>
	<artifactId>framework-message-tx</artifactId>
	<version>${project.parent.version}</version>
</dependency>
```

2、消费者需要实现`com.hbasesoft.framework.message.core.event.EventListener<T>`接口，并使用`@Tx`注解，使用方式与普通EventListener完全一样。

> 文件路径：`framework-tx/framework-tx-demo/framework-tx-demo-client/src/main/java/com/hbasesoft/framework/tx/demo/client/TestEventListener.java:26`

```java
import com.hbasesoft.framework.message.core.event.EventListener;
import com.hbasesoft.framework.tx.core.annotation.Tx;
import org.springframework.stereotype.Component;

@Component
@Tx  // 使用@Tx注解支持分布式事务
public class TestEventListener implements EventListener<String> {
    private static final int NUM_5 = 5;
    private int i = 0;

    @Override
    public String[] events() {
        return new String[] {
            "testEvent"
        };
    }

    @Override
    public void onEmmit(final String event, final String data) {
        // 创造随机失败场景
        if (new Random().nextInt(NUM_5) == 1) {
            throw new RuntimeException();
        }
        System.out.println(i++ + ":" + data);
    }
}
```

**关键点**：
- 实现 `EventListener<T>` 接口，泛型 `T` 指定事件数据类型（如 `String`）
- 使用 `@Component` 注解将监听器注入到 Spring 容器
- 使用 `@Tx` 注解支持分布式事务和重试机制

3、生产者使用 `com.hbasesoft.framework.message.core.event.EventEmmiter.emmit(String)` 发送事件，为了防止重试后消息重复发送，需要用 `TxInvokerProxy.invoke` 方法进行包裹

> 文件路径：`framework-tx/framework-tx-demo/framework-tx-demo-client/src/main/java/com/hbasesoft/framework/tx/demo/client/TestProducter.java:55`

```java
import com.hbasesoft.framework.message.core.event.EventEmmiter;
import com.hbasesoft.framework.tx.core.TxInvokerProxy;
import com.hbasesoft.framework.tx.core.annotation.Tx;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@GetMapping
@Tx  // 分布式事务注解，支持maxRetryTimes重试次数（默认5次）和retryConfigs重试配置
public synchronized String test(final @RequestParam("id") String id) {
    // 包裹后，下次重试不会再次触发事件发送
    TxInvokerProxy.invoke("client1", () -> {
        EventEmmiter.emmit("testEvent");  // 发送事务事件
        return null;
    });
    System.out.println("emmit event");

   // feClient2Consumer.test为远程方法，如果client2已经执行，下次重试会跳过该方法
    String value1 = TxInvokerProxy.invoke("client2", () -> {
        return feClient2Consumer.test(id);
    });
    System.out.println(value1);

   // feClient3Consumer.test为远程方法
    String value2 = TxInvokerProxy.invoke("client3", () -> {
        return feClient3Consumer.test(id);
    });
    System.out.println(value2);

 // 创造随机失败场景
    if (new Random().nextInt(NUM_5) == 1) {
        throw new RuntimeException();
    }
    System.out.println(i++ + ":" + id);

    return new StringBuilder().append(i).append("client1").append(id).append(':').append(value1).append(':')
        .append(value2).toString();
}
```

**关键点**：
- 使用 `@Tx` 注解标记方法支持分布式事务
- 使用 `EventEmmiter.emmit()` 发送事件（不是 TxEventEmmiter）
- 使用 `TxInvokerProxy.invoke()` 包裹事件发送，避免重试时重复发送
- 每个远程调用也需要用 `TxInvokerProxy.invoke()` 包裹，避免重试时重复执行

### 配置参数说明
