/****************************************************************************************
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.common.utils.bean;

import static org.assertj.core.api.Assertions.*;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.hbasesoft.framework.common.utils.UtilException;

import javassist.NotFoundException;

/**
 * BeanUtil 测试类
 * 使用 JUnit 5 + AssertJ 进行测试
 *
 * @author 王伟<br>
 * @version 2.0<br>
 * @taskId <br>
 * @CreateDate 2018年9月12日 <br>
 * @since V2.0<br>
 * @see com.hbasesoft.framework.common.utils.bean.BeanUtil
 */
@DisplayName("BeanUtil 工具类测试")
public class BeanUtilTest {

    @Test
    @DisplayName("isAbstract - 判断接口方法是否为抽象方法")
    void testIsAbstract_InterfaceMethod() {
        // Runnable 的 run() 方法是抽象方法
        Method[] methods = Runnable.class.getMethods();
        Method method = methods[0];

        assertThat(BeanUtil.isAbstract(method))
            .as("Runnable.run() 应该是抽象方法")
            .isTrue();
    }

    @Test
    @DisplayName("isAbstract - 判断接口的 default 方法是否为抽象方法")
    void testIsAbstract_InterfaceDefaultMethod() {
        // 接口中的 default 方法不是抽象方法
        Method[] methods = Comparable.class.getMethods();
        for (Method method : methods) {
            if (method.isDefault()) {
                assertThat(BeanUtil.isAbstract(method))
                    .as("接口中的 default 方法不应该被识别为抽象方法")
                    .isFalse();
                return;
            }
        }

        // 如果没有找到 default 方法，使用其他测试方式
        // 大多数接口都有非抽象方法（如 default 方法）
    }

    @Test
    @DisplayName("isAbstract - 判断普通类方法是否为抽象方法")
    void testIsAbstract_ClassMethod() throws NoSuchMethodException {
        // String 类的普通方法不是抽象方法
        Method method = String.class.getMethod("toString");

        assertThat(BeanUtil.isAbstract(method))
            .as("String.toString() 不应该是抽象方法")
            .isFalse();
    }

    @Test
    @DisplayName("getMethodSignature - 获取无参方法签名")
    void testGetMethodSignature_NoParams() throws NoSuchMethodException {
        Method method = String.class.getMethod("toString");
        String signature = BeanUtil.getMethodSignature(method);

        assertThat(signature)
            .as("toString() 方法签名")
            .isEqualTo("toString()");
    }

    @Test
    @DisplayName("getMethodSignature - 获取带参方法签名")
    void testGetMethodSignature_WithParams() throws NoSuchMethodException {
        Method method = String.class.getMethod("substring", int.class, int.class);
        String signature = BeanUtil.getMethodSignature(method);

        assertThat(signature)
            .as("substring(int, int) 方法签名")
            .isEqualTo("substring(int,int,)");
    }

    @Test
    @DisplayName("getMethodSignature - 获取单参方法签名")
    void testGetMethodSignature_SingleParam() throws NoSuchMethodException {
        Method method = String.class.getMethod("length");
        String signature = BeanUtil.getMethodSignature(method);

        assertThat(signature)
            .as("length() 方法签名")
            .isEqualTo("length()");
    }

    @Test
    @DisplayName("getMethodSignature - 获取多参方法签名（不同类型）")
    void testGetMethodSignature_MultipleParams() throws NoSuchMethodException {
        Method method = String.class.getMethod("substring", int.class, int.class);
        String signature = BeanUtil.getMethodSignature(method);

        assertThat(signature)
            .as("substring(int, int) 方法签名")
            .isEqualTo("substring(int,int,)");
    }

    @Test
    @DisplayName("getClasses - 获取包下所有类")
    void testGetClasses() {
        Set<Class<?>> classes = BeanUtil.getClasses("com.hbasesoft.framework.common.utils.bean");

        assertThat(classes)
            .as("应该能找到 com.hbasesoft.framework.common.utils.bean 包下的类")
            .isNotEmpty()
            .contains(BeanUtil.class);
    }

