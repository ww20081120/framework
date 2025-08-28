package com.hbasesoft.framework.ai.demo.ollama;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.yml")
class OllamaServiceTest {

	@Autowired
	private OllamaService ollamaService;

	@Test
	void testGetSimpleResponse() {
		String message = "Hello, how are you?";
		String response = ollamaService.getSimpleResponse(message);

		assertNotNull(response);
		assertFalse(response.isEmpty());
		System.out.println("Simple response: " + response);
	}

	@Test
	void testGetCodeGeneration() {
		String description = "a simple hello world program in Java";
		String code = ollamaService.getCodeGeneration(description);

		assertNotNull(code);
		assertFalse(code.isEmpty());
		assertTrue(code.contains("public class") || code.contains("System.out.println"));
		System.out.println("Generated code: " + code);
	}

	@Test
	void testGetStructuredOutput() {
		String description = "Return a JSON object with name and age fields";
		Map<String, Object> result = ollamaService.getStructuredOutput(description);

		assertNotNull(result);
		assertFalse(result.isEmpty());
		assertTrue(result.containsKey("name") || result.containsKey("age"));
		System.out.println("Structured output: " + result);
	}
}
