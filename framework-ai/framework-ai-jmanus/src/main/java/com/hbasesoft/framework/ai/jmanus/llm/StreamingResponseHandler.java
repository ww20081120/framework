/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.jmanus.llm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.AssistantMessage.ToolCall;
import org.springframework.ai.chat.metadata.ChatGenerationMetadata;
import org.springframework.ai.chat.metadata.ChatResponseMetadata;
import org.springframework.ai.chat.metadata.EmptyRateLimit;
import org.springframework.ai.chat.metadata.PromptMetadata;
import org.springframework.ai.chat.metadata.RateLimit;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.model.MessageAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hbasesoft.framework.ai.jmanus.event.JmanusEventPublisher;
import com.hbasesoft.framework.ai.jmanus.event.PlanExceptionEvent;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;

import reactor.core.publisher.Flux;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月19日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.jmanus.llm <br>
 */
@Component
public class StreamingResponseHandler {

	@Autowired
	private JmanusEventPublisher jmanusEventPublisher;

	@Autowired
	private LlmTraceRecorder llmTraceRecorder;

	/**
	 * Result container for streaming response processing
	 */
	public static class StreamingResult {

		private final ChatResponse lastResponse;

		public StreamingResult(ChatResponse lastResponse) {
			this.lastResponse = lastResponse;
		}

		public ChatResponse getLastResponse() {
			return lastResponse;
		}

		/**
		 * Get tool calls, preferring merged calls if available, otherwise fall back to
		 * last response
		 */
		public List<ToolCall> getEffectiveToolCalls() {
			return lastResponse != null && lastResponse.getResult() != null
					&& lastResponse.getResult().getOutput() != null
							? lastResponse.getResult().getOutput().getToolCalls()
							: null;
		}

		/**
		 * Get text content, preferring merged text if available, otherwise fall back to
		 * last response
		 */
		public String getEffectiveText() {
			return lastResponse != null && lastResponse.getResult() != null
					&& lastResponse.getResult().getOutput() != null ? lastResponse.getResult().getOutput().getText()
							: null;
		}

	}

