/**
 * 
 */
package com.hbasesoft.framework.common;

/**
 * <Description> <br>
 * 
 * @author xgf<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2014年11月2日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.common.exception <br>
 */

public class ServiceException extends FrameworkException {

    /**
     * serialVersionUID <br>
     */
    private static final long serialVersionUID = 1615477208457076971L;

    /**
     * ServiceException
     * 
     * @param exception <br>
     */
    public ServiceException(FrameworkException exception) {
        super(exception);
    }

    /**
     * ServiceException
     * 
     * @param code <br>
     * @param msg <br>
     * @param params <br>
     */
    public ServiceException(int code, String msg, Object... params) {
        super(code, msg, params);
    }

    /**
     * ServiceException
     * 
     * @param code <br>
     * @param arg0 <br>
     * @param arg1 <br>
     * @param params <br>
     */
    public ServiceException(int code, String arg0, Throwable arg1, Object... params) {
        super(code, arg0, arg1, params);
    }

    /**
     * ServiceException
     * 
     * @param code <br>
     * @param arg0 <br>
     * @param arg1 <br>
     */
    public ServiceException(int code, String arg0, Throwable arg1) {
        super(code, arg0, arg1);
    }

    /**
     * ServiceException
     * 
     * @param code <br>
     * @param msg <br>
     */
    public ServiceException(int code, String msg) {
        super(code, msg);
    }

    /**
     * ServiceException
     * 
     * @param code <br>
     * @param arg0 <br>
     */
    public ServiceException(int code, Throwable arg0) {
        super(code, arg0);
    }

    /**
     * ServiceException
     * 
     * @param code <br>
     */
    public ServiceException(int code) {
        super(code);
    }

    /**
     * ServiceException
     * 
     * @param arg0 <br>
     * @param arg1 <br>
     * @param params <br>
     */
    public ServiceException(String arg0, FrameworkException arg1, Object... params) {
        super(arg0, arg1, params);
    }

    /**
     * ServiceException
     * 
     * @param arg0 <br>
     * @param exception <br>
     */
    public ServiceException(String arg0, FrameworkException exception) {
        super(arg0, exception);
    }

    /**
     * @param arg0
     */
    public ServiceException(Throwable arg0) {
        super(arg0);
    }

}
