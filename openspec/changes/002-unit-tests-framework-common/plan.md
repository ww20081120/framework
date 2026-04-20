# framework-common 单元测试补充实施计划

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** 为 framework-common 模块编写全面的单元测试，提升代码覆盖率和代码质量

**架构方法：** 采用标准 JUnit 5 + AssertJ 风格，按优先级分批次实施。每个测试采用 TDD 方式：先写失败测试 → 运行失败 → 实现代码 → 运行通过 → 提交。

**技术栈：** JUnit Jupiter, AssertJ, Spring Boot Test

---

## 前置准备

### Task 0: 添加测试依赖到 pom.xml

**Files:**
- Modify: `framework-common/framework-common-core/pom.xml`

**Step 1: 检查依赖现状**
```bash
grep -A2 "artifactId:assertj" framework-common/framework-common-core/pom.xml
```
Expected: 无匹配（确认需要添加）

**Step 2: 添加 AssertJ 依赖**
在 `framework-common-core/pom.xml` 的 `<dependencies>` 节点中添加：
```xml
<dependency>
    <groupId>org.assertj</groupId>
    <artifactId>assertj-core</artifactId>
    <scope>test</scope>
</dependency>
```

**Step 3: 验证依赖添加**
```bash
cd framework-common/framework-common-core
mvn dependency:tree -Dincludes=org.assertj:assertj-core
```
Expected: 显示 assertj-core 依赖树

**Step 4: 提交**
```bash
git add framework-common/framework-common-core/pom.xml
git commit -m "test: add AssertJ dependency for unit tests"
```

---

## 第一阶段：核心工具类重写

### Task 1: 重写 CommonUtilTest

**Files:**
- Modify: `framework-common/framework-common-core/src/test/java/com/hbasesoft/framework/common/utils/CommonUtilTest.java`
- Test: `framework-common/framework-common-core/src/test/java/com/hbasesoft/framework/common/utils/CommonUtilTest.java`

**Step 1: 编写失败测试（新风格）**
```java
package com.hbasesoft.framework.common.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

@DisplayName("CommonUtil 单元测试")
class CommonUtilTest {

    @Test
    @DisplayName("应正确格式化消息 - 多参数场景")
    void should_formatMessage_when_multipleParams() {
        String result = CommonUtil.messageFormat("Hello {0}, age {1}", "Alice", 18);
        assertThat(result).isEqualTo("Hello Alice, age 18");
    }

    @Test
    @DisplayName("应返回原消息 - 无参数场景")
    void should_returnOriginalMessage_when_noParams() {
        String result = CommonUtil.messageFormat("No params");
        assertThat(result).isEqualTo("No params");
    }

    @Test
    @DisplayName("应生成唯一事务ID")
    void should_generateUniqueTransactionId() {
        String id1 = CommonUtil.getTransactionID();
        String id2 = CommonUtil.getTransactionID();
        assertThat(id1).isNotEmpty().isNotEqualTo(id2);
        assertThat(id1).hasSize(32); // UUID去除横线后32位
    }

    @Test
    @DisplayName("应生成随机码")
    void should_generateRandomCode() {
        String code = CommonUtil.getRandomCode();
        assertThat(code).isNotEmpty();
    }

    @Test
    @DisplayName("应生成指定位数随机数")
    void should_generateRandomNumber_withLength() {
        String num = CommonUtil.getRandomNumber(5);
        assertThat(num).hasSize(5);
        assertThat(num).containsOnlyDigits();
    }

    @Test
    @DisplayName("应生成指定长度随机字符")
    void should_generateRandomChar_withLength() {
        String str = CommonUtil.getRandomChar(10);
        assertThat(str).hasSize(10);
    }

    @Test
    @DisplayName("应移除所有符号")
    void should_removeAllSymbols() {
        String result = CommonUtil.removeAllSymbol("hello!@#world");
        assertThat(result).isEqualTo("helloworld");
    }

    @Test
    @DisplayName("应移除所有空白字符")
    void should_removeAllBlanks() {
        String result = CommonUtil.replaceAllBlank("  a  b\t\nc  ");
        assertThat(result).isEqualTo("abc");
    }
}
```

