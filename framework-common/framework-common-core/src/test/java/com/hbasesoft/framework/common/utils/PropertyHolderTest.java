/****************************************************************************************
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.common.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;

import com.hbasesoft.framework.common.utils.config.Property;

/**
 * PropertyHolder 测试类
 *
 * @author 王伟
 * @version 1.0
 * @since V4.3
 * @see com.hbasesoft.framework.common.utils.PropertyHolder
 */
@DisplayName("PropertyHolder 工具类测试")
@SpringBootTest(classes = PropertyHolderTest.TestConfiguration.class)
class PropertyHolderTest {

    /**
     * 测试配置类
     */
    @Configuration
    @EnableAutoConfiguration
    static class TestConfiguration {
    }

    /** 测试用的常量值 */
    private static final int NUM_3 = 3;
    private static final long NUM_3L = 3L;
    private static final long NUM_1000L = 1000L;

    @Test
    @DisplayName("应该成功获取所有属性")
    void testGetProperties() {
        // When: 获取所有属性
        Map<String, String> properties = PropertyHolder.getProperties();

        // Then: 验证包含预期的属性键
        assertThat(properties)
            .isNotNull()
            .isNotEmpty()
            .containsKey("test.str.str1")  // 来自 application.yml
            .containsKey("test01")           // 来自 ext01.properties
            .containsKey("test02");          // 来自 ext02.yml
    }

    @Test
    @DisplayName("应该成功获取布尔类型属性（有默认值）")
    void testGetBooleanPropertyWithDefaultValue() {
        // When & Then: 从配置中获取布尔值
        Boolean bool1 = PropertyHolder.getBooleanProperty("test.bool.bool1");
        assertThat(bool1).isTrue();

        Boolean bool2 = PropertyHolder.getBooleanProperty("test.bool.bool2");
        assertThat(bool2).isFalse();

        // When & Then: 获取不存在的属性，使用默认值
        Boolean bool3 = PropertyHolder.getBooleanProperty("test.bool.none", false);
        assertThat(bool3).isFalse();

        Boolean bool4 = PropertyHolder.getBooleanProperty("test.bool.none", true);
        assertThat(bool4).isTrue();
    }

    @Test
    @DisplayName("应该成功获取布尔类型属性（无默认值）")
    void testGetBooleanPropertyWithoutDefaultValue() {
        // When & Then: 从配置中获取布尔值
        Boolean bool1 = PropertyHolder.getBooleanProperty("test.bool.bool1");
        assertThat(bool1).isTrue();

        Boolean bool2 = PropertyHolder.getBooleanProperty("test.bool.bool2");
        assertThat(bool2).isFalse();

        // When & Then: 获取不存在的属性，返回 null
        Boolean bool3 = PropertyHolder.getBooleanProperty("test.bool.none");
        assertThat(bool3).isNull();
    }

    @Test
    @DisplayName("应该成功获取整数类型属性（有默认值）")
    void testGetIntPropertyWithDefaultValue() {
        // When & Then: 从配置中获取整数值
        Integer int1 = PropertyHolder.getIntProperty("test.int.int1");
        assertThat(int1).isEqualTo(1);

        Integer int2 = PropertyHolder.getIntProperty("test.int.int2");
        assertThat(int2).isEqualTo(-1);

        // When & Then: 获取不存在的属性，使用默认值
        Integer int3 = PropertyHolder.getIntProperty("test.int.none", NUM_3);
        assertThat(int3).isEqualTo(NUM_3);

        Integer int4 = PropertyHolder.getIntProperty("test.int.none", 100);
        assertThat(int4).isEqualTo(100);
    }

    @Test
    @DisplayName("应该成功获取整数类型属性（无默认值）")
    void testGetIntPropertyWithoutDefaultValue() {
        // When & Then: 从配置中获取整数值
        Integer int1 = PropertyHolder.getIntProperty("test.int.int1");
        assertThat(int1).isEqualTo(1);

        Integer int2 = PropertyHolder.getIntProperty("test.int.int2");
        assertThat(int2).isEqualTo(-1);

        // When & Then: 获取不存在的属性，返回 null
        Integer int3 = PropertyHolder.getIntProperty("test.int.none");
        assertThat(int3).isNull();
    }

