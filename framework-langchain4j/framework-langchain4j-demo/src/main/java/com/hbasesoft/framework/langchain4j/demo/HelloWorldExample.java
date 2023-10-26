/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.langchain4j.demo;

import com.hbasesoft.framework.common.utils.PropertyHolder;
import com.hbasesoft.framework.langchain4j.dashscope.QwenChatModel;

import dev.langchain4j.model.chat.ChatLanguageModel;

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
public class HelloWorldExample {

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param args <br>
     */
    public static void main(final String[] args) {
        // Create an instance of a model
        ChatLanguageModel model = QwenChatModel.builder().apiKey(PropertyHolder.getProperty("qwen.apikey")).build();

        // Start interacting
        String answer = model.generate("Hello world!");

        System.out.println(answer); // Hello! How can I assist you today?
    }
}
