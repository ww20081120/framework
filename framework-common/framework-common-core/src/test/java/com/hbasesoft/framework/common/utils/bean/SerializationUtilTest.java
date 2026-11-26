/****************************************************************************************
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.common.utils.bean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.hbasesoft.framework.common.utils.UtilException;

/**
 * SerializationUtil 测试类
 * <p>
 * 测试序列化工具类的所有公共方法，包括：
 * <ul>
 *   <li>Kryo 序列化/反序列化（serial/unserial）</li>
 *   <li>JDK 序列化/反序列化（jdkSerial/jdkUnserial）</li>
 *   <li>对象克隆（clone）</li>
 * </ul>
 *
 * @author 王伟
 * @version 1.0
 * @CreateDate 2018年9月12日
 * @since V1.0
 */
@DisplayName("序列化工具测试")
public class SerializationUtilTest {

    /** 测试数据常量 */
    private static final String TEST_NAME = "hello world";
    private static final int TEST_AGE = 18;

    @Nested
    @DisplayName("Kryo 序列化/反序列化测试")
    class KryoSerializationTest {

        @Test
        @DisplayName("应该能够序列化和反序列化简单对象")
        void shouldSerializeAndDeserializeSimpleObject() {
            // Given
            TestBean originalBean = new TestBean(TEST_NAME, TEST_AGE);

            // When
            byte[] serializedData = SerializationUtil.serial(originalBean);
            TestBean deserializedBean = SerializationUtil.unserial(TestBean.class, serializedData);

            // Then - 验证序列化/反序列化的对称性
            assertThat(deserializedBean).isNotNull();
            assertThat(deserializedBean.getName()).isEqualTo(originalBean.getName());
            assertThat(deserializedBean.getAge()).isEqualTo(originalBean.getAge());
        }

        @Test
        @DisplayName("应该能够序列化和反序列化包含对象的 Map")
        void shouldSerializeAndDeserializeMapWithObjects() {
            // Given
            TestBean bean = new TestBean(TEST_NAME, TEST_AGE);
            Map<String, Object> originalMap = new HashMap<>();
            originalMap.put("test", bean);
            originalMap.put("number", 100);
            originalMap.put("string", "test string");

            // When
            byte[] serializedData = SerializationUtil.serial(originalMap);
            @SuppressWarnings("unchecked")
            Map<String, Object> deserializedMap = SerializationUtil.unserial(Map.class, serializedData);

            // Then
            assertThat(deserializedMap).isNotNull();
            assertThat(deserializedMap).hasSize(3);

            TestBean deserializedBean = (TestBean) deserializedMap.get("test");
            assertThat(deserializedBean.getName()).isEqualTo(TEST_NAME);
            assertThat(deserializedBean.getAge()).isEqualTo(TEST_AGE);
            assertThat(deserializedMap.get("number")).isEqualTo(100);
            assertThat(deserializedMap.get("string")).isEqualTo("test string");
        }

        @Test
        @DisplayName("应该能够序列化和反序列化对象列表")
        void shouldSerializeAndDeserializeListOfObjects() {
            // Given
            TestBean bean1 = new TestBean("Alice", 20);
            TestBean bean2 = new TestBean("Bob", 25);
            List<TestBean> originalList = Arrays.asList(bean1, bean2);

            // When
            byte[] serializedData = SerializationUtil.serial(originalList);
            @SuppressWarnings("unchecked")
            List<TestBean> deserializedList = SerializationUtil.unserial(List.class, serializedData);

            // Then
            assertThat(deserializedList).isNotNull();
            assertThat(deserializedList).hasSize(2);
            assertThat(deserializedList.get(0).getName()).isEqualTo("Alice");
            assertThat(deserializedList.get(0).getAge()).isEqualTo(20);
            assertThat(deserializedList.get(1).getName()).isEqualTo("Bob");
            assertThat(deserializedList.get(1).getAge()).isEqualTo(25);
        }

