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
    private static final long serialVersionUID = 1615477208457076971L;

    /**
     * DaoException
     * @param exception <br>
     */
    public DaoException(FrameworkException exception) {
        super(exception);
    }

    /**
     * DaoException
     * @param code <br>
     * @param msg <br>
     * @param params <br>
     */
    public DaoException(int code, String msg, Object... params) {
        super(code, msg, params);
    }

    /**
     * DaoException
     * @param code <br>
     * @param arg0 <br>
     * @param arg1 <br>
     * @param params <br>
     */
    public DaoException(int code, String arg0, Throwable arg1, Object... params) {
        super(code, arg0, arg1, params);
    }

    /**
     * DaoException
     * @param code <br>
     * @param arg0 <br>
     * @param arg1 <br>
     */
    public DaoException(int code, String arg0, Throwable arg1) {
        super(code, arg0, arg1);
    }

    /**
     * DaoException
     * @param code <br>
     * @param msg <br>
     */
    public DaoException(int code, String msg) {
        super(code, msg);
    }

    /**
     * DaoException
     * @param code <br>
     * @param arg0 <br>
     */
    public DaoException(int code, Throwable arg0) {
        super(code, arg0);
    }

    /**
     * DaoException
     * @param code <br>
     */
    public DaoException(int code) {
        super(code);
    }

    /**
     * DaoException
     * @param arg0 <br>
     * @param arg1 <br>
     * @param params <br>
     */
    public DaoException(String arg0, FrameworkException arg1, Object... params) {
        super(arg0, arg1, params);
    }

    /**
     * DaoException
     * @param arg0 <br>
     * @param exception <br>
     */
    public DaoException(String arg0, FrameworkException exception) {
        super(arg0, exception);
    }

}
