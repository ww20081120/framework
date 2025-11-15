/*
 * Copyright 2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hbasesoft.framework.ai.agent.llm;

import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;

import com.hbasesoft.framework.ai.agent.planning.model.vo.ExecutionContext;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;

import reactor.core.publisher.Flux;

/**
 * 非阻塞流式处理器，提供真正的流式响应而不阻塞主线程
 * 
 * @author 王伟
 * @version 1.0
 * @since 2025年10月14日
 */
public class NonBlockingStreamingHandler {

    /**
     * 处理流式文本响应，返回Flux<String>用于非阻塞处理
     * 
     * @param responseFlux 流式响应Flux
     * @param contextName 上下文名称（用于日志）
     * @param context 执行上下文（用于通知监听器）
     * @param responseType 响应类型
     * @return 流式文本Flux
     */
    public Flux<String> processStreamingTextResponseNonBlocking(
            Flux<ChatResponse> responseFlux, 
            String contextName, 
            ExecutionContext context,
            String responseType) {
        
        AtomicReference<StringBuilder> textBuilder = new AtomicReference<>(new StringBuilder());
        
        return responseFlux
            .doOnSubscribe(subscription -> {
                LoggerUtil.info("🚀 开始流式处理 - {0} (响应类型: {1})", contextName, responseType);
                context.notifyStatusChange("开始" + contextName);
            })
            .doOnNext(chatResponse -> {
                if (chatResponse != null && chatResponse.getResult() != null 
                    && chatResponse.getResult().getOutput() != null) {
                    
                    String textChunk = chatResponse.getResult().getOutput().getText();
                    if (StringUtils.isNotEmpty(textChunk)) {
                        // 累积文本
                        textBuilder.get().append(textChunk);
                        
                        // 通知监听器流式响应
                        context.notifyLlmResponseStream(textChunk, responseType);
                        
                        // 如果是总结类型，同时通知总结流
                        if ("SUMMARY".equals(responseType)) {
                            context.notifySummaryStream(textChunk);
                        }
                        
                        LoggerUtil.debug("📝 流式片段 - {0}: {1}", contextName, textChunk);
                    }
                }
            })
            .doOnComplete(() -> {
                String fullText = textBuilder.get().toString();
                LoggerUtil.info("✅ 流式处理完成 - {0}, 总长度: {1} 字符", contextName, fullText.length());
                context.notifyStatusChange(contextName + "完成");
            })
            .doOnError(error -> {
                LoggerUtil.error("❌ 流式处理错误 - {0}: {1}", contextName, error.getMessage(), error);
                context.notifyError(error instanceof Exception ? (Exception) error : new RuntimeException(error));
            })
            .map(chatResponse -> {
                if (chatResponse != null && chatResponse.getResult() != null 
                    && chatResponse.getResult().getOutput() != null) {
                    String text = chatResponse.getResult().getOutput().getText();
                    return text != null ? text : "";
                }
                return "";
            })
            .filter(StringUtils::isNotEmpty);
    }

    /**
     * 处理流式文本响应并返回完整文本（阻塞方式，但保持监听器通知）
     * 
     * @param responseFlux 流式响应Flux
     * @param contextName 上下文名称
     * @param context 执行上下文
     * @param responseType 响应类型
     * @return 完整文本
     */
    public String processStreamingTextResponseWithListener(
            Flux<ChatResponse> responseFlux, 
            String contextName, 
            ExecutionContext context,
            String responseType) {
        
        AtomicReference<StringBuilder> textBuilder = new AtomicReference<>(new StringBuilder());
        
        try {
            responseFlux
                .doOnSubscribe(subscription -> {
                    LoggerUtil.info("🚀 开始流式处理 - {0} (响应类型: {1})", contextName, responseType);
                    context.notifyStatusChange("开始" + contextName);
                })
                .doOnNext(chatResponse -> {
                    if (chatResponse != null && chatResponse.getResult() != null 
                        && chatResponse.getResult().getOutput() != null) {
                        
                        String textChunk = chatResponse.getResult().getOutput().getText();
                        if (StringUtils.isNotEmpty(textChunk)) {
                            // 累积文本
                            textBuilder.get().append(textChunk);
                            
                            // 通知监听器流式响应
                            context.notifyLlmResponseStream(textChunk, responseType);
                            
                            // 如果是总结类型，同时通知总结流
                            if ("SUMMARY".equals(responseType)) {
                                context.notifySummaryStream(textChunk);
                            }
                            
                            LoggerUtil.debug("📝 流式片段 - {0}: {1}", contextName, textChunk);
                        }
                    }
                })
                .doOnComplete(() -> {
                    String fullText = textBuilder.get().toString();
                    LoggerUtil.info("✅ 流式处理完成 - {0}, 总长度: {1} 字符", contextName, fullText.length());
                    context.notifyStatusChange(contextName + "完成");
                })
                .doOnError(error -> {
                    LoggerUtil.error("❌ 流式处理错误 - {0}: {1}", contextName, error.getMessage(), error);
                    context.notifyError(error instanceof Exception ? (Exception) error : new RuntimeException(error));
                })
                .blockLast();
                
            return textBuilder.get().toString();
        } catch (Exception e) {
            LoggerUtil.error("流式处理异常 - {0}: {1}", contextName, e.getMessage(), e);
            context.notifyError(e);
            throw new RuntimeException("流式处理失败: " + e.getMessage(), e);
        }
    }

    /**
     * 创建一个简单的Flux用于测试
     * 
     * @param text 文本内容
     * @param chunkSize 分块大小
     * @return 流式Flux
     */
    public Flux<String> createTestFlux(String text, int chunkSize) {
        if (text == null || text.isEmpty()) {
            return Flux.empty();
        }
        
        return Flux.create(emitter -> {
            for (int i = 0; i < text.length(); i += chunkSize) {
                int end = Math.min(i + chunkSize, text.length());
                String chunk = text.substring(i, end);
                emitter.next(chunk);
                
                // 模拟网络延迟
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    emitter.error(e);
                    return;
                }
            }
            emitter.complete();
        });
    }
}
