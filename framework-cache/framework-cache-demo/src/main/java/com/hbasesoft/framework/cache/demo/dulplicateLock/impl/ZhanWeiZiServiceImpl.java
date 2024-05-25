/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.cache.demo.dulplicateLock.impl;

import java.util.Arrays;

import org.springframework.stereotype.Service;

import com.hbasesoft.framework.cache.core.annotation.DulplicateLock;
import com.hbasesoft.framework.cache.demo.dulplicateLock.ZhanWeiZiService;
import com.hbasesoft.framework.common.GlobalConstants;
import com.hbasesoft.framework.common.annotation.Key;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2018年3月23日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.test.dulplicateLock.impl <br>
 */
@Service
public class ZhanWeiZiServiceImpl implements ZhanWeiZiService {

    /** MAX_SITS */
    private static final int MAX_SITS = 10;

    /** seats */
    private String[] seats = new String[MAX_SITS];

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param no
     * @param name <br>
     */
    @Override
    @DulplicateLock(name = "seats", key = "${no}", expireTime = GlobalConstants.SECONDS)
    public void rob(final @Key("no") int no, final String name) {
        if (no >= 0 && no < MAX_SITS) {
            if (seats[no] != null) {
                System.out.println("我操" + no + "位子被" + name + "抢了");
            }
            seats[no] = name;
        }
        else {
            System.out.println("瞎抢什么，看清楚位子再强");
        }
        System.out.println(Arrays.toString(seats));
    }

}
