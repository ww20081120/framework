/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.common.utils;

import com.hbasesoft.framework.common.FrameworkException;

/**
 * <Description> <br>
 * 
 * @author 王伟 <br>
 * @version 1.0 <br>
 * @CreateDate 2014年11月7日 <br>
 * @see com.hbasesoft.framework.core.utils <br>
 */
public class AssertException extends FrameworkException {

    /**
     * serialVersionUID <br>
     */
    private static final long serialVersionUID = 660380252623842610L;

    /**
     * @param code
     * @param params
     */
    public AssertException(int code, Object... params) {
        super(code, params);
    }

    /**
     * @param code
     * @param t
     */
    public AssertException(int code, Throwable t) {
        super(code, t);
    }

    /**
     * @param code
     */
    public AssertException(int code) {
        super(code);
    }

    /**
     * @param t
     * @param code
     * @param params
     */
    public AssertException(Throwable t, int code, Object... params) {
        super(t, code, params);
    }

    /**
     * @param t
     */
    public AssertException(Throwable t) {
        super(t);
    }

}
