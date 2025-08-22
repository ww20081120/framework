/****************************************************************************************
Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
****************************************************************************************/
package com.hbasesoft.framework.ai.demo.test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import com.hbasesoft.framework.ai.demo.AIDemoApplication;

/**
 * <Description> <br>
 *
 * @author wangwei<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年6月25日 <br>
 * @since V1.0<br>
 * @see <br>
 */
@AutoConfigureMockMvc
@SpringBootTest(classes = AIDemoApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HelloworldControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testSimpleChat() throws Exception {
        // 模拟输入和响应
        String input = "hello world";
        // 发送 GET 请求并验证响应
        mockMvc.perform(get("/simple/helloworld/chat").param("input", input)).andExpect(status().isOk());
    }
}
