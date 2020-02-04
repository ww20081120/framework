/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.tx.demo.client;

import java.util.Random;

import org.springframework.stereotype.Component;

import com.hbasesoft.framework.message.core.event.EventData;
import com.hbasesoft.framework.message.tx.TxEventLinsener;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate Feb 4, 2020 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.tx.demo.client <br>
 */
@Component
public class TestEventLinsener implements TxEventLinsener {

    private int i = 0;

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

        if (new Random().nextInt(5) == 1) {
            throw new RuntimeException();
        }
        System.out.println(i++ + ":" + data.getMsgId());

    }

}
