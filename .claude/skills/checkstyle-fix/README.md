# Checkstyle修复技能使用指南

## 📌 技能概述

`checkstyle-fix` 是一个专门用于修复Maven Checkstyle违规的自定义技能。它遵循Java代码规范和Spring Boot最佳实践，特别适配于多模块Maven项目。

## 🚀 快速开始

### 基本用法

在Claude Code中直接调用：

```
/checkstyle-fix
```

或者用自然语言描述：

```
修复所有的checkstyle违规
```

### 高级用法

**修复特定模块：**
```
修复cloud-common模块的checkstyle违规
```

**修复特定文件：**
```
修复这个文件的checkstyle问题：cloud-common/src/main/java/com/example/Example.java
```

## ✨ 核心特性

### ✅ 正确的修复方法

- ✨ 添加适当的Javadoc注释
- ✨ 修正命名规范
- ✨ 调整代码格式
- ✨ 优化导入语句

### ❌ 绝对避免的做法

- ❌ 使用`@SuppressWarnings`抑制检查
- ❌ 将Spring Bean类设置为`final`（会破坏AOP代理）
- ❌ 修改checkstyle配置文件规避问题

## 📋 工作流程

该skill会自动执行以下步骤：

1. **诊断** - 运行`mvn checkstyle:check`识别违规
2. **分析** - 分类违规类型和数量
3. **修复** - 按模块逐一修复问题
4. **验证** - 编译、checkstyle检查、测试
5. **报告** - 生成修复摘要

## 🔧 技术细节

### 支持的违规类型

| 违规类型 | 修复方法 |
|---------|---------|
| 缺少Javadoc | 添加类/方法/字段的Javadoc注释 |
| 命名规范 | 修正为标准Java命名约定 |
| 行长度超限 | 重构长行或调整格式 |
| 导入问题 | 删除未使用的导入或优化导入顺序 |
| 修饰符缺失 | 添加必要的public/private等修饰符 |

### 多模块项目支持

自动识别项目中的所有Maven模块，并按以下顺序处理：

```
framework-cloud (根项目)
├── cloud-common      # 优先修复公共模块
├── cloud-api
├── cloud-app
├── cloud-bootstrap
└── cloud-middleware
```

## 📊 使用示例

### 示例1：全项目修复

```
用户: /checkstyle-fix

Claude执行：
1. mvn checkstyle:check (检查所有模块)
2. 发现66个违规，分布在3个模块
3. 逐模块修复
4. 验证：✅ 编译通过 ✅ Checkstyle通过 ✅ 测试通过

修复报告：
✅ Checkstyle修复完成
- 处理模块数：3个
- 修复文件数：12个
- 主要问题：缺少Javadoc (58处)，命名规范 (8处)
```

### 示例2：单模块修复

```
用户: 修复cloud-api模块的checkstyle问题

Claude执行：
1. cd cloud-api && mvn checkstyle:check
2. 发现8个违规
3. 修复所有违规
4. 验证通过

修复报告：
✅ cloud-api模块修复完成
- 修复文件数：3个
- 违规数：8 → 0
```

## 🎯 最佳实践

1. **定期使用** - 在每次代码提交前运行
2. **渐进修复** - 大项目建议分模块逐步修复
3. **版本控制** - 每个模块修复后单独提交
4. **团队协作** - 统一团队的checkstyle配置

## 🔍 故障排查

### 问题：修复后仍有违规
**解决：** 运行 `mvn clean checkstyle:check` 清理缓存后重新检查

### 问题：编译失败
**解决：** 检查批量替换是否引入语法错误，逐个文件验证

### 问题：测试失败
**解决：** 修复可能破坏了逻辑，使用git diff查看具体改动

## 📝 自定义配置

如果需要调整checkstyle规则，编辑项目的checkstyle配置文件：

```xml
<!-- checkstyle-rules.xml -->
<module name="Checker">
    <module name="TreeWalker">
        <!-- 自定义规则 -->
    </module>
</module>
```

## 🔗 相关资源

- [Checkstyle官方文档](https://checkstyle.sourceforge.io/)
- [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)
- [Spring Boot编码规范](https://spring.io/guides)

---

**提示：** 使用前请确保所有测试已通过，这样可以更容易识别修复引入的问题。
