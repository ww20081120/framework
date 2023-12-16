/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.cache.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.hbasesoft.framework.cache.demo.service.TestService;
import com.hbasesoft.framework.common.GlobalConstants;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2016年10月10日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.test.cache <br>
 */
@SpringBootTest
public class TestCache {

    /** MAX_SIZE */
    private static final int MAX_SIZE = 20;

    /** testService */
    @Autowired
    private TestService testService;

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @throws InterruptedException <br>
     */
    @Test
    public void testCache() throws InterruptedException {
        for (int i = 0; i < MAX_SIZE; i++) {
            System.out.println(testService.getTestContent("test"));
            Thread.sleep(GlobalConstants.SECONDS);
        }
    }

}
