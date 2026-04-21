# Demo 模块重构实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 将 framework-db-demo 拆分为 framework-db-demo-mysql 和 framework-db-demo-mongo 两个独立测试模块，使用 Testcontainers 管理数据库容器，所有测试迁移到 src/test，目标覆盖率 >= 70%。

**Architecture:** 两个独立 Maven 模块，各自拥有 Testcontainers 配置基类，按实体+功能分组组织测试类。MySQL 模块使用 `@Transactional` 回滚保证隔离，MongoDB 模块使用 `@BeforeEach` 清理数据。依赖版本由根 pom.xml 统一管理。

**Tech Stack:** Spring Boot 4.0.5, Java 21, JUnit 5, AssertJ, Testcontainers 1.20.6, MySQL 8.0, MongoDB 7.0, JaCoCo

---

### Task 1: 更新根 pom.xml 添加 testcontainers 版本管理

**Files:**
- Modify: `/Users/wangwei/Projects/f41/pom.xml`

- [ ] **Step 1: 在 `<properties>` 中添加 testcontainers 版本**

在 `pom.xml:77` 的 `<hibernate.version>` 之后添加：

```xml
<testcontainers.version>1.20.6</testcontainers.version>
```

- [ ] **Step 2: 在 `<dependencyManagement>` 中添加 testcontainers-bom**

在 `pom.xml` 的 `<dependencyManagement><dependencies>` 中，`spring-ai-bom` 之后添加：

```xml
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>testcontainers-bom</artifactId>
    <version>${testcontainers.version}</version>
    <type>pom</type>
    <scope>import</scope>
</dependency>
```

- [ ] **Step 3: 验证修改**

Run: `cd /Users/wangwei/Projects/f41 && mvn help:effective-pom -pl framework-db/framework-db-demo-mysql -N 2>&1 | head -5`
预期：无输出（模块尚不存在），但根 pom 编译不报错

Run: `cd /Users/wangwei/Projects/f41 && mvn validate -N`
预期：BUILD SUCCESS

---

### Task 2: 更新 framework-db/pom.xml 的模块列表

**Files:**
- Modify: `/Users/wangwei/Projects/f41/framework-db/pom.xml`

- [ ] **Step 1: 替换 modules 列表**

将 `/Users/wangwei/Projects/f41/framework-db/pom.xml` 的 `<modules>` 节替换为：

```xml
<modules>
    <module>framework-db-core</module>
    <module>framework-db-jpa</module>
    <module>framework-db-demo-mysql</module>
    <module>framework-db-demo-mongo</module>
    <module>framework-db-jdbc</module>
    <module>framework-db-cg</module>
    <module>framework-db-orm</module>
    <module>framework-db-mongo</module>
</modules>
```

注意：`framework-db-demo` 已移除，新增 `framework-db-demo-mysql` 和 `framework-db-demo-mongo`。

- [ ] **Step 2: 验证**

Run: `cd /Users/wangwei/Projects/f41/framework-db && mvn validate -N`
预期：BUILD SUCCESS（新模块目录尚不存在，但不影响父 pom 验证）

---

### Task 3: 创建 framework-db-demo-mysql 模块骨架

**Files:**
- Create: `/Users/wangwei/Projects/f41/framework-db/framework-db-demo-mysql/pom.xml`
- Create: `/Users/wangwei/Projects/f41/framework-db/framework-db-demo-mysql/src/main/java/com/hbasesoft/framework/db/demo/mysql/Application.java`
- Create: `/Users/wangwei/Projects/f41/framework-db/framework-db-demo-mysql/src/main/java/com/hbasesoft/framework/db/demo/mysql/GlobalConfig.java`
- Create: `/Users/wangwei/Projects/f41/framework-db/framework-db-demo-mysql/src/main/resources/application.yml`
- Create: `/Users/wangwei/Projects/f41/framework-db/framework-db-demo-mysql/src/test/resources/application-test.yml`

- [ ] **Step 1: 创建模块目录结构**

```bash
cd /Users/wangwei/Projects/f41/framework-db
mkdir -p framework-db-demo-mysql/src/main/java/com/hbasesoft/framework/db/demo/mysql
mkdir -p framework-db-demo-mysql/src/main/resources
mkdir -p framework-db-demo-mysql/src/test/java/com/hbasesoft/framework/db/demo/mysql
mkdir -p framework-db-demo-mysql/src/test/resources
```

- [ ] **Step 2: 创建 pom.xml**

创建 `framework-db-demo-mysql/pom.xml`：

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>com.hbasesoft.framework</groupId>
        <artifactId>framework-db</artifactId>
        <version>4.3.0</version>
    </parent>
    <artifactId>framework-db-demo-mysql</artifactId>
    <name>database demo mysql</name>
    <description>MySQL/JPA demo test module</description>
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
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-deploy-plugin</artifactId>
                <configuration>
                    <skip>true</skip>
                </configuration>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-install-plugin</artifactId>
                <configuration>
                    <skip>true</skip>
                </configuration>
            </plugin>
            <plugin>
                <groupId>org.jacoco</groupId>
                <artifactId>jacoco-maven-plugin</artifactId>
                <executions>
                    <execution>
                        <goals>
                            <goal>prepare-agent</goal>
                        </goals>
                    </execution>
                    <execution>
                        <id>report</id>
                        <phase>test</phase>
                        <goals>
                            <goal>report</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>
```

- [ ] **Step 3: 创建 Application.java**

创建 `framework-db-demo-mysql/src/main/java/com/hbasesoft/framework/db/demo/mysql/Application.java`：

```java
package com.hbasesoft.framework.db.demo.mysql;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import com.hbasesoft.framework.common.Bootstrap;

@ComponentScan(basePackages = "com.hbasesoft.framework.db.demo.mysql")
@SpringBootApplication
public class Application {

    public static void main(final String[] args) {
        Bootstrap.before();
        ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
        Bootstrap.after(context);
    }
}
```

- [ ] **Step 4: 创建 GlobalConfig.java**

创建 `framework-db-demo-mysql/src/main/java/com/hbasesoft/framework/db/demo/mysql/GlobalConfig.java`：

```java
package com.hbasesoft.framework.db.demo.mysql;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import com.hbasesoft.framework.common.Bootstrap;

@TestConfiguration
public class GlobalConfig implements ApplicationListener<ContextRefreshedEvent> {

    static {
        Bootstrap.before();
    }

    @Override
    public void onApplicationEvent(final ContextRefreshedEvent event) {
        if (event.getApplicationContext().getParent() == null) {
            Bootstrap.after(event.getApplicationContext());
        }
    }
}
```

- [ ] **Step 5: 创建主配置 application.yml**

创建 `framework-db-demo-mysql/src/main/resources/application.yml`：

```yaml
project:
  name: db-demo-mysql

spring:
  application:
    name: ${project.name}
  jpa:
    hibernate:
      ddl-auto: none
    show-sql: false

master:
  db:
    type: mysql
    url: jdbc:mysql://localhost:3306/test_db
    username: root
    password: root
```

- [ ] **Step 6: 创建测试配置 application-test.yml**

创建 `framework-db-demo-mysql/src/test/resources/application-test.yml`：

```yaml
project:
  name: db-demo-mysql-test

