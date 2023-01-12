/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.common.utils.engine;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.utils.Assert;
import com.hbasesoft.framework.common.utils.bean.TestBean;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2018年9月13日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.common.utils.engine <br>
 */
public class VelocityParseFactoryTest {

    /** number */
    private static final int NUM_18 = 18;

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Test
    public void parse() {
        TestBean bean = new TestBean("张三", NUM_18);
        Map<String, Object> params = new HashMap<>();
        params.put("b", bean);

        String template = "你好，我的名字叫${b.name}";
        String str = VelocityParseFactory.parse("template01", template, params);
        Assert.equals(str, "你好，我的名字叫张三", ErrorCodeDef.SYSTEM_ERROR);
    }
}
