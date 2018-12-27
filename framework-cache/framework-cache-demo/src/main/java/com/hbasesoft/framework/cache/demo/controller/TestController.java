/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.cache.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hbasesoft.framework.cache.core.annotation.CacheMethodConfig;
import com.hbasesoft.framework.cache.core.annotation.CacheProxy;
import com.hbasesoft.framework.cache.demo.service.TestService;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2016年11月26日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.test.cache.controller <br>
 */
@Controller
public class TestController {

    private TestService testService;

    @RequestMapping(value = "/test")
    @ResponseBody
    public String say(String name) {
        return testService.getTestContent(name);
    }

    public TestService getTestService() {
        return testService;
    }

    @CacheProxy(expireTime = 10, value = {
        @CacheMethodConfig("getTestContent")
    })
    public void setTestService(TestService testService) {
        this.testService = testService;
    }

}
