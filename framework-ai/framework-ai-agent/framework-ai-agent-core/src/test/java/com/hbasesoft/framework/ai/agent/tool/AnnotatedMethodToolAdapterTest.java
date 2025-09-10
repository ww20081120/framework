/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.agent.tool;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Method;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.aop.framework.ProxyFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * <Description> Test for AnnotatedMethodToolAdapter with Spring AOP proxy objects <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年9月10日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.agent.tool <br>
 */
public class AnnotatedMethodToolAdapterTest {

    /**
     * Test service class with Action and ActionParam annotations
     */
    public static class TestService {
        
        @Action(description = "Test method for verifying ActionParam annotations on proxy objects")
        public String testMethod(
                @ActionParam(description = "Test parameter 1", required = true) String param1,
                @ActionParam(description = "Test parameter 2", required = false) int param2) {
            return "Result: " + param1 + ", " + param2;
        }
    }

    @Test
    public void testGetActionParamAnnotationFromProxy() throws Exception {
        // Create a target object
        TestService target = new TestService();
        
        // Create a Spring AOP proxy
        ProxyFactory proxyFactory = new ProxyFactory(target);
        proxyFactory.setProxyTargetClass(true); // Use CGLIB proxy
        Object proxy = proxyFactory.getProxy();
        
        // Debug information
        System.out.println("Target class: " + target.getClass().getName());
        System.out.println("Proxy class: " + proxy.getClass().getName());
        System.out.println("Is AOP proxy: " + org.springframework.aop.support.AopUtils.isAopProxy(proxy));
        System.out.println("Is CGLIB proxy: " + org.springframework.aop.support.AopUtils.isCglibProxy(proxy));
        
        // Get the method from the proxy
        Method method = proxy.getClass().getMethod("testMethod", String.class, int.class);
        
        // Get the method from the target for comparison
        Method targetMethod = target.getClass().getMethod("testMethod", String.class, int.class);
        
        // Debug information about methods
        System.out.println("Proxy method: " + method);
        System.out.println("Target method: " + targetMethod);
        
        // Check annotations on proxy method parameters
        for (int i = 0; i < method.getParameters().length; i++) {
            java.lang.reflect.Parameter param = method.getParameters()[i];
            ActionParam actionParam = param.getAnnotation(ActionParam.class);
            System.out.println("Proxy parameter " + i + " annotation: " + actionParam);
        }
        
        // Check annotations on target method parameters
        for (int i = 0; i < targetMethod.getParameters().length; i++) {
            java.lang.reflect.Parameter param = targetMethod.getParameters()[i];
            ActionParam actionParam = param.getAnnotation(ActionParam.class);
            System.out.println("Target parameter " + i + " annotation: " + actionParam);
        }
        
        // Create the AnnotatedMethodToolAdapter
        ObjectMapper objectMapper = new ObjectMapper();
        AnnotatedMethodToolAdapter adapter = new AnnotatedMethodToolAdapter(proxy, method, 
                method.getAnnotation(Action.class), objectMapper);
        
        // Test that we can get the parameters JSON
        String parametersJson = adapter.getParameters();
        System.out.println("Parameters JSON: " + parametersJson);
        assertNotNull(parametersJson);
        assertTrue(parametersJson.contains("param1"), "JSON should contain param1");
        assertTrue(parametersJson.contains("param2"), "JSON should contain param2");
        assertTrue(parametersJson.contains("Test parameter 1"), "JSON should contain 'Test parameter 1'");
        assertTrue(parametersJson.contains("Test parameter 2"), "JSON should contain 'Test parameter 2'");
        
        // Test that we can run the method
        new java.util.HashMap<String, Object>() {{
            put("param1", "test");
            put("param2", 42);
        }};
        
        // Test the run method
        ToolExecuteResult result = adapter.run(new java.util.HashMap<String, Object>() {{
            put("param1", "test");
            put("param2", 42);
        }});
        
        assertNotNull(result);
        assertEquals("Result: test, 42", result.getOutput());
        assertEquals(false, result.isInterrupted());
    }
    
    @Test
    public void testGetActionParamAnnotationFromDirectObject() throws Exception {
        // Create a target object directly (not proxied)
        TestService target = new TestService();
        
        // Get the method from the target
        Method method = target.getClass().getMethod("testMethod", String.class, int.class);
        
        // Create the AnnotatedMethodToolAdapter
        ObjectMapper objectMapper = new ObjectMapper();
        AnnotatedMethodToolAdapter adapter = new AnnotatedMethodToolAdapter(target, method, 
                method.getAnnotation(Action.class), objectMapper);
        
        // Test that we can get the parameters JSON
        String parametersJson = adapter.getParameters();
        assertNotNull(parametersJson);
        assertTrue(parametersJson.contains("param1"));
        assertTrue(parametersJson.contains("param2"));
        assertTrue(parametersJson.contains("Test parameter 1"));
        assertTrue(parametersJson.contains("Test parameter 2"));
    }
}
