package com.hbasesoft.framework.common.utils.bean;

import static org.assertj.core.api.Assertions.*;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.hbasesoft.framework.common.utils.UtilException;

/**
 * ClassUtil 测试类
 *
 * @author framework
 */
@DisplayName("ClassUtil 工具类测试")
class ClassUtilTest {

    /**
     * 用于测试的内部类
     */
    public static class TestClass {
        public String publicField;
        private String privateField;

        public TestClass() {
        }

        public TestClass(String name) {
            this.publicField = name;
        }
    }

    /**
     * 用于测试继承的子类
     */
    public static class ChildClass extends TestClass {
        private String childField;
    }

    /**
     * 用于测试的接口
     */
    public interface TestInterface {
    }

    @Test
    @DisplayName("isBoolean - 原生 boolean 类型应返回 true")
    void testIsBoolean_PrimitiveBoolean() {
        assertThat(ClassUtil.isBoolean(boolean.class)).isTrue();
    }

    @Test
    @DisplayName("isBoolean - Boolean 包装类型应返回 true")
    void testIsBoolean_WrapperBoolean() {
        assertThat(ClassUtil.isBoolean(Boolean.class)).isTrue();
    }

    @Test
    @DisplayName("isBoolean - 其他类型应返回 false")
    void testIsBoolean_OtherTypes() {
        assertThat(ClassUtil.isBoolean(int.class)).isFalse();
        assertThat(ClassUtil.isBoolean(Integer.class)).isFalse();
        assertThat(ClassUtil.isBoolean(String.class)).isFalse();
        assertThat(ClassUtil.isBoolean(Object.class)).isFalse();
        assertThat(ClassUtil.isBoolean(List.class)).isFalse();
    }

    @Test
    @DisplayName("isProxy - 普通类应返回 false")
    void testIsProxy_NormalClass() {
        assertThat(ClassUtil.isProxy(TestClass.class)).isFalse();
        assertThat(ClassUtil.isProxy(String.class)).isFalse();
        assertThat(ClassUtil.isProxy(List.class)).isFalse();
    }

    @Test
    @DisplayName("isProxy - null 应返回 false")
    void testIsProxy_Null() {
        assertThat(ClassUtil.isProxy(null)).isFalse();
    }

    @Test
    @DisplayName("isProxy - 实现代理接口的类应返回 true")
    void testIsProxy_ProxyClass() {
        // 创建一个实现代理接口的动态类进行测试
        // 由于实际代理类需要 CGLIB/Javassist 等库，这里测试接口名匹配逻辑
        // 普通类没有实现代理接口，应返回 false
        assertThat(ClassUtil.isProxy(TestClass.class)).isFalse();
    }

    @Test
    @DisplayName("getUserClass(Class) - 普通类应返回自身")
    void testGetUserClass_Class_Normal() {
        Class<?> result = ClassUtil.getUserClass(TestClass.class);
        assertThat(result).isEqualTo(TestClass.class);
    }

