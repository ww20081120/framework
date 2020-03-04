/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.message.demo.delay;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.junit4.SpringRunner;

import com.hbasesoft.framework.common.Bootstrap;
import com.hbasesoft.framework.message.core.MessageHelper;
import com.hbasesoft.framework.message.core.MessagePublisher;
import com.hbasesoft.framework.message.demo.Application;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2019年4月10日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.message.demo.delay <br>
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DelayMessageProviderTest implements ApplicationContextAware {

    /** number */
    private static final int NUM_3 = 3;

    /** number */
    private static final int NUM_30 = 30;

    /** number */
    private static final int NUM_60 = 60;

    /** number */
    private static final int NUM_120 = 120;

    /** number */
    private static final int NUM_800 = 800;

    /** number */
    private static final long MAX_TIME = 10000000L;

    private ApplicationContext applicationContext;

    @Before
    public void init() {
        Bootstrap.before();
        Bootstrap.after(applicationContext);
    }

    @Test
    public void testDelayMessage() throws InterruptedException {
        MessagePublisher publisher = MessageHelper.createMessagePublisher();

        publisher.publish("log-p15", ("立即发送").getBytes());

        for (int i = 1; i < NUM_800; i++) {
            if (i <= NUM_30) {
                publisher.publish("log-p15", (i + "秒发送数据").getBytes(), i);
            }
            else if (i <= NUM_60) {
                if (i % 2 == 0) {
                    publisher.publish("log-p15", (i + "秒发送数据").getBytes(), i);
                }
            }
            else if (i <= NUM_120) {
                if (i % NUM_30 == 1) {
                    publisher.publish("log-p15", (i + "秒发送数据").getBytes(), i);
                }
            }
            else {
                if (i % NUM_60 == NUM_3) {
                    publisher.publish("log-p15", (i + "秒发送数据").getBytes(), i);
                }
            }
        }
        Thread.sleep(MAX_TIME);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param applicationContext
     * @throws BeansException <br>
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
