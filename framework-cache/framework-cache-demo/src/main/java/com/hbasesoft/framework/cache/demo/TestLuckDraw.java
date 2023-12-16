/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.cache.demo;

import javax.annotation.Resource;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.hbasesoft.framework.cache.demo.lock.LuckDrawService;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2017年1月4日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.test <br>
 */

@SpringBootTest
public class TestLuckDraw {

    /** MAX_SIZE */
    private static final int MAX_SIZE = 10;

    /** MAX_TIMES */
    private static final int MAX_TIMES = 100;

    /** SLEEP_TIME */
    private static final int MAX_SLEEP_TIME = 100000;

    /** luckDrawService */
    @Resource
    private LuckDrawService luckDrawService;

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @throws InterruptedException <br>
     */
    @Test
    public void testLock() throws InterruptedException {

        for (int i = 0; i < MAX_SIZE; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int j = 0; j < MAX_TIMES; j++) {
                        luckDrawService.luckDraw("1", "xxxx");
                    }
                }
            }).start();
        }
        Thread.sleep(MAX_SLEEP_TIME);

    }
}
