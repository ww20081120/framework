/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.cache.demo;

import com.hbasesoft.framework.cache.demo.dulplicateLock.ZhanWeiZiService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Random;

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
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestDulplicateLock {

    @Resource
    private ZhanWeiZiService zhanWeiZiService;

    @Test
    public void zhanwei() throws InterruptedException {
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                Random random = new Random();
                try {
                    for (int j = 0; j < 1000; j++) {
                        try {
                            zhanWeiZiService.rob(random.nextInt(10), Thread.currentThread().getName() + "_" + j);
                        }
                        catch (Exception e) {
                        }
                        Thread.sleep(10);
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }

        Thread.sleep(100000);
    }

}