	/**
	 * Process a streaming chat response flux with periodic progress logging
	 * 
	 * @param responseFlux The streaming chat response flux
	 * @param contextName  A descriptive name for logging context (e.g., "Agent
	 *                     thinking", "Plan creation")
	 * @return StreamingResult containing merged content and the last response
	 */
	public StreamingResult processStreamingResponse(Flux<ChatResponse> responseFlux, String contextName,
			String planId) {
		try {
			LlmTraceRecorder.initRequest();
			AtomicReference<Long> lastLogTime = new AtomicReference<>(System.currentTimeMillis());

			// Assistant Message
			AtomicReference<StringBuilder> messageTextContentRef = new AtomicReference<>(new StringBuilder());
			AtomicReference<List<ToolCall>> messageToolCallRef = new AtomicReference<>(
					Collections.synchronizedList(new ArrayList<>()));
			AtomicReference<Map<String, Object>> messageMetadataMapRef = new AtomicReference<>();

			// ChatGeneration Metadata
			AtomicReference<ChatGenerationMetadata> generationMetadataRef = new AtomicReference<>(
					ChatGenerationMetadata.NULL);

			// Usage
			AtomicReference<Integer> metadataUsagePromptTokensRef = new AtomicReference<Integer>(0);
			AtomicReference<Integer> metadataUsageGenerationTokensRef = new AtomicReference<Integer>(0);
			AtomicReference<Integer> metadataUsageTotalTokensRef = new AtomicReference<Integer>(0);

			AtomicReference<PromptMetadata> metadataPromptMetadataRef = new AtomicReference<>(PromptMetadata.empty());
			AtomicReference<RateLimit> metadataRateLimitRef = new AtomicReference<>(new EmptyRateLimit());

			AtomicReference<String> metadataIdRef = new AtomicReference<>("");
			AtomicReference<String> metadataModelRef = new AtomicReference<>("");
			AtomicReference<ChatResponse> finalChatResponseRef = new AtomicReference<>(null);

			AtomicInteger responseCounter = new AtomicInteger(0);
			long startTime = System.currentTimeMillis();

			responseFlux.doOnSubscribe(subscription -> {
				messageTextContentRef.set(new StringBuilder());
				messageMetadataMapRef.set(new HashMap<>());
				metadataIdRef.set("");
				metadataModelRef.set("");
				metadataUsagePromptTokensRef.set(0);
				metadataUsageGenerationTokensRef.set(0);
				metadataUsageTotalTokensRef.set(0);
				metadataPromptMetadataRef.set(PromptMetadata.empty());
				metadataRateLimitRef.set(new EmptyRateLimit());

			}).doOnNext(chatResponse -> {
				responseCounter.incrementAndGet();

				if (chatResponse.getResult() != null) {
					if (chatResponse.getResult().getMetadata() != null
							&& chatResponse.getResult().getMetadata() != ChatGenerationMetadata.NULL) {
						generationMetadataRef.set(chatResponse.getResult().getMetadata());
					}
					if (chatResponse.getResult().getOutput().getText() != null) {
						messageTextContentRef.get().append(chatResponse.getResult().getOutput().getText());
					}
					messageToolCallRef.get().addAll(chatResponse.getResult().getOutput().getToolCalls());
					messageMetadataMapRef.get().putAll(chatResponse.getResult().getOutput().getMetadata());
				}
				if (chatResponse.getMetadata() != null) {
					if (chatResponse.getMetadata().getUsage() != null) {
						Usage usage = chatResponse.getMetadata().getUsage();
						metadataUsagePromptTokensRef.set(usage.getPromptTokens() > 0 ? usage.getPromptTokens()
								: metadataUsagePromptTokensRef.get());
						metadataUsageGenerationTokensRef
								.set(usage.getCompletionTokens() > 0 ? usage.getCompletionTokens()
										: metadataUsageGenerationTokensRef.get());
						metadataUsageTotalTokensRef.set(usage.getTotalTokens() > 0 ? usage.getTotalTokens()
								: metadataUsageTotalTokensRef.get());
					}
					if (chatResponse.getMetadata().getPromptMetadata() != null
							&& chatResponse.getMetadata().getPromptMetadata().iterator().hasNext()) {
						metadataPromptMetadataRef.set(chatResponse.getMetadata().getPromptMetadata());
					}
					if (chatResponse.getMetadata().getRateLimit() != null
							&& !(metadataRateLimitRef.get() instanceof EmptyRateLimit)) {
						metadataRateLimitRef.set(chatResponse.getMetadata().getRateLimit());
					}
					if (StringUtils.isNotEmpty(chatResponse.getMetadata().getId())) {
						metadataIdRef.set(chatResponse.getMetadata().getId());
					}
					if (StringUtils.isNotEmpty(chatResponse.getMetadata().getModel())) {
						metadataModelRef.set(chatResponse.getMetadata().getModel());
					}
				}

				// Check if 10 seconds have passed since last log output
				long currentTime = System.currentTimeMillis();
				long timeSinceLastLog = currentTime - lastLogTime.get();
				if (timeSinceLastLog >= 10000) { // 10 seconds = 10000 milliseconds
					logProgress(contextName, messageTextContentRef.get().toString(), messageToolCallRef.get().size(),
							responseCounter.get(), startTime);
					lastLogTime.set(currentTime);
				}
			}).doOnComplete(() -> {

				var usage = new MessageAggregator.DefaultUsage(metadataUsagePromptTokensRef.get(),
						metadataUsageGenerationTokensRef.get(), metadataUsageTotalTokensRef.get());

				var chatResponseMetadata = ChatResponseMetadata.builder().id(metadataIdRef.get())
						.model(metadataModelRef.get()).rateLimit(metadataRateLimitRef.get()).usage(usage)
						.promptMetadata(metadataPromptMetadataRef.get()).build();
				finalChatResponseRef
						.set(new ChatResponse(List.of(new Generation(
								new AssistantMessage(messageTextContentRef.get().toString(),
										messageMetadataMapRef.get(), messageToolCallRef.get()),
								generationMetadataRef.get())), chatResponseMetadata));
				logCompletion(contextName, messageTextContentRef.get().toString(), messageToolCallRef.get().size(),
						responseCounter.get(), startTime, usage);

				messageTextContentRef.set(new StringBuilder());
				messageToolCallRef.set(Collections.synchronizedList(new ArrayList<>()));
				messageMetadataMapRef.set(new HashMap<>());
				metadataIdRef.set("");
				metadataModelRef.set("");
				metadataUsagePromptTokensRef.set(0);
				metadataUsageGenerationTokensRef.set(0);
				metadataUsageTotalTokensRef.set(0);
				metadataPromptMetadataRef.set(PromptMetadata.empty());
				metadataRateLimitRef.set(new EmptyRateLimit());

			}).doOnError(e -> {
				LoggerUtil.error("Aggregation Error", e);
				jmanusEventPublisher.publish(new PlanExceptionEvent(planId, e));
			}).blockLast();

			llmTraceRecorder.recordResponse(finalChatResponseRef.get());
			return new StreamingResult(finalChatResponseRef.get());
		} finally {
			LlmTraceRecorder.clearRequest();
		}
	}

	/**
	 * Process a streaming chat response flux for text-only content (e.g.,
	 * summaries)
	 * 
	 * @param responseFlux The streaming chat response flux
	 * @param contextName  A descriptive name for logging context
	 * @return The merged text content
	 */
	public String processStreamingTextResponse(Flux<ChatResponse> responseFlux, String contextName, String planId) {
		StreamingResult result = processStreamingResponse(responseFlux, contextName, planId);
		return result.getEffectiveText();
	}

	private void logProgress(String contextName, String currentText, int toolCallCount, int responseCount,
			long startTime) {
		int textLength = currentText != null ? currentText.length() : 0;
		String preview = getTextPreview(currentText, 100); // Show first 100 chars

		LoggerUtil.info(
				"🔄 {0} - Progress[{1}ms]: {2} responses received, {3} characters, {4} tool calls. Preview: '{5}'",
				contextName, System.currentTimeMillis() - startTime, responseCount, textLength, toolCallCount, preview);
	}

	private void logCompletion(String contextName, String finalText, int toolCallCount, int responseCount,
			long startTime, Usage usage) {
		int textLength = finalText != null ? finalText.length() : 0;
		String preview = getTextPreview(finalText, 200); // Show first 200 chars for
		// completion

		LoggerUtil.info(
				"✅ {0} - Completed[{1}ms]: {2} responses processed, {3} characters, {4} tool calls, {5} prompt tokens, "
						+ "{6} completion tokens, {7} total tokens. Preview: '{8}'",
				contextName, System.currentTimeMillis() - startTime, responseCount, textLength, toolCallCount,
				usage.getPromptTokens(), usage.getCompletionTokens(), usage.getTotalTokens(), preview);
	}

	private String getTextPreview(String text, int maxLength) {
		if (text == null || text.isEmpty()) {
			return "(empty)";
		}
		if (text.length() <= maxLength) {
			return text;
		}
		return text.substring(0, maxLength) + "...";
	}

}
