/****************************************************************************************
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.common.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import com.hbasesoft.framework.common.ErrorCodeDef;

/**
 * <Description> PropertyHolder Test <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @CreateDate 2025年8月15日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.common.utils <br>
 */
public class PropertyHolderTest {

    @BeforeEach
    public void setUp() {
        // Setup code if needed
    }

    @AfterEach
    public void tearDown() {
        // Clean up code if needed
    }

    @Test
    public void testGetProperty() {
        // 测试获取存在的属性
        String projectName = PropertyHolder.getProperty("project.name");
        assertNotNull(projectName);

        // 测试获取不存在的属性
        String nonExistent = PropertyHolder.getProperty("non.existent.property");
        assertNull(nonExistent);

        // 测试获取属性带默认值
        String withDefault = PropertyHolder.getProperty("non.existent.property", "default");
        assertEquals("default", withDefault);
    }

    @Test
    public void testGetIntProperty() {
        // 测试获取整数属性
        Integer intProp = PropertyHolder.getIntProperty("some.int.property");
        assertNull(intProp);

        // 测试获取整数属性带默认值
        Integer withDefault = PropertyHolder.getIntProperty("some.int.property", 42);
        assertEquals(Integer.valueOf(42), withDefault);
    }

    @Test
    public void testGetBooleanProperty() {
        // 测试获取布尔属性
        Boolean boolProp = PropertyHolder.getBooleanProperty("some.boolean.property");
        assertNull(boolProp);

        // 测试获取布尔属性带默认值为true
        Boolean withTrueDefault = PropertyHolder.getBooleanProperty("some.boolean.property", Boolean.TRUE);
        assertEquals(Boolean.TRUE, withTrueDefault);
        
        // 测试获取布尔属性带默认值为false
        Boolean withFalseDefault = PropertyHolder.getBooleanProperty("some.boolean.property", Boolean.FALSE);
        assertEquals(Boolean.FALSE, withFalseDefault);
        
        // 测试获取配置文件中实际存在的布尔属性
        Boolean actualTrueProp = PropertyHolder.getBooleanProperty("test.bool.bool1");
        assertEquals(Boolean.TRUE, actualTrueProp);
        
        // 测试获取配置文件中实际存在的布尔属性
        Boolean actualFalseProp = PropertyHolder.getBooleanProperty("test.bool.bool2");
        assertEquals(Boolean.FALSE, actualFalseProp);
        
        // 测试使用Boolean值进行安全的比较
        Boolean testValue = PropertyHolder.getBooleanProperty("some.boolean.property");
        assertNull(testValue);
        
        // 确保我们可以安全地检查值
        if (testValue != null) {
            boolean boolValue = testValue.booleanValue();
            // 这里的代码不会执行，因为我们知道testValue是null
        }
    }

    @Test
    public void testGetLongProperty() {
        // 测试获取长整型属性
        Long longProp = PropertyHolder.getLongProperty("some.long.property");
        assertNull(longProp);

        // 测试获取长整型属性带默认值
        Long withDefault = PropertyHolder.getLongProperty("some.long.property", Long.valueOf(1000L));
        assertEquals(Long.valueOf(1000L), withDefault);
    }

    @Test
    public void testGetVersion() {
        // 测试获取版本号
        String version = PropertyHolder.getVersion();
        assertNotNull(version);
    }
}