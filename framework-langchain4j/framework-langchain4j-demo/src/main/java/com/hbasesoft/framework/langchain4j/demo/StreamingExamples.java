/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.langchain4j.demo;

import static dev.langchain4j.data.message.SystemMessage.systemMessage;
import static dev.langchain4j.data.message.UserMessage.userMessage;
import static java.util.Arrays.asList;

import java.util.List;

import com.hbasesoft.framework.common.utils.PropertyHolder;
import com.hbasesoft.framework.langchain4j.dashscope.QwenStreamingChatModel;
import com.hbasesoft.framework.langchain4j.dashscope.QwenStreamingLanguageModel;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.model.StreamingResponseHandler;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.language.StreamingLanguageModel;
import dev.langchain4j.model.output.Response;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2023年10月26日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.langchain4j.demo <br>
 */
public class StreamingExamples {

    /**
     * <Description> <br>
     * 
     * @author 王伟<br>
     * @version 1.0<br>
     * @taskId <br>
     * @CreateDate 2023年10月26日 <br>
     * @since V1.0<br>
     * @see com.hbasesoft.framework.langchain4j.demo <br>
     */
    static class StreamingChatLanguageModelExample {

        /**
         * Description: <br>
         * 
         * @author 王伟<br>
         * @taskId <br>
         * @param args <br>
         */
        public static void main(final String[] args) {

            // Sorry, "demo" API key does not support streaming (yet). Please use your own key.
            StreamingChatLanguageModel model = QwenStreamingChatModel.builder()
                .apiKey(PropertyHolder.getProperty("qwen.apikey")).build();

            List<ChatMessage> messages = asList(systemMessage("You are a very sarcastic assistant"),
                userMessage("请给我讲一个笑话"));

            model.generate(messages, new StreamingResponseHandler<AiMessage>() {

                @Override
                public void onNext(final String token) {
                    System.out.print(token);
                }

                @Override
                public void onComplete(final Response<AiMessage> response) {
                    System.out.println("\n" + Thread.currentThread().getName() + "Streaming completed: " + response);
                }

                @Override
                public void onError(final Throwable error) {
                    error.printStackTrace();
                }
            });
            System.out.println(Thread.currentThread().getName() + "1111");
        }
    }

    /**
     * <Description> <br>
     * 
     * @author 王伟<br>
     * @version 1.0<br>
     * @taskId <br>
     * @CreateDate 2023年10月26日 <br>
     * @since V1.0<br>
     * @see com.hbasesoft.framework.langchain4j.demo <br>
     */
    public static class StreamingLanguageModelExample {

        /**
         * Description: <br>
         * 
         * @author 王伟<br>
         * @taskId <br>
         * @param args <br>
         */
        public static void main(final String[] args) {

            // Sorry, "demo" API key does not support streaming (yet). Please use your own key.
            StreamingLanguageModel model = QwenStreamingLanguageModel.builder()
                .apiKey(PropertyHolder.getProperty("qwen.apikey")).build();

            model.generate("Tell me a joke", new StreamingResponseHandler<String>() {

                @Override
                public void onNext(final String token) {
                    System.out.println("New token: '" + token + "'");
                }

                @Override
                public void onComplete(final Response<String> response) {
                    System.out.println("Streaming completed: " + response);
                }

                @Override
                public void onError(final Throwable error) {
                    error.printStackTrace();
                }
            });
        }
    }
}
