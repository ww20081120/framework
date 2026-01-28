# Framework 4.X 源代码树

## 根目录

* `README.md` - 项目主说明文件
* `pom.xml` - 项目根Maven配置文件
* `docs/` - 项目文档目录
* `framework-common/` - 基础工具模块
* `framework-db/` - 数据库访问模块
* `framework-cache/` - 缓存模块
* `framework-message/` - 消息模块
* `framework-job/` - 任务调度模块
* `framework-tx/` - 分布式事务模块
* `framework-tracing/` - 跟踪日志模块
* `framework-rule/` - 规则引擎模块
* `framework-shell/` - 控制台模块
* `framework-ai/` - AI功能模块
* `framework-dependencies/` - 项目依赖管理模块

## 各模块子目录结构

### framework-common
* `framework-common-core/`
* `framework-common-image/`
* `framework-common-xml/`

### framework-db
* `framework-db-cg/` - 代码生成工具
* `framework-db-core/` - 核心实现
* `framework-db-demo/` - 示例
* `framework-db-jdbc/` - JDBC支持
* `framework-db-jpa/` - JPA支持
* `framework-db-mongo/` - MongoDB支持
* `framework-db-orm/` - ORM支持

### framework-cache
* `framework-cache-core/` - 核心实现
* `framework-cache-demo/` - 示例
* `framework-cache-redis/` - Redis实现
* `framework-cache-simple/` - 简单实现

### framework-message
* `framework-message-core/` - 核心实现
* `framework-message-delay-db/` - 基于数据库的延迟消息
* `framework-message-demo/` - 示例
* `framework-message-kafka/` - Kafka实现
* `framework-message-redis/` - Redis实现
* `framework-message-rocketMQ/` - RocketMQ实现
* `framework-message-simple/` - 简单实现
* `framework-message-tx/` - 事务消息支持

### framework-job
* `framework-job-core/` - 核心实现
* `framework-job-demo/` - 示例
* `framework-job-elasticjob/` - ElasticJob集成
* `framework-job-quartz/` - Quartz集成
* `framework-job-xxl/` - XXL-JOB集成

### framework-tx
* `framework-tx-core/` - 核心实现
* `framework-tx-demo/` - 示例
* `framework-tx-integration/` - 客户端集成
* `framework-tx-server/` - 服务端实现 (含存储)
    * `framework-tx-server-storage-db/` - 数据库存储

### framework-tracing
* `framework-tracing-core/` - 核心实现
* `framework-tracing-demo/` - 示例
* `framework-tracing-file/` - 文件输出
* `framework-tracing-otlp/` - OTLP协议输出
* `framework-tracing-skywalking/` - Skywalking集成
* `framework-tracing-zipkin/` - Zipkin集成

### framework-rule
* `framework-rule-core/` - 核心实现
* `framework-rule-demo/` - 示例
* `framework-rule-file/` - 文件存储
* `framework-rule-plugin-event/` - 事件插件
* `framework-rule-plugin-stateMachine/` - 状态机插件
* `framework-rule-plugin-transaction/` - 事务插件

### framework-shell
* `framework-shell-core/` - 核心实现
* `framework-shell-demo/` - 示例
* `framework-shell-log/` - 日志命令
* `framework-shell-tx/` - 事务命令

### framework-ai
* `framework-ai-core/` - 核心实现
* `framework-ai-spring/` - Spring集成
* `framework-ai-agentscope/` - AgentScope集成
* `framework-ai-agent/` - Agent工具模块（开发中）
* `framework-ai-demo/` - 示例
    * `framework-ai-demo-openai/` - OpenAI示例
    * `framework-ai-demo-agentscope/` - AgentScope示例

### framework-dependencies
* `pom.xml` - 管理所有子模块和第三方依赖的版本