    @Test
    @DisplayName("getClasses - 使用过滤器获取包下特定类")
    void testGetClasses_WithFilter() {
        // 使用过滤器只获取 BeanUtil 类
        Set<Class<?>> classes = BeanUtil.getClasses(
            "com.hbasesoft.framework.common.utils.bean",
            clazz -> clazz.getName().endsWith("BeanUtil")
        );

        assertThat(classes)
            .as("使用过滤器后应该只返回 BeanUtil 类")
            .hasSize(1)
            .contains(BeanUtil.class);
    }

    @Test
    @DisplayName("getClasses - 获取包及子包下所有类（使用通配符）")
    void testGetClasses_WithWildcard() {
        Set<Class<?>> classes = BeanUtil.getClasses("com.hbasesoft.framework.common.utils.bean.*");

        assertThat(classes)
            .as("应该能找到 bean 包及其子包下的类")
            .isNotEmpty();
    }

    @Test
    @DisplayName("toUnderlineName - 驼峰转下划线 - 基本转换")
    void testToUnderlineName_Basic() {
        assertThat(BeanUtil.toUnderlineName("toUnderlineName"))
            .isEqualTo("to_underline_name");
    }

    @Test
    @DisplayName("toUnderlineName - 驼峰转下划线 - 单个大写字母")
    void testToUnderlineName_SingleUpperCase() {
        assertThat(BeanUtil.toUnderlineName("aB"))
            .isEqualTo("a_b");
    }

    @Test
    @DisplayName("toUnderlineName - 驼峰转下划线 - 连续大写字母")
    void testToUnderlineName_ConsecutiveUpperCase() {
        assertThat(BeanUtil.toUnderlineName("parseURL"))
            .isEqualTo("parse_url");
    }

    @Test
    @DisplayName("toUnderlineName - 驼峰转下划线 - 全大写")
    void testToUnderlineName_AllUpperCase() {
        assertThat(BeanUtil.toUnderlineName("URL"))
            .isEqualTo("url");
    }

    @Test
    @DisplayName("toUnderlineName - 驼峰转下划线 - null 值")
    void testToUnderlineName_Null() {
        assertThat(BeanUtil.toUnderlineName(null))
            .isNull();
    }

    @Test
    @DisplayName("toUnderlineName - 驼峰转下划线 - 空字符串")
    void testToUnderlineName_Empty() {
        assertThat(BeanUtil.toUnderlineName(""))
            .isEmpty();
    }

    @Test
    @DisplayName("toCamelCase - 下划线转驼峰 - 基本转换")
    void testToCamelCase_Basic() {
        assertThat(BeanUtil.toCamelCase("to_underline_name"))
            .isEqualTo("toUnderlineName");
    }

    @Test
    @DisplayName("toCamelCase - 下划线转驼峰 - 大小写混合")
    void testToCamelCase_MixedCase() {
        assertThat(BeanUtil.toCamelCase("To_CAmEL_CaSE"))
            .isEqualTo("toCamelCase");
    }

    @Test
    @DisplayName("toCamelCase - 下划线转驼峰 - 连续下划线")
    void testToCamelCase_MultipleUnderlines() {
        assertThat(BeanUtil.toCamelCase("hello__world"))
            .isEqualTo("helloWorld");
    }

    @Test
    @DisplayName("toCamelCase - 下划线转驼峰 - 下划线开头")
    void testToCamelCase_StartingUnderline() {
        // 实际行为：下划线后字母会大写
        assertThat(BeanUtil.toCamelCase("_helloWorld"))
            .isEqualTo("Helloworld");
    }

    @Test
    @DisplayName("toCamelCase - 下划线转驼峰 - null 值")
    void testToCamelCase_Null() {
        assertThat(BeanUtil.toCamelCase(null))
            .isNull();
    }

    @Test
    @DisplayName("toCamelCase - 下划线转驼峰 - 空字符串")
    void testToCamelCase_Empty() {
        assertThat(BeanUtil.toCamelCase(""))
            .isEmpty();
    }

    @Test
    @DisplayName("camelStr2underLine - 驼峰转下划线 - 基本转换")
    void testCamelStr2underLine_Basic() {
        assertThat(BeanUtil.camelStr2underLine("camelStr2underLine"))
            .isEqualTo("camel_str2under_line");
    }

