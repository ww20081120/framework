package com.hbasesoft.framework.ai.demo.ollama;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Service class for Ollama AI operations.
 */
@Service
public class OllamaService {

    /** The chat client. */
    private final ChatClient chatClient;

    /**
     * Constructor for OllamaService.
     *
     * @param chatClient the chat client
     */
    public OllamaService(final ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    /**
     * Gets a simple response for the given message.
     *
     * @param message the input message
     * @return the generated response
     */
    public String getSimpleResponse(final String message) {
        return chatClient.prompt().user(message).call().content();
    }

    /**
     * Generates code based on the provided description.
     *
     * @param description the description of the code to generate
     * @return the generated code
     */
    public String getCodeGeneration(final String description) {
        return chatClient.prompt()
            .system("You are an expert Java developer. Generate clean, efficient, and "
                + "well-documented Java code based on the user's request.")
            .user("Generate Java code for: " + description).call().content();
    }

    /**
     * Gets structured output based on the provided description.
     *
     * @param description the description for structured output
     * @return the structured output as a Map
     */
    public Map<String, Object> getStructuredOutput(final String description) {
        return chatClient.prompt().user(description).call().entity(Map.class);
    }
}
