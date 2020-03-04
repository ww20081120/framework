/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.message.demo;

import com.hbasesoft.framework.message.core.MessageHelper;
import com.hbasesoft.framework.message.core.MessageSubcriberFactory;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2017年2月19日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.test.message <br>
 */
public class MessageConsummer {

    /** Number */
    private static final int NUM_1000000 = 1000000;

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param args
     * @throws InterruptedException <br>
     */
    public static void main(final String[] args) throws InterruptedException {
        MessageSubcriberFactory factory = MessageHelper.createMessageSubcriberFactory();
        factory.registSubscriber("log-p15", false, new MessageLinsener());
        // factory.registSubscriber("log-p15", true, new MessageLinsener());
        // factory.registSubscriber("log-p21", true, new MessageLinsener());
        Thread.sleep(NUM_1000000);
        System.out.println("消费者者启动完成！");
    }
}
