/**
 * 
 */
package com.fccfc.framework.common.utils;

import com.fccfc.framework.common.FrameworkException;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2014年10月23日 <br>
 * @since V1.0<br>
 * @see com.fccfc.framework.common.exception <br>
 */
public class UtilException extends FrameworkException {

    /**
     * 
     */
    private static final long serialVersionUID = -9019614054469957536L;

    /**
     * @param exception
     */
    public UtilException(FrameworkException exception) {
        super(exception);
    }

    /**
     * @param code
     * @param msg
     * @param params
     */
    public UtilException(int code, String msg, Object... params) {
        super(code, msg, params);
    }

    /**
     * @param code
     * @param arg0
     * @param arg1
     * @param params
     */
    public UtilException(int code, String arg0, Throwable arg1, Object... params) {
        super(code, arg0, arg1, params);
    }

    /**
     * @param code
     * @param arg0
     * @param arg1
     */
    public UtilException(int code, String arg0, Throwable arg1) {
        super(code, arg0, arg1);
    }

    /**
     * @param code
     * @param msg
     */
    public UtilException(int code, String msg) {
        super(code, msg);
    }

    /**
     * @param code
     * @param arg0
     */
    public UtilException(int code, Throwable arg0) {
        super(code, arg0);
    }

    /**
     * @param code
     */
    public UtilException(int code) {
        super(code);
    }

    /**
     * @param arg0
     * @param arg1
     * @param params
     */
    public UtilException(String arg0, FrameworkException arg1, Object... params) {
        super(arg0, arg1, params);
    }

    /**
     * @param arg0
     * @param exception
     */
    public UtilException(String arg0, FrameworkException exception) {
        super(arg0, exception);
    }

}