**Step 2: 运行测试（预期失败或成功）**
```bash
cd framework-common/framework-common-core
mvn test -Dtest=CommonUtilTest
```
Expected: 部分通过（现有逻辑正确），部分失败（如断言风格不兼容需先调整）

**Step 3: 重构测试类（使用 AssertJ）**
完整替换 `CommonUtilTest.java` 内容为上述代码

**Step 4: 运行验证**
```bash
mvn test -Dtest=CommonUtilTest
```
Expected: PASS

**Step 5: 提交**
```bash
git add framework-common/framework-common-core/src/test/java/com/hbasesoft/framework/common/utils/CommonUtilTest.java
git commit -m "test: rewrite CommonUtilTest with JUnit5+AssertJ"
```

---

### Task 2: 重写 DateUtilTest

**Files:**
- Modify: `framework-common/framework-common-core/src/test/java/com/hbasesoft/framework/common/utils/date/DateUtilTest.java`

**Step 1: 分析 DateUtil 方法**
```bash
grep "public static" framework-common/framework-common-core/src/main/java/com/hbasesoft/framework/common/utils/date/DateUtil.java | head -20
```

**Step 2: 编写测试**
创建 `DateUtilTest.java`：
```java
package com.hbasesoft.framework.common.utils.date;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Date;
import static org.assertj.core.api.Assertions.*;

@DisplayName("DateUtil 单元测试")
class DateUtilTest {

    @Test
    @DisplayName("应格式化日期为字符串")
    void should_formatDate_toString() {
        Date date = new Date();
        String result = DateUtil.date2String(date, "yyyy-MM-dd");
        assertThat(result).matches("\\d{4}-\\d{2}-\\d{2}");
    }

    @Test
    @DisplayName("应解析字符串为日期")
    void should_parseString_toDate() {
        Date date = DateUtil.string2Date("2024-01-01", "yyyy-MM-dd");
        assertThat(date).isNotNull();
    }

    @Test
    @DisplayName("应计算日期差值")
    void should_calculateDaysBetween() {
        // 根据实际方法签名调整
    }
}
```

**Step 3: 运行测试**
```bash
mvn test -Dtest=DateUtilTest
```

**Step 4: 根据测试结果调整**

**Step 5: 提交**
```bash
git add framework-common/framework-common-core/src/test/java/com/hbasesoft/framework/common/utils/date/DateUtilTest.java
git commit -m "test: rewrite DateUtilTest with JUnit5+AssertJ"
```

---

### Task 3: 重写 HttpUtilTest

**Files:**
- Modify: `framework-common/framework-common-core/src/test/java/com/hbasesoft/framework/common/utils/io/HttpUtilTest.java`

**Step 1: 分析 HttpUtil API**
```bash
grep "public static" framework-common/framework-common-core/src/main/java/com/hbasesoft/framework/common/utils/io/HttpUtil.java
```

**Step 2: 编写测试**
```java
package com.hbasesoft.framework.common.utils.io;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

@DisplayName("HttpUtil 单元测试")
class HttpUtilTest {

    @Test
    @DisplayName("应正确编码URL参数")
    void should_encodeUrlParameters() {
        String result = HttpUtil.encodeParam("name=张三&age=18");
        assertThat(result).isNotEmpty();
    }

    @Test
    @DisplayName("应构建URL")
    void should_buildUrl() {
        // 根据 API 编写
    }
}
```

**Step 3: 运行并提交**
```bash
mvn test -Dtest=HttpUtilTest
git add framework-common/framework-common-core/src/test/java/com/hbasesoft/framework/common/utils/io/HttpUtilTest.java
git commit -m "test: rewrite HttpUtilTest with JUnit5+AssertJ"
```

---

### Task 4: 创建 BeanUtilTest（重写）

**Files:**
- Create: `framework-common/framework-common-core/src/test/java/com/hbasesoft/framework/common/utils/bean/BeanUtilTest.java`

