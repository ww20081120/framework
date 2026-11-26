# Framework 4.X 编码规范

## 1. 通用规范

* **编码**: 所有文件使用 UTF-8 编码。
* **换行符**: 使用 Unix 风格换行符 (LF)。
* **缩进**: 使用 4 个空格进行缩进，禁止使用 Tab。
* **行宽**: 单行代码尽量不超过 120 个字符。
* **命名**:
    *   采用有意义的英文命名，禁止使用拼音。
    *   类名使用 UpperCamelCase (大驼峰式) 风格。
    *   方法名、变量名、参数名使用 lowerCamelCase (小驼峰式) 风格。
    *   常量命名全部大写，单词间用下划线隔开。
    *   包名统一使用小写。
* **注释**:
    *   重要类和公共方法必须有 Javadoc 注释。
    *   关键业务逻辑和复杂算法应有行内注释说明。
    *   注释应清晰、准确、简洁，与代码同步更新。

## 2. Java 代码规范

* **代码风格**: 遵循阿里巴巴《Java开发手册》。
* **依赖管理**:
    *   所有第三方库依赖必须在 `framework-dependencies/pom.xml` 中统一管理版本。
    *   子模块按需引入依赖，避免冗余。
* **异常处理**:
    *   禁止捕获异常后不作任何处理。
    *   自定义异常应继承自 `RuntimeException` 或其子类。
    *   异常信息应清晰描述问题，便于排查。
* **日志记录**:
    *   使用 SLF4J API 进行日志记录。
    *   日志级别应合理使用 (ERROR, WARN, INFO, DEBUG, TRACE)。
    *   不要在循环中打印 DEBUG 或 TRACE 级别的日志。
* **资源管理**:
    *   对于数据库连接、文件流、网络连接等资源，使用 try-with-resources 语句确保正确关闭。
* **单元测试**:
    *   新增或修改功能必须编写单元测试。
    *   使用 JUnit 5 作为测试框架。
    *   测试覆盖率应达到一定标准（具体标准由项目组定义）。

## 3. Maven 规范

* **模块组织**: 采用多模块Maven结构，每个功能点或可复用组件独立成模块。
* **命名**:
    *   父模块 artifactId 为 `framework`。
    *   子模块 artifactId 格式为 `framework-{module-name}`。
    *   核心实现模块为 `framework-{module-name}-core`。
    *   不同中间件或技术的实现为 `framework-{module-name}-{tech}`。
    *   示例或演示模块为 `framework-{module-name}-demo`。
* **版本管理**: 所有子模块的版本号与父模块保持一致，在 `framework-dependencies` 中统一管理第三方依赖版本。

## 4. Git 提交规范

* **提交信息**: 提交信息应清晰描述本次提交的目的和内容。
* **Checkstyle**: 通过 Git Hook 在提交前自动运行 Checkstyle 检查，确保代码风格统一。