### 特性

* 分布式调度协调
* 弹性扩容缩容
* 失效转移
* 错过执行作业重触发
* 作业分片一致性，保证同一分片在分布式环境中仅一个执行实例
* 自诊断并修复分布式不稳定造成的问题
* 支持并行调度
* 支持作业生命周期操作
* 丰富的作业类型

### 版本信息

- **当前版本**：Framework 4.2.0
- **兼容性**：Java 8+
- **依赖**：ElasticJob 3.0.1、Zookeeper 3.5+

---

### 快速上手

#### 1. 添加Maven依赖

```xml
<dependency>
    <groupId>com.hbasesoft.framework</groupId>
    <artifactId>framework-job-core</artifactId>
    <version>${project.parent.version}</version>
</dependency>
```

#### 2. 配置Zookeeper

> src/main/resources/application.yml

```yaml
job:
  register:
    url: localhost:2181   # zookeeper的地址
    namespace: framework-job-core # job的命名空间
  event:
    enable: false
```

#### 3. 创建作业类

> framework-job/framework-job-demo/src/main/java/com/hbasesoft/framework/job/core/demo/FooJob.java

```java
import com.alibaba.fastjson2.JSONObject;
import com.hbasesoft.framework.common.utils.date.DateUtil;
import com.hbasesoft.framework.job.core.JobContext;
import com.hbasesoft.framework.job.core.SimpleJob;
import com.hbasesoft.framework.job.core.annotation.Job;

/**
 * 简单作业示例
 *
 * @author 王伟
 * @since V1.0
 */
@Job(cron = "0/5 * * * * ?", enable = "${job.FooJob}")
public class FooJob implements SimpleJob {

    /**
     * 执行作业
     *
     * @param jobContext 作业上下文，包含作业执行信息
     */
    @Override
    public void execute(final JobContext jobContext) {
        System.out.println(DateUtil.getCurrentTimestamp() + JSONObject.toJSONString(jobContext));
    }

}
```

#### 4. 分片作业示例

> framework-job/framework-job-demo/src/main/java/com/hbasesoft/framework/job/core/demo/FooJob2.java

```java
import com.alibaba.fastjson2.JSONObject;
import com.hbasesoft.framework.common.utils.date.DateUtil;
import com.hbasesoft.framework.job.core.JobContext;
import com.hbasesoft.framework.job.core.SimpleJob;
import com.hbasesoft.framework.job.core.annotation.Job;

/**
 * 分片作业示例
 *
 * @author 王伟
 * @since V1.0
 */
@Job(
    cron = "0/10 * * * * ?",
    shardingParam = "Beijing,Shanghai,Guangzhou,Shenzhen",
    name = "DemoShardingJob"
)
public class FooJob2 implements SimpleJob {

    /**
     * 执行作业
     *
     * @param jobContext 作业上下文，包含分片信息
     */
    @Override
    public void execute(final JobContext jobContext) {
        String jobName = jobContext.getJobName();
        int shardingItem = jobContext.getShardingItem();
        String shardingParameter = jobContext.getShardingParameter();

        System.out.println(String.format("时间：%s - 作业：%s - 分片：%d - 分片参数：%s - 任务参数：%s",
            DateUtil.getCurrentTimestamp(),
            jobName,
            shardingItem,
            shardingParameter,
            jobContext.getJobParameter()));

        // 根据分片执行不同的业务逻辑
        processBySharding(shardingItem, shardingParameter);
    }

    /**
     * 根据分片处理业务逻辑
     *
     * @param shardingItem 分片项
     * @param shardingParameter 分片参数
     */
    private void processBySharding(int shardingItem, String shardingParameter) {
        switch (shardingItem) {
            case 0:
                System.out.println("处理北京地区的任务");
                break;
            case 1:
                System.out.println("处理上海地区的任务");
                break;
            case 2:
                System.out.println("处理广州地区的任务");
                break;
            case 3:
                System.out.println("处理深圳地区的任务");
                break;
            default:
                System.out.println("处理其他地区的任务");
        }
    }

}
```

---

### 核心概念

#### 1. @Job注解

`@Job`注解用于标记一个类为作业实现，支持以下参数：

| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| name | String | 否 | "" | 作业名称 |
| cron | String | 是 | - | Cron表达式，定义执行时机 |
| shardingParam | String | 否 | "" | 分片参数，多个参数用逗号分隔 |
| enable | String | 否 | "true" | 是否启用，支持SpEL表达式 |
| streamingProcess | boolean | 否 | false | 是否为流式操作 |

**完整路径**：`com.hbasesoft.framework.job.core.annotation.Job`

##### 使用示例

