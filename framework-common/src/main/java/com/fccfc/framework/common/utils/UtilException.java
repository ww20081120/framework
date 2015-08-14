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
     * serialVersionUID
     */
    private static final long serialVersionUID = -9019614054469957536L;

    /**
     * UtilException
     * @param exception <br>
     */
    public UtilException(FrameworkException exception) {
        super(exception);
    }

    /**
     * UtilException
     * @param code <br>
     * @param msg <br>
     * @param params <br>
     */
    public UtilException(int code, String msg, Object... params) {
        super(code, msg, params);
    }

    /**
     * UtilException
     * @param code <br>
     * @param arg0 <br>
     * @param arg1 <br>
     * @param params <br>
     */
    public UtilException(int code, String arg0, Throwable arg1, Object... params) {
        super(code, arg0, arg1, params);
    }

    /**
     * UtilException
     * @param code <br>
     * @param arg0 <br>
     * @param arg1 <br>
     */
    public UtilException(int code, String arg0, Throwable arg1) {
        super(code, arg0, arg1);
    }

    /**
     * UtilException
     * @param code <br>
     * @param msg <br>
     */
    public UtilException(int code, String msg) {
        super(code, msg);
    }

    /**
     * UtilException
     * @param code <br>
     * @param arg0 <br>
     */
    public UtilException(int code, Throwable arg0) {
        super(code, arg0);
    }

    /**
     * UtilException
     * @param code <br>
     */
    public UtilException(int code) {
        super(code);
    }

    /**
     * UtilException
     * @param arg0 <br>
     * @param arg1 <br>
     * @param params <br>
     */
    public UtilException(String arg0, FrameworkException arg1, Object... params) {
        super(arg0, arg1, params);
    }

    /**
     * UtilException
     * @param arg0 <br>
     * @param exception <br>
     */
    public UtilException(String arg0, FrameworkException exception) {
        super(arg0, exception);
    }

}