**Step 1: 编写测试**
```java
package com.hbasesoft.framework.common.utils.bean;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Set;
import static org.assertj.core.api.Assertions.*;

@DisplayName("BeanUtil 单元测试")
class BeanUtilTest {

    @Test
    @DisplayName("应将驼峰转为下划线")
    void should_toUnderlineName() {
        String result = BeanUtil.toUnderlineName("userName");
        assertThat(result).isEqualTo("user_name");
    }

    @Test
    @DisplayName("应将下划线转为驼峰")
    void should_toCamelCase() {
        String result = BeanUtil.toCamelCase("user_name");
        assertThat(result).isEqualTo("userName");
    }

    @Test
    @DisplayName("应识别简单值类型")
    void should_identifySimpleValueType() {
        assertThat(BeanUtil.isSimpleValueType(Integer.class)).isTrue();
        assertThat(BeanUtil.isSimpleValueType(String.class)).isTrue();
        assertThat(BeanUtil.isSimpleValueType(Object.class)).isFalse();
    }

    @Test
    @DisplayName("应扫描包下所有类")
    void should_scanPackageClasses() {
        Set<Class<?>> classes = BeanUtil.getClasses("com.hbasesoft.framework.common.utils.bean");
        assertThat(classes).isNotEmpty();
        assertThat(classes).anySatisfy(c -> assertThat(c.getSimpleName()).contains("BeanUtil"));
    }

    @Test
    @DisplayName("应判断方法是否抽象")
    void should_checkMethodAbstract() {
        Method method = Runnable.class.getMethods()[0];
        assertThat(BeanUtil.isAbstract(method)).isTrue();
    }
}
```

**Step 2: 运行测试**
```bash
mvn test -Dtest=BeanUtilTest
```

**Step 3: 提交**
```bash
git add framework-common/framework-common-core/src/test/java/com/hbasesoft/framework/common/utils/bean/BeanUtilTest.java
git commit -m "test: rewrite BeanUtilTest with JUnit5+AssertJ"
```

---

## 第二阶段：新增缺失测试

### Task 5: 创建 ClassUtilTest

**Files:**
- Create: `framework-common/framework-common-core/src/test/java/com/hbasesoft/framework/common/utils/bean/ClassUtilTest.java`

**Step 1: 分析 ClassUtil 方法**
```bash
grep "public static" framework-common/framework-common-core/src/main/java/com/hbasesoft/framework/common/utils/bean/ClassUtil.java
```

**Step 2: 编写测试**
```java
package com.hbasesoft.framework.common.utils.bean;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

@DisplayName("ClassUtil 单元测试")
class ClassUtilTest {

    @Test
    @DisplayName("应获取类所有接口")
    void should_getAllInterfaces() {
        Class<?>[] interfaces = ClassUtil.getAllInterfaces(Runnable.class);
        assertThat(interfaces).contains(Runnable.class);
    }

    @Test
    @DisplayName("应判断是否为简单类型")
    void should_isSimpleType() {
        assertThat(ClassUtil.isSimpleType(Integer.TYPE)).isTrue();
        assertThat(ClassUtil.isSimpleType(String.class)).isTrue();
    }

    @Test
    @DisplayName("应获取类字段")
    void should_getFields() {
        // 根据 API 实现
    }
}
```

**Step 3: 运行并提交**
```bash
mvn test -Dtest=ClassUtilTest
git add framework-common/framework-common-core/src/test/java/com/hbasesoft/framework/common/utils/bean/ClassUtilTest.java
git commit -m "test: add ClassUtilTest"
```

---

### Task 6: 创建 IOUtilTest

**Files: Create: `framework-common/framework-common-core/src/test/java/com/hbasesoft/framework/common/utils/io/IOUtilTest.java`

**Step 1: 分析 IOUtil 方法**
```bash
grep "public static" framework-common/framework-common-core/src/main/java/com/hbasesoft/framework/common/utils/io/IOUtil.java
```

**Step 2: 编写测试**
```java
package com.hbasesoft.framework.common.utils.io;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.io.*;
import static org.assertj.core.api.Assertions.*;

@DisplayName("IOUtil 单元测试")
class IOUtilTest {

    @Test
    @DisplayName("应复制流")
    void should_copyStream() {
        InputStream input = new ByteArrayInputStream("test".getBytes());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        IOUtil.copy(input, out);
        assertThat(out.toString()).isEqualTo("test");
    }

    @Test
    @DisplayName("应关闭流")
    void should_closeQuietly() {
        InputStream stream = new ByteArrayInputStream("data".getBytes());
        IOUtil.closeQuietly(stream);
        // 验证流已关闭
    }

    @Test
    @DisplayName("应读取字符串")
    void should_readString() {
        String result = IOUtil.toString(new ByteArrayInputStream("hello".getBytes()));
        assertThat(result).isEqualTo("hello");
    }
}
```

