---
name: openspec-explore
description: Use when user wants to explore ideas, clarify requirements, or define change scope before technical design. Produces proposal.md.
license: MIT
compatibility: 需要安装 openspec CLI。
metadata:
  author: openspec
  version: "2.0"
  generatedBy: "1.3.0"
---

探索想法，引导思考，产出提案。

我将通过引导式对话帮用户理清想法，产出包含以下内容的变更提案：
- proposal.md（做什么、为什么、范围）

完成探索后，运行 /openspec-propose 生成设计和实施计划。

---

## 核心原则

这是一个**引导式思考过程**，不是被动陪聊。主动引导用户：
- 一次只问一个问题
- 用多选题引导方向
- 提出 2-3 种方案对比
- 砍掉不必要的需求（YAGNI）
- 项目过大时建议拆分

## 强制产出

**必须产出 proposal.md 才算完成。** 如果用户中途想放弃，提醒可以暂停，但 explore 的目标就是产出提案。

---

## 步骤

### 1. 理解需求

如果用户未提供明确输入，使用 **AskUserQuestion 工具** 询问：

> "你想要处理什么变更？描述你想要构建或修复的内容。"

根据描述，派生 kebab-case 格式的名称（例如 "add user authentication" → `add-user-auth`）。

**重要**：在不理解用户想要构建什么之前，不要继续。

### 2. 前置检查

```bash
openspec list --json
```

检查是否有同名变更已存在：
- 已存在且有 proposal.md → 询问用户是覆盖、继续完善还是新建
- 已存在但无 proposal.md → 继续，在现有变更上工作
- 不存在 → 创建新变更

### 3. 创建变更目录（如需要）

```bash
openspec new change "<name>"
```

### 4. 项目上下文探索

主动收集上下文，形成初步理解：

```
1. 根据 proposal 话题，搜索相关代码
   - 使用 MCP graph 工具（semantic_search_nodes）优先
   - 回退到 Grep/Glob 搜索
2. 检查 openspec/specs/ 下的已有规格
3. 检查最近的 git 提交，了解项目活跃方向
4. 形成初步理解
```

### 5. 逐个提问引导（brainstorming 方法论）

用**一次一个问题**的方式引导用户深入思考。

**提问原则：**
- 优先使用 AskUserQuestion 的多选模式（2-4 选项）
- 开放式问题用于深挖动机和约束
- 每个问题聚焦一个维度：目的、约束、成功标准、边界

**典型提问路径：**

```
Q1: 这个变更解决什么问题？（目的）
Q2: 成功标准是什么？（验收）
Q3: 有哪些技术约束？（约束）
Q4: 哪些是明确不需要的？（范围排除）
Q5: 有没有可以参考的现有实现？（上下文）
```

**注意：** 不需要严格按顺序，根据用户回答调整方向。但始终一次只问一个问题。

**YAGNI 意识：**
- 当用户提到"以后可能用到"的功能 → 建议移除
- 当用户描述过度泛化的方案 → 建议简化
- 聚焦当前明确需要的

**项目拆分意识：**

```
触发条件：
- 话题涉及 3+ 独立子系统
- 用户描述"一个平台，包含 A、B、C、D"
- 预计任务超过 15 个

应对：
- 帮用户识别独立子系统
- 建议拆分为多个 change
- 先聚焦第一个子系统
```

### 6. 逐段呈现需求理解

将理解的需求分段呈现，每段请用户确认。**聚焦业务层面**（做什么、为什么），不涉及技术方案选择（那是 propose 的职责）。

```
第 1 段 - 背景 & 问题
"我理解你想做的是 X，解决 Y 问题。对吗？"

第 2 段 - 目标 & 成功标准
"目标是实现 Z，成功的标准是 W。对吗？"

第 3 段 - 范围
"包含: A, B, C"
"不包含: D, E"
"这个范围合理吗？"

第 4 段 - 约束
"约束条件: ..."
"这些约束完整吗？"
```

每段确认后再进入下一段。如果用户否定，回到提问阶段。

### 7. 写入 proposal.md

```bash
openspec instructions proposal --change "<name>" --json
```

获取模板结构，将确认的内容写入。

输出路径：`openspec/changes/<name>/proposal.md`

**proposal.md 内容规范：**

```markdown
# <变更名称>

## 背景
[为什么要做这个变更，解决什么问题]

## 目标
[要达成什么，成功标准是什么]

## 范围

### 包含
- [具体要做的事情]

### 不包含
- [明确排除的内容]

## 约束
[技术约束、时间约束、兼容性要求等]

## 备注
[其他需要传达给技术设计阶段的信息，如参考系统、优先级等]
```

**约束规则：**
- 使用 `openspec instructions` 的 `template` 作为结构
- 应用 `context` 和 `rules` 作为约束 — 但不要将它们复制到文件中
- 读取任何已完成的依赖文件以获取上下文

### 8. Self-review

写入后自检：

```
占位符扫描: 无 TBD、TODO、"fill in details"
内部一致性: 范围与目标不矛盾，约束与方案兼容
范围聚焦: 单一 change 可实现，不需要拆分
歧义消除: 每个需求可被唯一解读
```

如果发现问题，立即修复。

### 9. 用户审查

> "提案已写入 `openspec/changes/<name>/proposal.md`，请审查后确认。如需调整请告诉我。"

等待用户确认。

### 10. 完成提示

```
探索完成！

**变更:** <name>
**提案:** openspec/changes/<name>/proposal.md

下一步: 运行 /openspec-propose 生成技术设计和实施计划
```

---

## 约束规则

- **强制产出 proposal.md** — 这是 explore 的目标，不是可选的
- **一次一个问题** — 不要连续抛出多个问题
- **主动引导** — 不是被动回答，而是主动引导思考方向
- **YAGNI 原则** — 帮用户砍掉不必要的需求
- **不实施代码** — 思考过程中绝不编写业务代码
- **不自动假设** — 不清楚时提问，不猜测
- **职责边界** — explore 只产出业务需求提案（做什么、为什么、范围），技术方案设计留给 /openspec-propose
