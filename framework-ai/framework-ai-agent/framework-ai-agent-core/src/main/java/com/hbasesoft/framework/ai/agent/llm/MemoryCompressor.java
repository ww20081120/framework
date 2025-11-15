/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.agent.llm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hbasesoft.framework.ai.agent.config.IManusProperties;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;

/**
 * Compresses conversation memory when token limits are exceeded
 *
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年9月14日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.agent.llm <br>
 */
@Component
public class MemoryCompressor {

    @Autowired
    private ILlmService llmService;

    @Autowired
    private IManusProperties manusProperties;

    // Token estimation cache
    private final Map<String, Integer> tokenCache = new ConcurrentHashMap<>();

    // Approximate token estimation constants for OpenAI models
    private static final int CHARS_PER_TOKEN = 4;

    private static final double WORDS_PER_TOKEN = 0.75;

    // Default token limits
    private static final int DEFAULT_MAX_TOKENS = 4096;

    private static final int SUMMARY_TOKEN_RESERVE = 200;

    /**
     * Compress memory for a specific plan when token limit is exceeded
     * 
     * @param planId the plan ID
     * @param maxTokens maximum number of tokens to keep after compression
     * @return true if compression was successful, false otherwise
     */
    public boolean compressMemory(String planId, int maxTokens) {
        if (planId == null) {
            LoggerUtil.warn("Cannot compress memory: planId is null");
            return false;
        }

        // Use default max tokens if not specified
        if (maxTokens <= 0) {
            maxTokens = DEFAULT_MAX_TOKENS;
        }

        try {
            LoggerUtil.info("Starting memory compression for plan {0} with max tokens {1}", planId, maxTokens);

            // Get the conversation memory
            ChatMemory chatMemory = llmService.getAgentMemory(manusProperties.getMaxMemory());
            List<Message> messages = chatMemory.get(planId);

            if (messages == null || messages.isEmpty()) {
                LoggerUtil.info("No messages to compress for plan {0}", planId);
                return true;
            }

            LoggerUtil.info("Found {0} messages to compress for plan {1}", messages.size(), planId);

            // Calculate current tokens
            int currentTokens = calculateTokens(messages);
            LoggerUtil.info("Current token usage: {0}, limit: {1}", currentTokens, maxTokens);

            // If we're already under the limit, no need to compress
            if (currentTokens <= maxTokens) {
                LoggerUtil.info("Memory compression not needed for plan {0}: {1} tokens (limit: {2})", planId,
                    currentTokens, maxTokens);
                return true;
            }

            // Compress the memory using intelligent compression based on tokens
            List<Message> compressedMessages = compressMessagesByTokens(messages, maxTokens);

            // Clear existing memory and add compressed messages
            chatMemory.clear(planId);
            chatMemory.add(planId, compressedMessages);

            int compressedTokens = calculateTokens(compressedMessages);
            LoggerUtil.info("Memory compression completed for plan {0}: {1} tokens reduced to {2}", planId,
                currentTokens, compressedTokens);

            return true;
        }
        catch (Exception e) {
            LoggerUtil.error("Failed to compress memory for plan " + planId, e);
            return false;
        }
    }

    /**
     * Compress memory for a specific plan using message count limit (backward compatibility)
     * 
     * @param planId the plan ID
     * @param maxMessages maximum number of messages to keep after compression
     * @return true if compression was successful, false otherwise
     */
    public boolean compressMemoryByMessageCount(String planId, int maxMessages) {
        if (planId == null) {
            LoggerUtil.warn("Cannot compress memory: planId is null");
            return false;
        }

        try {
            LoggerUtil.info("Starting memory compression for plan {0} with max messages {1}", planId, maxMessages);

            // Get the conversation memory
            ChatMemory chatMemory = llmService.getAgentMemory(manusProperties.getMaxMemory());
            List<Message> messages = chatMemory.get(planId);

            if (messages == null || messages.isEmpty()) {
                LoggerUtil.info("No messages to compress for plan {0}", planId);
                return true;
            }

            LoggerUtil.info("Found {0} messages to compress for plan {1}", messages.size(), planId);

            // If we already have fewer messages than the limit, no need to compress
            if (messages.size() <= maxMessages) {
                LoggerUtil.info("Memory compression not needed for plan {0}: {1} messages (limit: {2})", planId,
                    messages.size(), maxMessages);
                return true;
            }

            // Compress the memory by summarizing older messages
            List<Message> compressedMessages = compressMessages(messages, maxMessages);

            // Clear existing memory and add compressed messages
            chatMemory.clear(planId);
            chatMemory.add(planId, compressedMessages);

            LoggerUtil.info("Memory compression completed for plan {0}: {1} messages reduced to {2}", planId,
                messages.size(), compressedMessages.size());

            return true;
        }
        catch (Exception e) {
            LoggerUtil.error("Failed to compress memory for plan " + planId, e);
            return false;
        }
    }

