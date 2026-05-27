# 可视化辅助指南

基于浏览器的可视化头脑风暴辅助工具，用于展示 mockup、架构图和选项。

## 何时使用

按问题决定，而不是按会话决定。判断标准：**用户看到它比读到它能更好地理解吗？**

**使用浏览器** 当内容本身是视觉性的：

- **UI mockup** — 线框图、布局、导航结构、组件设计
- **架构图** — 系统组件、数据流、关系图
- **并排视觉对比** — 比较两种布局、两种配色、两种设计方向
- **设计细节** — 关于外观感觉、间距、视觉层次的问题
- **空间关系** — 状态机、流程图、实体关系图

**使用终端** 当内容是文本或表格时：

- **需求和范围问题** — "X 是什么意思？"、"哪些功能在范围内？"
- **概念性 A/B/C 选择** — 用文字描述的方案选择
- **权衡列表** — 优缺点、对比表格
- **技术决策** — API 设计、数据建模、架构方案选择
- **澄清问题** — 答案是文字而非视觉偏好的任何问题

关于 UI 的问题不一定就是视觉问题。"你想要什么样的向导？" 是概念性的——用终端。"这些向导布局哪个感觉更好？" 是视觉性的——用浏览器。

## 工作原理

服务器监视一个目录中的 HTML 文件，将最新的文件提供给浏览器。你将 HTML 内容写入 `screen_dir`，用户在浏览器中看到并可以点击选择选项。选择记录到 `state_dir/events`，你在下一轮对话时读取。

**内容片段 vs 完整文档：** 如果你的 HTML 文件以 `<!DOCTYPE` 或 `<html` 开头，服务器原样提供（仅注入辅助脚本）。否则，服务器自动将你的内容包装在框架模板中——添加头部、CSS 主题、选择指示器和所有交互基础设施。**默认写内容片段。** 只有在需要完全控制页面时才写完整文档。

## 启动会话

**注意：** 脚本路径相对于本技能目录（`.claude/skills/openspec-propose/scripts/`），执行时需使用完整路径或先 cd 到该目录。

```bash
# 启动服务器并持久化（mockup 保存到项目中）
.claude/skills/openspec-propose/scripts/start-server.sh --project-dir /path/to/project

# 返回: {"type":"server-started","port":52341,"url":"http://localhost:52341",
#        "screen_dir":"/path/to/project/.superpowers/brainstorm/12345-1706000000/content",
#        "state_dir":"/path/to/project/.superpowers/brainstorm/12345-1706000000/state"}
```

保存返回的 `screen_dir` 和 `state_dir`。告诉用户打开 URL。

**查找连接信息：** 服务器将启动 JSON 写入 `$STATE_DIR/server-info`。如果你在后台启动服务器且没有捕获输出，读取该文件获取 URL 和端口。使用 `--project-dir` 时，在 `<project>/.superpowers/brainstorm/` 查找会话目录。

**注意：** 将项目根目录作为 `--project-dir` 传入，这样 mockup 会持久化到 `.superpowers/brainstorm/` 并在服务器重启后保留。不传的话文件会存到 `/tmp` 并被清理。提醒用户将 `.superpowers/` 添加到 `.gitignore`（如果还没有的话）。

**按平台启动服务器：**

**Claude Code (macOS / Linux):**
```bash
# 默认模式即可 — 脚本自行将服务器置于后台
scripts/start-server.sh --project-dir /path/to/project
```

**Claude Code (Windows):**
```bash
# Windows 自动检测并使用前台模式，这会阻塞工具调用。
# 在 Bash 工具调用时设置 run_in_background: true，使服务器在
# 对话轮次间保持运行。
scripts/start-server.sh --project-dir /path/to/project
```
通过 Bash 工具调用时，设置 `run_in_background: true`。然后在下一轮读取 `$STATE_DIR/server-info` 获取 URL 和端口。

**Codex:**
```bash
# Codex 会回收后台进程。脚本自动检测 CODEX_CI 并
# 切换到前台模式。正常运行即可——不需要额外参数。
scripts/start-server.sh --project-dir /path/to/project
```

