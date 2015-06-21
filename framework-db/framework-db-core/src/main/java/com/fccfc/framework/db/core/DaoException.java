/**************************************************************************************** 
 Copyright © 2003-2012 ZTEsoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.fccfc.framework.db.core;

import com.fccfc.framework.common.FrameworkException;


/**
 * <Description> <br>
 * 
 * @author 王伟 <br>
 * @version 1.0 <br>
 * @CreateDate 2014年10月24日 <br>
 * @see com.fccfc.framework.dao <br>
 */
public class DaoException extends FrameworkException {

    /**
     * serialVersionUID <br>
     */
    private static final long serialVersionUID = 1615477208457076971L;

    /**
     * @param exception
     */
    public DaoException(FrameworkException exception) {
        super(exception);
    }

    /**
     * @param code
     * @param msg
     * @param params
     */
    public DaoException(int code, String msg, Object... params) {
        super(code, msg, params);
    }

    /**
     * @param code
     * @param arg0
     * @param arg1
     * @param params
     */
    public DaoException(int code, String arg0, Throwable arg1, Object... params) {
        super(code, arg0, arg1, params);
    }

    /**
     * @param code
     * @param arg0
     * @param arg1
     */
    public DaoException(int code, String arg0, Throwable arg1) {
        super(code, arg0, arg1);
    }

    /**
     * @param code
     * @param msg
     */
    public DaoException(int code, String msg) {
        super(code, msg);
    }

    /**
     * @param code
     * @param arg0
     */
    public DaoException(int code, Throwable arg0) {
        super(code, arg0);
    }

    /**
     * @param code
     */
    public DaoException(int code) {
        super(code);
    }

    /**
     * @param arg0
     * @param arg1
     * @param params
     */
    public DaoException(String arg0, FrameworkException arg1, Object... params) {
        super(arg0, arg1, params);
    }

    /**
     * @param arg0
     * @param exception
     */
    public DaoException(String arg0, FrameworkException exception) {
        super(arg0, exception);
    }

}
