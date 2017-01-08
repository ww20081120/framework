/**
 * 
 */
package com.hbasesoft.framework.common.utils;

import com.hbasesoft.framework.common.FrameworkException;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2014年10月23日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.common.exception <br>
 */
public class UtilException extends FrameworkException {

    /**
     * serialVersionUID <br>
     */
    private static final long serialVersionUID = -606867736687228572L;

    /**
     * @param exception
     */
    public UtilException(FrameworkException exception) {
        super(exception);
    }

    /**
     * @param code
     * @param params
     */
    public UtilException(int code, Object... params) {
        super(code, params);
    }

    /**
     * @param code
     * @param t
     */
    public UtilException(int code, Throwable t) {
        super(code, t);
    }

    /**
     * @param code
     */
    public UtilException(int code) {
        super(code);
    }

    /**
     * @param t
     * @param code
     * @param params
     */
    public UtilException(Throwable t, int code, Object... params) {
        super(t, code, params);
    }

    /**
     * @param t
     */
    public UtilException(Throwable t) {
        super(t);
    }

}
