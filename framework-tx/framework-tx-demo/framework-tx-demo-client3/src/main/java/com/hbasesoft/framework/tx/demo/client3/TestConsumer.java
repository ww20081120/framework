/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.tx.demo.client3;

import java.util.Random;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    private static final int NUM_7 = 7;

    /** Number */
    private static final int NUM_10 = 10;

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
    public synchronized String test(@RequestParam("id") String id) {
        i++;
        if (new Random().nextInt(NUM_10) == NUM_7) {
            throw new RuntimeException();
        }
        System.out.println(i + ":" + id);
        return i + "client3" + id;
    }
}
