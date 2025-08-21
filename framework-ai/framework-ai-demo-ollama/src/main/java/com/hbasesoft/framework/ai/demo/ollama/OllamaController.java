package com.hbasesoft.framework.ai.demo.ollama;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class OllamaController {

    private final OllamaService ollamaService;

    @Autowired
    public OllamaController(OllamaService ollamaService) {
        this.ollamaService = ollamaService;
    }

    @GetMapping("/ai")
    public String generate(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        return ollamaService.getSimpleResponse(message);
    }

    @GetMapping("/ai/code")
    public String generateCode(@RequestParam(value = "description", defaultValue = "a simple hello world program") String description) {
        return ollamaService.getCodeGeneration(description);
    }

    @GetMapping("/ai/structured")
    public Map<String, Object> generateStructured(@RequestParam(value = "description", defaultValue = "Return a JSON object with name and age fields") String description) {
        return ollamaService.getStructuredOutput(description);
    }
}