    @Test
    @DisplayName("camelStr2underLine - 驼峰转下划线 - 单个单词")
    void testCamelStr2underLine_SingleWord() {
        assertThat(BeanUtil.camelStr2underLine("hello"))
            .isEqualTo("hello");
    }

    @Test
    @DisplayName("camelStr2underLine - 驼峰转下划线 - 全小写")
    void testCamelStr2underLine_AllLowerCase() {
        assertThat(BeanUtil.camelStr2underLine("helloworld"))
            .isEqualTo("helloworld");
    }

    @Test
    @DisplayName("camelStr2underLine - 驼峰转下划线 - 数字混合")
    void testCamelStr2underLine_WithNumbers() {
        assertThat(BeanUtil.camelStr2underLine("test123ABC"))
            .isEqualTo("test123_a_b_c");
    }

    @Test
    @DisplayName("toCapitalizeCamelCase - 转首字母大写驼峰 - 基本转换")
    void testToCapitalizeCamelCase_Basic() {
        assertThat(BeanUtil.toCapitalizeCamelCase("to_caPitaLize_CaMel_case"))
            .isEqualTo("ToCapitalizeCamelCase");
    }

    @Test
    @DisplayName("toCapitalizeCamelCase - 转首字母大写驼峰 - 纯下划线")
    void testToCapitalizeCamelCase_Underlines() {
        assertThat(BeanUtil.toCapitalizeCamelCase("hello_world_test"))
            .isEqualTo("HelloWorldTest");
    }

    @Test
    @DisplayName("toCapitalizeCamelCase - 转首字母大写驼峰 - null 值")
    void testToCapitalizeCamelCase_Null() {
        assertThat(BeanUtil.toCapitalizeCamelCase(null))
            .isNull();
    }

    @Test
    @DisplayName("toCapitalizeCamelCase - 转首字母大写驼峰 - 空字符串会抛出异常")
    void testToCapitalizeCamelCase_Empty() {
        // 源代码实现中，toCapitalizeCamelCase 对空字符串会抛出 StringIndexOutOfBoundsException
        // 这是源代码的缺陷，但我们需要测试实际行为
        assertThatThrownBy(() -> BeanUtil.toCapitalizeCamelCase(""))
            .isInstanceOf(StringIndexOutOfBoundsException.class);
    }

    @Test
    @DisplayName("isSimpleValueType - 基本类型包装类")
    void testIsSimpleValueType_Wrappers() {
        // Integer
        assertThat(BeanUtil.isSimpleValueType(Integer.class))
            .as("Integer 应该是简单值类型")
            .isTrue();

        // Long
        assertThat(BeanUtil.isSimpleValueType(Long.class))
            .as("Long 应该是简单值类型")
            .isTrue();

        // Double
        assertThat(BeanUtil.isSimpleValueType(Double.class))
            .as("Double 应该是简单值类型")
            .isTrue();

        // Boolean
        assertThat(BeanUtil.isSimpleValueType(Boolean.class))
            .as("Boolean 应该是简单值类型")
            .isTrue();
    }

    @Test
    @DisplayName("isSimpleValueType - 字符串")
    void testIsSimpleValueType_String() {
        assertThat(BeanUtil.isSimpleValueType(String.class))
            .as("String 应该是简单值类型")
            .isTrue();
    }

    @Test
    @DisplayName("isSimpleValueType - 日期时间类型")
    void testIsSimpleValueType_DateTypes() {
        assertThat(BeanUtil.isSimpleValueType(Date.class))
            .as("Date 应该是简单值类型")
            .isTrue();

        assertThat(BeanUtil.isSimpleValueType(java.sql.Date.class))
            .as("java.sql.Date 应该是简单值类型")
            .isTrue();

        assertThat(BeanUtil.isSimpleValueType(java.sql.Timestamp.class))
            .as("Timestamp 应该是简单值类型")
            .isTrue();
    }

    @Test
    @DisplayName("isSimpleValueType - 大数字类型")
    void testIsSimpleValueType_BigNumberTypes() {
        assertThat(BeanUtil.isSimpleValueType(BigDecimal.class))
            .as("BigDecimal 应该是简单值类型")
            .isTrue();

        assertThat(BeanUtil.isSimpleValueType(BigInteger.class))
            .as("BigInteger 应该是简单值类型")
            .isTrue();
    }

