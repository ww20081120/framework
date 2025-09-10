/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.agent.tool;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * <Description> Test class for AnnotatedMethodToolAdapter array support <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年9月10日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.agent.tool <br>
 */
public class AnnotatedMethodToolAdapterArrayTest {

    @Test
    public void testGetParametersWithArrays() throws Exception {
        // Create test service and method
        ArraySupportTestService testService = new ArraySupportTestService();
        java.lang.reflect.Method method = ArraySupportTestService.class.getMethod("testWithArrays", 
            String.class, int[].class, String[].class, boolean[].class);

        Action action = method.getAnnotation(Action.class);
        ObjectMapper objectMapper = new ObjectMapper();

        // Create adapter
        AnnotatedMethodToolAdapter adapter = new AnnotatedMethodToolAdapter(testService, method, action, objectMapper);

        // Get parameters JSON
        String parametersJson = adapter.getParameters();
        System.out.println("Parameters JSON: " + parametersJson);

        // Verify that the JSON contains the expected structure
        assertTrue(parametersJson.contains("\"type\":\"object\""));
        assertTrue(parametersJson.contains("\"name\":{\"type\":\"string\""));
        assertTrue(parametersJson.contains("\"numbers\":{\"type\":\"array\""));
        assertTrue(parametersJson.contains("\"items\":{\"type\":\"integer\""));
        assertTrue(parametersJson.contains("\"tags\":{\"type\":\"array\""));
        assertTrue(parametersJson.contains("\"items\":{\"type\":\"string\""));
        assertTrue(parametersJson.contains("\"flags\":{\"type\":\"array\""));
        assertTrue(parametersJson.contains("\"items\":{\"type\":\"boolean\""));
    }

    @Test
    public void testGetParametersWithObjectContainingArrays() throws Exception {
        // Create test service and method
        ArraySupportTestService testService = new ArraySupportTestService();
        java.lang.reflect.Method method = ArraySupportTestService.class.getMethod("testWithObjectContainingArrays", 
            ArraySupportTestObject.class);

        Action action = method.getAnnotation(Action.class);
        ObjectMapper objectMapper = new ObjectMapper();

        // Create adapter
        AnnotatedMethodToolAdapter adapter = new AnnotatedMethodToolAdapter(testService, method, action, objectMapper);

        // Get parameters JSON
        String parametersJson = adapter.getParameters();
        System.out.println("Parameters JSON: " + parametersJson);

        // Verify that the JSON contains the expected structure
        assertTrue(parametersJson.contains("\"type\":\"object\""));
        assertTrue(parametersJson.contains("\"testObject\":{\"type\":\"object\""));
        assertTrue(parametersJson.contains("\"numbers\":{\"type\":\"array\""));
        assertTrue(parametersJson.contains("\"items\":{\"type\":\"integer\""));
        assertTrue(parametersJson.contains("\"tags\":{\"type\":\"array\""));
        assertTrue(parametersJson.contains("\"items\":{\"type\":\"string\""));
        assertTrue(parametersJson.contains("\"flags\":{\"type\":\"array\""));
        assertTrue(parametersJson.contains("\"items\":{\"type\":\"boolean\""));
    }

    @Test
    public void testRunWithArrays() throws Exception {
        // Create test service and method
        ArraySupportTestService testService = new ArraySupportTestService();
        java.lang.reflect.Method method = ArraySupportTestService.class.getMethod("testWithArrays", 
            String.class, int[].class, String[].class, boolean[].class);

        Action action = method.getAnnotation(Action.class);
        ObjectMapper objectMapper = new ObjectMapper();

        // Create adapter
        AnnotatedMethodToolAdapter adapter = new AnnotatedMethodToolAdapter(testService, method, action, objectMapper);

        // Create test input
        Map<String, Object> input = new HashMap<>();
        input.put("name", "Test User");
        
        // Add integer array
        input.put("numbers", Arrays.asList(1, 2, 3, 4, 5));
        
        // Add string array
        input.put("tags", Arrays.asList("tag1", "tag2", "tag3"));
        
        // Add boolean array
        input.put("flags", Arrays.asList(true, false, true));

        // Run the method
        ToolExecuteResult result = adapter.run(input);

        // Verify the result
        assertNotNull(result);
        assertFalse(result.isInterrupted());
        assertTrue(result.getOutput().contains("Test with arrays"));
        assertTrue(result.getOutput().contains("Test User"));
        assertTrue(result.getOutput().contains("[1, 2, 3, 4, 5]"));
        assertTrue(result.getOutput().contains("[tag1, tag2, tag3]"));
        assertTrue(result.getOutput().contains("[true, false, true]"));
    }