**Step 3: 运行并提交**
```bash
mvn test -Dtest=IOUtilTest
git add framework-common-framework-common-core/src/test/java/com/hbasesoft/framework/common/utils/io/IOUtilTest.java
git commit -m "test: add IOUtilTest"
```

---

### Task 7: 创建 OgnlUtilTest

**Files:** `framework-common/framework-common-core/src/test/java/com/hbasesoft/framework/common/utils/engine/OgnlUtilTest.java`

**Step 1: 分析 OgnlUtil 方法**
```bash
grep "public static" framework-common-framework-common-core/src/main/java/com/hbasesoft/framework/common/utils/engine/OgnlUtil.java
```

**Step 2: 编写测试**
```java
package com.hbasesoft.framework.common.utils.engine;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;
import static org.assertj.core.api.Assertions.*;

@DisplayName("OgnlUtil 单元测试")
class OgnlTest {

    @Test
    @DisplayName("应正确执行表达式")
    void should_evaluateOgnl() {
        Map<String, Object> context = new HashMap<>();
        context.put("name", "Alice");

        Object result = OgnlUtil.getValue("#name", context);
        assertThat(result).isEqualTo("Alice");
    }

    @Test
    @DisplayName("应执行复杂表达式")
    void should_handleComplexExpression() {
        // 测试复杂表达式
    }
}
```

**Step 3: 运行并提交**
```bash
mvn test -Dtest=OgnlUtilTest
git add framework-common-framework-common-core/src/test/java/com/hbasesoft/framework/common/utils/engine/OgnlUtilTest.java
git commit -m "test: add OgnlUtilTest"
```

---

## 第三阶段：安全加密类测试

### Task 8: 创建 JWTUtilTest

**Files:** `framework-common-core/src/test/java/com/hbasesoft/framework/common/utils/security/JWTUtilTest.java`

**Step 1: 分析 JWTUtil 方法**
```bash
grep "public static" framework-common/framework-common-core/src/main/java/com/hbasesoft/framework/common/utils/security/JWTUtil.java
```

**Step 2: 编写测试**
```java
package com.hbasesoft.framework.common.utils.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

@DisplayName("JWTUtil 单元测试")
class JWTUtilTest {

    @Test
    @DisplayName("应生成并验证Token")
    void should_generateAndVerifyToken() {
        String token = JWTUtil.createToken("userId", "secret");
        assertThat(token).isNotEmpty();

        boolean valid = JWTUtil.verify(token, "secret");
        assertThat(valid).isTrue();
    }

    @Test
    @DisplayName("应解析Token信息")
    void should_parseToken() {
        String token = JWTUtil.createToken("userId", "secret");
        Map<String, Object> claims = JWTUtil.parse(token, "secret");
        assertThat(claims).isNotEmpty();
    }

    @Test
    @DisplayName("应拒绝无效Token")
    void should_rejectInvalidToken() {
        boolean valid = JWTUtil.verify("invalid.token.here", "secret");
        assertThat(valid).isFalse();
    }
}
```

**Step 3: 运行并提交**
```bash
mvn test -Dtest=JWTUtilTest
git add framework-common/framework-common-core/src/test/java/com/hbasesoft/framework/common/utils/security/JWTUtilTest.java
git commit -m "test: add JWTUtilTest"
```

### Task 9: 创建 RSAUtilTest

**Files:** `framework-common-core/src/test/java/com/hbasesoft/framework/common/utils/security/RSAUtilTest.java`

**Step 1: 分析 RSAUtil 方法**
```bash
grep "public static" framework-common/framework-common-core/src/main/java/com/hbasesoft/framework/common/utils/security/RSAUtil.java
```

