package com.hbasesoft.framework.ai.demo.ollama;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureWebMvc
@TestPropertySource(locations = "classpath:application-test.yml")
class OllamaControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void testGenerate() throws Exception {
		MvcResult result = mockMvc.perform(get("/ai").param("message", "Hello, how are you?"))
				.andExpect(status().isOk()).andReturn();

		String response = result.getResponse().getContentAsString();
		assertNotNull(response);
		assertFalse(response.isEmpty());
		System.out.println("AI Response: " + response);
	}

	@Test
	void testGenerateCode() throws Exception {
		MvcResult result = mockMvc.perform(get("/ai/code").param("description", "a simple hello world program in Java"))
				.andExpect(status().isOk()).andReturn();

		String response = result.getResponse().getContentAsString();
		assertNotNull(response);
		assertFalse(response.isEmpty());
		assertTrue(response.contains("public class") || response.contains("System.out.println"));
		System.out.println("Generated Code: " + response);
	}

	@Test
	void testGenerateStructured() throws Exception {
		MvcResult result = mockMvc
				.perform(get("/ai/structured").param("description", "Return a JSON object with name and age fields"))
				.andExpect(status().isOk()).andReturn();

		String response = result.getResponse().getContentAsString();
		assertNotNull(response);
		assertFalse(response.isEmpty());
		assertTrue(response.contains("\"name\"") || response.contains("\"age\""));
		System.out.println("Structured Output: " + response);
	}
}