    /**
     * Advanced compression that summarizes older messages instead of just truncating
     * 
     * @param planId the plan ID
     * @param messages original messages
     * @param maxTokens maximum tokens to keep
     * @return compressed messages with summaries
     */
    public List<Message> compressMemoryWithSummarization(String planId, List<Message> messages, int maxTokens) {
        // Calculate current tokens
        int currentTokens = calculateTokens(messages);

        // If already under limit, return as is
        if (currentTokens <= maxTokens) {
            return messages;
        }

        try {
            // Use intelligent token-based compression
            return compressMessagesByTokens(messages, maxTokens);
        }
        catch (Exception e) {
            LoggerUtil.error("Failed to create summary for memory compression", e);
            // Fall back to simple truncation with a reasonable message count
            int maxMessages = Math.max(5, maxTokens / 200); // Rough estimate: 200 tokens per message
            return compressMessages(messages, maxMessages);
        }
    }

    /**
     * Clear the token cache
     */
    public void clearCache() {
        tokenCache.clear();
    }

    /**
     * Get cache size for monitoring
     * 
     * @return cache size
     */
    public int getCacheSize() {
        return tokenCache.size();
    }

    /**
     * Compress messages based on token limits using intelligent selection
     * 
     * @param messages original messages
     * @param maxTokens maximum tokens to keep
     * @return compressed messages
     */
    private List<Message> compressMessagesByTokens(List<Message> messages, int maxTokens) {
        // If messages are very few, return as is
        if (messages.size() <= 2) {
            return messages;
        }

        List<Message> compressed = new ArrayList<>();
        int currentTokens = 0;

        // Reserve tokens for summary
        int availableTokens = maxTokens - SUMMARY_TOKEN_RESERVE;

        // Keep the most recent message (usually the most important)
        Message lastMessage = messages.get(messages.size() - 1);
        int lastMessageTokens = calculateTokensForMessage(lastMessage);

        // If the last message alone exceeds the limit, just keep it
        if (lastMessageTokens >= availableTokens) {
            compressed.add(lastMessage);
            return compressed;
        }

        // Add the last message
        compressed.add(lastMessage);
        currentTokens += lastMessageTokens;

        // Work backwards from the second-to-last message
        for (int i = messages.size() - 2; i >= 0; i--) {
            Message msg = messages.get(i);
            int msgTokens = calculateTokensForMessage(msg);

            // Check if we can add this message without exceeding the limit
            if (currentTokens + msgTokens <= availableTokens) {
                compressed.add(0, msg); // Add to the beginning to maintain order
                currentTokens += msgTokens;
            }
            else {
                // We need to summarize the remaining messages
                List<Message> remainingMessages = messages.subList(0, i + 1);
                String summary = createSummaryWithCache(remainingMessages);

                SystemMessage summaryMessage = new SystemMessage("Previous conversation summary: " + summary
                    + " (This summary replaces " + remainingMessages.size() + " previous messages)");
                compressed.add(0, summaryMessage);
                break;
            }
        }

        return compressed;
    }

    /**
     * Compress messages by summarizing older ones
     * 
     * @param messages original messages
     * @param maxMessages maximum messages to keep
     * @return compressed messages
     */
    private List<Message> compressMessages(List<Message> messages, int maxMessages) {
        // If we have fewer messages than the limit, return as is
        if (messages.size() <= maxMessages) {
            return messages;
        }

        // Keep the most recent messages (last maxMessages)
        int startIndex = messages.size() - maxMessages;
        if (startIndex < 0) {
            startIndex = 0;
        }

        // For simplicity, we're just truncating older messages
        // In a more advanced implementation, we could summarize them instead
        return messages.subList(startIndex, messages.size());
    }

