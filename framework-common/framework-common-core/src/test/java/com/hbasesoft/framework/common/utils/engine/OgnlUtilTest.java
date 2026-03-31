/****************************************************************************************
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.common.utils.engine;

import static org.assertj.core.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.hbasesoft.framework.common.utils.UtilException;

/**
 * OgnlUtil 测试类
 *
 * @author framework
 */
@DisplayName("OgnlUtil 工具类测试")
class OgnlUtilTest {

    /**
     * 用于测试的简单类
     */
    public static class TestPerson {
        private String name;
        private int age;
        private Address address;

        public TestPerson() {
        }

        public TestPerson(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public Address getAddress() {
            return address;
        }

        public void setAddress(Address address) {
            this.address = address;
        }
    }

    /**
     * 用于测试的地址类
     */
    public static class Address {
        private String city;
        private String street;

        public Address() {
        }

        public Address(String city, String street) {
            this.city = city;
            this.street = street;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getStreet() {
            return street;
        }

        public void setStreet(String street) {
            this.street = street;
        }
    }

    @Test
    @DisplayName("getValue - 获取 Map 中的简单属性值")
    void testGetValue_SimpleProperty() {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("name", "张三");
        paramMap.put("age", 25);

        Object result = OgnlUtil.getValue("name", paramMap);
        assertThat(result).isEqualTo("张三");

        result = OgnlUtil.getValue("age", paramMap);
        assertThat(result).isEqualTo(25);
    }

    @Test
    @DisplayName("getValue - 获取对象嵌套属性")
    void testGetValue_NestedProperty() {
        TestPerson person = new TestPerson("李四", 30);
        Address address = new Address("北京", "长安街");
        person.setAddress(address);

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("person", person);

        Object result = OgnlUtil.getValue("person.name", paramMap);
        assertThat(result).isEqualTo("李四");

        result = OgnlUtil.getValue("person.age", paramMap);
        assertThat(result).isEqualTo(30);

        result = OgnlUtil.getValue("person.address.city", paramMap);
        assertThat(result).isEqualTo("北京");

        result = OgnlUtil.getValue("person.address.street", paramMap);
        assertThat(result).isEqualTo("长安街");
    }

    @Test
    @DisplayName("getValue - 执行算术运算")
    void testGetValue_ArithmeticOperation() {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("a", 10);
        paramMap.put("b", 5);

        Object result = OgnlUtil.getValue("a + b", paramMap);
        assertThat(result).isEqualTo(15);

        result = OgnlUtil.getValue("a - b", paramMap);
        assertThat(result).isEqualTo(5);

        result = OgnlUtil.getValue("a * b", paramMap);
        assertThat(result).isEqualTo(50);

        result = OgnlUtil.getValue("a / b", paramMap);
        assertThat(result).isEqualTo(2);

        result = OgnlUtil.getValue("a % b", paramMap);
        assertThat(result).isEqualTo(0);
    }

    @Test
    @DisplayName("getValue - 执行逻辑运算")
    void testGetValue_LogicalOperation() {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("a", true);
        paramMap.put("b", false);

        Object result = OgnlUtil.getValue("a && b", paramMap);
        assertThat(result).isEqualTo(false);

        result = OgnlUtil.getValue("a || b", paramMap);
        assertThat(result).isEqualTo(true);

        result = OgnlUtil.getValue("!a", paramMap);
        assertThat(result).isEqualTo(false);

        result = OgnlUtil.getValue("!b", paramMap);
        assertThat(result).isEqualTo(true);
    }

    @Test
    @DisplayName("getValue - 执行比较运算")
    void testGetValue_ComparisonOperation() {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("a", 10);
        paramMap.put("b", 5);

        Object result = OgnlUtil.getValue("a > b", paramMap);
        assertThat(result).isEqualTo(true);

        result = OgnlUtil.getValue("a < b", paramMap);
        assertThat(result).isEqualTo(false);

        result = OgnlUtil.getValue("a >= b", paramMap);
        assertThat(result).isEqualTo(true);

        result = OgnlUtil.getValue("a <= b", paramMap);
        assertThat(result).isEqualTo(false);

        result = OgnlUtil.getValue("a == b", paramMap);
        assertThat(result).isEqualTo(false);

        result = OgnlUtil.getValue("a != b", paramMap);
        assertThat(result).isEqualTo(true);
    }

    @Test
    @DisplayName("getValue - 使用三元表达式")
    void testGetValue_TernaryOperator() {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("score", 85);

        Object result = OgnlUtil.getValue("score >= 60 ? '及格' : '不及格'", paramMap);
        assertThat(result).isEqualTo("及格");

        paramMap.put("score", 45);
        result = OgnlUtil.getValue("score >= 60 ? '及格' : '不及格'", paramMap);
        assertThat(result).isEqualTo("不及格");
    }

