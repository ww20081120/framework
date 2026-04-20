# Framework 4.X 架构文档 (Brownfield)

## 1. 概述

### 1.1 项目背景
Framework 4.X 是一个企业级Java应用开发框架，旨在简化分布式系统开发，提供一套标准化的解决方案。它整合了缓存、数据库、消息、任务调度、分布式事务、日志追踪、规则引擎、AI等核心功能模块。该框架基于Spring Boot和Spring Cloud构建，支持JDK 21，旨在屏蔽底层中间件的复杂性，让开发者能够更专注于业务逻辑的实现。

### 1.2 架构目标
*   **模块化**: 将功能解耦为独立的Maven模块，便于按需引入和维护。
*   **标准化**: 提供一致的API和配置方式，降低学习成本。
*   **高性能**: 选用高性能的中间件和技术栈。
*   **高可用**: 通过合理的架构设计和组件选型，保障系统稳定运行。
*   **易扩展**: 架构设计具备良好的扩展性，方便集成新的技术和功能。
*   **可追溯**: 通过完善的日志和追踪机制，便于问题排查。

### 1.3 文档结构
*   **概述**: 项目背景、架构目标、文档结构。
*   **上下文**: 系统的外部依赖和交互。
*   **约束**: 技术选型、编码规范等约束条件。
*   **核心模块**: 详细描述各个核心功能模块的设计与实现。
*   **部署与运维**: 部署方式、监控方案等。
*   **附录**: 术语表、参考文档等。

## 2. 上下文 (Context)

### 2.1 外部系统依赖
Framework 4.X 作为一个底层开发框架，其运行依赖于一系列外部系统：
*   **Java运行环境**: JDK 21。
*   **构建工具**: Apache Maven。
*   **关系型数据库**: MySQL (主数据库)。通过Hibernate也支持Oracle, H2等。
*   **NoSQL数据库**: MongoDB。
*   **缓存系统**: Redis。
*   **消息中间件**: Apache Kafka, RocketMQ, Redis Pub/Sub。
*   **任务调度系统**: XXL-JOB, Elastic-Job, Quartz。
*   **搜索引擎**: Elasticsearch (主要用于AI向量存储)。
*   **分布式追踪系统**: Skywalking, Zipkin, OpenTelemetry Collector。
*   **AI服务**: 阿里云 DashScope (通义千问API)。
*   **版本控制**: Git。

### 2.2 系统交互图 (C4 Context Diagram)
(此部分可插入一张C4上下文图，展示Framework与外部系统如MySQL, Redis, Kafka, 开发者等的交互关系)

## 3. 约束 (Constraints)

### 3.1 技术选型约束
*   **编程语言**: Java 21。
*   **核心框架**: Spring Boot 3.5, Spring Cloud 2025.0.0。
*   **构建工具**: Apache Maven。
*   **数据库**: 主要支持MySQL，通过Hibernate支持其他关系型数据库。
*   **缓存**: Redis。
*   **消息队列**: Kafka, RocketMQ, Redis Pub/Sub。
*   **任务调度**: XXL-JOB, Elastic-Job, Quartz。
*   **分布式追踪**: Skywalking, OpenTelemetry。
*   **AI**: Langchain4j, DashScope API。
*   **日志**: SLF4J + Logback。

### 3.2 编码规范约束
*   遵循项目统一的[编码规范](./coding-standards.md)。
*   所有模块均需包含单元测试。

### 3.3 部署约束
*   应用打包为可执行的JAR文件。
*   通过标准的Java命令或容器化方式运行。

## 4. 核心模块架构 (Core Modules Architecture)

Framework 4.X 由多个核心模块组成，每个模块负责特定的功能领域。

### 4.1 framework-common (基础工具)
*   **职责**: 提供项目通用的常量、工具类和基础组件。
*   **关键组件**: 
    *   基础实体类 (`BaseEntity`)
    *   通用工具类 (字符串、集合、日期等处理)
    *   异常处理框架
    *   配置读取工具
*   **依赖**: 无强依赖，主要依赖JDK和Spring。

### 4.2 framework-db (数据库访问)
*   **职责**: 提供一个轻量级、易用的DAO框架，融合Hibernate和MyBatis的优势。
*   **特性**: 
    *   零配置ORM。
    *   SQL与代码分离。
    *   支持QueryWrapper/LambdaQueryWrapper。
    *   支持JPA Criteria API。
*   **子模块**:
    *   `framework-db-core`: 核心实现。
    *   `framework-db-jpa`: JPA支持。
    *   `framework-db-jdbc`: JDBC支持。
    *   `framework-db-mongo`: MongoDB支持。
    *   `framework-db-cg`: 代码生成工具。
*   **依赖**: `framework-common`, Hibernate, MyBatis相关库。

### 4.3 framework-cache (缓存)
*   **职责**: 提供统一的缓存访问接口。
*   **特性**: 
    *   注解驱动的缓存。
    *   支持Redis分布式锁。
*   **子模块**:
    *   `framework-cache-core`: 核心抽象。
    *   `framework-cache-redis`: Redis实现。
    *   `framework-cache-simple`: 简单内存实现。
