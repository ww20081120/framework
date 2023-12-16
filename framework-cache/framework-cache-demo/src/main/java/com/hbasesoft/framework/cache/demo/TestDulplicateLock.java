/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.cache.demo;

import java.util.Random;

import javax.annotation.Resource;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.hbasesoft.framework.cache.demo.dulplicateLock.ZhanWeiZiService;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2018年3月23日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.test.dulplicateLock <br>
 */
@SpringBootTest
public class TestDulplicateLock {

    /** MAX_SIZE */
    private static final int MAX_SIZE = 10;

    /** MAX_TIMES */
    private static final int MAX_TIMES = 1000;

    /** SLEEP_TIME */
    private static final int SLEEP_TIME = 10;

    /** SLEEP_TIME */
    private static final int MAX_SLEEP_TIME = 100000;

    /** zhanWeiZiService */
    @Resource
    private ZhanWeiZiService zhanWeiZiService;

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @throws InterruptedException <br>
     */
    @Test
    public void zhanwei() throws InterruptedException {
        for (int i = 0; i < MAX_SIZE; i++) {
            new Thread(() -> {
                Random random = new Random();
                try {
                    for (int j = 0; j < MAX_TIMES; j++) {
                        try {
                            zhanWeiZiService.rob(random.nextInt(SLEEP_TIME),
                                Thread.currentThread().getName() + "_" + j);
                        }
                        catch (Exception e) {
                        }
                        Thread.sleep(SLEEP_TIME);
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }

        Thread.sleep(MAX_SLEEP_TIME);
    }

}
