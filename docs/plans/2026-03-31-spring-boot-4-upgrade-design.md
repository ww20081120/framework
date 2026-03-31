# Spring Boot 4.0.5 升级设计文档

**日期**: 2026-03-31
**版本**: 1.0
**作者**: Claude Code

---

## 1. 概述

### 1.1 升级目标

将框架的 Spring Boot 版本从 3.5.10 升级到 4.0.5，同时将所有依赖升级到最新稳定版本。

### 1.2 升级动机

- **获取新功能**: Spring Boot 4.x 提供的新特性和增强
- **依赖兼容性**: 其他依赖（如 spring-ai）对更高版本 Spring Boot 的需求
- **长期规划**: 提前布局，为未来升级做准备
- **安全/修复**: 获取安全补丁和 bug 修复

### 1.3 设计原则

- **用户 API 100% 兼容**: public 方法签名不变
- **框架内部可调整**: 内部实现可适当优化
- **优先核心功能**: 保证核心模块功能完整性

---

## 2. 当前状态

### 2.1 项目信息

| 项目 | 当前值 |
|------|--------|
| 项目版本 | 4.3.0 |
| Java 版本 | 21 |
| 分支 | 4.3 |
| 子模块数量 | 60+ |

### 2.2 当前依赖版本

| 依赖 | 当前版本 |
|------|----------|
| spring-boot-starter-parent | 3.5.10 |
| spring-ai-bom | 1.1.2 |
| elasticjob-lite | 3.0.4 |
| fastjson2 | 2.0.60 |
| lombok | 1.18.42 |
| skywalking | 9.5.0 |
| jgit | 7.5.0 |

---

## 3. 升级方案

### 3.1 策略

采用 **单分支直接升级** 策略，直接在 4.3 分支上修改，保持项目版本号 4.3.0 不变。

### 3.2 版本升级清单

| 依赖 | 当前版本 | 目标版本 | 变更类型 |
|------|----------|----------|----------|
| spring-boot-starter-parent | 3.5.10 | 4.0.5 | 🔴 必须 |
| spring-ai-bom | 1.1.2 | 2.0.0-M4 | 🔴 必须 |
| elasticjob-lite | 3.0.4 | 3.0.5 | 🟡 建议 |
| fastjson2 | 2.0.60 | 2.0.61 | 🟡 建议 |
| lombok | 1.18.42 | 1.18.44 | 🟡 建议 |
| skywalking | 9.5.0 | 9.6.0 | 🟡 建议 |
| jgit | 7.5.0 | 7.6.0 | 🟡 建议 |

**保持不变的依赖**（已是最新稳定版）：
- mysql-connector-j: 9.5.0
- druid: 1.2.27
- caffeine: 3.2.3
- xxl-job-core: 3.3.1
- ognl: 3.4.9
- commons-lang3: 3.20.0
- commons-io: 2.21.0
- commons-collections4: 4.5.0
- velocity-engine-core: 2.4.1
- kryo5: 5.6.2
- thumbnailator: 0.4.21
- dom4j: 2.2.0
- jasypt: 1.9.3

---

## 4. 技术影响分析

### 4.1 框架核心变更

| 变更项 | 影响 | 风险等级 |
|--------|------|----------|
| Spring Framework 7.0 | 需要 7.0.6+ | 🟡 中 |
| Jakarta EE 11 | 完全采用 | 🟢 低 |
| Jackson 3.x | 默认使用，Jackson 2 废弃 | 🔴 高 |
| HttpHeaders 不再实现 MultiValueMap | 代码需适配 | 🔴 高 |

### 4.2 关键依赖兼容性

#### Spring AI 2.0.0-M4
- **状态**: 里程碑版本，支持 Spring Boot 4.0
- **风险**: 非 GA 版本，稳定性待验证
- **缓解**: 先验证核心功能，必要时暂缓 AI 模块

#### Spring Data
- **MongoDB**: 需要 5.x（需 JDK 17+ + Spring Framework 7.0.6+）
- **Cassandra**: 需升级到兼容 Spring Framework 7 的版本

### 4.3 代码破坏性变更

#### HttpHeaders 接口变更
```java
// ❌ 旧写法将失效
MultiValueMap<String, String> headers = httpHeaders;

// ✅ 新写法
HttpHeaders headers = ...;
```

#### Jackson 3 变更
- 包名和坐标变更
- 与 fastjson2 共存需验证

---

## 5. 实施步骤

### 5.1 阶段 1: 版本修改

1. 修改根 `pom.xml` 版本号
2. 执行 `mvn clean compile` 收集编译错误

### 5.2 阶段 2: 依赖修复

1. 解决版本冲突
2. 处理 Spring AI 2.0.0-M4 的 API 变更
3. 验证第三方依赖兼容性

### 5.3 阶段 3: 代码适配

1. 搜索 HttpHeaders 使用情况并适配
2. 处理 Jackson 3 相关代码
3. 修复其他破坏性变更

### 5.4 阶段 4: 测试验证

1. 运行所有单元测试 `mvn test`
2. 启动各 demo 模块验证
3. 运行代码质量检查

---

## 6. 风险评估与缓解

| 风险 | 等级 | 缓解措施 |
|------|------|----------|
| Spring AI M1 不稳定 | 🔴 高 | 先验证核心功能，必要时暂缓 AI 模块 |
| HttpHeaders 代码散布 | 🔴 高 | 使用全局搜索 + 批量适配 |
| Jackson 3 变更 | 🔴 高 | 验证与 fastjson2 共存 |
| 第三方依赖不兼容 | 🟡 中 | 逐个验证，必要时降级 |
| 测试覆盖不足 | 🟡 中 | 重点验证核心模块 |

---

## 7. 成功标准

- [ ] `mvn clean compile` 成功
- [ ] 所有单元测试通过
- [ ] demo 模块启动正常
- [ ] 核心功能手动验证（db、cache、tx、message）
- [ ] 代码质量检查通过
- [ ] 用户 API 100% 兼容

---

## 8. 参考资料

- [Spring Boot 4.0 Migration Guide](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Migration-Guide)
- [Spring Boot 4.0 Release Notes](https://spring.io/blog/2025/11/20/spring-boot-4-0-0-available-now)
- [Introducing Jackson 3 support in Spring](https://spring.io/blog/2025/10/07/introducing-jackson-3-support-in-spring)
- [Migrate HttpHeaders MultiValueMap methods](https://docs.openrewrite.org/recipes/java/spring/framework/migratehttpheadersmultivaluemapmethods/)