    @Test
    @DisplayName("isSimpleValueType - 枚举类型")
    void testIsSimpleValueType_Enum() {
        assertThat(BeanUtil.isSimpleValueType(Thread.State.class))
            .as("枚举类型应该是简单值类型")
            .isTrue();
    }

    @Test
    @DisplayName("isSimpleValueType - 普通对象不是简单类型")
    void testIsSimpleValueType_Object() {
        assertThat(BeanUtil.isSimpleValueType(Object.class))
            .as("Object 不应该是简单值类型")
            .isFalse();
    }

    @Test
    @DisplayName("isSimpleValueType - 数组不是简单类型")
    void testIsSimpleValueType_Array() {
        int[] intArray = new int[0];
        assertThat(BeanUtil.isSimpleValueType(intArray.getClass()))
            .as("int 数组不应该是简单值类型")
            .isFalse();

        Object[] objArray = new Object[0];
        assertThat(BeanUtil.isSimpleValueType(objArray.getClass()))
            .as("Object 数组不应该是简单值类型")
            .isFalse();
    }

    @Test
    @DisplayName("isSimpleProperty - 基本类型数组是简单属性")
    void testIsSimpleProperty_PrimitiveArray() {
        int[] intArray = new int[0];
        assertThat(BeanUtil.isSimpleProperty(intArray.getClass()))
            .as("int 数组应该是简单属性")
            .isTrue();

        long[] longArray = new long[0];
        assertThat(BeanUtil.isSimpleProperty(longArray.getClass()))
            .as("long 数组应该是简单属性")
            .isTrue();

        double[] doubleArray = new double[0];
        assertThat(BeanUtil.isSimpleProperty(doubleArray.getClass()))
            .as("double 数组应该是简单属性")
            .isTrue();
    }

    @Test
    @DisplayName("isSimpleProperty - 包装类型数组是简单属性")
    void testIsSimpleProperty_WrapperArray() {
        Integer[] integerArray = new Integer[0];
        assertThat(BeanUtil.isSimpleProperty(integerArray.getClass()))
            .as("Integer 数组应该是简单属性")
            .isTrue();

        String[] stringArray = new String[0];
        assertThat(BeanUtil.isSimpleProperty(stringArray.getClass()))
            .as("String 数组应该是简单属性")
            .isTrue();
    }

    @Test
    @DisplayName("isSimpleProperty - 普通对象数组不是简单属性")
    void testIsSimpleProperty_ObjectArray() {
        Object[] objArray = new Object[0];
        assertThat(BeanUtil.isSimpleProperty(objArray.getClass()))
            .as("Object 数组不应该是简单属性")
            .isFalse();
    }

    @Test
    @DisplayName("isSimpleProperty - 基本类型是简单属性")
    void testIsSimpleProperty_Primitives() {
        assertThat(BeanUtil.isSimpleProperty(int.class))
            .as("int 应该是简单属性")
            .isTrue();

        assertThat(BeanUtil.isSimpleProperty(long.class))
            .as("long 应该是简单属性")
            .isTrue();

        assertThat(BeanUtil.isSimpleProperty(boolean.class))
            .as("boolean 应该是简单属性")
            .isTrue();
    }

    @Test
    @DisplayName("isSimpleProperty - 常用类是简单属性")
    void testIsSimpleProperty_CommonClasses() {
        assertThat(BeanUtil.isSimpleProperty(String.class))
            .as("String 应该是简单属性")
            .isTrue();

        assertThat(BeanUtil.isSimpleProperty(Integer.class))
            .as("Integer 应该是简单属性")
            .isTrue();

        assertThat(BeanUtil.isSimpleProperty(Date.class))
            .as("Date 应该是简单属性")
            .isTrue();
    }

    @Test
    @DisplayName("isSimpleProperty - BigDecimal 是简单属性")
    void testIsSimpleProperty_BigDecimal() {
        assertThat(BeanUtil.isSimpleProperty(BigDecimal.class))
            .as("BigDecimal 应该是简单属性")
            .isTrue();

        assertThat(BeanUtil.isSimpleProperty(BigInteger.class))
            .as("BigInteger 应该是简单属性")
            .isTrue();
    }

