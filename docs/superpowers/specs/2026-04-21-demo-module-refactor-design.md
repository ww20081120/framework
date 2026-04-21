# Demo 模块重构设计文档

## 概述

将 `framework-db-demo` 拆分为两个独立的测试模块：`framework-db-demo-mysql`（MySQL + JPA）和 `framework-db-demo-mongo`（MongoDB），使用 Testcontainers 管理数据库容器，将所有测试从 `src/main` 迁移到 `src/test`，目标行覆盖率 >= 70%。

## 1. 模块结构变更

### 1.1 变更前

```
framework-db/
├── framework-db-demo          # 合并的 demo（MySQL + MongoDB）
```

### 1.2 变更后

```
framework-db/
├── framework-db-demo-mysql    # MySQL/JPA 测试模块（新建）
├── framework-db-demo-mongo    # MongoDB 测试模块（新建）
```

原 `framework-db-demo` 模块删除。

### 1.3 父 pom.xml 变更

**根 pom.xml（`/pom.xml`）：**

- `<properties>` 新增：`<testcontainers.version>1.20.6</testcontainers.version>`
- `<dependencyManagement>` 新增 testcontainers-bom：

```xml
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>testcontainers-bom</artifactId>
    <version>${testcontainers.version}</version>
    <type>pom</type>
    <scope>import</scope>
</dependency>
```

**framework-db/pom.xml：**

- `<modules>` 列表移除 `framework-db-demo`
- `<modules>` 新增 `framework-db-demo-mysql` 和 `framework-db-demo-mongo`

## 2. MySQL 测试模块设计

### 2.1 目录结构

```
framework-db-demo-mysql/
├── pom.xml
└── src/
    ├── main/java/com/hbasesoft/framework/db/demo/mysql/
    │   ├── entity/
    │   │   ├── StudentEntity.java
    │   │   ├── CourseEntity.java
    │   │   ├── StaffEntity.java
    │   │   └── CountEntity.java
    │   └── dao/
    │       ├── StudentDao.java
    │       ├── CourseDao.java
    │       ├── StaffDao.java
    │       └── mysql/
    │           ├── IStudentMySqlDao.java
    │           ├── ICourseMySqlDao.java
    │           └── StaffMySqlDao.java
    └── test/java/com/hbasesoft/framework/db/demo/mysql/
        ├── config/
        │   └── AbstractMysqlTestConfig.java
        ├── student/
        │   ├── StudentCrudTest.java
        │   ├── StudentQueryTest.java
        │   └── StudentPaginationTest.java
        ├── staff/
        │   ├── StaffCrudTest.java
        │   ├── StaffLambdaQueryTest.java
        │   ├── StaffSpecificationTest.java
        │   └── StaffBatchOperationTest.java
        └── course/
            └── CourseCrudTest.java
```

### 2.2 依赖

```xml
<dependencies>
    <dependency>
        <groupId>com.hbasesoft.framework</groupId>
        <artifactId>framework-db-jpa</artifactId>
        <version>${project.parent.version}</version>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.testcontainers</groupId>
        <artifactId>mysql</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>com.mysql</groupId>
        <artifactId>mysql-connector-j</artifactId>
        <scope>runtime</scope>
    </dependency>
</dependencies>
```

### 2.3 Testcontainers 配置

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public abstract class AbstractMysqlTestConfig {

    static final MySQLContainer<?> MYSQL_CONTAINER;

    static {
        MYSQL_CONTAINER = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("test_db")
            .withUsername("test")
            .withPassword("test");
        MYSQL_CONTAINER.start();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("master.db.url", MYSQL_CONTAINER::getJdbcUrl);
        registry.add("master.db.username", MYSQL_CONTAINER::getUsername);
        registry.add("master.db.password", MYSQL_CONTAINER::getPassword);
    }
}
```

### 2.4 测试类方法覆盖映射

| 测试类 | 覆盖的 BaseDao/BaseJpaDao 方法 |
|--------|------------------------------|
| StudentCrudTest | save, saveBatch, update, updateBySql, delete, deleteById, deleteByIds |
| StudentQueryTest | get, getByHql, getByProperty, queryAll, queryByHql, queryByProperty, queryBySql |
| StudentPaginationTest | queryPager, queryStudentCourse, countCourse, countCoursePass, groupBy, executeBatch |
| StaffCrudTest | save, saveBatch, update, updateBatch, delete, deleteById, deleteByIds, deleteBatch |
| StaffLambdaQueryTest | getByLambda, queryByLambda, deleteByLambda, updateByLambda, queryPagerByLambda |
| StaffSpecificationTest | getBySpecification, queryBySpecification, updateBySpecification, deleteBySpecification, queryPagerBySpecification |
| StaffBatchOperationTest | executeBatch, updateByCriteria, deleteByCriteria |
| CourseCrudTest | save, delete, get, queryAll |

### 2.5 数据管理策略

- 每个测试方法使用 `@Transactional` 自动回滚，保证测试隔离
- 测试数据在方法内构造，不依赖外部 CSV/JSON 文件
- 使用 AssertJ 断言（`assertThat`, `assertThatThrownBy`）

## 3. MongoDB 测试模块设计

### 3.1 目录结构

```
framework-db-demo-mongo/
├── pom.xml
└── src/
    ├── main/java/com/hbasesoft/framework/db/demo/mongo/
    │   ├── entity/
    │   │   ├── StudentEntity.java
    │   │   ├── CourseEntity.java
    │   │   └── CountEntity.java
    │   └── dao/
    │       ├── StudentDao.java
    │       ├── CourseDao.java
    │       └── mongodb/
    │           ├── IStudentMongoDBDao.java
    │           └── ICourseMongoDBDao.java
    └── test/java/com/hbasesoft/framework/db/demo/mongo/
        ├── config/
        │   └── AbstractMongoTestConfig.java
        ├── student/
        │   ├── MongoStudentCrudTest.java
        │   └── MongoStudentQueryTest.java
        └── course/
            └── MongoCourseCrudTest.java