spring:
  application:
    name: ${project.name}
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQLDialect
```

注意：Testcontainers 的连接信息通过 `@DynamicPropertySource` 动态注入，不在此文件中配置。`ddl-auto: create-drop` 让 Hibernate 自动建表。

- [ ] **Step 7: 验证编译**

Run: `cd /Users/wangwei/Projects/f41/framework-db && mvn compile -pl framework-db-demo-mysql -am -DskipTests`
预期：BUILD SUCCESS

---

### Task 4: 创建 MySQL 实体类和 DAO 接口

**Files:**
- Create: `framework-db-demo-mysql/src/main/java/com/hbasesoft/framework/db/demo/mysql/entity/StaffEntity.java`
- Create: `framework-db-demo-mysql/src/main/java/com/hbasesoft/framework/db/demo/mysql/entity/StudentEntity.java`
- Create: `framework-db-demo-mysql/src/main/java/com/hbasesoft/framework/db/demo/mysql/entity/CourseEntity.java`
- Create: `framework-db-demo-mysql/src/main/java/com/hbasesoft/framework/db/demo/mysql/entity/CountEntity.java`
- Create: `framework-db-demo-mysql/src/main/java/com/hbasesoft/framework/db/demo/mysql/dao/StaffDao.java`
- Create: `framework-db-demo-mysql/src/main/java/com/hbasesoft/framework/db/demo/mysql/dao/StudentDao.java`
- Create: `framework-db-demo-mysql/src/main/java/com/hbasesoft/framework/db/demo/mysql/dao/CourseDao.java`
- Create: `framework-db-demo-mysql/src/main/java/com/hbasesoft/framework/db/demo/mysql/dao/mysql/StaffMySqlDao.java`
- Create: `framework-db-demo-mysql/src/main/java/com/hbasesoft/framework/db/demo/mysql/dao/mysql/IStudentMySqlDao.java`
- Create: `framework-db-demo-mysql/src/main/java/com/hbasesoft/framework/db/demo/mysql/dao/mysql/ICourseMySqlDao.java`

- [ ] **Step 1: 创建实体类目录**

```bash
mkdir -p /Users/wangwei/Projects/f41/framework-db/framework-db-demo-mysql/src/main/java/com/hbasesoft/framework/db/demo/mysql/entity
mkdir -p /Users/wangwei/Projects/f41/framework-db/framework-db-demo-mysql/src/main/java/com/hbasesoft/framework/db/demo/mysql/dao/mysql
```

- [ ] **Step 2: 创建 StaffEntity.java**

```java
package com.hbasesoft.framework.db.demo.mysql.entity;

import com.hbasesoft.framework.db.core.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "t_staff")
@Getter
@Setter
public class StaffEntity extends BaseEntity {

    private static final long serialVersionUID = 704766015383881438L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "position")
    private String position;

    @Column(name = "department")
    private String department;

    @Column(name = "salary")
    private Double salary;

    @Column(name = "hire_date")
    private Date hireDate;

    @Column(name = "name")
    private String name;
}
```

注意：将 `id` 类型改为 `Integer`（与 `GenerationType.IDENTITY` + MySQL 自增主键一致），列名改为 `id`（MySQL 不建议用 `_id`）。

- [ ] **Step 3: 创建 StudentEntity.java**

```java
package com.hbasesoft.framework.db.demo.mysql.entity;

import com.hbasesoft.framework.db.core.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import org.hibernate.annotations.GenericGenerator;

@Entity(name = "T_STUDENT")
public class StudentEntity extends BaseEntity {

    public static final String NAME = "name";
    public static final String AGE = "age";

    private static final long serialVersionUID = -5443184537634014662L;

    @Id
    @GeneratedValue(generator = "paymentableGenerator")
    @GenericGenerator(name = "paymentableGenerator", strategy = "uuid")
    @Column(name = "id")
    private String id;

    @Column(name = "name")
    private String name;

    @Column(name = "AGE")
    private Integer age;

    @Transient
    private Integer score;

    @Transient
    private String courseName;

    public String getId() { return id; }
    public void setId(final String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(final String name) { this.name = name; }
    public int getAge() { return age; }
    public void setAge(final int age) { this.age = age; }
    public Integer getScore() { return score; }
    public void setScore(final Integer score) { this.score = score; }
    public String getCourseName() { return courseName; }
    public void setCourseName(final String courseName) { this.courseName = courseName; }
}
```

注意：需要 `import jakarta.persistence.Transient;`。

- [ ] **Step 4: 创建 CourseEntity.java**

```java
package com.hbasesoft.framework.db.demo.mysql.entity;

import com.hbasesoft.framework.db.core.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import org.hibernate.annotations.GenericGenerator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "T_COURSE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseEntity extends BaseEntity {

    private static final long serialVersionUID = 3095712295731014196L;

    public static final String COURSE_NAME = "courseName";

    @Id
    @GeneratedValue(generator = "paymentableGenerator")
    @GenericGenerator(name = "paymentableGenerator", strategy = "uuid")
    @Column(name = "id")
    private String id;

    @Column(name = "COURSE_NAME")
    private String courseName;

    @Column(name = "REMARK")
    private String remark;
}
```

- [ ] **Step 5: 创建 CountEntity.java**

```java
package com.hbasesoft.framework.db.demo.mysql.entity;

import com.hbasesoft.framework.db.core.BaseEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CountEntity extends BaseEntity {

    private static final long serialVersionUID = -3115703439314376932L;

    private Integer total;
    private String name;
}
```

- [ ] **Step 6: 创建 DAO 接口**

创建 `dao/StaffDao.java`：

```java
package com.hbasesoft.framework.db.demo.mysql.dao;

import com.hbasesoft.framework.db.core.BaseDao;
import com.hbasesoft.framework.db.demo.mysql.entity.StaffEntity;

public interface StaffDao extends BaseDao<StaffEntity> {
}
```

创建 `dao/StudentDao.java`：

```java
package com.hbasesoft.framework.db.demo.mysql.dao;

import com.hbasesoft.framework.db.core.BaseDao;
import com.hbasesoft.framework.db.demo.mysql.entity.StudentEntity;

public interface StudentDao extends BaseDao<StudentEntity> {
}
```

注意：简化版 StudentDao，移除 `createTable`、`countCoursePass` 等自定义方法。这些需要预建表和联表查询，不适合在纯接口方法测试中使用。

创建 `dao/CourseDao.java`：

```java
package com.hbasesoft.framework.db.demo.mysql.dao;

import com.hbasesoft.framework.db.core.BaseDao;
import com.hbasesoft.framework.db.demo.mysql.entity.CourseEntity;

public interface CourseDao extends BaseDao<CourseEntity> {
}
```

- [ ] **Step 7: 创建 MySQL DAO 实现接口**

创建 `dao/mysql/StaffMySqlDao.java`：

```java
package com.hbasesoft.framework.db.demo.mysql.dao.mysql;

import com.hbasesoft.framework.db.Dao;
import com.hbasesoft.framework.db.demo.mysql.dao.StaffDao;

@Dao
public interface StaffMySqlDao extends StaffDao {
}
```

创建 `dao/mysql/StudentMySqlDao.java`：

```java
package com.hbasesoft.framework.db.demo.mysql.dao.mysql;

import com.hbasesoft.framework.db.Dao;
import com.hbasesoft.framework.db.demo.mysql.dao.StudentDao;

@Dao
public interface StudentMySqlDao extends StudentDao {
}
```

创建 `dao/mysql/CourseMySqlDao.java`：

```java
package com.hbasesoft.framework.db.demo.mysql.dao.mysql;

import com.hbasesoft.framework.db.Dao;
import com.hbasesoft.framework.db.demo.mysql.dao.CourseDao;

