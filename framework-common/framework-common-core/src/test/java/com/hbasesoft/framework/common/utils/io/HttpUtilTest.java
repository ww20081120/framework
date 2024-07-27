/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.common.utils.io;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2017年9月22日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.common <br>
 */
public class HttpUtilTest {

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Test
    public void doGet() {
        System.out.println(HttpUtil.doGet("https://www.towngasvcc.com"));
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Test
    public void doPost() {
        Map<String, String> param = new HashMap<>();
        System.out.println(HttpUtil.doPost("http://www.baidu.com", param));
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Test
    public void doGetDowloadFile() {
        HttpUtil.downloadFile("https://www.baidu.com/img/"
            + "PCfb_5bf082d29588c07f842ccde3f97243ea.png", "target/a2.jpg");
    }
}
