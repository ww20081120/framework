/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.agent.tool;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * <Description> Test class for AnnotatedMethodToolAdapter <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年9月1日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.agent.tool <br>
 */
public class AnnotatedMethodToolAdapterTest {

	@Test
	public void testGetParametersWithDates() throws Exception {
		// Create test service and method
		TestService testService = new TestService();
		java.lang.reflect.Method method = TestService.class.getMethod("testWithDates", String.class, Date.class,
				LocalDate.class, LocalDateTime.class);

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
		assertTrue(parametersJson.contains("\"birthDate\":{\"type\":\"string\",\"format\":\"date-time\""));
		assertTrue(parametersJson.contains("\"localDate\":{\"type\":\"string\",\"format\":\"date-time\""));
		assertTrue(parametersJson.contains("\"localDateTime\":{\"type\":\"string\",\"format\":\"date-time\""));
	}

	@Test
	public void testGetParametersWithObject() throws Exception {
		// Create test service and method
		TestService testService = new TestService();
		java.lang.reflect.Method method = TestService.class.getMethod("testWithObject", TestObject.class);

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
		assertTrue(parametersJson.contains("\"properties\":{\"name\":{\"type\":\"string\""));
		assertTrue(parametersJson.contains("\"age\":{\"type\":\"integer\""));
		assertTrue(parametersJson.contains("\"active\":{\"type\":\"boolean\""));
		assertTrue(parametersJson.contains("\"score\":{\"type\":\"number\""));
	}

	@Test
	public void testGetParametersWithPrimitives() throws Exception {
		// Create test service and method
		TestService testService = new TestService();
		java.lang.reflect.Method method = TestService.class.getMethod("testWithPrimitives", byte.class, short.class,
				float.class, char.class);

		Action action = method.getAnnotation(Action.class);
		ObjectMapper objectMapper = new ObjectMapper();

		// Create adapter
		AnnotatedMethodToolAdapter adapter = new AnnotatedMethodToolAdapter(testService, method, action, objectMapper);

		// Get parameters JSON
		String parametersJson = adapter.getParameters();
		System.out.println("Parameters JSON: " + parametersJson);

		// Verify that the JSON contains the expected structure
		assertTrue(parametersJson.contains("\"type\":\"object\""));
		assertTrue(parametersJson.contains("\"byteValue\":{\"type\":\"integer\""));
		assertTrue(parametersJson.contains("\"shortValue\":{\"type\":\"integer\""));
		assertTrue(parametersJson.contains("\"floatValue\":{\"type\":\"number\""));
		assertTrue(parametersJson.contains("\"charValue\":{\"type\":\"string\""));
	}

	@Test
	public void testRunWithDates() throws Exception {
		// Create test service and method
		TestService testService = new TestService();
		java.lang.reflect.Method method = TestService.class.getMethod("testWithDates", String.class, Date.class,
				LocalDate.class, LocalDateTime.class);

		Action action = method.getAnnotation(Action.class);
		ObjectMapper objectMapper = new ObjectMapper();

		// Create adapter
		AnnotatedMethodToolAdapter adapter = new AnnotatedMethodToolAdapter(testService, method, action, objectMapper);

		// Create test input
		Map<String, Object> input = new HashMap<>();
		input.put("name", "Test User");
		input.put("birthDate", "2025-09-01T00:00:00Z");
		input.put("localDate", "2025-09-01");
		input.put("localDateTime", "2025-09-01T10:30:00");

		// Run the method
		ToolExecuteResult result = adapter.run(input);

		// Verify the result
		assertNotNull(result);
		assertFalse(result.isInterrupted());
		assertTrue(result.getOutput().contains("Test with dates"));
		assertTrue(result.getOutput().contains("Test User"));
	}

	@Test
	public void testRunWithObject() throws Exception {
		// Create test service and method
		TestService testService = new TestService();
		java.lang.reflect.Method method = TestService.class.getMethod("testWithObject", TestObject.class);

		Action action = method.getAnnotation(Action.class);
		ObjectMapper objectMapper = new ObjectMapper();

		// Create adapter
		AnnotatedMethodToolAdapter adapter = new AnnotatedMethodToolAdapter(testService, method, action, objectMapper);

		// Create test input
		Map<String, Object> input = new HashMap<>();
		Map<String, Object> objectInput = new HashMap<>();
		objectInput.put("name", "Test Object");
		objectInput.put("age", 25);
		objectInput.put("active", true);
		objectInput.put("score", 95.5);
		input.put("testObject", objectInput);

		// Run the method
		ToolExecuteResult result = adapter.run(input);

		// Verify the result
		assertNotNull(result);
		assertFalse(result.isInterrupted());
		assertTrue(result.getOutput().contains("Test with object"));
		assertTrue(result.getOutput().contains("Test Object"));
	}
}