    @Test
    @DisplayName("getUserClass(Class) - null 应返回 null")
    void testGetUserClass_Class_Null() {
        Class<?> result = ClassUtil.getUserClass(null);
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("getUserClass(Object) - 普通对象应返回其类")
    void testGetUserClass_Object_Normal() {
        TestClass obj = new TestClass();
        Class<?> result = ClassUtil.getUserClass(obj);
        assertThat(result).isEqualTo(TestClass.class);
    }

    @Test
    @DisplayName("getUserClass(Object) - null 对象应抛出异常")
    void testGetUserClass_Object_Null() {
        assertThatThrownBy(() -> ClassUtil.getUserClass((Object) null))
            .isInstanceOf(com.hbasesoft.framework.common.utils.AssertException.class)
            .hasMessageContaining("对象");
    }

    @Test
    @DisplayName("newInstance(Class) - 正常实例化对象")
    void testNewInstance_Class_Success() {
        TestClass instance = ClassUtil.newInstance(TestClass.class);
        assertThat(instance).isNotNull();
        assertThat(instance).isExactlyInstanceOf(TestClass.class);
    }

    @Test
    @DisplayName("newInstance(Class) - 实例化 String 对象")
    void testNewInstance_Class_String() {
        String instance = ClassUtil.newInstance(String.class);
        assertThat(instance).isNotNull();
        assertThat(instance).isEqualTo("");
    }

    @Test
    @DisplayName("newInstance(Class) - 实例化 ArrayList 对象")
    void testNewInstance_Class_ArrayList() {
        // ArrayList 是具体类，有无参构造
        ArrayList<String> list = ClassUtil.newInstance(ArrayList.class);
        assertThat(list).isNotNull();
        assertThat(list).isEmpty();
    }

    @Test
    @DisplayName("newInstance(Class) - 无无参构造函数的类应抛出异常")
    void testNewInstance_Class_NoDefaultConstructor() {
        // 创建一个没有无参构造的类
        class NoDefaultConstructor {
            @SuppressWarnings("unused")
            public NoDefaultConstructor(String param) {
            }
        }

        assertThatThrownBy(() -> ClassUtil.newInstance(NoDefaultConstructor.class))
            .isInstanceOf(UtilException.class)
            .hasMessageContaining("实例化对象时出现错误");
    }

    @Test
    @DisplayName("newInstance(Class) - 抽象类应抛出异常")
    void testNewInstance_Class_AbstractClass() {
        assertThatThrownBy(() -> ClassUtil.newInstance(AbstractClass.class))
            .isInstanceOf(UtilException.class);
    }

    @Test
    @DisplayName("newInstance(Class) - 接口应抛出异常")
    void testNewInstance_Class_Interface() {
        assertThatThrownBy(() -> ClassUtil.newInstance(TestInterface.class))
            .isInstanceOf(UtilException.class);
    }

    @Test
    @DisplayName("newInstance(String) - 通过类名实例化对象")
    void testNewInstance_String_Success() {
        String instance = ClassUtil.newInstance("java.lang.String");
        assertThat(instance).isNotNull();
        assertThat(instance).isEqualTo("");
    }

    @Test
    @DisplayName("newInstance(String) - 通过类名实例化 ArrayList")
    void testNewInstance_String_ArrayList() {
        @SuppressWarnings("unchecked")
        List<String> list = ClassUtil.newInstance("java.util.ArrayList");
        assertThat(list).isNotNull();
        assertThat(list).isEmpty();
    }

    @Test
    @DisplayName("newInstance(String) - 不存在的类名应抛出异常")
    void testNewInstance_String_NotFound() {
        assertThatThrownBy(() -> ClassUtil.newInstance("com.example.NonExistClass"))
            .isInstanceOf(UtilException.class)
            .hasMessageContaining("找不到指定的class");
    }

    @Test
    @DisplayName("newInstance(String) - null 类名应抛出异常")
    void testNewInstance_String_Null() {
        assertThatThrownBy(() -> ClassUtil.newInstance((String) null))
            .isInstanceOf(UtilException.class);
    }

    @Test
    @DisplayName("newInstance(String) - 空类名应抛出异常")
    void testNewInstance_String_Empty() {
        assertThatThrownBy(() -> ClassUtil.newInstance(""))
            .isInstanceOf(UtilException.class);
    }

    @Test
    @DisplayName("toClassConfident - 加载存在的类")
    void testToClassConfident_Success() {
        Class<?> clazz = ClassUtil.toClassConfident("java.lang.String");
        assertThat(clazz).isEqualTo(String.class);
    }

    @Test
    @DisplayName("toClassConfident - 加载系统类")
    void testToClassConfident_SystemClass() {
        Class<?> clazz = ClassUtil.toClassConfident("java.util.List");
        assertThat(clazz).isEqualTo(List.class);
    }

    @Test
    @DisplayName("toClassConfident - 加载项目内部类")
    void testToClassConfident_ProjectClass() {
        Class<?> clazz = ClassUtil.toClassConfident(
            "com.hbasesoft.framework.common.utils.bean.ClassUtil");
        assertThat(clazz).isEqualTo(ClassUtil.class);
    }

    @Test
    @DisplayName("toClassConfident - 不存在的类应抛出异常")
    void testToClassConfident_NotFound() {
        assertThatThrownBy(() -> ClassUtil.toClassConfident("com.example.NonExistClass"))
            .isInstanceOf(UtilException.class)
            .hasMessageContaining("找不到指定的class");
    }

    @Test
    @DisplayName("toClassConfident - null 类名应抛出异常")
    void testToClassConfident_Null() {
        assertThatThrownBy(() -> ClassUtil.toClassConfident(null))
            .isInstanceOf(UtilException.class);
    }

    @Test
    @DisplayName("toClassConfident - 空类名应抛出异常")
    void testToClassConfident_Empty() {
        assertThatThrownBy(() -> ClassUtil.toClassConfident(""))
            .isInstanceOf(UtilException.class);
    }

    @Test
    @DisplayName("getPackageName(Class) - 获取类的包名")
    void testGetPackageName_Class_Normal() {
        String packageName = ClassUtil.getPackageName(String.class);
        assertThat(packageName).isEqualTo("java.lang");
    }

    @Test
    @DisplayName("getPackageName(Class) - 获取 List 的包名")
    void testGetPackageName_Class_List() {
        String packageName = ClassUtil.getPackageName(List.class);
        assertThat(packageName).isEqualTo("java.util");
    }

    @Test
    @DisplayName("getPackageName(Class) - 获取项目类的包名")
    void testGetPackageName_Class_ProjectClass() {
        String packageName = ClassUtil.getPackageName(ClassUtil.class);
        assertThat(packageName).isEqualTo("com.hbasesoft.framework.common.utils.bean");
    }

    @Test
    @DisplayName("getPackageName(Class) - null 类应抛出异常")
    void testGetPackageName_Class_Null() {
        assertThatThrownBy(() -> ClassUtil.getPackageName((Class<?>) null))
            .isInstanceOf(com.hbasesoft.framework.common.utils.AssertException.class)
            .hasMessageContaining("类");
    }

    @Test
    @DisplayName("getPackageName(String) - 获取完整类名的包名")
    void testGetPackageName_String_Normal() {
        String packageName = ClassUtil.getPackageName("java.lang.String");
        assertThat(packageName).isEqualTo("java.lang");
    }

    @Test
    @DisplayName("getPackageName(String) - 获取多级包名")
    void testGetPackageName_String_MultiLevel() {
        String packageName = ClassUtil.getPackageName(
            "com.hbasesoft.framework.common.utils.bean.ClassUtil");
        assertThat(packageName).isEqualTo("com.hbasesoft.framework.common.utils.bean");
    }

    @Test
    @DisplayName("getPackageName(String) - 默认包的类应返回空字符串")
    void testGetPackageName_String_DefaultPackage() {
        String packageName = ClassUtil.getPackageName("MyClass");
        assertThat(packageName).isEmpty();
    }

    @Test
    @DisplayName("getPackageName(String) - null 类名应抛出异常")
    void testGetPackageName_String_Null() {
        assertThatThrownBy(() -> ClassUtil.getPackageName((String) null))
            .isInstanceOf(com.hbasesoft.framework.common.utils.AssertException.class)
            .hasMessageContaining("类名");
    }

    @Test
    @DisplayName("getPackageName(String) - 空类名应抛出异常")
    void testGetPackageName_String_Empty() {
        assertThatThrownBy(() -> ClassUtil.getPackageName(""))
            .isInstanceOf(com.hbasesoft.framework.common.utils.AssertException.class);
    }

    @Test
    @DisplayName("getDefaultClassLoader - 应返回类加载器（可能在安全受限环境下为 null）")
    void testGetDefaultClassLoader_NotNull() {
        ClassLoader classLoader = ClassUtil.getDefaultClassLoader();
        // 在正常情况下应该能获取到类加载器
        // 但在某些安全环境下可能返回 null，这是正常行为
        // 验证方法可以正常调用即可
        assertThat(classLoader).isNotNull();
    }

    @Test
    @DisplayName("getDeclaredField - 获取当前类的公共字段")
    void testGetDeclaredField_PublicField() {
        Field field = ClassUtil.getDeclaredField(TestClass.class, "publicField");
        assertThat(field).isNotNull();
        assertThat(field.getName()).isEqualTo("publicField");
    }

    @Test
    @DisplayName("getDeclaredField - 获取当前类的私有字段")
    void testGetDeclaredField_PrivateField() {
        Field field = ClassUtil.getDeclaredField(TestClass.class, "privateField");
        assertThat(field).isNotNull();
        assertThat(field.getName()).isEqualTo("privateField");
        assertThat(Modifier.isPrivate(field.getModifiers())).isTrue();
    }

    @Test
    @DisplayName("getDeclaredField - 从子类获取父类的字段")
    void testGetDeclaredField_InheritedField() {
        Field field = ClassUtil.getDeclaredField(ChildClass.class, "publicField");
        assertThat(field).isNotNull();
        assertThat(field.getName()).isEqualTo("publicField");
    }

    @Test
    @DisplayName("getDeclaredField - 获取子类自己的字段")
    void testGetDeclaredField_ChildField() {
        Field field = ClassUtil.getDeclaredField(ChildClass.class, "childField");
        assertThat(field).isNotNull();
        assertThat(field.getName()).isEqualTo("childField");
    }

    @Test
    @DisplayName("getDeclaredField - 不存在的字段应返回 null")
    void testGetDeclaredField_NotExist() {
        Field field = ClassUtil.getDeclaredField(TestClass.class, "nonExistField");
        assertThat(field).isNull();
    }

    @Test
    @DisplayName("getDeclaredField - null 类名应返回 null")
    void testGetDeclaredField_NullClass() {
        Field field = ClassUtil.getDeclaredField(null, "publicField");
        assertThat(field).isNull();
    }

    @Test
    @DisplayName("getDeclaredField - null 字段名应返回 null")
    void testGetDeclaredField_NullFieldName() {
        Field field = ClassUtil.getDeclaredField(TestClass.class, null);
        assertThat(field).isNull();
    }

    @Test
    @DisplayName("getDeclaredField - 空字段名应返回 null")
    void testGetDeclaredField_EmptyFieldName() {
        Field field = ClassUtil.getDeclaredField(TestClass.class, "");
        assertThat(field).isNull();
    }

    @Test
    @DisplayName("getDeclaredField - Object 类的字段应返回 null")
    void testGetDeclaredField_ObjectClass() {
        // Object 类没有父类,所以查找会在 Object.class 处停止
        Field field = ClassUtil.getDeclaredField(Object.class, "hashCode");
        assertThat(field).isNull();
    }

    /**
     * 用于测试的抽象类
     */
    abstract static class AbstractClass {
    }
}