**Gemini CLI:**
```bash
# 使用 --foreground 并在 shell 工具调用中设置 is_background: true
# 使进程在轮次间保持运行
scripts/start-server.sh --project-dir /path/to/project --foreground
```

**其他环境：** 服务器必须在后台跨对话轮次持续运行。如果你的环境会回收分离的进程，使用 `--foreground` 并通过平台的后台执行机制启动命令。

如果 URL 在浏览器中无法访问（常见于远程/容器化环境），绑定非回环主机：

```bash
scripts/start-server.sh \
  --project-dir /path/to/project \
  --host 0.0.0.0 \
  --url-host localhost
```

使用 `--url-host` 控制返回的 URL JSON 中显示的主机名。

## 工作循环

1. **检查服务器存活**，然后**写入 HTML** 到 `screen_dir` 中的新文件：
   - 每次写入前，检查 `$STATE_DIR/server-info` 是否存在。如果不存在（或 `$STATE_DIR/server-stopped` 存在），说明服务器已关闭——在继续之前用 `start-server.sh` 重启。服务器在 30 分钟不活动后自动退出。
   - 使用语义化文件名：`platform.html`、`visual-style.html`、`layout.html`
   - **不要复用文件名** — 每个屏幕用新文件
   - 使用 Write 工具 — **不要用 cat/heredoc**（会在终端输出噪音）
   - 服务器自动提供最新的文件

2. **告诉用户会看到什么并结束你的回合：**
   - 提醒 URL（每一步都提醒，不只是第一次）
   - 简要文字说明屏幕上的内容（如 "正在展示首页的 3 种布局方案"）
   - 请用户在终端回复："看一下然后告诉我你的想法。如果愿意可以点击选择一个选项。"

3. **在下一轮** — 用户在终端回复后：
   - 读取 `$STATE_DIR/events`（如果存在）— 包含用户的浏览器交互（点击、选择）的 JSON 行
   - 与用户的终端文字合并获取完整画面
   - 终端消息是主要反馈；`state_dir/events` 提供结构化交互数据

4. **迭代或前进** — 如果反馈需要改变当前屏幕，写一个新文件（如 `layout-v2.html`）。只有当前步骤验证通过后才进入下一个问题。

5. **返回终端时卸载** — 当下一步不需要浏览器时（如澄清问题、权衡讨论），推送一个等待屏幕来清除过时内容：

   ```html
   <!-- 文件名: waiting.html (或 waiting-2.html 等) -->
   <div style="display:flex;align-items:center;justify-content:center;min-height:60vh">
     <p class="subtitle">正在终端中继续...</p>
   </div>
   ```

   这防止用户盯着一个已经解决的选择，而对话已经进入下一步。当下一个视觉问题出现时，照常推送新内容文件。

6. 重复直到完成。

## 编写内容片段

只写页面内部的内容。服务器自动将其包装在框架模板中（头部、主题 CSS、选择指示器和所有交互基础设施）。

**最小示例：**

```html
<h2>哪种布局更好？</h2>
<p class="subtitle">考虑可读性和视觉层次</p>

<div class="options">
  <div class="option" data-choice="a" onclick="toggleSelect(this)">
    <div class="letter">A</div>
    <div class="content">
      <h3>单列布局</h3>
      <p>清爽、专注的阅读体验</p>
    </div>
  </div>
  <div class="option" data-choice="b" onclick="toggleSelect(this)">
    <div class="letter">B</div>
    <div class="content">
      <h3>双列布局</h3>
      <p>侧边栏导航加主内容区</p>
    </div>
  </div>
</div>
```

就这样。不需要 `<html>`、CSS 或 `<script>` 标签。服务器会提供所有这些。

## 可用的 CSS 类

框架模板为你的内容提供以下 CSS 类：

### 选项（A/B/C 选择）

```html
<div class="options">
  <div class="option" data-choice="a" onclick="toggleSelect(this)">
    <div class="letter">A</div>
    <div class="content">
      <h3>标题</h3>
      <p>描述</p>
    </div>
  </div>
</div>
```

