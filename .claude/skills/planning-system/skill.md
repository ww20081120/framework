---
name: planning-system
description: Use when creating Sprint plans, breaking down Stories, or tracking development execution. Use when starting a new Sprint or updating Sprint progress.
---

# Sprint Planning System

## Overview

专注于 **Sprint 级别的计划与执行**。假设产品蓝图（PRD、路线图）已由产品负责人定义，本技能负责：

- 创建 Sprint 计划
- 分解 User Story
- 跟踪执行进度
- 管理 Sprint 变更

---

## 规划层级关系

```
产品蓝图（产品负责人负责）     Sprint计划（本技能）
┌─────────────────────┐       ┌──────────────────────┐
│ 01-生态路线图.md     │ ──┐   │ 04-Sprint总计划.md    │
│ 02-平台产品规划.md   │   │   │ sprints/YYYY/QQ/     │
│ 03-版本路线图.md     │   └──→ │   Sprint-NN/         │
└─────────────────────┘       │   ├── 总览.md         │
                              │   └── {产品线}.md     │
                              └──────────────────────┘
```

**本技能只处理右侧的 Sprint 计划部分。**

---

## When to Use

| 场景 | 使用本技能 |
|------|-----------|
| 创建新 Sprint | ✅ |
| 分解 User Story | ✅ |
| 更新 Sprint 进度 | ✅ |
| Sprint 评审/回顾 | ✅ |
| 编写产品 PRD | ❌ (使用 product-doc-creation) |
| 规划产品路线图 | ❌ (产品负责人负责) |

---

## Directory Structure

```
docs/04-planning/
├── 04-Sprint总计划.md            # Sprint 总计划（本技能核心）
├── CHANGELOG.md                  # 版本变更记录
└── sprints/                      # Sprint 执行文档（本技能核心）
    ├── YYYY/                     # 年份
    │   └── QQ/                   # 季度
    │       └── Sprint-NN/        # Sprint 编号
    │           ├── 总览.md       # Sprint 总览
    │           └── {产品线}.md   # 各产品线计划
    └── archive/                  # 已归档的 Sprint
        └── YYYY/
            └── QQ/
```

**本技能管理**：`04-Sprint总计划.md` + `sprints/` 目录

**非本技能范围**：`01-生态路线图.md`、`02-平台产品规划.md`、`03-版本路线图.md`

---

## 核心工作流程

### 1. 创建 Sprint

```bash
# 1. 创建目录
SPRING_DIR="docs/04-planning/sprints/2026/Q2/Sprint-05"
mkdir -p "$SPRING_DIR"

# 2. 复制模板
cp ".claude/skills/planning-system/templates/sprint-overview.md" \
   "$SPRING_DIR/总览.md"
cp ".claude/skills/planning-system/templates/product-sprint.md" \
   "$SPRING_DIR/B7-事件网格.md"
```

### 2. 填写 Sprint 总览

在 `总览.md` 中填写：
- Sprint 周期和目标
- 团队分配
- Story 列表（从产品 PRD 的 stories/ 中挑选）
- 风险识别

### 3. 更新 Sprint 总计划

在 `04-Sprint总计划.md` 中添加新 Sprint 行。

---

## Story 生命周期

```
┌─────────┐    ┌──────────┐    ┌─────────┐    ┌──────────┐    ┌──────┐
│  待开始  │ → │ 进行中   │ → │ 评审中  │ → │ 验收中   │ → │ 完成 │
└─────────┘    └──────────┘    └─────────┘    └──────────┘    └──────┘
     ↑                              │
     └──────────────────────────────┘
                   未通过
```

| 状态 | 图标 | 进入标准 | 负责人 |
|------|------|----------|--------|
| 待开始 | 📋 | Story 已创建，任务已分解 | 开发者 |
| 进行中 | 🟡 | 代码开发中，每日更新 | 开发者 |
| 评审中 | 👀 | 代码审查请求已发出 | Tech Lead |
| 验收中 | ✅ | CR 通过，自测通过 | Tech Lead |
| 完成 | ✅ | 所有验收标准满足 | Tech Lead |
| 阻塞 | 🚫 | 等待依赖或外部资源 | 开发者 |

---

## 完成标准 (Definition of Done)

每个 Story 完成必须满足：

- [ ] **代码**: 功能实现完成，代码符合规范
- [ ] **单元测试**: 测试覆盖率 ≥ 80%
- [ ] **代码审查**: 至少 1 人 Approve
- [ ] **集成测试**: 相关集成测试通过
- [ ] **文档**: API 变更有文档更新
- [ ] **CHANGELOG**: 变更已记录

---

## Sprint 变更管理

### 变更类型

| 类型 | 影响 | 审批 |
|------|------|------|
| Story 范围调整 | 当前 Sprint | Tech Lead |
| Sprint 间 Story 移动 | 总计划 | Scrum Master |
| 新增 Story | 需求池 | 产品负责人 |

### 变更流程

1. **评估影响** → 确定变更类型
2. **获取审批** → 按变更类型
3. **更新文档** → Sprint 文档 + 总计划
4. **通知团队** → 同步变更

---

## Templates

模板文件位于本技能的 `templates/` 目录：

| 模板文件 | 用途 |
|----------|------|
| `templates/sprint-overview.md` | Sprint 总览模板 |
| `templates/product-sprint.md` | 产品 Sprint 计划模板 |

### 如何使用

```bash
# Sprint 文档
cp .claude/skills/planning-system/templates/sprint-overview.md \
   docs/04-planning/sprints/2026/Q2/Sprint-05/总览.md

# 产品计划
cp .claude/skills/planning-system/templates/product-sprint.md \
   docs/04-planning/sprints/2026/Q2/Sprint-05/B7-事件网格.md
```

---

## File Naming Conventions

| 类型 | 模式 | 示例 |
|------|------|------|
| Sprint 目录 | `Sprint-NN` | `Sprint-05` |
| Sprint 总览 | `总览.md` | 固定名称 |
| 产品 Sprint | `{产品线代码}.md` | `B7-事件网格.md` |

---

## Quick Start

### 创建新 Sprint

1. 从产品 PRD 的 `stories/README.md` 获取待开发 Story
2. 创建 Sprint 目录和文档（复制模板）
3. 填写 `总览.md`：目标、团队、Story 列表
4. 为各产品线创建计划文档
5. 更新 `04-Sprint总计划.md`

### 每日更新

在 `{产品线}.md` 中更新 Story 状态：
```markdown
| Story | 负责人 | 状态 | 更新时间 |
|-------|--------|------|----------|
| F01-03-S1 | @xxx | 🟡 进行中 | 2026-05-19 |
```

### Sprint 结束

1. 填写 `总览.md` 的" Sprint 评审"部分
2. 将 Sprint 目录移动到 `archive/`
3. 总结经验教训

---

## 与产品蓝图的关系

| 产品蓝图（输入） | Sprint 计划（输出） |
|------------------|-------------------|
| PRD 中的 Feature | Sprint 中的 Story |
| Stories 索引 | Story 分配到 Sprint |
| 产品优先级 | Sprint 排期 |

**工作模式**：产品蓝图是**输入**，Sprint 计划是**执行方案**。

---

## Common Mistakes

| 错误 | 正确做法 |
|------|----------|
| 在 Sprint 文档中重写产品需求 | 只引用 PRD，不重复产品定义 |
| 混淆产品规划和 Sprint 计划 | 产品蓝图由产品负责，Sprint 计划由开发负责 |
| Story 太大无法在一个 Sprint 完成 | 拆分为更小的 Story |
| 忘记更新进度 | 每日站会前更新状态 |