**Step 2: 编写测试**
```java
package com.hbasesoft.framework.common.utils.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.security.KeyPair;
import static org.assertj.core.api.Assertions.*;

@DisplayName("RSAUtil 单元测试")
class RSAUtilTest {

    @Test
    @DisplayName("应生成RSA密钥对")
    void should_generateKeyPair() {
        KeyPair keyPair = RSAUtil.generateKeyPair();
        assertThat(keyPair).isNotNull();
        assertThat(keyPair.getPublic()).isNotNull();
        assertThat(keyPair.getPrivate()).isNotNull();
    }

    @Test
    @DisplayName("应加密解密数据")
    void should_encryptAndDecrypt() {
        KeyPair keyPair = RSAUtil.generateKeyPair();
        String data = "Secret Message";

        String encrypted = RSAUtil.encrypt(data, keyPair.getPublic());
        String decrypted = RSAUtil.decrypt(encrypted, keyPair.getPrivate());

        assertThat(decrypted).isEqualTo(data);
    }

    @Test
    @DisplayName("应签名和验签")
    void should_signAndVerify() {
        // 测试签名验证功能
    }
}
```

**Step 3: 运行并提交**
```bash
mvn test -Dtest=RSAUtilTest
git add framework-common/framework-common-core/src/test/java/com/hbasesoft/framework/common/utils/security/RSAUtilTest.java
git commit -m "test: add RSAUtilTest"
```

### Task 10: 创建 DESUtilTest

**Files:** `framework-common-core/src/test/java/com/hbasesoft/framework/common/utils/security/DESUtilTest.java`

**Step 1: 分析 DESUtil 方法**
```bash
grep "public static" framework-common-framework-common-core/src/main/java/com/hbasesoft/framework/common/utils/security/DESUtil.java
```

**Step 2: 编写测试**
```java
package com.hbasesoft.framework.common.utils.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

@DisplayName("DESUtil 单元测试")
class DESUtilTest {

    @Test
    @DisplayName("应加密解密数据")
    void should_encryptAndDecrypt() {
        String key = "12345678"; // DES需8字节密钥
        String data = "Hello World";

        String encrypted = DESUtil.encrypt(data, key);
        assertThat(encrypted).isNotEqualTo(data);

        String decrypted = DESUtil.decrypt(encrypted, key);
        assertThat(decrypted).isEqualTo(data);
    }

    @Test
    @DisplayName("应拒绝无效密钥")
    void should_failWithInvalidKey() {
        assertThatThrownBy(() -> DESUtil.decrypt("encrypted", "wrong"))
            .isInstanceOf(Exception.class);
    }
}
```

**Step 3: 运行并提交**
```bash
mvn test -Dtest=DESUtilTest
git add framework-common/framework-common-core/src/test/java/com/hbasesoft/framework/common/utils/security/DESUtilTest.java
git commit -m "test: add DESUtilTest"
```

### Task 11: 创建 DataUtilTest（重写）

**Files:** `framework-common-core/src/test/java/com/hbasesoft/framework/common/utils/security/DataUtilTest.java`

**Step 1: 分析 DataUtil 方法**
```bash
grep "public static" framework-common/framework-common-core/src/main/java/com/hbasesoft/framework/common/utils/security/DataUtil.java
```

**Step 2: 编写测试**
```java
package com.hbasesoft.framework.common.utils.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

@DisplayName("DataUtil 单元测试")
class DataUtilTest {

    @Test
    @DisplayName("应加密解密密码")
    void should_encryptAndDecryptPassword() {
        String password = "mypassword123";
        String encrypted = DataUtil.encrypt(password);
        assertThat(encrypted).isNotEqualTo(password);

        String decrypted = DataUtil.decrypt(encrypted);
        assertThat(decrypted).isEqualTo(password);
    }

    @Test
    @DisplayName("应生成不同密文")
    void should_generateDifferentCipherText() {
        String pwd = "password";
        String c1 = DataUtil.encrypt(pwd);
        String c2 = DataUtil.encrypt(pwd);
        // 相同原文多次加密应产生不同密文（有盐值）
        assertThat(c1).isNotEqualTo(c2);
    }
}
```

**Step 3: 运行并提交**
```bash
mvn test -Dtest=DataUtilTest
git add framework-common-framework-common-core/src/test/java/com/hbasesoft/framework/common/utils/security/DataUtilTest.java
git commit -m "rewrite DataUtilTest with JUnit5+AssertJ"
```

---

## 第四阶段：其他工具类测试

