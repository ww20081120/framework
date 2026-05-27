---
name: checkstyle-fix
description: 专门用于修复Maven Checkstyle违规的技能，遵循Java代码规范和Spring Boot最佳实践
arguments:
  - name: scope
    description: 修复范围（可选）：single-module（当前模块）、all-modules（所有模块）、specific-file（特定文件）
    required: false
    default: all-modules
  - name: file
    description: 要修复的特定文件路径（仅当scope为specific-file时使用）
    required: false
---

# Checkstyle违规修复工作流

当用户请求修复Checkstyle违规时，请严格按照以下步骤执行：

## 🔍 第一步：诊断阶段

### 1.1 检查违规情况
```bash
# 运行checkstyle检查
mvn checkstyle:check

# 或者查看已有的检查结果
find . -name "checkstyle-result.xml" -not -path "*/node_modules/*" | head -5
```

### 1.2 分析违规类型
查看违规详情，识别主要问题类型：
- 缺少Javadoc注释
- 命名规范问题
- 代码格式问题
- 导入语句问题
- 其他规范问题

**读取违规结果文件并总结违规类型：**
```bash
cat target/checkstyle-result.xml | grep -o 'severity="[^"]*"' | sort | uniq -c
```

## ⚠️ 第二步：修复原则（重要！）

### 2.1 绝对禁止的做法
- ❌ **不要使用`@SuppressWarnings`抑制检查** - 这会掩盖问题
- ❌ **不要将Spring Bean类设置为`final`** - 这会破坏Spring的AOP代理机制
- ❌ **不要将Spring `@Service`、`@Controller`、`@Component`类设为final**
- ❌ **不要将`@Autowired`字段设为final**
- ❌ **不要修改checkstyle配置文件来规避问题**

### 2.2 正确的修复方法
- ✅ **添加适当的Javadoc注释** - 这是最常见的修复方法
- ✅ **修正命名规范** - 遵循Java命名约定
- ✅ **调整代码格式** - 使用IDE格式化功能
- ✅ **优化导入语句** - 删除未使用的导入
- ✅ **添加必要的修饰符** - 如需要的话添加`public`、`private`等

## 🔧 第三步：执行修复

### 3.1 多模块项目处理
```bash
# 列出所有模块
mvn -q exec:exec -Dexec.executable=echo -Dexec.args='${project.modules}'

# 检查哪些模块有违规
for module in cloud-common cloud-api cloud-app cloud-middleware; do
  echo "检查模块: $module"
  cd $module && mvn checkstyle:check 2>&1 | grep -E "(BUILD SUCCESS|BUILD FAILURE|violations)"
  cd ..
done
```

### 3.2 按模块修复
对于每个有违规的模块：

1. **读取违规详情**
```bash
# 查看具体违规
cat target/checkstyle-result.xml | grep -A 2 "error"
```

2. **分类处理违规**

#### 类型A：缺少Javadoc
**常见错误信息：**
- `Missing a Javadoc comment`
- `Expected @param tag for 'parameterName'`
- `Expected @return tag`

**修复方法：**
```java
/**
 * 方法功能的简短描述
 *
 * @param paramName 参数说明
 * @return 返回值说明
 * @throws Exception 异常说明
 */
public void methodName(String paramName) {
    // 方法实现
}
```

#### 类型B：类注释缺失
**修复方法：**
```java
/**
 * 类功能的描述.
 *
 * @author 作者名
 * @since 日期
 */
public class ClassName {
    // 类实现
}
```

#### 类型C：命名规范
- 类名：帕斯卡命名（`ClassName`）
- 方法名：驼峰命名（`methodName`）
- 常量：全大写下划线（`CONSTANT_NAME`）
- 变量：驼峰命名（`variableName`）

#### 类型D：行长度和格式
- 最大行长度：通常为120或150字符
- 使用IDE格式化功能：`Cmd+Shift+F` (IntelliJ) 或 `Ctrl+Shift+F` (Eclipse)

### 3.3 验证修复
```bash
# 每修复一个文件后立即验证编译
mvn compile

# 修复完一个模块后验证checkstyle
mvn checkstyle:check

# 最终验证
mvn clean compile checkstyle:check
```

## 📋 第四步：完成检查清单

### 4.1 单模块检查
- [ ] 所有文件已编译通过
- [ ] `mvn checkstyle:check` 无违规
- [ ] 所有测试通过：`mvn test`

### 4.2 多模块检查
- [ ] 已识别所有包含违规的模块
- [ ] 每个模块都已单独修复
- [ ] 所有模块的checkstyle检查都通过
- [ ] 整个项目编译通过
- [ ] 所有模块的测试通过

### 4.3 最终验证
```bash
# 全项目检查
mvn clean checkstyle:check

# 确认输出包含 "BUILD SUCCESS"
```

## 🚫 常见错误处理

### 问题：批量替换后编译失败
**原因：** 可能是语法错误或类型不匹配
**解决：**
```bash
# 查看编译错误详情
mvn compile 2>&1 | grep -A 5 "ERROR"

# 逐个文件修复，而不是批量替换
```

### 问题：修复后仍有违规
**原因：** 可能遗漏了某些违规类型
**解决：**
```bash
# 重新运行检查，查看剩余违规
mvn checkstyle:check

# 读取详细的违规报告
cat target/checkstyle-result.xml | grep 'error' | wc -l
```

### 问题：添加Javadoc后仍报错
**原因：** Javadoc格式不正确或缺少必要的标签
**解决：** 确保包含所有必要的标签（@param, @return, @throws等）

## 📊 第五步：生成修复报告

完成修复后，向用户报告：

```
✅ Checkstyle修复完成

修复摘要：
- 处理模块数：X个
- 修复文件数：Y个
- 主要问题类型：
  - 缺少Javadoc：Z处
  - 命名规范：N处
  - 其他：M处

验证结果：
- ✅ 编译通过
- ✅ Checkstyle检查通过
- ✅ 测试通过

修改的文件列表：
1. module1/Class1.java - 添加Javadoc注释
2. module2/Class2.java - 修正命名规范
...
```

## 🎯 最佳实践

1. **渐进式修复** - 先修复一个模块，验证后再继续下一个
2. **版本控制** - 每个模块修复后提交一次，便于回滚
3. **测试优先** - 修复前确保所有测试通过
4. **文档同步** - 如果修改了公共API，同步更新相关文档
5. **团队沟通** - 对于架构性的修改，先与团队确认

## 📝 使用示例

### 修复所有模块
```
用户: /checkstyle-fix
或
用户: 修复所有的checkstyle违规
```

### 修复特定模块
```
用户: 修复cloud-common模块的checkstyle违规
```

### 修复特定文件
```
用户: /checkstyle-fix scope=single-module file=cloud-common/src/main/java/com/example/Example.java
```

---

**注意事项：**
- 本技能专为Spring Boot + Maven多模块项目设计
- 遵循Java代码规范和团队编码标准
- 优先保证代码质量和可维护性
- 所有修复都应通过编译和测试验证
