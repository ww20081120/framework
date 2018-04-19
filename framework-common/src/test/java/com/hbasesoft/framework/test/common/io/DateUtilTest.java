/**************************************************************************************** 
22 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.test.common.io;

import org.junit.Test;

import com.hbasesoft.framework.common.utils.date.DateConstants;
import com.hbasesoft.framework.common.utils.date.DateUtil;

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
public class DateUtilTest {

    @Test
    public void string2Date() {
        System.out.println(DateUtil.string2Date("2012-08-15", DateConstants.DATE_FORMAT_10));
    }

    @Test
    public void date2String() {
        System.out.println(DateUtil.date2String(DateUtil.string2Date("2012/08/15", DateConstants.DATE_FORMAT_10_2),
            DateConstants.DATE_FORMAT_10));
    }
}