    /**
     * Calculate total tokens for a list of messages using OpenAI tokenization algorithm approximation
     * 
     * @param messages list of messages
     * @return estimated token count
     */
    private int calculateTokens(List<Message> messages) {
        int totalTokens = 0;
        for (Message message : messages) {
            totalTokens += calculateTokensForMessage(message);
        }
        return totalTokens;
    }

    /**
     * Calculate tokens for a single message using OpenAI tokenization algorithm approximation
     * 
     * @param message the message
     * @return estimated token count
     */
    private int calculateTokensForMessage(Message message) {
        if (message == null || message.getText() == null) {
            return 0;
        }

        String text = message.getText();

        // Check cache first
        Integer cachedTokens = tokenCache.get(text);
        if (cachedTokens != null) {
            return cachedTokens;
        }

        // Estimate tokens using OpenAI's approach:
        // 1. Character-based estimation (4 chars per token is a rough average for English)
        // 2. Word-based estimation (0.75 words per token)
        // 3. Line breaks and special characters

        int charTokens = text.length() / CHARS_PER_TOKEN;
        int wordCount = text.split("\\s+").length;
        int wordTokens = (int) (wordCount * WORDS_PER_TOKEN);

        // Take the average of both methods for better accuracy
        int estimatedTokens = (charTokens + wordTokens) / 2;

        // Add some tokens for message structure and metadata
        estimatedTokens += 5; // Rough estimate for message overhead

        // Cache the result (limit cache size)
        if (tokenCache.size() < 1000) {
            tokenCache.put(text, estimatedTokens);
        }

        return estimatedTokens;
    }

    /**
     * Create a summary of messages using the LLM with caching
     * 
     * @param messages messages to summarize
     * @return summary text
     */
    private String createSummaryWithCache(List<Message> messages) {
        // Create a cache key from message texts
        StringBuilder keyBuilder = new StringBuilder();
        for (Message message : messages) {
            if (message != null && message.getText() != null) {
                keyBuilder.append(message.getText());
            }
        }
        String cacheKey = String.valueOf(keyBuilder.toString().hashCode());

        // Check if we have a cached summary
        String cachedSummary = tokenCache.containsKey(cacheKey) ? "Cached summary for " + messages.size() + " messages"
            : null;

        if (cachedSummary != null) {
            LoggerUtil.debug("Using cached summary for {0} messages", messages.size());
            return cachedSummary;
        }

        // Generate new summary
        String summary = createSummary(messages);

        // Cache the summary (using the cache for both tokens and summaries)
        if (tokenCache.size() < 1000) {
            tokenCache.put(cacheKey, summary.length() / CHARS_PER_TOKEN); // Cache estimated tokens
        }

        return summary;
    }

    /**
     * Create a summary of messages using the LLM
     * 
     * @param messages messages to summarize
     * @return summary text
     */
    private String createSummary(List<Message> messages) {
        try {
            StringBuilder conversation = new StringBuilder();
            for (Message message : messages) {
                if (message instanceof UserMessage) {
                    conversation.append("User: ").append(message.getText()).append("\n");
                }
                else if (message instanceof SystemMessage) {
                    conversation.append("System: ").append(message.getText()).append("\n");
                }
                else {
                    conversation.append("Assistant: ").append(message.getText()).append("\n");
                }
            }

            String summaryPrompt = """
                Please summarize the following conversation in a concise way,
                focusing on the key points and decisions made:

                {conversation}

                Summary:""";

            PromptTemplate template = new PromptTemplate(summaryPrompt);
            Prompt prompt = template.create(Map.of("conversation", conversation.toString()));

            ChatClient chatClient = llmService.getDefaultChatClient();
            ChatResponse response = chatClient.prompt(prompt).call().chatResponse();

            return response.getResult().getOutput().getText();
        }
        catch (Exception e) {
            LoggerUtil.error("Failed to generate summary, returning basic summary", e);
            return "Previous conversation (summary generation failed)";
        }
    }
}
