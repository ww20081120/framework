/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.langchain4j.dashscope;

import static com.alibaba.dashscope.common.Role.ASSISTANT;
import static com.alibaba.dashscope.common.Role.SYSTEM;
import static com.alibaba.dashscope.common.Role.USER;
import static dev.langchain4j.model.output.FinishReason.LENGTH;
import static dev.langchain4j.model.output.FinishReason.STOP;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.alibaba.dashscope.aigc.generation.GenerationOutput;
import com.alibaba.dashscope.aigc.generation.GenerationOutput.Choice;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.hbasesoft.framework.common.GlobalConstants;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.model.output.FinishReason;
import dev.langchain4j.model.output.TokenUsage;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2023年10月25日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.langchain4j.dashscope <br>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class QwenHelper {

    /**
     * Description: 转成通义千问的api<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param messages
     * @return <br>
     */
    public static List<Message> toQwenMessages(final List<ChatMessage> messages) {
        return messages.stream().map(QwenHelper::toQwenMessage).collect(Collectors.toList());
    }

    /**
     * Description: 转成通义千问的api <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param message
     * @return <br>
     */
    public static Message toQwenMessage(final ChatMessage message) {
        return Message.builder().role(roleFrom(message)).content(message.text()).build();
    }

    /**
     * Description: 角色转化<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param message
     * @return <br>
     */
    public static String roleFrom(final ChatMessage message) {
        if (message instanceof AiMessage) {
            return ASSISTANT.getValue();
        }
        else if (message instanceof SystemMessage) {
            return SYSTEM.getValue();
        }
        else {
            return USER.getValue();
        }
    }

    /**
     * Description: 结果转化<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param result
     * @return <br>
     */
    public static String answerFrom(final GenerationResult result) {
        return Optional.of(result).map(GenerationResult::getOutput).map(GenerationOutput::getChoices)
            .filter(choices -> !choices.isEmpty()).map(choices -> choices.get(0)).map(Choice::getMessage)
            .map(Message::getContent)
            // Compatible with some older models.
            .orElseGet(() -> Optional.of(result).map(GenerationResult::getOutput).map(GenerationOutput::getText)
                .orElse(GlobalConstants.BLANK));
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param result
     * @return <br>
     */
    public static TokenUsage tokenUsageFrom(final GenerationResult result) {
        return Optional.of(result).map(GenerationResult::getUsage)
            .map(usage -> new TokenUsage(usage.getInputTokens(), usage.getOutputTokens()))
            .orElse(new TokenUsage(null, null));
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param result
     * @return <br>
     */
    public static FinishReason finishReasonFrom(final GenerationResult result) {
        String finishReason = Optional.of(result).map(GenerationResult::getOutput).map(GenerationOutput::getChoices)
            .filter(choices -> !choices.isEmpty()).map(choices -> choices.get(0)).map(Choice::getFinishReason)
            .orElse("");

        switch (finishReason) {
            case "stop":
                return STOP;
            case "length":
                return LENGTH;
            default:
                return null;
        }
    }

}
