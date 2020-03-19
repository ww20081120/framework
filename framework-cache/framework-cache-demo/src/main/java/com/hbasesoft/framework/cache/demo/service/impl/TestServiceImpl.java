/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.cache.demo.service.impl;

import org.springframework.stereotype.Service;

import com.hbasesoft.framework.cache.demo.service.TestService;
import com.hbasesoft.framework.common.utils.date.DateUtil;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2016年10月10日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.test.cache.service.impl <br>
 */
@Service
public class TestServiceImpl implements TestService {

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param key
     * @return <br>
     */
    public String getTestContent(final String key) {
        System.out.println("test");
        return DateUtil.getCurrentTimestamp() + ":" + key;
    }

}
