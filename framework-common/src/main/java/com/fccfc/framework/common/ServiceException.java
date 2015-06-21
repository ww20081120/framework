/**
 * 
 */
package com.fccfc.framework.common;

/**
 * <Description> <br>
 * 
 * @author xgf<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2014年11月2日 <br>
 * @since V7.3<br>
 * @see com.fccfc.framework.common.exception <br>
 */

public class ServiceException extends FrameworkException {

    /**
     * serialVersionUID <br>
     */
    private static final long serialVersionUID = 1615477208457076971L;

    /**
     * @param exception
     */
    public ServiceException(FrameworkException exception) {
        super(exception);
    }

    /**
     * @param code
     * @param msg
     * @param params
     */
    public ServiceException(int code, String msg, Object... params) {
        super(code, msg, params);
    }

    /**
     * @param code
     * @param arg0
     * @param arg1
     * @param params
     */
    public ServiceException(int code, String arg0, Throwable arg1, Object... params) {
        super(code, arg0, arg1, params);
    }

    /**
     * @param code
     * @param arg0
     * @param arg1
     */
    public ServiceException(int code, String arg0, Throwable arg1) {
        super(code, arg0, arg1);
    }

    /**
     * @param code
     * @param msg
     */
    public ServiceException(int code, String msg) {
        super(code, msg);
    }

    /**
     * @param code
     * @param arg0
     */
    public ServiceException(int code, Throwable arg0) {
        super(code, arg0);
    }

    /**
     * @param code
     */
    public ServiceException(int code) {
        super(code);
    }

    /**
     * @param arg0
     * @param arg1
     * @param params
     */
    public ServiceException(String arg0, FrameworkException arg1, Object... params) {
        super(arg0, arg1, params);
    }

    /**
     * @param arg0
     * @param exception
     */
    public ServiceException(String arg0, FrameworkException exception) {
        super(arg0, exception);
    }

}
