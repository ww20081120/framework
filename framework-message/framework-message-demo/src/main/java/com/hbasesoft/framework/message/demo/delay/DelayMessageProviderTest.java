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
        
        for (int i = 1; i < 800; i++) {
            if (i <= 30) {
                publisher.publish("log-p15", (i + "秒发送数据").getBytes(), i);
            }
            else if (i <= 60) {
                if (i % 2 == 0) {
                    publisher.publish("log-p15", (i + "秒发送数据").getBytes(), i);
                }
            }
            else if (i <= 120) {
                if (i % 30 == 1) {
                    publisher.publish("log-p15", (i + "秒发送数据").getBytes(), i);
                }
            }
            else {
                if (i % 60 == 3) {
                    publisher.publish("log-p15", (i + "秒发送数据").getBytes(), i);
                }
            }
        }
        Thread.sleep(10000000l);
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