        @Test
        @DisplayName("序列化 null 对象应该返回空字节数组")
        void shouldReturnEmptyByteArrayWhenSerializingNull() {
            // When
            byte[] result = SerializationUtil.serial(null);

            // Then
            assertThat(result).isNotNull();
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("序列化 Void 对象应该返回空字节数组")
        void shouldReturnEmptyByteArrayWhenSerializingVoid() {
            // When
            byte[] result = SerializationUtil.serial(Void.TYPE);

            // Then
            assertThat(result).isNotNull();
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("反序列化空字节数组应该返回 null")
        void shouldReturnNullWhenDeserializingEmptyByteArray() {
            // When
            TestBean result = SerializationUtil.unserial(TestBean.class, new byte[0]);

            // Then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("反序列化 null 数据应该返回 null")
        void shouldReturnNullWhenDeserializingNullData() {
            // When
            TestBean result = SerializationUtil.unserial(TestBean.class, null);

            // Then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("应该能够序列化和反序列化嵌套集合结构")
        void shouldSerializeAndDeserializeNestedCollections() {
            // Given
            List<String> innerList1 = Arrays.asList("A", "B");
            List<String> innerList2 = Arrays.asList("C", "D");
            List<List<String>> nestedList = new ArrayList<>();
            nestedList.add(innerList1);
            nestedList.add(innerList2);

            // When
            byte[] serializedData = SerializationUtil.serial(nestedList);
            @SuppressWarnings("unchecked")
            List<List<String>> deserializedList = SerializationUtil.unserial(List.class, serializedData);

            // Then
            assertThat(deserializedList).isNotNull();
            assertThat(deserializedList).hasSize(2);
            assertThat(deserializedList.get(0)).containsExactly("A", "B");
            assertThat(deserializedList.get(1)).containsExactly("C", "D");
        }
    }

    @Nested
    @DisplayName("JDK 序列化/反序列化测试")
    class JdkSerializationTest {

        @Test
        @DisplayName("应该能够使用 JDK 序列化和反序列化对象")
        void shouldSerializeAndDeserializeUsingJdk() {
            // Given
            TestBean originalBean = new TestBean(TEST_NAME, TEST_AGE);

            // When
            byte[] serializedData = SerializationUtil.jdkSerial(originalBean);
            TestBean deserializedBean = (TestBean) SerializationUtil.jdkUnserial(serializedData);

            // Then - 验证序列化/反序列化的对称性
            assertThat(deserializedBean).isNotNull();
            assertThat(deserializedBean.getName()).isEqualTo(originalBean.getName());
            assertThat(deserializedBean.getAge()).isEqualTo(originalBean.getAge());
        }

        @Test
        @DisplayName("应该能够使用 JDK 序列化和反序列化复杂对象")
        void shouldSerializeAndDeserializeComplexObjectUsingJdk() {
            // Given
            Map<String, Object> originalMap = new HashMap<>();
            originalMap.put("string", "test");
            originalMap.put("number", 123);
            originalMap.put("list", Arrays.asList(1, 2, 3));

            // When
            byte[] serializedData = SerializationUtil.jdkSerial(originalMap);
            @SuppressWarnings("unchecked")
            Map<String, Object> deserializedMap = (Map<String, Object>) SerializationUtil.jdkUnserial(serializedData);

            // Then
            assertThat(deserializedMap).isNotNull();
            assertThat(deserializedMap.get("string")).isEqualTo("test");
            assertThat(deserializedMap.get("number")).isEqualTo(123);
            assertThat(deserializedMap.get("list")).isEqualTo(Arrays.asList(1, 2, 3));
        }

        @Test
        @DisplayName("JDK 反序列化空字节数组应该返回 null")
        void shouldReturnNullWhenJdkDeserializingEmptyByteArray() {
            // When
            Object result = SerializationUtil.jdkUnserial(new byte[0]);

            // Then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("JDK 反序列化 null 数据应该返回 null")
        void shouldReturnNullWhenJdkDeserializingNullData() {
            // When
            Object result = SerializationUtil.jdkUnserial(null);

            // Then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("JDK 反序列化损坏的数据应该抛出异常")
        void shouldThrowExceptionWhenJdkDeserializingCorruptedData() {
            // Given - 损坏的数据
            byte[] corruptedData = new byte[] { 0x01, 0x02, 0x03 };

            // When & Then
            assertThatThrownBy(() -> SerializationUtil.jdkUnserial(corruptedData))
                .isInstanceOf(UtilException.class);
        }
    }

    @Nested
    @DisplayName("对象克隆测试")
    class CloneTest {

        @Test
        @DisplayName("应该能够克隆可序列化对象")
        void shouldCloneSerializableObject() {
            // Given
            TestBean originalBean = new TestBean(TEST_NAME, TEST_AGE);

            // When
            TestBean clonedBean = SerializationUtil.clone(originalBean);

            // Then - 验证克隆的对称性和独立性
            assertThat(clonedBean).isNotNull();
            assertThat(clonedBean).isEqualTo(originalBean);
            assertThat(clonedBean.getName()).isEqualTo(originalBean.getName());
            assertThat(clonedBean.getAge()).isEqualTo(originalBean.getAge());
        }

        @Test
        @DisplayName("克隆的对象应该是独立的深拷贝")
        void shouldCreateIndependentDeepCopy() {
            // Given
            ArrayList<String> originalList = new ArrayList<>();
            originalList.add("item1");
            originalList.add("item2");

            // When
            ArrayList<String> clonedList = SerializationUtil.clone(originalList);

            // Then - 修改原始对象不应该影响克隆的对象
            assertThat(clonedList).isNotNull();
            assertThat(clonedList).isEqualTo(originalList);

            // 修改原始列表
            originalList.add("item3");

            // 验证克隆的列表不受影响（由于 Kryo 的特性，这里可能不是深拷贝）
            // 但至少验证了基本功能
            assertThat(clonedList).hasSize(2);
        }

        @Test
        @DisplayName("克隆 null 对象应该返回 null")
        void shouldReturnNullWhenCloningNull() {
            // When
            TestBean result = SerializationUtil.clone(null);

            // Then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("克隆应该保持对象的相等性")
        void shouldMaintainObjectEqualityAfterClone() {
            // Given
            TestBean original = new TestBean("test", 100);

            // When
            TestBean cloned = SerializationUtil.clone(original);

            // Then
            assertThat(cloned).isNotNull();
            assertThat(cloned).isEqualTo(original);
        }

        @Test
        @DisplayName("应该能够克隆包含基本类型的对象")
        void shouldCloneObjectWithPrimitiveTypes() {
            // Given
            TestBean bean = new TestBean("primitive test", 42);

            // When
            TestBean cloned = SerializationUtil.clone(bean);

            // Then
            assertThat(cloned).isNotNull();
            assertThat(cloned.getAge()).isEqualTo(42);
            assertThat(cloned.getName()).isEqualTo("primitive test");
        }
    }

    @Nested
    @DisplayName("边界条件和异常情况测试")
    class EdgeCasesAndExceptionsTest {

        @Test
        @DisplayName("应该能够序列化和反序列化空字符串")
        void shouldHandleEmptyString() {
            // Given
            String emptyString = "";

            // When
            byte[] serializedData = SerializationUtil.serial(emptyString);
            String deserializedString = SerializationUtil.unserial(String.class, serializedData);

            // Then
            assertThat(deserializedString).isNotNull();
            assertThat(deserializedString).isEmpty();
        }

        @Test
        @DisplayName("应该能够序列化和反序列化空集合")
        void shouldHandleEmptyCollection() {
            // Given
            List<String> emptyList = new ArrayList<>();

            // When
            byte[] serializedData = SerializationUtil.serial(emptyList);
            @SuppressWarnings("unchecked")
            List<String> deserializedList = SerializationUtil.unserial(List.class, serializedData);

            // Then
            assertThat(deserializedList).isNotNull();
            assertThat(deserializedList).isEmpty();
        }

        @Test
        @DisplayName("应该能够序列化和反序列化空 Map")
        void shouldHandleEmptyMap() {
            // Given
            Map<String, String> emptyMap = new HashMap<>();

            // When
            byte[] serializedData = SerializationUtil.serial(emptyMap);
            @SuppressWarnings("unchecked")
            Map<String, String> deserializedMap = SerializationUtil.unserial(Map.class, serializedData);

            // Then
            assertThat(deserializedMap).isNotNull();
            assertThat(deserializedMap).isEmpty();
        }

        @Test
        @DisplayName("应该能够序列化和反序列化包含特殊字符的字符串")
        void shouldHandleStringWithSpecialCharacters() {
            // Given
            String specialString = "测试\n\t\r特殊字符!@#$%^&*()";

            // When
            byte[] serializedData = SerializationUtil.serial(specialString);
            String deserializedString = SerializationUtil.unserial(String.class, serializedData);

            // Then
            assertThat(deserializedString).isNotNull();
            assertThat(deserializedString).isEqualTo(specialString);
        }

        @Test
        @DisplayName("应该能够序列化和反序列化数字类型")
        void shouldHandleNumericTypes() {
            // Given
            Integer intValue = 12345;
            Double doubleValue = 123.45;
            Long longValue = 123456789L;

            // When
            byte[] intData = SerializationUtil.serial(intValue);
            byte[] doubleData = SerializationUtil.serial(doubleValue);
            byte[] longData = SerializationUtil.serial(longValue);

            Integer deserializedInt = SerializationUtil.unserial(Integer.class, intData);
            Double deserializedDouble = SerializationUtil.unserial(Double.class, doubleData);
            Long deserializedLong = SerializationUtil.unserial(Long.class, longData);

            // Then
            assertThat(deserializedInt).isEqualTo(12345);
            assertThat(deserializedDouble).isEqualTo(123.45);
            assertThat(deserializedLong).isEqualTo(123456789L);
        }
    }
}