    @Test
    @DisplayName("应该成功获取长整数类型属性（有默认值）")
    void testGetLongPropertyWithDefaultValue() {
        // When & Then: 从配置中获取长整数值
        Long long1 = PropertyHolder.getLongProperty("test.long.long1");
        assertThat(long1).isEqualTo(NUM_1000L);

        Long long2 = PropertyHolder.getLongProperty("test.long.long2");
        assertThat(long2).isEqualTo(3000L);

        // When & Then: 获取不存在的属性，使用默认值
        Long long3 = PropertyHolder.getLongProperty("test.long.none", NUM_3L);
        assertThat(long3).isEqualTo(NUM_3L);

        Long long4 = PropertyHolder.getLongProperty("test.long.none", 5000L);
        assertThat(long4).isEqualTo(5000L);
    }

    @Test
    @DisplayName("应该成功获取长整数类型属性（无默认值）")
    void testGetLongPropertyWithoutDefaultValue() {
        // When & Then: 从配置中获取长整数值
        Long long1 = PropertyHolder.getLongProperty("test.long.long1");
        assertThat(long1).isEqualTo(NUM_1000L);

        Long long2 = PropertyHolder.getLongProperty("test.long.long2");
        assertThat(long2).isEqualTo(3000L);

        // When & Then: 获取不存在的属性，返回 null
        Long long3 = PropertyHolder.getLongProperty("test.long.none");
        assertThat(long3).isNull();
    }

    @Test
    @DisplayName("应该成功获取字符串类型属性（有默认值）")
    void testGetPropertyWithStringDefaultValue() {
        // When & Then: 从配置中获取字符串值
        String str1 = PropertyHolder.getProperty("test.str.str1");
        assertThat(str1).isEqualTo("abc");

        String str2 = PropertyHolder.getProperty("test.str.str2");
        assertThat(str2).isEqualTo("bcd");

        // When & Then: 获取整数配置并作为字符串返回
        String intStr = PropertyHolder.getProperty("test.int.int2");
        assertThat(intStr).isEqualTo("-1");

        // When & Then: 获取长整数配置并作为字符串返回
        String longStr = PropertyHolder.getProperty("test.long.long2");
        assertThat(longStr).isEqualTo("3000");

        // When & Then: 获取布尔配置并作为字符串返回
        String boolStr = PropertyHolder.getProperty("test.bool.bool2");
        assertThat(boolStr).isEqualTo("false");

        // When & Then: 获取不存在的属性，使用默认值
        String defaultStr = PropertyHolder.getProperty("test.str.none", "abcdefg");
        assertThat(defaultStr).isEqualTo("abcdefg");
    }

    @Test
    @DisplayName("应该成功获取字符串类型属性（无默认值）")
    void testGetPropertyWithoutDefaultValue() {
        // When & Then: 从配置中获取字符串值
        String str1 = PropertyHolder.getProperty("test.str.str1");
        assertThat(str1).isEqualTo("abc");

        String str2 = PropertyHolder.getProperty("test.str.str2");
        assertThat(str2).isEqualTo("bcd");

        // When & Then: 获取不存在的属性，返回 null
        String nullStr = PropertyHolder.getProperty("test.str.none");
        assertThat(nullStr).isNull();
    }

    @Test
    @DisplayName("应该成功获取项目名称")
    void testGetProjectName() {
        // When: 获取项目名称
        String projectName = PropertyHolder.getProjectName();

        // Then: 验证项目名称不为空
        assertThat(projectName)
            .isNotNull()
            .isNotEmpty();
    }

    @Test
    @DisplayName("应该成功获取本地属性对象")
    void testGetLocalProperty() {
        // When: 获取本地属性对象
        Property localProperty = PropertyHolder.getLocalProperty();

        // Then: 验证本地属性对象不为空
        assertThat(localProperty).isNotNull();
    }

    @Test
    @DisplayName("应该成功获取版本号")
    void testGetVersion() {
        // When: 获取版本号
        String version = PropertyHolder.getVersion();

        // Then: 验证版本号不为空，默认值为 1.0
        assertThat(version)
            .isNotNull()
            .isNotEmpty();
    }

    @Test
    @DisplayName("应该成功获取扩展配置文件中的属性")
    void testGetExtendedProperties() {
        // When & Then: 从 ext01.properties 获取属性
        String ext01Value = PropertyHolder.getProperty("test01");
        assertThat(ext01Value).isEqualTo("bbb");

        // When & Then: 从 ext02.yml 获取属性
        String ext02Value = PropertyHolder.getProperty("test02");
        assertThat(ext02Value).isEqualTo("aaa");
    }
}
