# Checkstyle修复技能 - 快速示例

## 场景1：快速诊断问题

```
用户: 帮我检查一下项目的checkstyle问题

Claude会执行：
1. 运行 mvn checkstyle:check
2. 分析违规类型和数量
3. 生成问题报告

示例输出：
📊 Checkstyle问题诊断报告

总体情况：
- 总违规数：66个
- 涉及模块：3个
- 主要问题：缺少Javadoc (58处)，命名规范 (8处)

模块详情：
- cloud-common: 12个违规
- cloud-api: 28个违规
- cloud-app: 26个违规
```

## 场景2：修复特定类型的问题

```
用户: 只修复cloud-api模块缺少Javadoc的问题

Claude会执行：
1. 进入cloud-api模块
2. 找出所有缺少Javadoc的类和方法
3. 添加适当的Javadoc注释
4. 验证修复结果
```

## 场景3：批量修复后的验证

```
用户: 我已经手动修复了一些文件，帮我验证一下checkstyle

Claude会执行：
1. mvn clean compile checkstyle:check
2. 检查是否还有遗留问题
3. 如果有，列出剩余问题
4. 提供修复建议
```

## 常用命令参考

### 手动执行命令

```bash
# 检查所有模块的checkstyle
mvn checkstyle:check

# 检查特定模块
cd cloud-common && mvn checkstyle:check

# 清理后重新检查
mvn clean checkstyle:check

# 生成checkstyle报告
mvn checkstyle:checkstyle

# 查看违规详情
cat target/checkstyle-result.xml
```

### 查看特定类型的违规

```bash
# 查找所有Javadoc相关违规
grep -i "javadoc" target/checkstyle-result.xml

# 统计违规数量
grep -c "error" target/checkstyle-result.xml

# 按严重程度分组
grep "severity" target/checkstyle-result.xml | sort | uniq -c
```

## 常见Javadoc模板

### Service类

```java
/**
 * ${业务领域}服务实现.
 *
 * @author 作者名
 * @since 2026-02-27
 */
@Service
public class UserService {

    /**
     * 根据ID查询用户信息.
     *
     * @param id 用户ID
     * @return 用户信息，如果不存在返回null
     */
    public User getById(Long id) {
        return userMapper.selectById(id);
    }
}
```

### Controller类

```java
/**
 * ${业务领域}控制器.
 *
 * @author 作者名
 * @since 2026-02-27
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    /**
     * 创建新用户.
     *
     * @param request 用户创建请求
     * @return 创建的用户信息
     */
    @PostMapping
    public ResponseEntity<User> create(@RequestBody UserRequest request) {
        // 实现
    }
}
```

### 工具类

```java
/**
 * ${功能描述}工具类.
 *
 * @author 作者名
 * @since 2026-02-27
 */
public class StringUtil {

    /**
     * 判断字符串是否为空.
     *
     * @param str 待判断的字符串
     * @return 如果为null或空字符串返回true，否则返回false
     */
    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }
}
```

## 实际使用案例

### 案例1：新开发功能后的检查

```
开发流程：
1. 编写代码
2. 运行：/checkstyle-fix
3. Claude自动修复违规
4. 继续开发
```

### 案例2：代码审查前

```
代码审查前检查：
1. 提交代码前运行：/checkstyle-fix
2. 确保无违规后再提交
3. 减少审查时的格式问题反馈
```

### 案例3：CI/CD集成

```bash
# 在CI脚本中添加
#!/bin/bash
mvn clean checkstyle:check
if [ $? -ne 0 ]; then
  echo "Checkstyle检查失败，请修复违规后再提交"
  exit 1
fi
```

## 故障排查速查表

| 问题 | 检查命令 | 解决方案 |
|-----|---------|---------|
| 修复后仍有违规 | `mvn clean checkstyle:check` | 清理缓存后重试 |
| 编译失败 | `mvn compile 2>&1 \| grep ERROR` | 查看具体错误信息 |
| 找不到违规文件 | `find . -name "checkstyle-result.xml"` | 检查是否在正确目录 |
| 部分模块未修复 | `mvn checkstyle:check -pl \`ls -d */\`` | 检查所有子模块 |

---

**提示：** 这些示例可以直接在Claude Code中使用，支持自然语言描述，不需要记忆命令。
