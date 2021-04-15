/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framewor2.log.demo;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2021年4月15日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framewor2.log.demo <br>
 */
@RestController
public class TestController {

    @Resource
    private TestService testService;

    @GetMapping("/{param}")
    public String test(@PathVariable("param") String param) {
        System.out.println("TestController:" + param);
        return testService.test(param);
    }
}
