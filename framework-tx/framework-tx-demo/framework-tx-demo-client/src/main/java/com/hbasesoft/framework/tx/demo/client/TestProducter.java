/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.tx.demo.client;

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
public class TestProducter {

    @Resource
    private FeginClient2Consumer feClient2Consumer;

    @Resource
    private FeginClient3Consumer feClient3Consumer;

    private int i = 0;

    @GetMapping
    @Tx
    public synchronized String test(@RequestParam("id") String id) {
        String value1 = TxInvokerProxy.invoke("client2", () -> {
            return feClient2Consumer.test(id);
        });
        System.out.println(value1);

        String value2 = TxInvokerProxy.invoke("client3", () -> {
            return feClient3Consumer.test(id);
        });
        System.out.println(value2);

        if (new Random().nextInt(5) == 1) {
            throw new RuntimeException();
        }
        System.out.println(i++ + ":" + id);

        return new StringBuilder().append(i).append("client1").append(id).append(':').append(value1).append(':')
            .append(value2).toString();
    }
}
