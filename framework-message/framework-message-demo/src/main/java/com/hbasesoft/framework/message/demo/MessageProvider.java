/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.message.demo;

import com.hbasesoft.framework.message.core.MessageHelper;
import com.hbasesoft.framework.message.core.MessagePublisher;

/**
 * <Description> <br>
 *
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2017年2月19日 <br>
 * @see com.hbasesoft.test.message <br>
 * @since V1.0<br>
 */
public class MessageProvider {

    /** max count */
    private static final int MAX_COUNT = 100000;

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param args
     * @throws InterruptedException <br>
     */
    public static void main(final String[] args) throws InterruptedException {
        MessagePublisher publisher = MessageHelper.createMessagePublisher();
        int i = 0;
        while (i < MAX_COUNT) {
            ++i;
            // publisher.publish("log-p21", (i + "").getBytes());
            // publisher.publish("testEvent", SerializationUtil.serial(data), MessagePublisher.PUBLISH_TYPE_ORDERLY);
            publisher.publish("log-p15", (i + "").getBytes());

            // if (i % 5 == 0) {
            // publisher.publish("log-p2", (++i + "").getBytes());
            // }
            System.out.println("produce " + i);
        }
    }

}
