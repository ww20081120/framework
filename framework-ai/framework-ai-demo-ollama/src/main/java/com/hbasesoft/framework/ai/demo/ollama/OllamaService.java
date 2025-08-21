package com.hbasesoft.framework.ai.demo.ollama;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class OllamaService {

    private final ChatClient chatClient;

    public OllamaService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public String getSimpleResponse(String message) {
        return chatClient.prompt()
                .user(message)
                .call()
                .content();
    }

    public String getCodeGeneration(String description) {
        return chatClient.prompt()
                .system("You are an expert Java developer. Generate clean, efficient, and well-documented Java code based on the user's request.")
                .user("Generate Java code for: " + description)
                .call()
                .content();
    }

    public Map<String, Object> getStructuredOutput(String description) {
        return chatClient.prompt()
                .user(description)
                .call()
                .entity(Map.class);
    }
}