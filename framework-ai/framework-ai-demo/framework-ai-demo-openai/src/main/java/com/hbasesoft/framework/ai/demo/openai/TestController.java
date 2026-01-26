/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.demo.openai;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.cloud.ai.graph.agent.ReactAgent;

import jakarta.annotation.Resource;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年12月28日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.demo.openai <br>
 */
@RequestMapping("/test")
@RestController
public class TestController {

    /** agent */
    @Resource(name = "TestAgent")
    private ReactAgent agent;

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param text
     * @return <br>
     */
    @GetMapping("/say")
    public String say(final @RequestParam("text") @NonNull String text) {
        try {
            UserMessage userMessage = new UserMessage(text);
            AssistantMessage response = agent.call(userMessage);
            return response.getText();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
