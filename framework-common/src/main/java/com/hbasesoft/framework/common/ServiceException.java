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
    private static final long serialVersionUID = -8671861646248499738L;

    /**
     * @param exception
     */
    public ServiceException(FrameworkException exception) {
        super(exception);
    }

    /**
     * @param code
     * @param params
     */
    public ServiceException(int code, Object... params) {
        super(code, params);
    }

    /**
     * @param code
     * @param t
     */
    public ServiceException(int code, Throwable t) {
        super(code, t);
    }

    /**
     * @param code
     */
    public ServiceException(int code) {
        super(code);
    }

    /**
     * @param t
     * @param code
     * @param params
     */
    public ServiceException(Throwable t, int code, Object... params) {
        super(t, code, params);
    }

    /**
     * @param t
     */
    public ServiceException(Throwable t) {
        super(t);
    }

}
