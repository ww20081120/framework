# Git Hooks 配置

本目录存放项目的 Git Hook 脚本，用于在代码提交前自动执行代码质量检查。

## 文件说明

### pre-commit.sh
Git pre-commit hook 脚本，在每次执行 `git commit` 前自动运行以下检查：

1. **CheckStyle 检查** - 检查代码风格是否符合规范
2. **PMD 检查** - 检查代码质量和潜在的bug
3. **SpotBugs 检查** - 检查常见的Java代码缺陷

## 工作原理

1. `pom.xml` 中配置了 `githook-maven-plugin` 插件
2. 当执行 `mvn initialize` 时，插件会自动安装 git hooks
3. 安装的 hook 会调用本目录下的 `pre-commit.sh` 脚本
4. 脚本通过 `${maven.multiModuleProjectDirectory}` 变量定位项目根目录

## 配置开关

代码质量检查可以通过 `pom.xml` 中的属性控制：

```xml
<code.quality.checks.enabled>true</code.quality.checks.enabled>
```

- `true` - 启用检查（默认）
- `false` - 禁用检查

## 手动测试

可以手动运行脚本来测试：

```bash
bash config/githooks/pre-commit.sh
```

## 维护说明

将脚本提取到外部文件的好处：

1. **避免 XML 格式化破坏** - shell 脚本不会被 XML 格式化工具修改
2. **更易维护** - 可以使用标准的 shell 脚本编辑器
3. **版本控制友好** - git diff 会更清晰显示脚本变更
4. **独立测试** - 可以单独测试脚本逻辑