**多选：** 在容器上添加 `data-multiselect` 允许用户选择多个选项。每次点击切换选中状态。指示器栏显示选中数量。

```html
<div class="options" data-multiselect>
  <!-- 同样的选项标记 — 用户可以选/取消选多个 -->
</div>
```

### 卡片（视觉设计）

```html
<div class="cards">
  <div class="card" data-choice="design1" onclick="toggleSelect(this)">
    <div class="card-image"><!-- mockup 内容 --></div>
    <div class="card-body">
      <h3>名称</h3>
      <p>描述</p>
    </div>
  </div>
</div>
```

### Mockup 容器

```html
<div class="mockup">
  <div class="mockup-header">预览：仪表盘布局</div>
  <div class="mockup-body"><!-- 你的 mockup HTML --></div>
</div>
```

### 分屏视图（并排对比）

```html
<div class="split">
  <div class="mockup"><!-- 左侧 --></div>
  <div class="mockup"><!-- 右侧 --></div>
</div>
```

### 优缺点

```html
<div class="pros-cons">
  <div class="pros"><h4>优点</h4><ul><li>好处</li></ul></div>
  <div class="cons"><h4>缺点</h4><ul><li>不足</li></ul></div>
</div>
```

### Mock 元素（线框图构建块）

```html
<div class="mock-nav">Logo | 首页 | 关于 | 联系</div>
<div style="display: flex;">
  <div class="mock-sidebar">导航</div>
  <div class="mock-content">主内容区域</div>
</div>
<button class="mock-button">操作按钮</button>
<input class="mock-input" placeholder="输入框">
<div class="placeholder">占位区域</div>
```

### 排版和章节

- `h2` — 页面标题
- `h3` — 章节标题
- `.subtitle` — 标题下方的次要文字
- `.section` — 带底部间距的内容块
- `.label` — 小号大写标签文字

## 浏览器事件格式

用户在浏览器中点击选项时，交互记录到 `$STATE_DIR/events`（每行一个 JSON 对象）。推送新屏幕时文件自动清空。

```jsonl
{"type":"click","choice":"a","text":"方案 A - 简单布局","timestamp":1706000101}
{"type":"click","choice":"c","text":"方案 C - 复杂网格","timestamp":1706000108}
{"type":"click","choice":"b","text":"方案 B - 混合布局","timestamp":1706000115}
```

完整的事件流展示了用户的探索路径——他们可能在确定前点击多个选项。最后一个 `choice` 事件通常是最终选择，但点击模式可能揭示犹豫或偏好，值得询问。

如果 `$STATE_DIR/events` 不存在，说明用户没有在浏览器中交互——只使用终端文字。

## 设计提示

- **根据问题调整保真度** — 布局问题用线框图，细节问题用精细设计
- **在每个页面上解释问题** — "哪种布局感觉更专业？" 而不是 "选一个"
- **在前进前迭代** — 如果反馈改变了当前屏幕，写一个新版本
- **每屏最多 2-4 个选项**
- **在重要时使用真实内容** — 比如摄影作品集，使用真实图片（Unsplash）。占位内容会掩盖设计问题。
- **保持 mockup 简洁** — 聚焦于布局和结构，不是像素级设计

## 文件命名

- 使用语义化名称：`platform.html`、`visual-style.html`、`layout.html`
- 不要复用文件名 — 每个屏幕必须是新文件
- 迭代版本：追加版本后缀如 `layout-v2.html`、`layout-v3.html`
- 服务器按修改时间提供最新文件

## 清理

```bash
scripts/stop-server.sh $SESSION_DIR
```

如果会话使用了 `--project-dir`，mockup 文件会保留在 `.superpowers/brainstorm/` 中供后续参考。只有 `/tmp` 会话在停止时会被删除。

## 参考

- 框架模板（CSS 参考）：`scripts/frame-template.html`
- 辅助脚本（客户端）：`scripts/helper.js`