```java
// 基础用法
@Job(cron = "0/5 * * * * ?")
public class SimpleJob implements SimpleJob {
    // 实现
}

// 带分片的作业
@Job(
    cron = "0/10 * * * * ?",
    shardingParam = "Beijing,Shanghai,Guangzhou",
    enable = "${job.enable}"
)
public class ShardingJob implements SimpleJob {
    // 实现
}

// 流式处理作业
@Job(
    cron = "0 0 2 * * ?",
    streamingProcess = true
)
public class StreamingJob implements SimpleJob {
    // 实现
}
```

#### 2. SimpleJob接口

`SimpleJob`是框架的核心接口，定义了作业的基本执行规范。

**完整路径**：`com.hbasesoft.framework.job.core.SimpleJob`

```java
/**
 * 简单作业接口
 *
 * @author 王伟
 * @since V1.0
 */
public interface SimpleJob {

    /**
     * 执行作业
     *
     * @param jobContext 作业上下文，包含作业执行的所有信息
     */
    void execute(JobContext jobContext);
}
```

#### 3. JobContext类

`JobContext`提供了作业执行时的上下文信息，包含分片、任务等数据。

**完整路径**：`com.hbasesoft.framework.job.core.JobContext`

```java
/**
 * 作业上下文信息
 *
 * @author 王伟
 * @since V1.0
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JobContext {

    /** 任务名称 */
    private String jobName;

    /** 任务Id */
    private String taskId;

    /** 分片数量 */
    private int shardingTotalCount;

    /** 任务参数 */
    private String jobParameter;

    /** 分片对象 */
    private int shardingItem;

    /** 共享参数 */
    private String shardingParameter;
}
```

**JobContext属性说明**：

| 属性名 | 类型 | 说明 |
|--------|------|------|
| jobName | String | 作业名称，默认为类名 |
| taskId | String | 任务ID，唯一标识一次执行 |
| shardingTotalCount | int | 分片总数 |
| jobParameter | String | 作业参数，可在注解中配置 |
| shardingItem | int | 当前分片项，从0开始 |
| shardingParameter | String | 当前分片对应的参数 |

---

### 使用指南

#### 基本用法

1. **创建简单作业**

```java
@Job(cron = "0/30 * * * * ?")  // 每30秒执行一次
public class SimpleDataSyncJob implements SimpleJob {

    @Override
    public void execute(JobContext jobContext) {
        // 执行数据同步逻辑
        syncData();

        // 可通过jobContext获取执行信息
        System.out.println("作业执行完成 - 分片：" + jobContext.getShardingItem());
    }

    private void syncData() {
        // 数据同步实现
    }
}
```

2. **使用作业参数**

```java
@Job(
    cron = "0 0 1 * * ?",  // 每天凌晨1点执行
    jobParameter = "batch_size=1000,timeout=30000"
)
public class BatchReportJob implements SimpleJob {

    @Override
    public void execute(JobContext jobContext) {
        String jobParam = jobContext.getJobParameter();
        // 解析参数
        Map<String, String> params = parseParams(jobParam);

        int batchSize = Integer.parseInt(params.get("batch_size"));
        int timeout = Integer.parseInt(params.get("timeout"));

        // 使用参数执行任务
        generateReport(batchSize, timeout);
    }

    private Map<String, String> parseParams(String param) {
        // 参数解析实现
        return new HashMap<>();
    }

    private void generateReport(int batchSize, int timeout) {
        // 报告生成实现
    }
}
```

#### 高级用法

1. **带分片的作业处理**

```java
@Job(
    cron = "0/5 * * * * ?",
    shardingParam = "north,south,east,west"
)
public class RegionalJob implements SimpleJob {

    @Override
    public void execute(JobContext jobContext) {
        int shardingItem = jobContext.getShardingItem();
        String region = jobContext.getShardingParameter();

        switch (shardingItem) {
            case 0:
                processRegion(region, "北区");
                break;
            case 1:
                processRegion(region, "南区");
                break;
            case 2:
                processRegion(region, "东区");
                break;
            case 3:
                processRegion(region, "西区");
                break;
        }
    }

    private void processRegion(String region, String regionName) {
        // 根据地区处理业务逻辑
        System.out.println("处理" + regionName + "的数据，地区参数：" + region);
    }
}
```

2. **条件控制作业执行**

```java
@Job(
    cron = "0 0 * * * ?",  // 每小时执行
    enable = "${env.production}"  // 仅在生产环境启用
)
public class ScheduledCleanupJob implements SimpleJob {

    @Override
    public void execute(JobContext jobContext) {
        if (shouldCleanup()) {
            cleanupTempFiles();
            cleanupExpiredData();
        }
    }

    private boolean shouldCleanup() {
        // 判断是否需要清理
        return true;
    }

    private void cleanupTempFiles() {
        // 清理临时文件
    }

    private void cleanupExpiredData() {
        // 清理过期数据
    }
}
```

3. **错误处理和重试**

