/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.test.common.io;

import org.junit.Test;

import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.utils.PropertyHolder;

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
public class ErrorMessageTest {

    @Test
    public void testMessage() {
        System.out.println(PropertyHolder.getErrorMessage(ErrorCodeDef.CAN_NOT_FIND_VER_NAME_10003, "张三丰"));
    }
}
