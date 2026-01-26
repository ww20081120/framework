/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.demo.agentscope;

import org.springframework.boot.CommandLineRunner;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.agentscope.core.ReActAgent;
import io.agentscope.core.message.Msg;
import io.agentscope.core.studio.StudioManager;
import io.agentscope.core.studio.StudioUserAgent;
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
public class TestController implements CommandLineRunner {

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

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param args
     * @throws Exception <br>
     */
    @Override
    public void run(final String... args) throws Exception {
        try {
            // 创建用户 Agent
            StudioUserAgent user = StudioUserAgent.builder().name("User").studioClient(StudioManager.getClient())
                .webSocketClient(StudioManager.getWebSocketClient()).build();

            // 对话循环
            System.out.println("Starting conversation (type 'exit' to quit)");
            System.out.println("Open http://localhost:3000 to interact\n");
            Msg msg = null;
            int turn = 1;
            while (true) {
                System.out.println("[Turn " + turn + "] Waiting for user input...");
                msg = user.call(msg).block();

                if (msg == null || "exit".equalsIgnoreCase(msg.getTextContent())) {
                    System.out.println("\nConversation ended");
                    break;
                }

                System.out.println("[Turn " + turn + "] User: " + msg.getTextContent());
                msg = agent.call(msg).block();

                if (msg != null) {
                    System.out.println("[Turn " + turn + "] Agent: " + msg.getTextContent() + "\n");
                }
                turn++;
            }
        }
        finally {
            System.out.println("\nShutting down...");
            StudioManager.shutdown();
            System.out.println("Done\n");
        }
    }
}
