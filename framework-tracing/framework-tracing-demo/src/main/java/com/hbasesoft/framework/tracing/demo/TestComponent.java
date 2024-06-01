/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.tracing.demo;

import org.springframework.stereotype.Component;

import com.hbasesoft.framework.common.utils.logger.LoggerUtil;

import jakarta.annotation.Resource;

/**
 * <Description> <br>
 * 
 * @author ww200<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2024年6月1日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.tracing.demo <br>
 */
@Component
public class TestComponent {

    /** */
    @Resource
    private TestComponent2 testComponent2;

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param value <br>
     */
    public void test(final String value) {
        LoggerUtil.info("test {0}", value);
        testComponent2.test(value);
    }
}