@Dao
public interface CourseMySqlDao extends CourseDao {
}
```

- [ ] **Step 8: 验证编译**

Run: `cd /Users/wangwei/Projects/f41/framework-db && mvn compile -pl framework-db-demo-mysql -am -DskipTests`
预期：BUILD SUCCESS

---

### Task 5: 创建 MySQL 测试基础设施

**Files:**
- Create: `framework-db-demo-mysql/src/test/java/com/hbasesoft/framework/db/demo/mysql/config/AbstractMysqlTestConfig.java`
- Create: `framework-db-demo-mysql/src/test/java/com/hbasesoft/framework/db/demo/mysql/config/TestDataHelper.java`

- [ ] **Step 1: 创建测试目录**

```bash
mkdir -p /Users/wangwei/Projects/f41/framework-db/framework-db-demo-mysql/src/test/java/com/hbasesoft/framework/db/demo/mysql/config
mkdir -p /Users/wangwei/Projects/f41/framework-db/framework-db-demo-mysql/src/test/java/com/hbasesoft/framework/db/demo/mysql/staff
mkdir -p /Users/wangwei/Projects/f41/framework-db/framework-db-demo-mysql/src/test/java/com/hbasesoft/framework/db/demo/mysql/student
mkdir -p /Users/wangwei/Projects/f41/framework-db/framework-db-demo-mysql/src/test/java/com/hbasesoft/framework/db/demo/mysql/course
```

- [ ] **Step 2: 创建 AbstractMysqlTestConfig.java**

```java
package com.hbasesoft.framework.db.demo.mysql.config;

import com.hbasesoft.framework.db.demo.mysql.Application;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;

@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
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

- [ ] **Step 3: 创建 TestDataHelper.java**

```java
package com.hbasesoft.framework.db.demo.mysql.config;

import com.hbasesoft.framework.db.demo.mysql.entity.CourseEntity;
import com.hbasesoft.framework.db.demo.mysql.entity.StaffEntity;
import com.hbasesoft.framework.db.demo.mysql.entity.StudentEntity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public final class TestDataHelper {

    private TestDataHelper() {
    }

    public static StaffEntity createStaff(String firstName, String lastName,
                                           String department, Double salary) {
        StaffEntity staff = new StaffEntity();
        staff.setFirstName(firstName);
        staff.setLastName(lastName);
        staff.setDepartment(department);
        staff.setSalary(salary);
        staff.setPosition("Developer");
        staff.setHireDate(new Date());
        staff.setName(firstName + " " + lastName);
        return staff;
    }

    public static List<StaffEntity> createStaffBatch() {
        List<StaffEntity> list = new ArrayList<>();
        list.add(createStaff("John", "Doe", "项目一部", 75000.00));
        list.add(createStaff("Jane", "Doe", "项目一部", 80000.00));
        list.add(createStaff("Michael", "Smith", "项目一部", 65000.00));
        list.add(createStaff("Emily", "Johnson", "项目二部", 55000.00));
        list.add(createStaff("David", "Brown", "项目二部", 70000.00));
        list.add(createStaff("Jessica", "Jones", "项目二部", 60000.00));
        list.add(createStaff("Matthew", "Williams", "项目二部", 65000.00));
        list.add(createStaff("Olivia", "Taylor", "项目二部", 55000.00));
        list.add(createStaff("Daniel", "Anderson", "项目三部", 50000.00));
        list.add(createStaff("Sophia", "Thomas", "项目三部", 45000.00));
        return list;
    }

    public static StudentEntity createStudent(String name, int age) {
        StudentEntity student = new StudentEntity();
        student.setName(name);
        student.setAge(age);
        return student;
    }

    public static CourseEntity createCourse(String courseName, String remark) {
        CourseEntity course = new CourseEntity();
        course.setCourseName(courseName);
        course.setRemark(remark);
        return course;
    }
}
```

- [ ] **Step 4: 验证编译**

Run: `cd /Users/wangwei/Projects/f41/framework-db && mvn test-compile -pl framework-db-demo-mysql -am -DskipTests`
预期：BUILD SUCCESS

---

### Task 6: 创建 StaffCrudTest

**Files:**
- Create: `framework-db-demo-mysql/src/test/java/com/hbasesoft/framework/db/demo/mysql/staff/StaffCrudTest.java`

- [ ] **Step 1: 创建测试类**

```java
package com.hbasesoft.framework.db.demo.mysql.staff;

import com.hbasesoft.framework.db.demo.mysql.config.AbstractMysqlTestConfig;
import com.hbasesoft.framework.db.demo.mysql.config.TestDataHelper;
import com.hbasesoft.framework.db.demo.mysql.dao.StaffDao;
import com.hbasesoft.framework.db.demo.mysql.entity.StaffEntity;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
public class StaffCrudTest extends AbstractMysqlTestConfig {

    @Resource(name = "staffMySqlDao")
    private StaffDao staffDao;

    @Test
    void shouldSaveAndRetrieveEntity() {
        StaffEntity entity = TestDataHelper.createStaff("John", "Doe", "项目一部", 75000.00);
        staffDao.save(entity);

        assertThat(entity.getId()).isNotNull();

        StaffEntity retrieved = staffDao.get(entity.getId());
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getFirstName()).isEqualTo("John");
        assertThat(retrieved.getLastName()).isEqualTo("Doe");
        assertThat(retrieved.getDepartment()).isEqualTo("项目一部");
        assertThat(retrieved.getSalary()).isEqualTo(75000.00);
    }

    @Test
    void shouldSaveBatchEntities() {
        List<StaffEntity> batch = TestDataHelper.createStaffBatch();
        staffDao.saveBatch(batch);

        Integer count = staffDao.get(q -> q.count("id"), Integer.class);
        assertThat(count).isEqualTo(10);
    }

    @Test
    void shouldUpdateEntity() {
        StaffEntity entity = TestDataHelper.createStaff("John", "Doe", "项目一部", 75000.00);
        staffDao.save(entity);

        entity.setFirstName("张三");
        staffDao.update(entity);

        StaffEntity updated = staffDao.get(entity.getId());
        assertThat(updated.getFirstName()).isEqualTo("张三");
    }

    @Test
    void shouldUpdateBatchEntities() {
        List<StaffEntity> batch = TestDataHelper.createStaffBatch();
        staffDao.saveBatch(batch);

        List<StaffEntity> all = staffDao.queryAll();
        for (StaffEntity e : all) {
            if (e.getSalary() > 60000) {
                e.setDepartment("管理层");
            }
        }
        staffDao.updateBatch(all);

        Integer count = staffDao.get(q -> q.count("id").eq("department", "管理层"), Integer.class);
        assertThat(count).isGreaterThan(0);
    }

    @Test
    void shouldDeleteEntity() {
        StaffEntity entity = TestDataHelper.createStaff("John", "Doe", "项目一部", 75000.00);
        staffDao.save(entity);
        assertThat(staffDao.get(entity.getId())).isNotNull();

        staffDao.delete(entity);

        StaffEntity deleted = staffDao.get(entity.getId());
        assertThat(deleted).isNull();
    }

    @Test
    void shouldDeleteById() {
        StaffEntity entity = TestDataHelper.createStaff("John", "Doe", "项目一部", 75000.00);
        staffDao.save(entity);
        Integer id = entity.getId();

        staffDao.deleteById(id);

        assertThat(staffDao.get(id)).isNull();
    }

    @Test
    void shouldDeleteByIds() {
        List<StaffEntity> batch = TestDataHelper.createStaffBatch();
        staffDao.saveBatch(batch);

        List<Integer> ids = batch.stream()
            .map(StaffEntity::getId)
            .collect(Collectors.toList())
            .subList(0, 3);

        staffDao.deleteByIds(ids);

        Integer remaining = staffDao.get(q -> q.count("id"), Integer.class);
        assertThat(remaining).isEqualTo(7);
    }

    @Test
    void shouldDeleteBatch() {
        List<StaffEntity> batch = TestDataHelper.createStaffBatch();
        staffDao.saveBatch(batch);

        List<StaffEntity> toDelete = batch.subList(0, 3);
        staffDao.deleteBatch(toDelete);

        Integer remaining = staffDao.get(q -> q.count("id"), Integer.class);
        assertThat(remaining).isEqualTo(7);
    }
}
```

