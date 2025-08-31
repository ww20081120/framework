package com.hbasesoft.framework.ai.demo.ollama;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Configuration class for Ollama chat client.
 */
@Configuration
public class OllamaConfig {

    /**
     * Creates a ChatClient bean with the provided OllamaChatModel.
     *
     * @param chatModel the Ollama chat model
     * @return the ChatClient instance
     */
    @Bean
    @Primary
    public ChatClient chatClient(final OllamaChatModel chatModel) {
        return ChatClient.builder(chatModel).build();
    }
}
