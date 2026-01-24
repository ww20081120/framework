/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.demo.agentscope;

import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.agentscope.core.ReActAgent;
import io.agentscope.core.message.Msg;
import jakarta.annotation.Resource;
import reactor.core.publisher.Mono;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2026年1月21日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.demo.ollama <br>
 */
@RequestMapping("/test")
@RestController
public class TestController {

    /** Agent代理实例 */
    @Resource(name = "TestAgent")
    private ReActAgent agent;

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
            Mono<Msg> resp = agent.call(Msg.builder().name("user").textContent(text).build());
            return resp.block().getTextContent();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
