# PMD 代码质量检查配置

本目录包含 PMD 代码质量检查的规则配置。

## 文件说明

### pmd-rules.xml
自定义 PMD 规则集，包含以下类别的检查：
- **最佳实践** (Best Practices)
- **代码风格** (Code Style)
- **设计规范** (Design)
- **错误倾向** (Error Prone)
- **多线程** (Multithreading)
- **性能优化** (Performance)

### pmd-exclude.properties
排除特定文件或特定规则的配置文件。

## 使用方式

### 1. 查看 PMD 检查结果
```bash
mvn pmd:check
```

### 2. 生成 PMD 报告
```bash
mvn pmd:pmd
# 报告位置: target/site/pmd.html
```

### 3. 自定义规则

#### 启用/禁用特定规则
编辑 `pmd-rules.xml`，使用 `<exclude>` 或 `<include>` 标签：

```xml
<rule ref="category/java/bestpractices.xml">
    <exclude name="规则名称"/>
</rule>
```

#### 排除特定文件
编辑 `pmd-exclude.properties`：

```properties
# 排除特定类
com/example/SomeClass.java=EmptyIfStmt

# 排除整个包
com/example/legacy/**=*
```

## 常见 PMD 规则说明

### 最佳实践 (Best Practices)
- `AvoidReassigningParameters` - 避免重新赋值方法参数
- `AvoidStringBufferField` - 避免使用 StringBuffer 作为字段
- `ForLoopCanBeForeach` - for 循环可以改为 foreach

### 代码风格 (Code Style)
- `ShortVariable` - 变量名过短
- `LongVariable` - 变量名过长
- `UnnecessaryConstructor` - 不必要的构造函数

### 设计规范 (Design)
- `SimplifyBooleanReturns` - 简化布尔返回值
- `SwitchStmtsShouldHaveDefault` - switch 语句应该有 default

### 错误倾向 (Error Prone)
- `EmptyIfStmt` - 空的 if 语句
- `EmptyWhileStmt` - 空的 while 语句
- `MissingBreakInSwitch` - switch 中缺少 break

## 调整建议

### 严格模式（生产环境）
减少 `<exclude>` 标签，启用更多规则检查。

### 宽松模式（开发初期）
增加 `<exclude>` 标签，暂时禁用某些规则。

### 持续改进
定期审查和更新规则，逐步提升代码质量。

## 参考资料

- [PMD 官方文档](https://pmd.github.io/)
- [PMD 规则列表](https://pmd.github.io/pmd/pmd_rules_java.html)