---

### Task 7: 创建 StaffLambdaQueryTest

**Files:**
- Create: `framework-db-demo-mysql/src/test/java/com/hbasesoft/framework/db/demo/mysql/staff/StaffLambdaQueryTest.java`

- [ ] **Step 1: 创建测试类**

```java
package com.hbasesoft.framework.db.demo.mysql.staff;

import com.hbasesoft.framework.db.demo.mysql.config.AbstractMysqlTestConfig;
import com.hbasesoft.framework.db.demo.mysql.config.TestDataHelper;
import com.hbasesoft.framework.db.demo.mysql.dao.StaffDao;
import com.hbasesoft.framework.db.demo.mysql.entity.CountEntity;
import com.hbasesoft.framework.db.demo.mysql.entity.StaffEntity;
import com.hbasesoft.framework.db.core.utils.PagerList;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
public class StaffLambdaQueryTest extends AbstractMysqlTestConfig {

    @Resource(name = "staffMySqlDao")
    private StaffDao staffDao;

    @BeforeEach
    void setUp() {
        staffDao.saveBatch(TestDataHelper.createStaffBatch());
    }

    @Test
    void shouldGetByLambda() {
        StaffEntity entity = staffDao.getByLambda(
            q -> q.eq(StaffEntity::getFirstName, "Michael").eq(StaffEntity::getLastName, "Smith"));

        assertThat(entity).isNotNull();
        assertThat(entity.getSalary()).isEqualTo(65000.00);
    }

    @Test
    void shouldGetByLambdaWithProjection() {
        Integer count = staffDao.getByLambda(
            q -> q.count(StaffEntity::getId).eq(StaffEntity::getLastName, "Doe"), Integer.class);

        assertThat(count).isEqualTo(2);
    }

    @Test
    void shouldQueryByLambda() {
        List<StaffEntity> entities = staffDao.queryByLambda(
            q -> q.le(StaffEntity::getSalary, 60000.00));

        assertThat(entities).hasSize(5);
    }

    @Test
    void shouldQueryByLambdaWithProjection() {
        List<CountEntity> entities = staffDao.queryByLambda(
            q -> q.count(StaffEntity::getId, CountEntity::getTotal)
                .select(StaffEntity::getDepartment, CountEntity::getName)
                .groupBy(StaffEntity::getDepartment),
            CountEntity.class);

        assertThat(entities).hasSize(3);
    }

    @Test
    void shouldQueryPagerByLambda() {
        PagerList<StaffEntity> page = staffDao.queryPagerByLambda(
            q -> q.le(StaffEntity::getSalary, 60000.00), 1, 2);

        assertThat(page).hasSize(2);
        assertThat(page.getTotalCount()).isEqualTo(5);
    }

    @Test
    void shouldQueryPagerByLambdaWithProjection() {
        PagerList<StaffEntity> page = staffDao.queryPagerByLambda(
            q -> q.select(StaffEntity::getId)
                .select(StaffEntity::getFirstName)
                .select(StaffEntity::getLastName)
                .le(StaffEntity::getSalary, 60000.00),
            1, 2, StaffEntity.class);

        assertThat(page).hasSize(2);
        assertThat(page.getTotalCount()).isEqualTo(5);
    }

    @Test
    void shouldDeleteByLambda() {
        staffDao.deleteByLambda(q -> q.in(StaffEntity::getLastName, "Doe", "Johnson"));

        Integer remaining = staffDao.getByLambda(
            q -> q.count(StaffEntity::getId), Integer.class);
        assertThat(remaining).isEqualTo(7);
    }

    @Test
    void shouldUpdateByLambda() {
        staffDao.updateByLambda(
            q -> q.set(StaffEntity::getSalary, 62000.00)
                .set(StaffEntity::getDepartment, "财务部")
                .in(StaffEntity::getLastName, "Doe", "Smith"));

        Integer count = staffDao.getByLambda(
            q -> q.count(StaffEntity::getId).eq(StaffEntity::getDepartment, "财务部"),
            Integer.class);
        assertThat(count).isEqualTo(3);
    }
}
```

---

### Task 8: 创建 StaffQueryTest（QueryWrapper 条件查询）

**Files:**
- Create: `framework-db-demo-mysql/src/test/java/com/hbasesoft/framework/db/demo/mysql/staff/StaffQueryTest.java`

- [ ] **Step 1: 创建测试类**

```java
package com.hbasesoft.framework.db.demo.mysql.staff;

import com.hbasesoft.framework.db.demo.mysql.config.AbstractMysqlTestConfig;
import com.hbasesoft.framework.db.demo.mysql.config.TestDataHelper;
import com.hbasesoft.framework.db.demo.mysql.dao.StaffDao;
import com.hbasesoft.framework.db.demo.mysql.entity.CountEntity;
import com.hbasesoft.framework.db.demo.mysql.entity.StaffEntity;
import com.hbasesoft.framework.db.core.utils.PagerList;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
public class StaffQueryTest extends AbstractMysqlTestConfig {

    @Resource(name = "staffMySqlDao")
    private StaffDao staffDao;

    @BeforeEach
    void setUp() {
        staffDao.saveBatch(TestDataHelper.createStaffBatch());
    }

    @Test
    void shouldGetByQueryWrapper() {
        StaffEntity entity = staffDao.get(q -> q.eq("firstName", "Michael").eq("lastName", "Smith"));

        assertThat(entity).isNotNull();
        assertThat(entity.getSalary()).isEqualTo(65000.00);
    }

    @Test
    void shouldGetByQueryWrapperWithProjection() {
        Integer count = staffDao.get(q -> q.count("id").eq("lastName", "Doe"), Integer.class);

        assertThat(count).isEqualTo(2);
    }

    @Test
    void shouldQueryAll() {
        List<StaffEntity> all = staffDao.queryAll();

        assertThat(all).hasSize(10);
    }

    @Test
    void shouldQueryByQueryWrapper() {
        List<StaffEntity> entities = staffDao.query(q -> q.le("salary", 60000.00));

        assertThat(entities).hasSize(5);
    }

    @Test
    void shouldQueryByQueryWrapperWithProjection() {
        List<CountEntity> entities = staffDao.query(
            q -> q.count("id", "total")
                .select("department", "name")
                .groupBy("department"),
            CountEntity.class);

        assertThat(entities).hasSize(3);
    }

    @Test
    void shouldQueryByQueryWrapperToMap() {
        @SuppressWarnings("rawtypes")
        List<Map> entities = staffDao.query(
            q -> q.select("age").count("id", "count").max("id").min("id", "mid")
                .avg("salary", "avgSalary").sum("salary", "sumSalary")
                .groupBy("department"),
            Map.class);

        assertThat(entities).isNotEmpty();
    }

    @Test
    void shouldQueryPagerByQueryWrapper() {
        PagerList<StaffEntity> page = staffDao.queryPager(q -> q.le("salary", 60000.00), 1, 2);

        assertThat(page).hasSize(2);
        assertThat(page.getTotalCount()).isEqualTo(5);
    }

    @Test
    void shouldQueryPagerByQueryWrapperWithProjection() {
        List<StaffEntity> entities = staffDao.queryPager(
            q -> q.select("id").select("firstName").select("lastName").le("salary", 60000.00),
            1, 2, StaffEntity.class);

        assertThat(entities).hasSize(2);
    }

    @Test
    void shouldUpdateByQueryWrapper() {
        staffDao.update(q -> q.set("salary", 62000.00).le("salary", 60000.00));

        Integer count = staffDao.get(q -> q.count("id").ge("salary", 62000.00), Integer.class);
        assertThat(count).isEqualTo(10);
    }

    @Test
    void shouldDeleteByQueryWrapper() {
        staffDao.delete(q -> q.in("lastName", "Doe", "Johnson"));

        Integer remaining = staffDao.get(q -> q.count("id"), Integer.class);
        assertThat(remaining).isEqualTo(7);
    }
}
```