    @Test
    public void testRunWithObjectContainingArrays() throws Exception {
        // Create test service and method
        ArraySupportTestService testService = new ArraySupportTestService();
        java.lang.reflect.Method method = ArraySupportTestService.class.getMethod("testWithObjectContainingArrays", 
            ArraySupportTestObject.class);

        Action action = method.getAnnotation(Action.class);
        ObjectMapper objectMapper = new ObjectMapper();

        // Create adapter
        AnnotatedMethodToolAdapter adapter = new AnnotatedMethodToolAdapter(testService, method, action, objectMapper);

        // Create test input
        Map<String, Object> input = new HashMap<>();
        Map<String, Object> objectInput = new HashMap<>();
        objectInput.put("name", "Test Object");
        objectInput.put("numbers", Arrays.asList(10, 20, 30));
        objectInput.put("tags", Arrays.asList("a", "b", "c"));
        objectInput.put("flags", Arrays.asList(false, true, false));
        input.put("testObject", objectInput);

        // Run the method
        ToolExecuteResult result = adapter.run(input);

        // Verify the result
        assertNotNull(result);
        assertFalse(result.isInterrupted());
        assertTrue(result.getOutput().contains("Test with object containing arrays"));
        assertTrue(result.getOutput().contains("Test Object"));
        assertTrue(result.getOutput().contains("[10, 20, 30]"));
        assertTrue(result.getOutput().contains("['a', 'b', 'c']"));
        assertTrue(result.getOutput().contains("[false, true, false]"));
    }

    @Test
    public void testRunWithStringArrayOnly() throws Exception {
        // Create test service and method
        ArraySupportTestService testService = new ArraySupportTestService();
        java.lang.reflect.Method method = ArraySupportTestService.class.getMethod("testWithStringArray", 
            String[].class);

        Action action = method.getAnnotation(Action.class);
        ObjectMapper objectMapper = new ObjectMapper();

        // Create adapter
        AnnotatedMethodToolAdapter adapter = new AnnotatedMethodToolAdapter(testService, method, action, objectMapper);

        // Create test input
        Map<String, Object> input = new HashMap<>();
        input.put("values", Arrays.asList("value1", "value2", "value3"));

        // Run the method
        ToolExecuteResult result = adapter.run(input);

        // Verify the result
        assertNotNull(result);
        assertFalse(result.isInterrupted());
        assertTrue(result.getOutput().contains("Test with string array"));
        assertTrue(result.getOutput().contains("[value1, value2, value3]"));
    }

    @Test
    public void testGetParametersWithList() throws Exception {
        // Create test service and method
        TestService testService = new TestService();
        java.lang.reflect.Method method = TestService.class.getMethod("testWithObjectList", 
            List.class);

        Action action = method.getAnnotation(Action.class);
        ObjectMapper objectMapper = new ObjectMapper();

        // Create adapter
        AnnotatedMethodToolAdapter adapter = new AnnotatedMethodToolAdapter(testService, method, action, objectMapper);

        // Get parameters JSON
        String parametersJson = adapter.getParameters();
        System.out.println("Parameters JSON for List: " + parametersJson);

        // Verify that the JSON contains the expected structure
        assertTrue(parametersJson.contains("\"type\":\"object\""));
        assertTrue(parametersJson.contains("\"testObjects\":{\"type\":\"array\""));
        assertTrue(parametersJson.contains("\"items\":{\"type\":\"object\""));
    }

    @Test
    public void testRunWithList() throws Exception {
        // Create test service and method
        TestService testService = new TestService();
        java.lang.reflect.Method method = TestService.class.getMethod("testWithObjectList", 
            List.class);

        Action action = method.getAnnotation(Action.class);
        ObjectMapper objectMapper = new ObjectMapper();

        // Create adapter
        AnnotatedMethodToolAdapter adapter = new AnnotatedMethodToolAdapter(testService, method, action, objectMapper);

        // Create test input
        Map<String, Object> input = new HashMap<>();
        List<Map<String, Object>> objectList = new ArrayList<>();
        
        Map<String, Object> object1 = new HashMap<>();
        object1.put("name", "Test Object 1");
        object1.put("age", 25);
        object1.put("active", true);
        object1.put("score", 95.5);
        objectList.add(object1);
        
        Map<String, Object> object2 = new HashMap<>();
        object2.put("name", "Test Object 2");
        object2.put("age", 30);
        object2.put("active", false);
        object2.put("score", 88.0);
        objectList.add(object2);
        
        input.put("testObjects", objectList);

        // Run the method
        ToolExecuteResult result = adapter.run(input);

        // Verify the result
        assertNotNull(result);
        assertFalse(result.isInterrupted());
        assertTrue(result.getOutput().contains("Test with object list"));
        assertTrue(result.getOutput().contains("Test Object 1"));
        assertTrue(result.getOutput().contains("Test Object 2"));
        assertTrue(result.getOutput().contains("age=25"));
        assertTrue(result.getOutput().contains("age=30"));
    }
}
