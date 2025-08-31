/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.agent;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;

import static org.junit.jupiter.api.Assertions.*;

/**
 * AgentsRegistrar 测试类<br>
 * 用于验证 AgentsRegistrar 能否正确处理 bean 名称冲突。
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月31日 <br>
 * @since V1.0<br>
 */
public class AgentsRegistrarTest {

    @Test
    public void testGenerateUniqueBeanName() {
        // 创建模拟的 BeanDefinitionRegistry
        DefaultListableBeanFactory registry = new DefaultListableBeanFactory();
        
        // 创建 AgentsRegistrar 实例
        AgentsRegistrar registrar = new AgentsRegistrar();
        
        // 创建 registeredBeanNames 映射
        Map<String, Integer> registeredBeanNames = new HashMap<>();
        
        // 预先注册一个同名的 bean 来模拟冲突场景
        registry.registerBeanDefinition("memoryServiceImpl", new RootBeanDefinition());
        
        // 测试冲突情况：相同简单类名但不同包
        String beanName1 = invokeGenerateUniqueBeanName(registrar, 
            "com.hbasesoft.framework.ai.agent.file.memory.MemoryServiceImpl", registry, registeredBeanNames);
        assertEquals("memoryMemoryServiceImpl", beanName1);
        
        // 测试另一个冲突情况
        String beanName2 = invokeGenerateUniqueBeanName(registrar, 
            "com.hbasesoft.framework.ai.agent.jpa.memory.service.impl.MemoryServiceImpl", registry, registeredBeanNames);
        assertEquals("implMemoryServiceImpl", beanName2);
        
        // 验证已注册的名称映射
        assertEquals(2, registeredBeanNames.size());
        assertTrue(registeredBeanNames.containsKey("memoryMemoryServiceImpl"));
        assertTrue(registeredBeanNames.containsKey("implMemoryServiceImpl"));
    }
    
    /**
     * 通过反射调用私有方法 generateUniqueBeanName
     */
    private String invokeGenerateUniqueBeanName(AgentsRegistrar registrar, String beanClassName, 
            BeanDefinitionRegistry registry, Map<String, Integer> registeredBeanNames) {
        try {
            // 使用反射访问私有方法
            java.lang.reflect.Method method = AgentsRegistrar.class.getDeclaredMethod(
                "generateUniqueBeanName", String.class, BeanDefinitionRegistry.class, Map.class);
            method.setAccessible(true);
            return (String) method.invoke(registrar, beanClassName, registry, registeredBeanNames);
        } catch (Exception e) {
            throw new RuntimeException("Failed to invoke generateUniqueBeanName method", e);
        }
    }
}