---

### Task 9: 创建 StudentCrudTest 和 CourseCrudTest

**Files:**
- Create: `framework-db-demo-mysql/src/test/java/com/hbasesoft/framework/db/demo/mysql/student/StudentCrudTest.java`
- Create: `framework-db-demo-mysql/src/test/java/com/hbasesoft/framework/db/demo/mysql/course/CourseCrudTest.java`

- [ ] **Step 1: 创建 StudentCrudTest.java**

```java
package com.hbasesoft.framework.db.demo.mysql.student;

import com.hbasesoft.framework.db.demo.mysql.config.AbstractMysqlTestConfig;
import com.hbasesoft.framework.db.demo.mysql.config.TestDataHelper;
import com.hbasesoft.framework.db.demo.mysql.dao.StudentDao;
import com.hbasesoft.framework.db.demo.mysql.entity.StudentEntity;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
public class StudentCrudTest extends AbstractMysqlTestConfig {

    @Resource(name = "studentMySqlDao")
    private StudentDao studentDao;

    @Test
    void shouldSaveAndRetrieveStudent() {
        StudentEntity student = TestDataHelper.createStudent("张三", 18);
        studentDao.save(student);

        assertThat(student.getId()).isNotNull();

        StudentEntity retrieved = studentDao.get(student.getId());
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getName()).isEqualTo("张三");
        assertThat(retrieved.getAge()).isEqualTo(18);
    }

    @Test
    void shouldSaveBatchStudents() {
        List<StudentEntity> students = Arrays.asList(
            TestDataHelper.createStudent("张三", 18),
            TestDataHelper.createStudent("李四", 19),
            TestDataHelper.createStudent("王五", 20)
        );
        studentDao.saveBatch(students);

        List<StudentEntity> all = studentDao.queryAll();
        assertThat(all).hasSize(3);
    }

    @Test
    void shouldUpdateStudent() {
        StudentEntity student = TestDataHelper.createStudent("张三", 18);
        studentDao.save(student);

        student.setName("李四");
        studentDao.update(student);

        StudentEntity updated = studentDao.get(student.getId());
        assertThat(updated.getName()).isEqualTo("李四");
    }

    @Test
    void shouldUpdateByQueryWrapper() {
        StudentEntity student = TestDataHelper.createStudent("张三", 18);
        studentDao.save(student);

        studentDao.update(q -> q.set("name", "李四").eq("id", student.getId()));

        StudentEntity updated = studentDao.get(student.getId());
        assertThat(updated.getName()).isEqualTo("李四");
    }

    @Test
    void shouldDeleteStudent() {
        StudentEntity student = TestDataHelper.createStudent("张三", 18);
        studentDao.save(student);

        studentDao.delete(student);

        assertThat(studentDao.get(student.getId())).isNull();
    }

    @Test
    void shouldDeleteById() {
        StudentEntity student = TestDataHelper.createStudent("张三", 18);
        studentDao.save(student);
        String id = student.getId();

        studentDao.deleteById(id);

        assertThat(studentDao.get(id)).isNull();
    }

    @Test
    void shouldDeleteByQueryWrapper() {
        StudentEntity student = TestDataHelper.createStudent("张三", 18);
        studentDao.save(student);

        studentDao.delete(q -> q.eq("id", student.getId()));

        assertThat(studentDao.get(student.getId())).isNull();
    }

    @Test
    void shouldQueryByProperty() {
        studentDao.saveBatch(Arrays.asList(
            TestDataHelper.createStudent("张三", 18),
            TestDataHelper.createStudent("李四", 18),
            TestDataHelper.createStudent("王五", 20)
        ));

        List<StudentEntity> students = studentDao.query(q -> q.eq(StudentEntity.AGE, 18));
        assertThat(students).hasSize(2);
    }

    @Test
    void shouldQueryPagerStudents() {
        studentDao.saveBatch(Arrays.asList(
            TestDataHelper.createStudent("张三", 18),
            TestDataHelper.createStudent("李四", 19),
            TestDataHelper.createStudent("王五", 20),
            TestDataHelper.createStudent("赵六", 21),
            TestDataHelper.createStudent("钱七", 22)
        ));

        var page = studentDao.queryPager(q -> q.ge("age", 18), 1, 2);
        assertThat(page).hasSize(2);
        assertThat(page.getTotalCount()).isEqualTo(5);
    }

    @Test
    void shouldUpdateByLambda() {
        StudentEntity student = TestDataHelper.createStudent("张三", 18);
        studentDao.save(student);

        studentDao.updateByLambda(q -> q.set(StudentEntity::getName, "李四").eq(StudentEntity::getId, student.getId()));

        StudentEntity updated = studentDao.get(student.getId());
        assertThat(updated.getName()).isEqualTo("李四");
    }
}
```

- [ ] **Step 2: 创建 CourseCrudTest.java**

```java
package com.hbasesoft.framework.db.demo.mysql.course;

import com.hbasesoft.framework.db.demo.mysql.config.AbstractMysqlTestConfig;
import com.hbasesoft.framework.db.demo.mysql.config.TestDataHelper;
import com.hbasesoft.framework.db.demo.mysql.dao.CourseDao;
import com.hbasesoft.framework.db.demo.mysql.entity.CourseEntity;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
public class CourseCrudTest extends AbstractMysqlTestConfig {

    @Resource(name = "courseMySqlDao")
    private CourseDao courseDao;

    @Test
    void shouldSaveAndRetrieveCourse() {
        CourseEntity course = TestDataHelper.createCourse("语文", "基础语文课程");
        courseDao.save(course);

        assertThat(course.getId()).isNotNull();

        CourseEntity retrieved = courseDao.get(course.getId());
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getCourseName()).isEqualTo("语文");
        assertThat(retrieved.getRemark()).isEqualTo("基础语文课程");
    }

    @Test
    void shouldSaveBatchCourses() {
        List<CourseEntity> courses = Arrays.asList(
            TestDataHelper.createCourse("语文", "基础语文"),
            TestDataHelper.createCourse("数学", "高等数学"),
            TestDataHelper.createCourse("英语", "英语口语")
        );
        courseDao.saveBatch(courses);

        List<CourseEntity> all = courseDao.queryAll();
        assertThat(all).hasSize(3);
    }

    @Test
    void shouldDeleteCourse() {
        CourseEntity course = TestDataHelper.createCourse("语文", "基础语文");
        courseDao.save(course);

        courseDao.delete(course);

        assertThat(courseDao.get(course.getId())).isNull();
    }

    @Test
    void shouldQueryAllCourses() {
        courseDao.saveBatch(Arrays.asList(
            TestDataHelper.createCourse("语文", "基础语文"),
            TestDataHelper.createCourse("数学", "高等数学")
        ));

        List<CourseEntity> all = courseDao.queryAll();
        assertThat(all).hasSize(2);
    }

    @Test
    void shouldGetByProperty() {
        courseDao.saveBatch(Arrays.asList(
            TestDataHelper.createCourse("语文", "基础语文"),
            TestDataHelper.createCourse("数学", "高等数学")
        ));

        CourseEntity course = courseDao.get(q -> q.eq(CourseEntity.COURSE_NAME, "语文"));
        assertThat(course).isNotNull();
        assertThat(course.getRemark()).isEqualTo("基础语文");
    }

    @Test
    void shouldCountCourses() {
        courseDao.saveBatch(Arrays.asList(
            TestDataHelper.createCourse("语文", "基础语文"),
            TestDataHelper.createCourse("数学", "高等数学"),
            TestDataHelper.createCourse("英语", "英语口语")
        ));

        Long count = courseDao.get(q -> q.count("id"), Long.class);
        assertThat(count).isEqualTo(3);
    }

    @Test
    void shouldCountByLambda() {
        courseDao.saveBatch(Arrays.asList(
            TestDataHelper.createCourse("语文", "基础语文"),
            TestDataHelper.createCourse("数学", "高等数学"),
            TestDataHelper.createCourse("英语", "英语口语")
        ));

        Long count = courseDao.getByLambda(q -> q.count(CourseEntity::getId), Long.class);
        assertThat(count).isEqualTo(3);
    }
}
```

