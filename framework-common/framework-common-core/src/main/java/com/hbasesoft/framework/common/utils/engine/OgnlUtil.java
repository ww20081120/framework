/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.common.utils.engine;

import java.util.Map;

import com.hbasesoft.framework.common.utils.UtilException;

import ognl.Ognl;
import ognl.OgnlContext;
import ognl.OgnlException;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2018年12月12日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.common.utils.engine <br>
 */
public final class OgnlUtil {

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param script
     * @param paramMap
     * @return <br>
     */
    public static Object getValue(final String script, final Map<String, Object> paramMap) {
        OgnlContext context = new OgnlContext(null, null, new DefaultMemberAccess(true));
        try {
            return Ognl.getValue(script, context, paramMap);
        }
        catch (OgnlException e) {
            throw new UtilException(e);
        }
    }
}
