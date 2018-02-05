/**
 * 
 */
package com.hbasesoft.framework.common;

import com.hbasesoft.framework.common.utils.PropertyHolder;

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
public class FrameworkException extends RuntimeException {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -7504811348332310234L;

    /**
     * 错误码
     */
    private int code;

    /**
     * FrameworkException
     * 
     * @param code <br>
     */
    public FrameworkException(int code) {
        super(code + GlobalConstants.BLANK);
        this.code = code;
    }

    /**
     * FrameworkException
     * 
     * @param code <br>
     * @param msg <br>
     * @param params <br>
     */
    public FrameworkException(int code, Object... params) {
        super(PropertyHolder.getErrorMessage(code, params));
        this.code = code;
    }

    public FrameworkException(Throwable t) {
        super(t);
        if (t instanceof FrameworkException) {
            this.code = ((FrameworkException) t).getCode();
        }
        else {
            this.code = ErrorCodeDef.SYSTEM_ERROR_10001;
        }

    }

    /**
     * FrameworkException
     * 
     * @param code <br>
     * @param arg0 <br>
     */
    public FrameworkException(int code, Throwable t) {
        super(PropertyHolder.getErrorMessage(code), t);
        this.code = code;
    }

    /**
     * FrameworkException
     * 
     * @param code <br>
     * @param arg0 <br>
     * @param arg1 <br>
     * @param params <br>
     */
    public FrameworkException(Throwable t, int code, Object... params) {
        super(PropertyHolder.getErrorMessage(code, params), t);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