---

### Task 10: 运行 MySQL 模块测试并验证

- [ ] **Step 1: 运行全部测试**

Run: `cd /Users/wangwei/Projects/f41/framework-db && mvn test -pl framework-db-demo-mysql -am`
预期：BUILD SUCCESS，所有测试通过

- [ ] **Step 2: 检查测试覆盖率**

Run: `cd /Users/wangwei/Projects/f41/framework-db/framework-db-demo-mysql && cat target/site/jacoco/index.html | grep -A2 "Total"`
预期：行覆盖率 >= 70%

如果覆盖率不够，需要补充缺失的测试用例。

---

### Task 11: 创建 framework-db-demo-mongo 模块骨架

**Files:**
- Create: `framework-db-demo-mongo/pom.xml`
- Create: MongoDB 模块下的所有源文件

- [ ] **Step 1: 创建模块目录**

```bash
cd /Users/wangwei/Projects/f41/framework-db
mkdir -p framework-db-demo-mongo/src/main/java/com/hbasesoft/framework/db/demo/mongo
mkdir -p framework-db-demo-mongo/src/main/resources
mkdir -p framework-db-demo-mongo/src/test/java/com/hbasesoft/framework/db/demo/mongo/config
mkdir -p framework-db-demo-mongo/src/test/java/com/hbasesoft/framework/db/demo/mongo/student
mkdir -p framework-db-demo-mongo/src/test/java/com/hbasesoft/framework/db/demo/mongo/course
mkdir -p framework-db-demo-mongo/src/test/resources
```

- [ ] **Step 2: 创建 pom.xml**

创建 `framework-db-demo-mongo/pom.xml`：

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>com.hbasesoft.framework</groupId>
        <artifactId>framework-db</artifactId>
        <version>4.3.0</version>
    </parent>
    <artifactId>framework-db-demo-mongo</artifactId>
    <name>database demo mongo</name>
    <description>MongoDB demo test module</description>
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
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-deploy-plugin</artifactId>
                <configuration>
                    <skip>true</skip>
                </configuration>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-install-plugin</artifactId>
                <configuration>
                    <skip>true</skip>
                </configuration>
            </plugin>
            <plugin>
                <groupId>org.jacoco</groupId>
                <artifactId>jacoco-maven-plugin</artifactId>
                <executions>
                    <execution>
                        <goals>
                            <goal>prepare-agent</goal>
                        </goals>
                    </execution>
                    <execution>
                        <id>report</id>
                        <phase>test</phase>
                        <goals>
                            <goal>report</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>
```

- [ ] **Step 3: 创建 Application.java**

创建 `framework-db-demo-mongo/src/main/java/com/hbasesoft/framework/db/demo/mongo/Application.java`：

```java
package com.hbasesoft.framework.db.demo.mongo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import com.hbasesoft.framework.common.Bootstrap;

@ComponentScan(basePackages = "com.hbasesoft.framework.db.demo.mongo")
@SpringBootApplication
public class Application {

    public static void main(final String[] args) {
        Bootstrap.before();
        ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
        Bootstrap.after(context);
    }
}
```

- [ ] **Step 4: 创建 GlobalConfig.java**

创建 `framework-db-demo-mongo/src/main/java/com/hbasesoft/framework/db/demo/mongo/GlobalConfig.java`：

```java
package com.hbasesoft.framework.db.demo.mongo;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import com.hbasesoft.framework.common.Bootstrap;

@TestConfiguration
public class GlobalConfig implements ApplicationListener<ContextRefreshedEvent> {

    static {
        Bootstrap.before();
    }

    @Override
    public void onApplicationEvent(final ContextRefreshedEvent event) {
        if (event.getApplicationContext().getParent() == null) {
            Bootstrap.after(event.getApplicationContext());
        }
    }
}
```

- [ ] **Step 5: 创建主配置 application.yml**

创建 `framework-db-demo-mongo/src/main/resources/application.yml`：

```yaml
project:
  name: db-demo-mongo

spring:
  application:
    name: ${project.name}

master:
  db:
    type: mongodb
  mongodb:
    url: mongodb://localhost:27017/test01?retryWrites=false
```

- [ ] **Step 6: 创建测试配置 application-test.yml**

创建 `framework-db-demo-mongo/src/test/resources/application-test.yml`：

```yaml
project:
  name: db-demo-mongo-test

spring:
  application:
    name: ${project.name}
```

注意：MongoDB 连接信息通过 `@DynamicPropertySource` 动态注入。

---

### Task 12: 创建 MongoDB 实体类和 DAO 接口

**Files:**
- Create: MongoDB 模块的 entity 和 dao 包下的所有文件

- [ ] **Step 1: 创建目录结构**

```bash
mkdir -p /Users/wangwei/Projects/f41/framework-db/framework-db-demo-mongo/src/main/java/com/hbasesoft/framework/db/demo/mongo/entity
mkdir -p /Users/wangwei/Projects/f41/framework-db/framework-db-demo-mongo/src/main/java/com/hbasesoft/framework/db/demo/mongo/dao/mongodb
```

- [ ] **Step 2: 创建实体类**

MongoDB 模块的 StudentEntity 不需要 JPA 注解：

创建 `entity/StudentEntity.java`：

```java
package com.hbasesoft.framework.db.demo.mongo.entity;

import com.hbasesoft.framework.db.core.BaseEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentEntity extends BaseEntity {

    public static final String NAME = "name";
    public static final String AGE = "age";

    private static final long serialVersionUID = -5443184537634014662L;

    private String id;
    private String name;
    private Integer age;
    private Integer score;
    private String courseName;
}
```

创建 `entity/CourseEntity.java`：

```java
package com.hbasesoft.framework.db.demo.mongo.entity;

import com.hbasesoft.framework.db.core.BaseEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourseEntity extends BaseEntity {

    private static final long serialVersionUID = 3095712295731014196L;

    public static final String COURSE_NAME = "courseName";

    private String id;
    private String courseName;
    private String remark;
}
```

创建 `entity/CountEntity.java`：

```java
package com.hbasesoft.framework.db.demo.mongo.entity;

