/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framewor2.log.demo;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

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
@Service
public class TestServiceImpl implements TestService {

    @Resource
    private TestHandler TestHandler;

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param test
     * @return <br>
     */
    @Override
    public String test(String test) {
        System.out.println("TestService:" + test);
        return TestHandler.test(test);
    }

}