### Task 12: 创建 PropertyHolderTest

**Files:** `framework-common-core/src/test/java/com/hbasesoft/framework/common/utils/PropertyHolderTest.java`

**Step 1: 分析 PropertyHolder**
```bash
grep "public static" framework-common/framework-common-core/src/main/java/com/hbasesoft/framework/common/utils/PropertyHolder.java
```

**Step 2: 编写测试**
```java
package com.hbasesoft.framework.common.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@DisplayName("PropertyHolder 单元测试")
class PropertyHolderTest {

    @Test
    @DisplayName("应获取配置属性")
    void should_getProperty() {
        String value = PropertyHolder.getProperty("test.key");
        // 根据实际配置调整
    }

    @Test
    @DisplayName("应获取默认值")
    void should_getProperty_withDefault() {
        String value = PropertyHolder.getProperty("unknown.key", "default");
        assertThat(value).isEqualTo("default");
    }
}
```

**Step 3: 运行并提交**
```bash
mvn test -Dtest=PropertyHolderTest
git add framework-common/framework-common-core/src/test/java/com/hbasesoft/framework/common/utils/PropertyHolderTest.java
git commit -m "rewrite PropertyHolderTest with JUnit5+AssertJ"
```

### Task 13: 创建 ThreadUtilTest

**Files:** `framework-common-core/src/test/java/com/hbasesoft/framework/common/utils/thread/ThreadUtilTest.java`

**Step 1: 分析 ThreadUtil**
```bash
grep "public static" framework-common-framework-common-core/src/main/java/com/hbasesoft/framework/common/utils/thread/ThreadUtil.java
```

**Step 2: 编写测试**
```java
package com.hbasesoft.framework.common.utils.thread;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

@DisplayName("ThreadUtil 单元测试")
class ThreadUtilTest {

    @Test
    @DisplayName("应休眠线程")
    void should_sleepThread() throws InterruptedException {
        long start = System.currentTimeMillis();
        ThreadUtil.sleep(100);
        long duration = System.currentTimeMillis() - start;

        assertThat(duration).isGreaterThanOrEqualTo(100);
    }

    @Test
    @DisplayName("应获取当前线程")
    void should_getCurrentThread() {
        Thread t = ThreadUtil.currentThread();
        assertThat(t).isNotNull();
        assertThat(t.isAlive()).isTrue();
    }
}
```

**Step 3: 运行并提交**
```bash
mvn test -Dtest=ThreadUtilTest
git add framework-common-framework-common-core/src/test/java/com/hbasesoft/framework/common/utils/thread/ThreadUtilTest.java
git commit -m "test: add ThreadUtilTest"
```

### Task 14: 创建 SerializationUtilTest（重写）

**Files:** `framework-common-core/src/test/java/com/hbasesoft/framework/common/utils/bean/SerializationUtilTest.java`

**Step 1: 分析 SerializationUtil**
```bash
grep "public static" framework-common-framework-common-core/src/main/java/com/hbasesoft/framework/common/utils/bean/SerializationUtil.java
```

**Step 2: 编写测试**
```java
package com.hbasesoft.framework.common.utils.bean;

import org.junit.jupiter.api DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

@DisplayName("SerializationUtil 单元测试")
class SerializationUtilTest {

    @Test
    @DisplayName("应序列化反序列化对象")
    void should_serializeAndDeserialize() {
        Object obj = new TestBean();
        byte[] serialized = SerializationUtil.serialize(obj);

        Object deserialized = SerializationUtil.deserialize(serialized);
        assertThat(deserialized).isEqualToComparingFieldByField(obj);
    }

    @Test
    @DisplayName("应使用Kryo序列化")
    void should_useKryoSerialization() {
        // 测试 Kryo 序列化
    }
}
```

**Step 3: 运行并提交**
```bash
mvn test -Dtest=SerializationUtilTest
git add framework-common/framework-common-core/src/test/java/com/hbasesoft/framework/common/utils/bean/SerializationUtilTest.java
git commit -m "rewrite SerializationUtilTest with JUnit5+AssertJ"
```

### Task 15: 创建 URLUtilTest

**Files:** `framework-common-core/src/test/java/com/hbasesoft/framework/common/utils/security/URLUtilTest.java`