import com.hbasesoft.framework.db.core.BaseEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CountEntity extends BaseEntity {

    private static final long serialVersionUID = -3115703439314376932L;

    private Integer total;
    private String name;
}
```

- [ ] **Step 3: 创建 DAO 接口**

创建 `dao/StudentDao.java`：

```java
package com.hbasesoft.framework.db.demo.mongo.dao;

import com.hbasesoft.framework.db.core.BaseDao;
import com.hbasesoft.framework.db.demo.mongo.entity.StudentEntity;

public interface StudentDao extends BaseDao<StudentEntity> {
}
```

创建 `dao/CourseDao.java`：

```java
package com.hbasesoft.framework.db.demo.mongo.dao;

import com.hbasesoft.framework.db.core.BaseDao;
import com.hbasesoft.framework.db.demo.mongo.entity.CourseEntity;

public interface CourseDao extends BaseDao<CourseEntity> {
}
```

创建 `dao/mongodb/StudentMongoDBDao.java`：

```java
package com.hbasesoft.framework.db.demo.mongo.dao.mongodb;

import com.hbasesoft.framework.db.demo.mongo.dao.StudentDao;
import com.hbasesoft.framework.db.mongo.Dao4Mongo;

@Dao4Mongo
public interface StudentMongoDBDao extends StudentDao {
}
```

创建 `dao/mongodb/CourseMongoDBDao.java`：

```java
package com.hbasesoft.framework.db.demo.mongo.dao.mongodb;

import com.hbasesoft.framework.db.demo.mongo.dao.CourseDao;
import com.hbasesoft.framework.db.mongo.Dao4Mongo;

@Dao4Mongo
public interface CourseMongoDBDao extends CourseDao {
}
```

- [ ] **Step 4: 验证编译**

Run: `cd /Users/wangwei/Projects/f41/framework-db && mvn compile -pl framework-db-demo-mongo -am -DskipTests`
预期：BUILD SUCCESS

---

### Task 13: 创建 MongoDB 测试类

**Files:**
- Create: `framework-db-demo-mongo/src/test/java/com/hbasesoft/framework/db/demo/mongo/config/AbstractMongoTestConfig.java`
- Create: `framework-db-demo-mongo/src/test/java/com/hbasesoft/framework/db/demo/mongo/config/MongoTestDataHelper.java`
- Create: `framework-db-demo-mongo/src/test/java/com/hbasesoft/framework/db/demo/mongo/student/MongoStudentCrudTest.java`
- Create: `framework-db-demo-mongo/src/test/java/com/hbasesoft/framework/db/demo/mongo/student/MongoStudentQueryTest.java`
- Create: `framework-db-demo-mongo/src/test/java/com/hbasesoft/framework/db/demo/mongo/course/MongoCourseCrudTest.java`

- [ ] **Step 1: 创建 AbstractMongoTestConfig.java**

```java
package com.hbasesoft.framework.db.demo.mongo.config;

import com.hbasesoft.framework.db.demo.mongo.Application;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;

@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
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
        registry.add("master.db.type", () -> "mongodb");
    }
}
```

- [ ] **Step 2: 创建 MongoTestDataHelper.java**

```java
package com.hbasesoft.framework.db.demo.mongo.config;

import com.hbasesoft.framework.db.demo.mongo.entity.CourseEntity;
import com.hbasesoft.framework.db.demo.mongo.entity.StudentEntity;

import java.util.Arrays;
import java.util.List;

public final class MongoTestDataHelper {

    private MongoTestDataHelper() {
    }

    public static StudentEntity createStudent(String name, int age) {
        StudentEntity student = new StudentEntity();
        student.setName(name);
        student.setAge(age);
        return student;
    }

    public static List<StudentEntity> createStudentBatch() {
        return Arrays.asList(
            createStudent("张三", 18),
            createStudent("李四", 19),
            createStudent("王五", 20),
            createStudent("赵六", 18),
            createStudent("钱七", 21)
        );
    }

    public static CourseEntity createCourse(String courseName, String remark) {
        CourseEntity course = new CourseEntity();
        course.setCourseName(courseName);
        course.setRemark(remark);
        return course;
    }
}
```

- [ ] **Step 3: 创建 MongoStudentCrudTest.java**

```java
package com.hbasesoft.framework.db.demo.mongo.student;

import com.hbasesoft.framework.db.demo.mongo.config.AbstractMongoTestConfig;
import com.hbasesoft.framework.db.demo.mongo.config.MongoTestDataHelper;
import com.hbasesoft.framework.db.demo.mongo.dao.StudentDao;
import com.hbasesoft.framework.db.demo.mongo.entity.StudentEntity;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class MongoStudentCrudTest extends AbstractMongoTestConfig {

    @Resource(name = "studentMongoDBDao")
    private StudentDao studentDao;

    @BeforeEach
    void cleanUp() {
        List<StudentEntity> all = studentDao.queryAll();
        if (all != null && !all.isEmpty()) {
            studentDao.deleteBatch(all);
        }
    }

    @Test
    void shouldSaveAndRetrieveStudent() {
        StudentEntity student = MongoTestDataHelper.createStudent("张三", 18);
        studentDao.save(student);

        assertThat(student.getId()).isNotNull();

        StudentEntity retrieved = studentDao.get(student.getId());
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getName()).isEqualTo("张三");
        assertThat(retrieved.getAge()).isEqualTo(18);
    }

    @Test
    void shouldSaveBatchStudents() {
        List<StudentEntity> students = MongoTestDataHelper.createStudentBatch();
        studentDao.saveBatch(students);

        List<StudentEntity> all = studentDao.queryAll();
        assertThat(all).hasSize(5);
    }

    @Test
    void shouldUpdateStudent() {
        StudentEntity student = MongoTestDataHelper.createStudent("张三", 18);
        studentDao.save(student);

        student.setName("李四");
        studentDao.update(student);

        StudentEntity updated = studentDao.get(student.getId());
        assertThat(updated.getName()).isEqualTo("李四");
    }

    @Test
    void shouldUpdateBatchStudents() {
        List<StudentEntity> students = MongoTestDataHelper.createStudentBatch();
        studentDao.saveBatch(students);

        for (StudentEntity s : students) {
            s.setAge(25);
        }
        studentDao.updateBatch(students);

        List<StudentEntity> all = studentDao.queryAll();
        assertThat(all).allMatch(s -> s.getAge() == 25);
    }

    @Test
    void shouldDeleteStudent() {
        StudentEntity student = MongoTestDataHelper.createStudent("张三", 18);
        studentDao.save(student);

        studentDao.delete(student);

        assertThat(studentDao.get(student.getId())).isNull();
    }

    @Test
    void shouldDeleteById() {
        StudentEntity student = MongoTestDataHelper.createStudent("张三", 18);
        studentDao.save(student);

        studentDao.deleteById(student.getId());

        assertThat(studentDao.get(student.getId())).isNull();
    }

    @Test
    void shouldDeleteByIds() {
        List<StudentEntity> students = MongoTestDataHelper.createStudentBatch();
        studentDao.saveBatch(students);

        List<String> ids = students.stream()
            .map(StudentEntity::getId)
            .collect(Collectors.toList())
            .subList(0, 3);
        studentDao.deleteByIds(ids);

        List<StudentEntity> remaining = studentDao.queryAll();
        assertThat(remaining).hasSize(2);
    }
}
```

- [ ] **Step 4: 创建 MongoStudentQueryTest.java**

```java
package com.hbasesoft.framework.db.demo.mongo.student;

