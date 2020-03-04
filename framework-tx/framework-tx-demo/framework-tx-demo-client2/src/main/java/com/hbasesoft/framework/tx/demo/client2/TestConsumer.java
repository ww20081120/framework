/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.tx.demo.client2;

import java.util.Random;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hbasesoft.framework.tx.core.TxInvokerProxy;
import com.hbasesoft.framework.tx.core.annotation.Tx;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate Feb 1, 2020 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.tx.demo.client <br>
 */
@RestController
public class TestConsumer {

    /** Number */
    private static final int NUM_3 = 3;

    /** Number */
    private static final int NUM_9 = 9;

    /** fe */
    @Resource
    private FeginClient3Consumer feClient3Consumer;

    /** index */
    private int i = 0;

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param id
     * @return <br>
     */
    @GetMapping
    @Tx
    public synchronized String test(@RequestParam("id") String id) {
        i++;

        String value1 = TxInvokerProxy.invoke("client4", () -> {
            return feClient3Consumer.test(id);
        });
        System.out.println(value1);

        if (new Random().nextInt(NUM_9) == NUM_3) {
            throw new RuntimeException();
        }
        System.out.println(i + ":" + id);

        return i + "client2" + id;
    }
}