*   **依赖**: `framework-common`, Redis客户端(Jedis/Lettuce)。

### 4.4 framework-message (消息)
*   **职责**: 提供统一的消息发布与订阅API。
*   **特性**: 
    *   支持多种消息中间件。
    *   简单的事件驱动模型。
*   **子模块**:
    *   `framework-message-core`: 核心抽象。
    *   `framework-message-kafka`: Kafka实现。
    *   `framework-message-rocketMQ`: RocketMQ实现。
    *   `framework-message-redis`: Redis实现。
    *   `framework-message-tx`: 事务消息支持。
*   **依赖**: `framework-common`, 各消息中间件客户端。

### 4.5 framework-job (任务调度)
*   **职责**: 封装主流任务调度框架，提供统一的调度接口。
*   **特性**: 
    *   支持分布式、分片。
*   **子模块**:
    *   `framework-job-core`: 核心抽象。
    *   `framework-job-quartz`: Quartz实现。
    *   `framework-job-xxl`: XXL-JOB实现。
    *   `framework-job-elasticjob`: Elastic-Job实现。
*   **依赖**: `framework-common`, 各任务调度框架。

### 4.6 framework-tx (分布式事务)
*   **职责**: 提供最终一致性的分布式事务解决方案。
*   **特性**: 
    *   基于补偿的重试机制。
    *   注解驱动。
*   **子模块**:
    *   `framework-tx-core`: 核心实现。
    *   `framework-tx-integration`: 客户端集成。
    *   `framework-tx-server`: 服务端实现与存储。
*   **依赖**: `framework-common`, `framework-message`。

### 4.7 framework-tracing (日志追踪)
*   **职责**: 提供分布式系统的调用链追踪和日志记录。
*   **特性**: 
    *   方法级追踪。
    *   支持多种追踪系统。
*   **子模块**:
    *   `framework-tracing-core`: 核心实现。
    *   `framework-tracing-file`: 文件输出。
    *   `framework-tracing-skywalking`: Skywalking集成。
    *   `framework-tracing-otlp`: OpenTelemetry集成。
    *   `framework-tracing-zipkin`: Zipkin集成。
*   **依赖**: `framework-common`, 各追踪系统SDK。

### 4.8 framework-rule (规则引擎)
*   **职责**: 提供基于JSON的轻量级规则引擎。
*   **特性**: 
    *   插件化架构。
    *   支持状态机工作流。
*   **子模块**:
    *   `framework-rule-core`: 核心实现。
    *   `framework-rule-file`: 文件存储规则。
    *   `framework-rule-plugin-*`: 各种插件。
*   **依赖**: `framework-common`。

### 4.9 framework-shell (控制台)
*   **职责**: 提供命令行交互接口。
*   **特性**: 
    *   自定义命令。
*   **子模块**:
    *   `framework-shell-core`: 核心实现。
    *   `framework-shell-*`: 特定功能命令。
*   **依赖**: `framework-common`。

### 4.10 framework-ai (人工智能)
*   **职责**: 集成AI能力，提供自然语言处理等功能。
*   **特性**: 
    *   NL2SQL。
    *   支持国内大模型。
*   **子模块**:
    *   `framework-ai-core`: 核心实现。
    *   `framework-ai-jmanus`: 特定集成。
    *   `framework-ai-demo-*`: 示例。
*   **依赖**: `framework-common`, Langchain4j, DashScope SDK。

### 4.11 framework-langchain4j (Langchain4j扩展)
*   **职责**: 对Langchain4j库进行扩展，更好地支持国内AI生态。
*   **子模块**:
    *   `framework-langchain4j-dashscope`: 通义千问集成。
    *   `framework-langchain4j-elasticsearch`: Elasticsearch向量存储。
*   **依赖**: Langchain4j, 各AI服务SDK。

## 5. 部署与运维 (Deployment & Operations)

### 5.1 部署方式
*   **构建**: 使用 `mvn clean package` 命令打包各模块为JAR文件。
*   **运行**: 通过 `java -jar module-name.jar` 命令运行。
*   **容器化**: 推荐使用Docker容器化部署，便于环境隔离和管理。

### 5.2 配置管理
*   使用 Spring Boot 的 `application.yml` 进行配置。
*   敏感信息（如数据库密码）应使用 Jasypt 进行加密。

### 5.3 监控与日志
*   **日志**: 应用日志通过 Logback 输出。
*   **指标**: 通过 Micrometer 暴露应用指标。
*   **追踪**: 通过集成的 Skywalking/OpenTelemetry 进行分布式追踪。

## 6. 附录 (Appendices)

### 6.1 术语表
*   **Framework**: 指本项目 Framework 4.X。
*   **Module**: 指 Framework 下的 Maven 子模块。
*   **Core Module**: 指功能模块的核心实现部分。

### 6.2 参考文档
*   [源代码树](./source-tree.md)
*   [技术栈](./tech-stack.md)
*   [编码规范](./coding-standards.md)
*   [项目README](../../README.md)
*   各模块下的 `README.md` 或文档文件。