import com.hbasesoft.framework.db.demo.mongo.config.AbstractMongoTestConfig;
import com.hbasesoft.framework.db.demo.mongo.config.MongoTestDataHelper;
import com.hbasesoft.framework.db.demo.mongo.dao.StudentDao;
import com.hbasesoft.framework.db.demo.mongo.entity.StudentEntity;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class MongoStudentQueryTest extends AbstractMongoTestConfig {

    @Resource(name = "studentMongoDBDao")
    private StudentDao studentDao;

    @BeforeEach
    void setUp() {
        List<StudentEntity> all = studentDao.queryAll();
        if (all != null && !all.isEmpty()) {
            studentDao.deleteBatch(all);
        }
        studentDao.saveBatch(MongoTestDataHelper.createStudentBatch());
    }

    @Test
    void shouldQueryAll() {
        List<StudentEntity> all = studentDao.queryAll();
        assertThat(all).hasSize(5);
    }

    @Test
    void shouldGetByQueryWrapper() {
        StudentEntity student = studentDao.get(q -> q.eq("name", "张三"));
        assertThat(student).isNotNull();
        assertThat(student.getAge()).isEqualTo(18);
    }

    @Test
    void shouldQueryByQueryWrapper() {
        List<StudentEntity> students = studentDao.query(q -> q.eq(StudentEntity.AGE, 18));
        assertThat(students).hasSize(2);
    }

    @Test
    void shouldGetByLambda() {
        StudentEntity student = studentDao.getByLambda(q -> q.eq(StudentEntity::getName, "张三"));
        assertThat(student).isNotNull();
        assertThat(student.getAge()).isEqualTo(18);
    }

    @Test
    void shouldQueryByLambda() {
        List<StudentEntity> students = studentDao.queryByLambda(q -> q.eq(StudentEntity::getAge, 18));
        assertThat(students).hasSize(2);
    }

    @Test
    void shouldQueryPager() {
        var page = studentDao.queryPager(q -> q.ge("age", 18), 1, 2);
        assertThat(page).hasSize(2);
        assertThat(page.getTotalCount()).isEqualTo(5);
    }

    @Test
    void shouldQueryPagerByLambda() {
        var page = studentDao.queryPagerByLambda(q -> q.ge(StudentEntity::getAge, 18), 1, 2);
        assertThat(page).hasSize(2);
        assertThat(page.getTotalCount()).isEqualTo(5);
    }

    @Test
    void shouldDeleteByQueryWrapper() {
        studentDao.delete(q -> q.eq("name", "张三"));

        List<StudentEntity> remaining = studentDao.queryAll();
        assertThat(remaining).hasSize(4);
    }

    @Test
    void shouldDeleteByLambda() {
        studentDao.deleteByLambda(q -> q.eq(StudentEntity::getName, "张三"));

        List<StudentEntity> remaining = studentDao.queryAll();
        assertThat(remaining).hasSize(4);
    }

    @Test
    void shouldUpdateByQueryWrapper() {
        studentDao.update(q -> q.set("age", 99).eq("name", "张三"));

        StudentEntity updated = studentDao.get(q -> q.eq("name", "张三"));
        assertThat(updated.getAge()).isEqualTo(99);
    }

    @Test
    void shouldUpdateByLambda() {
        studentDao.updateByLambda(q -> q.set(StudentEntity::getAge, 99).eq(StudentEntity::getName, "张三"));

        StudentEntity updated = studentDao.getByLambda(q -> q.eq(StudentEntity::getName, "张三"));
        assertThat(updated.getAge()).isEqualTo(99);
    }
}
```

- [ ] **Step 5: 创建 MongoCourseCrudTest.java**

```java
package com.hbasesoft.framework.db.demo.mongo.course;

import com.hbasesoft.framework.db.demo.mongo.config.AbstractMongoTestConfig;
import com.hbasesoft.framework.db.demo.mongo.config.MongoTestDataHelper;
import com.hbasesoft.framework.db.demo.mongo.dao.CourseDao;
import com.hbasesoft.framework.db.demo.mongo.entity.CourseEntity;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class MongoCourseCrudTest extends AbstractMongoTestConfig {

    @Resource(name = "courseMongoDBDao")
    private CourseDao courseDao;

    @BeforeEach
    void cleanUp() {
        List<CourseEntity> all = courseDao.queryAll();
        if (all != null && !all.isEmpty()) {
            courseDao.deleteBatch(all);
        }
    }

    @Test
    void shouldSaveAndRetrieveCourse() {
        CourseEntity course = MongoTestDataHelper.createCourse("语文", "基础语文");
        courseDao.save(course);

        assertThat(course.getId()).isNotNull();

        CourseEntity retrieved = courseDao.get(course.getId());
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getCourseName()).isEqualTo("语文");
    }

    @Test
    void shouldSaveBatchCourses() {
        List<CourseEntity> courses = Arrays.asList(
            MongoTestDataHelper.createCourse("语文", "基础语文"),
            MongoTestDataHelper.createCourse("数学", "高等数学"),
            MongoTestDataHelper.createCourse("英语", "英语口语")
        );
        courseDao.saveBatch(courses);

        List<CourseEntity> all = courseDao.queryAll();
        assertThat(all).hasSize(3);
    }

    @Test
    void shouldDeleteCourse() {
        CourseEntity course = MongoTestDataHelper.createCourse("语文", "基础语文");
        courseDao.save(course);

        courseDao.delete(course);

        assertThat(courseDao.get(course.getId())).isNull();
    }

    @Test
    void shouldQueryAllCourses() {
        courseDao.saveBatch(Arrays.asList(
            MongoTestDataHelper.createCourse("语文", "基础语文"),
            MongoTestDataHelper.createCourse("数学", "高等数学")
        ));

        List<CourseEntity> all = courseDao.queryAll();
        assertThat(all).hasSize(2);
    }

    @Test
    void shouldGetByProperty() {
        courseDao.saveBatch(Arrays.asList(
            MongoTestDataHelper.createCourse("语文", "基础语文"),
            MongoTestDataHelper.createCourse("数学", "高等数学")
        ));

        CourseEntity course = courseDao.get(q -> q.eq(CourseEntity.COURSE_NAME, "语文"));
        assertThat(course).isNotNull();
        assertThat(course.getRemark()).isEqualTo("基础语文");
    }
}
```

---

### Task 14: 运行 MongoDB 模块测试并验证

- [ ] **Step 1: 运行全部测试**

Run: `cd /Users/wangwei/Projects/f41/framework-db && mvn test -pl framework-db-demo-mongo -am`
预期：BUILD SUCCESS

- [ ] **Step 2: 检查覆盖率**

Run: `cd /Users/wangwei/Projects/f41/framework-db/framework-db-demo-mongo && cat target/site/jacoco/index.html | grep -A2 "Total"`
预期：行覆盖率 >= 70%

---

### Task 15: 删除原 framework-db-demo 模块

**Files:**
- Delete: `/Users/wangwei/Projects/f41/framework-db/framework-db-demo/` 整个目录

- [ ] **Step 1: 确认新模块测试全部通过**

Run: `cd /Users/wangwei/Projects/f41/framework-db && mvn test -pl framework-db-demo-mysql,framework-db-demo-mongo -am`
预期：BUILD SUCCESS

- [ ] **Step 2: 删除原 demo 模块目录**

```bash
rm -rf /Users/wangwei/Projects/f41/framework-db/framework-db-demo
```

- [ ] **Step 3: 最终验证**

Run: `cd /Users/wangwei/Projects/f41 && mvn clean test -pl framework-db -am`
预期：BUILD SUCCESS，所有模块编译和测试通过
