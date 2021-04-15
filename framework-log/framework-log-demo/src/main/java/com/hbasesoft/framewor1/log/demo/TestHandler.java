/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framewor1.log.demo;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.hbasesoft.framewor1.log.demo.client.TestRemoteService;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2021年4月15日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framewor1.log.demo <br>
 */
@Component
public class TestHandler {

    @Resource
    private TestRemoteService testRemoteService;

    public String test(String param) {
        System.out.println("TestHandler:" + param);
        return testRemoteService.test(param);
    }
}