```

### 3.2 依赖

```xml
<dependencies>
    <dependency>
        <groupId>com.hbasesoft.framework</groupId>
        <artifactId>framework-db-mongo</artifactId>
        <version>${project.parent.version}</version>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.testcontainers</groupId>
        <artifactId>mongodb</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

### 3.3 Testcontainers 配置

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public abstract class AbstractMongoTestConfig {

    static final MongoDBContainer MONGO_CONTAINER;

    static {
        MONGO_CONTAINER = new MongoDBContainer("mongo:7.0");
        MONGO_CONTAINER.start();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("master.mongodb.url", () ->
            MONGO_CONTAINER.getConnectionString() + "/test01?retryWrites=false");
    }
}
```

### 3.4 数据清理策略

MongoDB 不支持 `@Transactional` 自动回滚，采用以下策略：

- 每个测试类使用 `@BeforeEach` 清理对应集合
- 使用 `@DirtiesContext` 在必要时重置 Spring 上下文
- 或利用 MongoDB 7.0 的事务支持（如可用）

### 3.5 测试类方法覆盖映射

| 测试类 | 覆盖的 BaseDao/BaseMongoDao 方法 |
|--------|-------------------------------|
| MongoStudentCrudTest | save, saveBatch, update, updateBatch, delete, deleteById, deleteByIds |
| MongoStudentQueryTest | get, query, queryAll, queryPager, countCoursePass, countStudentSize |
| MongoCourseCrudTest | save, delete, get, queryAll |

## 4. 覆盖率策略

### 4.1 工具

JaCoCo Maven Plugin

### 4.2 覆盖率目标

| 层级 | 目标 |
|------|------|
| Entity 类 | >= 90%（Lombok 生成代码不纳入） |
| BaseDao 方法（MySQL） | >= 70% |
| BaseJpaDao 扩展方法 | >= 70% |
| BaseMongoDao 方法 | >= 70% |

### 4.3 JaCoCo 配置

每个 demo 子模块的 `pom.xml` 添加：

```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <executions>
        <execution>
            <goals><goal>prepare-agent</goal></goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals><goal>report</goal></goals>
        </execution>
    </executions>
</plugin>
```

## 5. 测试规范

### 5.1 断言

统一使用 AssertJ：

```java
assertThat(result).isNotNull();
assertThat(result.getName()).isEqualTo("expected");
assertThatThrownBy(() -> dao.get(null)).isInstanceOf(Exception.class);
```

### 5.2 异常场景

- 查询不存在的 ID
- null/空参数
- 分页边界（pageIndex=0, pageSize=1）

### 5.3 数据验证

每次写操作后立即查询验证数据一致性。

## 6. 迁移计划

1. 根 pom.xml 添加 testcontainers 版本管理和 BOM
2. framework-db/pom.xml 更新 modules 列表
3. 创建 framework-db-demo-mysql 模块骨架
4. 迁移实体类和 DAO 接口到 MySQL 模块
5. 编写 MySQL 模块的 Testcontainers 配置和测试类
6. 创建 framework-db-demo-mongo 模块骨架
7. 迁移实体类和 DAO 接口到 MongoDB 模块
8. 编写 MongoDB 模块的 Testcontainers 配置和测试类
9. 添加 JaCoCo 配置并验证覆盖率
10. 删除原 framework-db-demo 模块
