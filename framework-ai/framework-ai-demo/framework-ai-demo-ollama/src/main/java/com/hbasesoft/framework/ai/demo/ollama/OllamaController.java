package com.hbasesoft.framework.ai.demo.ollama;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * REST controller for Ollama AI services.
 */
@RestController
public class OllamaController {

    /** The Ollama service. */
    private final OllamaService ollamaService;

    /**
     * Constructor for OllamaController.
     *
     * @param ollamaService the Ollama service
     */
    @Autowired
    public OllamaController(final OllamaService ollamaService) {
        this.ollamaService = ollamaService;
    }

    /**
     * Generates a response for the given message.
     *
     * @param message the input message
     * @return the generated response
     */
    @GetMapping("/ai")
    public String generate(@RequestParam(value = "message", defaultValue = "Tell me a joke") final String message) {
        return ollamaService.getSimpleResponse(message);
    }

    /**
     * Generates code based on the provided description.
     *
     * @param description the description of the code to generate
     * @return the generated code
     */
    @GetMapping("/ai/code")
    public String generateCode(
            @RequestParam(value = "description", defaultValue = 
            "a simple hello world program") final String description) {
        return ollamaService.getCodeGeneration(description);
    }

    /**
     * Generates structured output based on the provided description.
     *
     * @param description the description for structured output
     * @return the structured output as a Map
     */
    @GetMapping("/ai/structured")
    public Map<String, Object> generateStructured(
            @RequestParam(value = "description", defaultValue = 
            "Return a JSON object with name and age fields") final String description) {
        return ollamaService.getStructuredOutput(description);
    }
}
