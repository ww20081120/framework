# AgentsRegistrar 改进说明

## 问题描述

在原始的 `AgentsRegistrar` 实现中，当扫描和注册带有 `@Component` 或 `@Service` 注解的类时，使用 `ClassUtils.getShortName(beanName)` 方法获取简单类名作为 bean 名称。这种方式在以下情况下会导致名称冲突：

1. 不同包下存在同名类，例如：
   - `com.hbasesoft.framework.ai.agent.file.memory.MemoryServiceImpl`
   - `com.hbasesoft.framework.ai.agent.jpa.memory.service.impl.MemoryServiceImpl`

2. 这两个类都会被注册为 `memoryServiceImpl`，导致 Spring 容器中的 bean 名称冲突。

## 改进方案

改进后的 `AgentsRegistrar` 采用以下策略来生成唯一的 bean 名称：

### 1. 首选简单类名
首先尝试使用简单类名作为 bean 名称，保持向后兼容性。

### 2. 包名前缀解决冲突
当检测到名称冲突时，使用包名的最后一部分作为前缀来区分：
- `com.hbasesoft.framework.ai.agent.file.memory.MemoryServiceImpl` → `memoryMemoryServiceImpl`
- `com.hbasesoft.framework.ai.agent.jpa.memory.service.impl.MemoryServiceImpl` → `implMemoryServiceImpl`

### 3. 序号后缀处理重复
如果包名前缀仍然导致冲突，则添加序号后缀来确保唯一性。

## 实现细节

```java
private String generateUniqueBeanName(String beanClassName, BeanDefinitionRegistry registry,
        Map<String, Integer> registeredBeanNames) {
    // 首先尝试使用简单类名
    String simpleBeanName = ClassUtils.getShortName(beanClassName);
    simpleBeanName = Character.toLowerCase(simpleBeanName.charAt(0)) + simpleBeanName.substring(1);

    // 检查是否已存在同名的 bean
    if (registry.containsBeanDefinition(simpleBeanName) || registeredBeanNames.containsKey(simpleBeanName)) {
        // 如果存在冲突，使用包名前缀来区分
        String packageName = ClassUtils.getPackageName(beanClassName);
        // 提取包名的最后一部分作为前缀
        String[] packageParts = packageName.split("\\.");
        String packageSuffix = packageParts.length > 0 ? packageParts[packageParts.length - 1] : "";

        // 构造带包名前缀的 bean 名称
        String prefixedBeanName = packageSuffix + simpleBeanName.substring(0, 1).toUpperCase()
                + simpleBeanName.substring(1);

        // 检查带前缀的名称是否也存在冲突
        if (registry.containsBeanDefinition(prefixedBeanName) || registeredBeanNames.containsKey(prefixedBeanName)) {
            // 如果仍然存在冲突，添加序号后缀
            Integer count = registeredBeanNames.getOrDefault(prefixedBeanName, 1);
            String numberedBeanName = prefixedBeanName + count;
            while (registry.containsBeanDefinition(numberedBeanName)
                    || registeredBeanNames.containsKey(numberedBeanName)) {
                count++;
                numberedBeanName = prefixedBeanName + count;
            }
            registeredBeanNames.put(prefixedBeanName, count + 1);
            return numberedBeanName;
        } else {
            registeredBeanNames.put(prefixedBeanName, 1);
            return prefixedBeanName;
        }
    } else {
        // 没有冲突，直接使用简单类名
        registeredBeanNames.put(simpleBeanName, 1);
        return simpleBeanName;
    }
}
```

## 示例效果

假设有以下两个类：

1. `com.hbasesoft.framework.ai.agent.file.memory.MemoryServiceImpl`
2. `com.hbasesoft.framework.ai.agent.jpa.memory.service.impl.MemoryServiceImpl`

改进前：
- 两者都会注册为 `memoryServiceImpl`，导致冲突

改进后：
- 第一个类注册为 `memoryMemoryServiceImpl`
- 第二个类注册为 `implMemoryServiceImpl`

这样就成功避免了名称冲突问题，同时保持了 bean 名称的可读性和逻辑性。
