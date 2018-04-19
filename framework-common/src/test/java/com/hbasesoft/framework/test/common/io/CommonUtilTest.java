/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.test.common.io;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import com.hbasesoft.framework.common.utils.CommonUtil;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2018年4月19日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.test.common.io <br>
 */
public class CommonUtilTest {

    @Test
    public void testEmpty() {
        StringUtils.isNotEmpty("12341");
        CollectionUtils.isNotEmpty(new ArrayList<>());
        MapUtils.isNotEmpty(new HashMap<>());
    }

    @Test
    public void match() {
        String a = "NOT:10,100,110";
        String b = "101";
        System.out.println(CommonUtil.match(a, b));
    }
}