```java
@Job(cron = "0/10 * * * * ?")
public class RetryJob implements SimpleJob {

    private static final int MAX_RETRIES = 3;

    @Override
    public void execute(JobContext jobContext) {
        int retryCount = 0;
        boolean success = false;

        while (retryCount < MAX_RETRIES && !success) {
            try {
                processTask();
                success = true;
            } catch (Exception e) {
                retryCount++;
                System.err.println("任务执行失败，重试次数：" + retryCount);
                if (retryCount >= MAX_RETRIES) {
                    throw new RuntimeException("任务执行失败，已达到最大重试次数", e);
                }
                // 等待一段时间后重试
                try {
                    Thread.sleep(1000 * retryCount);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("重试被中断", ie);
                }
            }
        }
    }

    private void processTask() {
        // 任务处理逻辑
    }
}
```

---

### 配置说明

#### 完整配置项

| 配置项 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| job.register.url | String | - | Zookeeper连接地址 |
| job.register.namespace | String | framework-job-core | 作业命名空间 |
| job.event.enable | boolean | false | 是否启用事件监听 |
| job.streaming.enable | boolean | false | 是否启用流式处理 |

#### 高级配置示例

> src/main/resources/application.yml

```yaml
job:
  # 注册中心配置
  register:
    url: localhost:2181
    namespace: framework-job-core
    session-timeout: 30000
    connection-timeout: 5000
    base-sleep-time-milliseconds: 1000
    max-sleep-time-milliseconds: 3000
    max-retries: 3

  # 事件监听配置
  event:
    enable: true
    job-events:
      - JOB_EXECUTION_SUCCESS
      - JOB_EXECUTION_FAILED
      - JOB_CRASHED
    listener-class: com.example.job.JobEventListener

  # 流式处理配置
  streaming:
    enable: false
    chunk-size: 1000
    timeout: 30000
    retry-count: 3

  # 作业配置
  jobs:
    demo:
      enabled: true
      cron: "0/5 * * * * ?"
      sharding-total-count: 3
      sharding-parameters: "Beijing,Shanghai,Guangzhou"
```

---

### 最佳实践

#### 1. 作业设计原则

- **单一职责**：每个作业只负责一个明确的业务功能
- **幂等性**：作业执行应该是幂等的，可以安全重试
- **异常处理**：合理处理异常，避免作业静默失败
- **资源管理**：及时释放资源，避免内存泄漏

#### 2. 分片策略

- **均匀分布**：确保数据均匀分布到各个分片
- **业务相关**：分片参数与业务相关，如地区、品类等
- **扩展性**：预留分片数量，支持业务增长

#### 3. 性能优化

- **批处理**：大量数据时使用批处理
- **异步处理**：耗时操作考虑异步执行
- **监控告警**：设置合理的执行超时时间

#### 4. 错误处理

```java
@Job(cron = "0/10 * * * * ?")
public class RobustJob implements SimpleJob {

    @Override
    public void execute(JobContext jobContext) {
        try {
            // 业务逻辑
            processBusinessLogic();

        } catch (BusinessException e) {
            // 业务异常处理
            handleBusinessException(e);

        } catch (SystemException e) {
            // 系统异常处理
            handleSystemException(e);

        } catch (Throwable t) {
            // 未知异常处理
            handleUnknownException(t);
        }
    }

    private void handleBusinessException(BusinessException e) {
        // 记录业务异常
        log.error("业务执行失败", e);
        // 可以考虑重试或标记失败
    }

    private void handleSystemException(SystemException e) {
        // 记录系统异常
        log.error("系统错误", e);
        // 可能需要立即停止或告警
    }

    private void handleUnknownException(Throwable t) {
        // 记录未知异常
        log.error("未知错误", t);
        // 发送告警通知
    }
}
```

---

### 注意事项

1. **Cron表达式**：确保Cron表达式正确，避免过于频繁的执行
2. **分片配置**：分片参数数量不应超过分片总数
3. **资源占用**：长时间运行的作业要考虑资源占用问题
4. **网络分区**：在网络分区情况下可能出现作业重复执行
5. **依赖服务**：作业依赖的服务必须可用，考虑服务降级策略

### 详细教程参考

[ElasticJob Lite手册](http://elasticjob.io/docs/elastic-job-lite/00-overview/)

### 常见问题

#### Q: 如何处理作业执行失败的情况？

A: 在execute方法中使用try-catch捕获异常，并实现重试机制。可以结合业务逻辑判断是否需要重试。

#### Q: 如何避免同一数据被多个分片处理？

A: 确保分片参数和数据分片策略一致，保证每个分片处理的数据范围不重叠。

#### Q: 作业执行时间超过了下次执行时间怎么办？

A: 框架会自动跳过本次执行，不会重叠执行。如果作业执行时间较长，建议调整Cron表达式。

#### Q: 如何动态启用/禁用作业？

A: 使用enable属性，配合SpEL表达式实现动态控制。
