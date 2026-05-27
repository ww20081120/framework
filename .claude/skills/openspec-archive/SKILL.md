---
name: openspec-archive
description: Use when user wants to finalize and archive a completed change after implementation is done
license: MIT
compatibility: 需要安装 openspec CLI。
metadata:
  author: openspec
  version: "1.0"
  generatedBy: "1.3.0"
---

在实验性工作流中归档已完成的变更。

**输入**：可选择指定变更名称。如果省略，检查是否可以从对话上下文中推断。如果模糊或有歧义，你**必须**提示用户选择可用的变更。

**步骤**

1. **如果未提供变更名称，提示选择**

   运行 `openspec list --json` 获取可用变更。使用 **AskUserQuestion 工具** 让用户选择。

   仅显示活跃变更（不包括已归档的）。
   如果可用，包含每个变更所使用的模式。

   **重要**：不要猜测或自动选择变更。始终让用户选择。

2. **检查产物完成状态**

   运行 `openspec status --change "<name>" --json` 检查产物完成情况。

   解析 JSON 以了解：
   - `schemaName`：正在使用的工作流
   - `artifacts`：产物列表及其状态（`done` 或其他）

   **如果有产物未完成（状态不是 `done`）：**
   - 显示警告，列出未完成的产物
   - 使用 **AskUserQuestion 工具** 确认用户是否要继续
   - 用户确认后继续

3. **检查任务完成状态**

   读取任务文件（通常是 `tasks.md`）检查是否有未完成的任务。

   统计标记为 `- [ ]`（未完成）和 `- [x]`（已完成）的任务数量。

   **如果发现未完成的任务：**
   - 显示警告，显示未完成任务的计数
   - 使用 **AskUserQuestion 工具** 确认用户是否要继续
   - 用户确认后继续

   **如果不存在任务文件：** 无需任务相关警告，直接继续。

4. **评估增量规格同步状态（可选功能）**

   **注意：此步骤需要 `openspec-sync-specs` 技能支持。如果该技能未安装，跳过此步骤。**

   检查 `openspec/changes/<name>/specs/` 中是否存在增量规格。如果不存在，直接继续，不提示同步。

   **如果增量规格存在且 `openspec-sync-specs` 技能可用：**
   - 将每个增量规格与 `openspec/specs/<capability>/spec.md` 中的对应主规格进行比较
   - 确定需要应用的变更（新增、修改、删除、重命名）
   - 在提示前显示合并摘要

   **提示选项：**
   - 如果需要同步："立即同步（推荐）"、"不同步直接归档"
   - 如果已同步："立即归档"、"重新同步"、"取消"

   如果用户选择同步，使用 Agent 工具（subagent_type: "general-purpose"，prompt: "使用 Skill 工具调用 openspec-sync-specs 处理变更 '<name>'。增量规格分析：<包含分析的增量规格摘要>"）。无论选择什么，都继续归档。

   **如果 `openspec-sync-specs` 技能不可用但增量规格存在：**
   - 提示："检测到增量规格，但同步技能未安装。可以手动同步或直接归档。"

5. **执行归档**

   如果归档目录不存在则创建：
   ```bash
   mkdir -p openspec/changes/archive
   ```

   使用当前日期生成目标名称：`YYYY-MM-DD-<change-name>`

   **检查目标是否已存在：**
   - 如果已存在：失败并报错，建议重命名现有归档或使用不同日期
   - 如果不存在：将变更目录移动到归档目录

   ```bash
   mv openspec/changes/<name> openspec/changes/archive/YYYY-MM-DD-<name>
   ```

6. **显示摘要**

   显示归档完成摘要，包括：
   - 变更名称
   - 使用的模式
   - 归档位置
   - 是否同步了规格（如适用）
   - 关于任何警告的说明（未完成的产物/任务）

**成功时的输出**

```
## 归档完成

**变更：** <change-name>
**模式：** <schema-name>
**归档至：** openspec/changes/archive/YYYY-MM-DD-<name>/
**规格：** ✓ 已同步到主规格（或 "无增量规格" 或 "同步已跳过"）

所有产物已完成。所有任务已完成。
```

**约束规则**
- 如果未提供变更，始终提示选择
- 使用产物图（openspec status --json）检查完成状态
- 遇到警告时不要阻止归档 - 只需通知并确认
- 移动到归档时保留 .openspec.yaml（它会随目录一起移动）
- 显示清晰的操作结果摘要
- 如果请求同步，使用 openspec-sync-specs 方法（代理驱动）
- 如果增量规格存在，始终运行同步评估并在提示前显示合并摘要
