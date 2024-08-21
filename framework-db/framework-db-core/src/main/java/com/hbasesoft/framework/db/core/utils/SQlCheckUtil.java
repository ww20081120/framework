/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.db.core.utils;

import org.apache.commons.lang3.StringUtils;

import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.utils.Assert;
import com.hbasesoft.framework.db.core.DaoConstants;

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

    /** asterisk */
    public static final String ASTERISK = " * ";

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param sql <br>
     */
    public static void checkSql(final String sql) {
        Assert.isTrue(!StringUtils.trim(sql).toLowerCase().startsWith(DaoConstants.SQL_SELECT_PREFIX)
            || sql.indexOf(ASTERISK) == -1, ErrorCodeDef.UNSUUPORT_ASTERISK);
    }
}