**Step 1: 分析 URLUtil**
```bash
grep "public static" framework-common-framework-common-core/src/main/java/com/hbasesoft/framework/common/utils/security/URLUtil.java
```

**Step 2: 编写测试**
```java
package com.hbasesoft.framework.common.utils.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

@DisplayName("URLUtil 单元测试")
class URLUtilTest {

    @Test
    @DisplayName("应编码URL")
    void should_encodeURL() {
        String url = URLUtil.encode("http://example.com/测试");
        assertThat(url).doesNotContain("测试");
    }

    @Test
    @DisplayName("应解码URL")
    void should_decodeURL() {
        String decoded = URLUtil.decode("%E6%B5%E8%AF%95");
        assertThat(decoded).isEqualTo("测");
    }

    @�试DisplayName("应处理特殊字符")
    void should_handleSpecialChars() {
        // 测试特殊字符处理
    }
}
```

**Step 3: 运行并提交**
```bash
mvn test -test=URLUtilTest
git add framework-common-framework-common-core/src/test/java/com/hbasesoft/framework/common/utils/security/URLUtilTest.java
git-commit -m "test: add URLUtilTest"
```

---

## 第五阶段：XML/Image工具测试

### Task 16: 重写 XmlTest

**Files:** `framework-xml/src/test/java/com/hbasesoft/framework/common/utils/xml/XmlTest.java`

**Step 1: 分析 XML 工具类**
```bash
grep "public static" framework-common/framework-common-xml/src/main/java/com/hbasesoft/framework/common/utils/xml/*.java
```

**Step 2: 重写测试**
```java
package com.hbasesoft.framework.common.utils.xml;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

@DisplayName("XML 工具单元测试")
class XmlTest {

    @Test
    @DisplayName("应解析XML")
    void should_parseXml() {
        String xml = "<root><child>value</child></root>";
        // 根据实际API调整
    }

    @Test
    @DisplayName("应序列化对象为XML")
    void should_toXml() {
        // 测试序列化
    }
}
```

**Step 3: 运行并提交**
```bash
cd framework-common/framework-common-xml
mvn test
git add src/test/java/com/hbasesoft/framework/common/utils/xml/XmlTest.java
git commit -m "rewrite XmlTest with JUnit5+AssertJ"
```

### Task 17: 创建 ImageUtilTest

**Files:** `framework-image/src/test/java/com/hbasesoft/framework/common/util/ImageUtilTest.java`

**Step 1: 分析 ImageUtil**
```bash
grep "public static" framework-common-framework-common-image/src/main/java/com/hbasesoft/framework/common/util/ImageUtil.java
```

**Step 2: 编写测试**
```java
package com.hbasesoft.framework.common.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import static org.assertj.core.api.Assertions.*;

@DisplayName("ImageUtil 单元测试")
class ImageUtilTest {

    @Test
    @DisplayName("应缩放图片")
    void should_scaleImage() throws Exception {
        // 创建测试用小图
        // 测试缩放功能
    }

    @Test
    @DisplayName("应裁剪图片")
    void should_cropImage() {
        // 测试裁剪功能
    }

    @Test
    @DisplayName("应添加水印")
    void should_addWatermark() {
        // 测试水印功能
    }
}
```

**Step 3: 运行并提交**
```bash
cd framework-common/framework-common-image
mvn test
git add src/test/java/com/hbasesoft/framework/common/util/ImageUtilTest.java
git commit -m "add ImageUtilTest"
```

---

## 验收标准

全部任务完成后运行：

```bash
# 检查测试覆盖率（需添加 JaCoCo 插件）
mvn clean test jacoco:report

# 查看覆盖率报告
open framework-common/framework-common-core/target/site/jacoco/index.html

# 全量测试
mvn test
```

预期目标：
- **代码覆盖率 > 70%**
- **所有测试通过**
- **符合 JUnit 5 + AssertJ 风格**
- **测试命名规范，文档清晰**

---

## 备注

1. **YAGNI 原则**：不添加不需要的测试场景
2. **DRY 原则**：提取通用测试工具类
3. **TDD 流程**：每个类独立完成测试→实现→验证→提交
4. **迭代优先级**：核心工具→加密工具→其他
