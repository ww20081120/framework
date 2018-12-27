/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.message.demo.event;

import org.springframework.stereotype.Service;

import com.hbasesoft.framework.message.core.event.EventData;
import com.hbasesoft.framework.message.core.event.EventEmmiter;
import com.hbasesoft.framework.message.core.event.EventLinsener;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2017年2月24日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.test.event <br>
 */
@Service
public class TestEventHandler implements EventLinsener {

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    @Override
    public String[] events() {
        return new String[] {
            "testEvent"
        };
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param event
     * @param data <br>
     */
    @Override
    public void onEmmit(String event, EventData data) {
        System.out.println(event + ":" + data);
        try {
            Thread.sleep(1500);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        for (int i = 0; i < 10000; i++) {

            EventData data = new EventData();
            data.put("key", "value" + i);
            EventEmmiter.emmit("testEvent", data);
        }
    }

}