    @Test
    @DisplayName("getValue - 调用对象方法")
    void testGetValue_MethodInvocation() {
        TestPerson person = new TestPerson("王五", 35);

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("person", person);

        Object result = OgnlUtil.getValue("person.getName()", paramMap);
        assertThat(result).isEqualTo("王五");

        result = OgnlUtil.getValue("person.getAge()", paramMap);
        assertThat(result).isEqualTo(35);
    }

    @Test
    @DisplayName("getValue - 使用集合索引")
    void testGetValue_CollectionIndex() {
        Map<String, Object> paramMap = new HashMap<>();
        int[] array = {1, 2, 3, 4, 5};
        paramMap.put("array", array);

        Object result = OgnlUtil.getValue("array[0]", paramMap);
        assertThat(result).isEqualTo(1);

        result = OgnlUtil.getValue("array[2]", paramMap);
        assertThat(result).isEqualTo(3);

        result = OgnlUtil.getValue("array[4]", paramMap);
        assertThat(result).isEqualTo(5);
    }

    @Test
    @DisplayName("getValue - 静态方法访问")
    void testGetValue_StaticMethod() {
        Map<String, Object> paramMap = new HashMap<>();

        Object result = OgnlUtil.getValue("@java.lang.Math@max(10, 20)", paramMap);
        assertThat(result).isEqualTo(20);

        result = OgnlUtil.getValue("@java.lang.Math@min(10, 20)", paramMap);
        assertThat(result).isEqualTo(10);

        result = OgnlUtil.getValue("@java.lang.Math@abs(-100)", paramMap);
        assertThat(result).isEqualTo(100);
    }

    @Test
    @DisplayName("getValue - 静态字段访问")
    void testGetValue_StaticField() {
        Map<String, Object> paramMap = new HashMap<>();

        Object result = OgnlUtil.getValue("@java.lang.Boolean@TRUE", paramMap);
        assertThat(result).isEqualTo(Boolean.TRUE);

        result = OgnlUtil.getValue("@java.lang.Integer@MAX_VALUE", paramMap);
        assertThat(result).isEqualTo(Integer.MAX_VALUE);
    }

    @Test
    @DisplayName("getValue - 空值处理")
    void testGetValue_NullHandling() {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("value", null);

        Object result = OgnlUtil.getValue("value", paramMap);
        assertThat(result).isNull();

        result = OgnlUtil.getValue("value == null", paramMap);
        assertThat(result).isEqualTo(true);

        result = OgnlUtil.getValue("value != null", paramMap);
        assertThat(result).isEqualTo(false);
    }

    @Test
    @DisplayName("getValue - 字符串连接")
    void testGetValue_StringConcatenation() {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("firstName", "张");
        paramMap.put("lastName", "三");

        Object result = OgnlUtil.getValue("firstName + lastName", paramMap);
        assertThat(result).isEqualTo("张三");
    }

    @Test
    @DisplayName("getValue - 复杂表达式")
    void testGetValue_ComplexExpression() {
        TestPerson person = new TestPerson("赵六", 28);
        person.setAddress(new Address("上海", "南京路"));

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("person", person);
        paramMap.put("discount", 0.8);

        Object result = OgnlUtil.getValue(
            "person.address.city + '-' + person.address.street + '-' + person.name",
            paramMap
        );
        assertThat(result).isEqualTo("上海-南京路-赵六");

        result = OgnlUtil.getValue("person.age > 25 && person.age < 30", paramMap);
        assertThat(result).isEqualTo(true);
    }

    @Test
    @DisplayName("getValue - 表达式语法错误应抛出异常")
    void testGetValue_SyntaxError() {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("value", 10);

        assertThatThrownBy(() -> OgnlUtil.getValue("value + ", paramMap))
            .isInstanceOf(UtilException.class)
            .hasCauseInstanceOf(ognl.OgnlException.class);
    }

    @Test
    @DisplayName("getValue - 访问不存在的属性应抛出异常")
    void testGetValue_NonExistentProperty() {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("person", new TestPerson());

        // OGNL 访问不存在的属性会抛出异常，被包装为 UtilException
        assertThatThrownBy(() -> OgnlUtil.getValue("person.nonExistentProperty", paramMap))
            .isInstanceOf(UtilException.class)
            .hasCauseInstanceOf(ognl.OgnlException.class);
    }

    @Test
    @DisplayName("getValue - 空参数 Map")
    void testGetValue_EmptyParamMap() {
        Map<String, Object> paramMap = new HashMap<>();

        Object result = OgnlUtil.getValue("1 + 1", paramMap);
        assertThat(result).isEqualTo(2);

        result = OgnlUtil.getValue("'Hello' + ' World'", paramMap);
        assertThat(result).isEqualTo("Hello World");
    }

    @Test
    @DisplayName("getValue - 类型转换")
    void testGetValue_TypeConversion() {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("intValue", "123");
        paramMap.put("doubleValue", "45.67");

        Object result = OgnlUtil.getValue("intValue", paramMap);
        assertThat(result).isEqualTo("123");

        result = OgnlUtil.getValue("doubleValue", paramMap);
        assertThat(result).isEqualTo("45.67");
    }

