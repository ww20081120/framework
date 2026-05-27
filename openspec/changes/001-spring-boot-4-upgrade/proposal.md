# Spring Boot 4 升级及依赖版本全面审计

## 背景

框架已从 Spring Boot 3.5.10 升级到 4.0.5（design.md 和 plan.md 已记录）。编译和构建均通过。现需对 pom.xml 中所有管理的依赖版本进行全面审计，确认是否均已升级到最新稳定版，并补齐遗漏的升级项。

## 目标

将 pom.xml 中所有可升级的依赖更新到最新稳定版，确保框架依赖版本保持最新。

## 范围

### 包含

升级以下19个依赖：

| 依赖 | 当前版本 | 目标版本 | 备注 |
|------|----------|----------|------|
| spring-boot | 4.0.5 | 4.0.6 | Spring Boot 4.0 最新补丁 |
| druid | 1.2.27 | 1.2.28 | |
| fastjson2 | 2.0.61 | 2.0.62 | |
| caffeine | 3.2.3 | 3.2.4 | |
| jaxb | 4.0.6 | 4.0.8 | |
| jgit | 7.5.0.202512021534-r | 7.6.0.202603022253-r | |
| lombok | 1.18.44 | 1.18.46 | |
| mysql-connector | 9.5.0 | 9.7.0 | |
| opentelemetry | 1.57.0 | 1.62.0 | |
| opentelemetry-semconv | 1.37.0 | 1.41.1 | |
| feign | 13.6 | 13.12 | |
| testcontainers | 2.0.3 | 2.0.5 | |
| xxl-job | 3.3.1 | 3.4.0 | |
| hibernate | 7.3.0.Final | 7.3.6.Final | 7.x 系列最新补丁 |
| spotbugs-maven-plugin | 4.9.8.2 | 4.9.8.3 | |
| spring-ai | 2.0.0-M4 | 2.0.0-M7 | 里程碑版本 |
| alibaba-ai | 1.1.0.0 | 1.1.2.3 | |
| spring-cloud | 2025.1.0 | 2025.1.1 | |
| commons-io | 2.21.0 | 2.22.0 | Maven 插件误报已修正 |

同时验证升级后编译、测试通过。

### 不包含

- 以下预览版/破坏性版本不升级：ognl (3.5.0-BETA4)、otel spring-boot (1.7.0-RC1)、agentscope (1.1.0-RC2)
- hibernate 8.x 不升级（Alpha 版，需 Jakarta Persistence 4.0）
- commons-beanutils 不升级（Maven 报告为误报）
- 不改变项目版本号（保持 4.3.0）

## 约束

- 在 4.3 分支上直接修改
- 升级后必须通过 `mvn clean compile` 和 `mvn test`
- 用户 API 保持 100% 兼容
- spring-ai 2.0.0-M7 仍是里程碑版本，需关注 API 变更

## 备注

- 本次升级基于 2026-05-27 的 Maven Central 最新版本
- commons-io 2.22.0 于 2026-04-19 发布，Maven versions 插件因 groupId 历史问题误报
- Spring Boot 4.0.6 于 2026-04 发布
- Hibernate 7.3.6.Final 于 2026-05-24 发布
- 升级完成后可归档 001-spring-boot-4-upgrade 变更
