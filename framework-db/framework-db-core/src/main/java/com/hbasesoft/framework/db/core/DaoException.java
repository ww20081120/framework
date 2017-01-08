/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.db.core;

import com.hbasesoft.framework.common.FrameworkException;

/**
 * <Description> <br>
 * 
 * @author 王伟 <br>
 * @version 1.0 <br>
 * @CreateDate 2014年10月24日 <br>
 * @see com.hbasesoft.framework.dao <br>
 */
public class DaoException extends FrameworkException {

    /**
     * serialVersionUID <br>
     */
    private static final long serialVersionUID = 8246373948586046129L;

    /**
     * @param exception
     */
    public DaoException(FrameworkException exception) {
        super(exception);
    }

    /**
     * @param code
     * @param params
     */
    public DaoException(int code, Object... params) {
        super(code, params);
    }

    /**
     * @param code
     * @param t
     */
    public DaoException(int code, Throwable t) {
        super(code, t);
    }

    /**
     * @param code
     */
    public DaoException(int code) {
        super(code);
    }

    /**
     * @param t
     * @param code
     * @param params
     */
    public DaoException(Throwable t, int code, Object... params) {
        super(t, code, params);
    }

    /**
     * @param t
     */
    public DaoException(Throwable t) {
        super(t);
    }

}
