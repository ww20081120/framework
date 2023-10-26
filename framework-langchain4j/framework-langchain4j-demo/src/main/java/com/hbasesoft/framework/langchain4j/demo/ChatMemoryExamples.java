/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.langchain4j.demo;

import java.io.IOException;

import com.hbasesoft.framework.common.utils.PropertyHolder;
import com.hbasesoft.framework.langchain4j.dashscope.QwenChatModel;

import dev.langchain4j.chain.ConversationalChain;

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
public class ChatMemoryExamples {

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param args
     * @throws IOException <br>
     */
    public static void main(final String[] args) throws IOException {
        ConversationalChain chain = ConversationalChain.builder()
            .chatLanguageModel(QwenChatModel.builder().apiKey(PropertyHolder.getProperty("qwen.apikey")).build())
            // .chatMemory() // you can override default chat memory
            .build();

        String answer = chain.execute("Hello, my name is Klaus");
        System.out.println(answer); // Hello Klaus! How can I assist you today?

        String answerWithName = chain.execute("What is my name?");
        System.out.println(answerWithName); // Your name is Klaus.
    }
}