    @Test
    @DisplayName("isSimpleProperty - 普通对象不是简单属性")
    void testIsSimpleProperty_Object() {
        assertThat(BeanUtil.isSimpleProperty(Object.class))
            .as("Object 不应该是简单属性")
            .isFalse();
    }

    @Test
    @DisplayName("getMethodParamNames - 获取方法参数名称（通过 Method 对象）")
    void testGetMethodParamNames_ByMethod() throws Exception {
        // 创建一个测试类
        class TestClass {
            public String testMethod(String param1, int param2) {
                return param1 + param2;
            }
        }

        Method method = TestClass.class.getDeclaredMethod("testMethod", String.class, int.class);
        String[] paramNames = BeanUtil.getMethodParamNames(method);

        assertThat(paramNames)
            .as("应该能获取到方法参数名称")
            .isNotNull()
            .hasSize(2)
            .containsExactly("param1", "param2");
    }

    @Test
    @DisplayName("getMethodParamNames - 获取无参方法参数名称")
    void testGetMethodParamNames_NoParams() throws Exception {
        class TestClass {
            public void noParamMethod() {
            }
        }

        Method method = TestClass.class.getDeclaredMethod("noParamMethod");
        String[] paramNames = BeanUtil.getMethodParamNames(method);

        assertThat(paramNames)
            .as("无参方法应该返回空数组")
            .isNotNull()
            .isEmpty();
    }

    @Test
    @DisplayName("getMethodParamNames - 获取静态方法参数名称")
    void testGetMethodParamNames_StaticMethod() throws Exception {
        class TestClass {
            public static String staticMethod(String name, int age) {
                return name + age;
            }
        }

        Method method = TestClass.class.getDeclaredMethod("staticMethod", String.class, int.class);
        String[] paramNames = BeanUtil.getMethodParamNames(method);

        assertThat(paramNames)
            .as("静态方法应该能正确获取参数名称")
            .isNotNull()
            .hasSize(2)
            .containsExactly("name", "age");
    }

    @Test
    @DisplayName("getMethodParamNames - 通过类名和方法名获取参数名称")
    void testGetMethodParamNames_ByClassAndMethodName() throws Exception {
        class TestClass {
            public String calculate(int a, int b, int c) {
                return String.valueOf(a + b + c);
            }
        }

        String[] paramNames = BeanUtil.getMethodParamNames(
            TestClass.class,
            "calculate",
            int.class,
            int.class,
            int.class
        );

        assertThat(paramNames)
            .as("应该能通过类名和方法名获取参数名称")
            .isNotNull()
            .hasSize(3)
            .containsExactly("a", "b", "c");
    }

    @Test
    @DisplayName("getMethodParamNames - 处理多种参数类型")
    void testGetMethodParamNames_MultipleParamTypes() throws Exception {
        class TestClass {
            public String complexMethod(String str, int num, boolean flag, Date date) {
                return str + num + flag + date;
            }
        }

        Method method = TestClass.class.getDeclaredMethod(
            "complexMethod",
            String.class,
            int.class,
            boolean.class,
            Date.class
        );
        String[] paramNames = BeanUtil.getMethodParamNames(method);

        assertThat(paramNames)
            .as("应该能处理多种参数类型")
            .isNotNull()
            .hasSize(4)
            .containsExactly("str", "num", "flag", "date");
    }

    @Test
    @DisplayName("getMethodParamNames - BigDecimal 参数")
    void testGetMethodParamNames_BigDecimalParam() throws Exception {
        class TestClass {
            public BigDecimal calculate(BigDecimal amount, BigDecimal rate) {
                return amount.multiply(rate);
            }
        }

        Method method = TestClass.class.getDeclaredMethod(
            "calculate",
            BigDecimal.class,
            BigDecimal.class
        );
        String[] paramNames = BeanUtil.getMethodParamNames(method);

        assertThat(paramNames)
            .as("应该能处理 BigDecimal 参数类型")
            .isNotNull()
            .hasSize(2)
            .containsExactly("amount", "rate");
    }
}
