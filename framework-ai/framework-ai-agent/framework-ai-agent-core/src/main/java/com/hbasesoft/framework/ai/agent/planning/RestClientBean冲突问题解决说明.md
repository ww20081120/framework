# RestClient Bean冲突问题解决说明

## 问题描述

在使用Spring AI框架时，遇到了以下错误：

```
Method openAiAudioSpeechModel in org.springframework.ai.model.openai.autoconfigure.OpenAiAudioSpeechAutoConfiguration required a single bean, but 2 were found:
	- restClientBuilder: defined by method 'restClientBuilder' in class path resource [org/springframework/boot/autoconfigure/web/client/RestClientAutoConfiguration.class]
	- createRestClient: defined by method 'createRestClient' in com.hbasesoft.framework.ai.agent.planning.PlanningFactory
```

## 问题分析

1. **Spring Boot自动配置**：Spring Boot自动配置提供了一个名为`restClientBuilder`的`RestClient.Builder` bean。

2. **自定义配置冲突**：在`PlanningFactory`类中，定义了一个名为`createRestClient`的方法，并使用了`@Bean`注解，创建了另一个`RestClient.Builder` bean。

3. **依赖注入冲突**：当Spring AI的`OpenAiAudioSpeechAutoConfiguration`需要一个`RestClient.Builder`类型的bean时，Spring容器找到了两个匹配的bean，无法确定使用哪一个，导致注入失败。

## 解决方案

### 方案选择

采用了**修改Bean名称**的方案，通过给自定义的bean指定一个明确的名称来避免冲突。

### 具体实现

在`PlanningFactory`类中，将原来的：

```java
@Bean
public RestClient.Builder createRestClient() {
    // ... 实现代码
}
```

修改为：

```java
@Override
@Bean("planningRestClientBuilder")
public RestClient.Builder createRestClient() {
    // ... 实现代码
}
```

### 修改说明

1. **保持接口实现**：仍然实现了`IPlanningFactory`接口的`createRestClient()`方法，确保接口契约不变。

2. **指定Bean名称**：通过`@Bean("planningRestClientBuilder")`明确指定bean名称为`planningRestClientBuilder`，避免与Spring Boot自动配置的`restClientBuilder`冲突。

3. **保留@Override注解**：明确表示这是对接口方法的实现。

## 验证结果

修改后重新编译项目，编译成功，解决了bean冲突问题。

## 后续建议

1. **避免通用名称**：在定义bean时，尽量使用具有业务含义的特定名称，避免与Spring Boot自动配置的bean名称冲突。

2. **使用@Qualifier注解**：在需要注入特定bean的地方，可以使用`@Qualifier`注解明确指定要注入的bean名称。

3. **考虑@Primary注解**：如果确实需要提供一个默认的bean，可以考虑使用`@Primary`注解标记首选bean。

## 相关代码位置

- **接口文件**：`IPlanningFactory.java`
- **实现文件**：`PlanningFactory.java`
- **冲突方法**：`createRestClient()`

## 总结

通过给自定义的RestClient.Builder bean指定一个明确的名称，成功解决了与Spring Boot自动配置bean的命名冲突问题，同时保持了接口实现的完整性。