    @Test
    @DisplayName("getValue - 链式属性访问")
    void testGetValue_ChainedPropertyAccess() {
        TestPerson person = new TestPerson();
        Address address = new Address();
        address.setCity("深圳");
        person.setAddress(address);

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("person", person);

        Object result = OgnlUtil.getValue("person.address.city", paramMap);
        assertThat(result).isEqualTo("深圳");
    }

    @Test
    @DisplayName("getValue - 正则表达式匹配")
    void testGetValue_RegularExpression() {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("email", "test@example.com");
        paramMap.put("phone", "12345");

        Object result = OgnlUtil.getValue("email.matches('[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\\\.[a-zA-Z]{2,}')", paramMap);
        assertThat(result).isEqualTo(true);

        result = OgnlUtil.getValue("phone.matches('\\\\d+')", paramMap);
        assertThat(result).isEqualTo(true);
    }

    @Test
    @DisplayName("getValue - instanceof 运算符")
    void testGetValue_Instanceof() {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("str", "Hello");
        paramMap.put("num", 123);

        Object result = OgnlUtil.getValue("str instanceof java.lang.String", paramMap);
        assertThat(result).isEqualTo(true);

        result = OgnlUtil.getValue("num instanceof java.lang.Integer", paramMap);
        assertThat(result).isEqualTo(true);

        result = OgnlUtil.getValue("str instanceof java.lang.Integer", paramMap);
        assertThat(result).isEqualTo(false);
    }

    @Test
    @DisplayName("getValue - 空字符串表达式")
    void testGetValue_EmptyExpression() {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("value", "test");

        Object result = OgnlUtil.getValue("", paramMap);
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("getValue - 布尔值转换")
    void testGetValue_BooleanConversion() {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("flag", "true");

        Object result = OgnlUtil.getValue("flag", paramMap);
        assertThat(result).isEqualTo("true");
    }

    @Test
    @DisplayName("getValue - 位运算")
    void testGetValue_BitwiseOperation() {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("a", 5);  // 二进制: 101
        paramMap.put("b", 3);  // 二进制: 011

        Object result = OgnlUtil.getValue("a & b", paramMap);
        assertThat(result).isEqualTo(1);  // 101 & 011 = 001

        result = OgnlUtil.getValue("a | b", paramMap);
        assertThat(result).isEqualTo(7);  // 101 | 011 = 111

        result = OgnlUtil.getValue("a ^ b", paramMap);
        assertThat(result).isEqualTo(6);  // 101 ^ 011 = 110
    }

    @Test
    @DisplayName("getValue - 移位运算")
    void testGetValue_ShiftOperation() {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("value", 4);

        Object result = OgnlUtil.getValue("value << 2", paramMap);
        assertThat(result).isEqualTo(16);

        result = OgnlUtil.getValue("value >> 1", paramMap);
        assertThat(result).isEqualTo(2);
    }

    @Test
    @DisplayName("getValue - 括号表达式")
    void testGetValue_Parentheses() {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("a", 10);
        paramMap.put("b", 5);
        paramMap.put("c", 2);

        Object result = OgnlUtil.getValue("(a + b) * c", paramMap);
        assertThat(result).isEqualTo(30);

        result = OgnlUtil.getValue("a + (b * c)", paramMap);
        assertThat(result).isEqualTo(20);
    }

    @Test
    @DisplayName("getValue - 构造新对象")
    void testGetValue_ObjectCreation() {
        Map<String, Object> paramMap = new HashMap<>();

        Object result = OgnlUtil.getValue("new java.lang.String('test')", paramMap);
        assertThat(result).isEqualTo("test");

        result = OgnlUtil.getValue("new java.lang.Integer(100)", paramMap);
        assertThat(result).isEqualTo(100);
    }

    @Test
    @DisplayName("getValue - 集合创建和操作")
    void testGetValue_Creation() {
        Map<String, Object> paramMap = new HashMap<>();

        Object result = OgnlUtil.getValue("{1, 2, 3}", paramMap);
        assertThat(result).isInstanceOf(Object[].class);

        result = OgnlUtil.getValue("#{'key1': 'value1', 'key2': 'value2'}", paramMap);
        assertThat(result).isInstanceOf(Map.class);
    }

    @Test
    @DisplayName("getValue - Lambda 表达式")
    void testGetValue_Lambda() {
        Map<String, Object> paramMap = new HashMap<>();
        int[] numbers = {1, 2, 3, 4, 5};
        paramMap.put("numbers", numbers);

        Object result = OgnlUtil.getValue("#fact = :[ #this==1 ? 1 : #this*#fact(#this-1) ], #fact(5)", paramMap);
        assertThat(result).isEqualTo(120);
    }
}
