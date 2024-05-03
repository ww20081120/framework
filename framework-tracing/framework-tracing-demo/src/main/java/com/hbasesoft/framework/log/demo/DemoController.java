/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.log.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate Jul 13, 2020 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.log.demo <br>
 */
@RequestMapping
@RestController
public class DemoController {

    /** */
    @Resource
    private DemoService demoService;

    /**
     * @Method say
     * @param name
     * @return java.lang.String
     * @Author 李煜龙
     * @Description TODD
     * @Date 2023/1/29 10:54
     */
    @GetMapping("/say")
    public String say(final @RequestParam("name") String name) {
        return demoService.say(name);
    }
}
