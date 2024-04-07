/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.common.utils.engine;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.utils.Assert;
import com.hbasesoft.framework.common.utils.CommonUtil;
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
public class JavaScriptUtilTest {

    /** number */
    private static final int NUM_18 = 18;

    /** number */
    private static final int NUM_20 = 20;

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Test
    public void eval() {
        String script = "1+1";
        Double result = Double.valueOf(CommonUtil.getString(JavaScriptUtil.eval(script, null)));
        Assert.isTrue(result - 2 == 0, ErrorCodeDef.SYSTEM_ERROR);

        TestBean bean = new TestBean("张三", NUM_18);
        Map<String, Object> params = new HashMap<>();
        params.put("b", bean);

        script = "b.getAge() + 2";
        result = Double.valueOf(CommonUtil.getString(JavaScriptUtil.eval(script, params)));
        Assert.isTrue(result - NUM_20 == 0, ErrorCodeDef.SYSTEM_ERROR);
    }

}
