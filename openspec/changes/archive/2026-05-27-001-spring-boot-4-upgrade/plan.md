# Spring Boot 4.0.5 升级实施计划

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**目标:** 将框架从 Spring Boot 3.5.10 升级到 4.0.5，同时升级所有相关依赖到最新稳定版本。

**架构:** 单分支直接升级策略，在 4.3 分支上修改 pom.xml 版本号，解决依赖冲突，适配代码破坏性变更，确保用户 API 100% 兼容。

**技术栈:** Spring Boot 4.0.5, Spring AI 2.0.0-M4, Jackson 3, Jakarta EE 11, Java 21

---

## Task 1: 修改根 pom.xml 版本号

**Files:**
- Modify: `pom.xml:13-18`

**Step 1: 修改 spring-boot-starter-parent 版本**

找到 `<parent>` 部分，修改版本号：

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>4.0.5</version>
    <relativePath />
</parent>
```

**Step 2: 修改 properties 部分的版本号**

在 `<properties>` 部分修改以下版本：

```xml
<properties>
    <!-- ... 其他属性保持不变 ... -->
    <spring-ai.version>2.0.0-M4</spring-ai.version>
    <fastjson2.version>2.0.61</fastjson2.version>
    <elasticjob.version>3.0.5</elasticjob.version>
    <lombok.version>1.18.44</lombok.version>
    <skywalking.version>9.6.0</skywalking.version>
    <jgit.version>7.6.0</jgit.version>
    <!-- ... 其他属性保持不变 ... -->
</properties>
```

**Step 3: 提交版本号修改**

```bash
git add pom.xml
git commit -m "feat: 升级 Spring Boot 到 4.0.5 及相关依赖"
```

---

## Task 2: 编译验证并收集错误

**Files:**
- None

**Step 1: 清理并编译**

```bash
mvn clean compile
```

**Step 2: 记录编译错误**

将编译输出保存到文件供后续分析：

```bash
mvn clean compile > compile-errors.txt 2>&1
```

预期结果：
- 可能会有依赖下载错误
- 可能有 API 变更导致的编译错误
- 记录所有 `[ERROR]` 行

---

## Task 3: 检查 HttpHeaders 使用情况

**Files:**
- Search: 所有 Java 源文件

**Step 1: 搜索 HttpHeaders 的 MultiValueMap 转换**

```bash
find . -name "*.java" -type f -exec grep -l "HttpHeaders" {} \;
```

**Step 2: 搜索可能的 MultiValueMap 转换**

```bash
grep -r "MultiValueMap.*HttpHeaders\|HttpHeaders.*MultiValueMap" --include="*.java" .
```

**Step 3: 分析搜索结果**

如果找到以下模式，需要适配：

```java
// ❌ 旧写法
MultiValueMap<String, String> headers = httpHeaders;

// ✅ 新写法
HttpHeaders headers = ...;
// 或使用 HttpHeaders 的专用方法
```

---

## Task 4: 检查 Jackson 相关代码

**Files:**
- Search: 所有 Java 源文件

**Step 1: 搜索 Jackson 直接导入**

```bash
grep -r "import com.fasterxml.jackson" --include="*.java" .
```

**Step 2: 检查 fastjson2 共存情况**

确认 fastjson2 的使用不受 Jackson 3 影响：

```bash
grep -r "import com.alibaba.fastjson2" --include="*.java" .
```

---

## Task 5: 处理 Spring AI 2.0.0-M4 API 变更

**Files:**
- Modify: `framework-ai/framework-ai-spring/pom.xml`
- Modify: `framework-ai` 相关 Java 文件

**Step 1: 检查 AI 模块编译**

```bash
cd framework-ai
mvn clean compile
```

**Step 2: 处理 Spring AI API 变更**

根据 Spring AI 2.0 的变更文档，检查以下可能的变更点：
- 包名变更
- API 方法签名变更
- 配置属性变更

**Step 3: 修复编译错误**

根据编译错误逐个修复 AI 模块的问题。

---

## Task 6: 验证 Spring Data 兼容性

**Files:**
- Modify: `framework-db/framework-db-mongo/pom.xml`（如需要）
- Modify: `framework-tx/framework-tx-server/framework-tx-server-storage-cassandra/pom.xml`（如需要）

**Step 1: 编译 MongoDB 模块**

```bash
cd framework-db/framework-db-mongo
mvn clean compile
```

**Step 2: 编译 Cassandra 模块**

```bash
cd framework-tx/framework-tx-server/framework-tx-server-storage-cassandra
mvn clean compile
```

**Step 3: 检查 Spring Data 版本**

Spring Boot 4.0.5 自带的 Spring Data 版本应该已兼容 Spring Framework 7。

---

## Task 7: 运行单元测试

**Files:**
- All test files

**Step 1: 运行所有测试**

```bash
mvn test
```

**Step 2: 记录测试失败**

```bash
mvn test > test-results.txt 2>&1
```

**Step 3: 分析失败的测试**

重点关注：
- Spring 上下文加载失败
- Bean 配置问题
- Mock 对象问题

---

## Task 8: 验证 demo 模块

**Files:**
- Demo 模块

**Step 1: 启动 db-demo**

```bash
cd framework-db/framework-db-demo
mvn spring-boot:run
```

预期结果：应用正常启动，无错误日志

**Step 2: 启动 cache-demo**

```bash
cd framework-cache/framework-cache-demo
mvn spring-boot:run
```

预期结果：应用正常启动

**Step 3: 启动 tx-demo**

```bash
cd framework-tx/framework-tx-demo/framework-tx-demo-client
mvn spring-boot:run
```

预期结果：应用正常启动

---

## Task 9: 运行代码质量检查

**Files:**
- None

**Step 1: 运行 checkstyle**

```bash
mvn checkstyle:check
```

**Step 2: 运行 PMD**

```bash
mvn pmd:check
```

**Step 3: 运行 SpotBugs**

```bash
mvn spotbugs:check
```

**Step 4: 修复质量问题**

根据检查结果修复代码质量问题。

---

## Task 10: 最终验证

**Files:**
- None

**Step 1: 完整构建测试**

```bash
mvn clean install
```

**Step 2: 验证核心功能**

- [ ] 数据库连接正常
- [ ] 缓存功能正常
- [ ] 事务功能正常
- [ ] 消息功能正常
- [ ] 任务调度正常

**Step 3: 提交最终版本**

```bash
git add -A
git commit -m "feat: 完成 Spring Boot 4.0.5 升级"
```

---

## 执行顺序建议

1. **Task 1**: 修改版本号（必须第一步）
2. **Task 2**: 编译验证（检查基础错误）
3. **Task 3-4**: 代码搜索分析（了解影响范围）
4. **Task 5-6**: 修复特定模块问题
5. **Task 7**: 运行测试
6. **Task 8**: 验证 demo
7. **Task 9**: 代码质量检查
8. **Task 10**: 最终验证

---

## 回滚策略

如果升级过程中遇到无法解决的问题：

```bash
git reset --hard HEAD~1  # 回退最后一次提交
# 或
git log  # 查看提交历史
git reset --hard <commit-hash>  # 回退到指定提交
```

---

## 参考资料

- [Spring Boot 4.0 Migration Guide](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Migration-Guide)
- [Spring AI 2.0.0-M4 Release Notes](https://spring.io/blog/2026/03/26/spring-ai-2-0-0-m4-and-1-1-4-and-1-0-5-available)
- 项目设计文档: `docs/plans/2026-03-31-spring-boot-4-upgrade-design.md`
