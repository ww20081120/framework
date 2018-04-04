/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.db.core.utils;

import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.GlobalConstants;
import com.hbasesoft.framework.common.utils.Assert;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2018年4月4日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.db.core.utils <br>
 */
public final class SQlCheckUtil {

    public static void checkSql(String sql) {
        Assert.isTrue(sql.indexOf(GlobalConstants.ASTERISK) == -1, ErrorCodeDef.UNSUUPORT_ASTERISK);
    }
}
