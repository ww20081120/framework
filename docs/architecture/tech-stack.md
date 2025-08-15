# Framework 4.X 技术栈

## 1. 编程语言与框架

* **核心语言**: Java 21
* **构建工具**: Apache Maven
* **核心框架**: 
    * Spring Boot 3.5
    * Spring Cloud 2025.0.0
    * Spring Framework (通过Spring Boot引入)

## 2. 核心中间件与技术

### 数据存储
* **关系型数据库**: MySQL 9.3.0 (主要)。通过Hibernate支持其他数据库。
* **NoSQL数据库**: MongoDB (通过`framework-db-mongo`支持)。
* **缓存**: Redis。通过`framework-cache-redis`模块提供分布式缓存和分布式锁。
* **搜索引擎**: Elasticsearch 8.10.4 (通过`framework-langchain4j-elasticsearch`用于AI向量存储)。

### 消息中间件
* **Apache Kafka**: 通过`framework-message-kafka`模块支持异步消息和事件驱动。
* **RocketMQ**: 通过`framework-message-rocketMQ`模块支持异步消息。
* **Redis Pub/Sub**: 通过`framework-message-redis`模块支持轻量级消息。

### 任务调度
* **XXL-JOB**: 通过`framework-job-xxl`模块支持分布式任务调度。
* **Elastic-Job**: 通过`framework-job-elasticjob`模块支持分布式任务调度和分片。
* **Quartz**: 通过`framework-job-quartz`模块支持基础任务调度。

### 分布式事务
* **自研补偿式事务**: `framework-tx`模块。通过N次重试，跳过执行成功的部分，一直重试失败部分，来达到业务最终执行完成。

### 日志与追踪
* **日志框架**: SLF4J, Logback (由Spring Boot引入)。
* **分布式追踪**: 
    * Apache SkyWalking 9.4.0 (通过`framework-tracing-skywalking`模块)。
    * OpenTelemetry 1.45.0 (通过`framework-tracing-otlp`模块)。
    * Zipkin (通过`framework-tracing-zipkin`模块)。

### 规则引擎
* **自研JSON规则引擎**: `framework-rule`模块。支持基于状态机的工作流引擎等插件。

### 人工智能
* **Langchain4j**: 0.23.0。用于构建AI应用的核心库。
* **国内大模型API**: 阿里云 DashScope 2.19.5 (通义千问)。
* **向量数据库**: Elasticsearch (作为向量存储)。

### 其他工具与库
* **JSON处理**: Alibaba Fastjson2 2.0.57
* **表达式语言**: OGNL 3.4.7
* **模板引擎**: Apache Velocity 2.4.1
* **图像处理**: Thumbnailator 0.4.20
* **通用工具库**: Apache Commons (Lang3 3.17.0, Collections4 4.4, Beanutils 1.10.1, IO 2.19.0)
* **配置加密**: Jasypt 1.9.3
* **序列化**: Kryo 5.6.2
* **数据库连接池**: Alibaba Druid 1.2.24
* **XML处理**: Dom4j 2.1.4
* **缓存库**: Caffeine 3.2.2
* **SSH**: JSch 0.1.55
* **JAXB**: 4.0.5 (用于XML绑定)
* **Lombok**: 1.18.38 (用于简化Java代码)
* **Micrometer**: 1.4.1 (用于应用指标监控和追踪桥接)

## 3. 开发与运维工具

* **代码质量**: Checkstyle (通过Maven插件)
* **Git Hooks**: `githook-maven-plugin` 用于提交前自动检查代码风格。
* **版本管理**: Maven Versions Plugin 用于批量修改版本。