/**
 * 
 */
package com.hbasesoft.framework.common;

import java.text.MessageFormat;

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
public class FrameworkException extends Exception {

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
     * @param code <br>
     */
    public FrameworkException(int code) {
        this.code = code;
    }

    /**
     * FrameworkException
     * @param code <br>
     * @param msg <br>
     */
    public FrameworkException(int code, String msg) {
        super(msg);
        this.code = code;
    }

    /**
     * FrameworkException
     * @param code <br>
     * @param msg <br>
     * @param params <br>
     */
    public FrameworkException(int code, String msg, Object... params) {
        this(code, MessageFormat.format(msg, params));
    }

    /**
     * FrameworkException
     * @param exception <br>
     */
    public FrameworkException(FrameworkException exception) {
        this(exception.code, exception);
    }

    /**
     * FrameworkException
     * @param code <br>
     * @param arg0 <br>
     */
    public FrameworkException(int code, Throwable arg0) {
        super(arg0);
        this.code = code;
    }

    /**
     * FrameworkException
     * @param arg0 <br>
     * @param exception <br>
     */
    public FrameworkException(String arg0, FrameworkException exception) {
        this(exception.code, arg0, exception);
    }

    /**
     * FrameworkException
     * @param code <br>
     * @param arg0 <br>
     * @param arg1 <br>
     */
    public FrameworkException(int code, String arg0, Throwable arg1) {
        super(arg0, arg1);
        this.code = code;
    }

    /**
     * FrameworkException
     * @param arg0 <br>
     * @param arg1 <br>
     * @param params <br>
     */
    public FrameworkException(String arg0, FrameworkException arg1, Object... params) {
        this(arg1.code, MessageFormat.format(arg0, params), arg1);
    }

    /**
     * FrameworkException
     * @param code <br>
     * @param arg0 <br>
     * @param arg1 <br>
     * @param params <br>
     */
    public FrameworkException(int code, String arg0, Throwable arg1, Object... params) {
        this(code, MessageFormat.format(arg0, params), arg1);
    }

    public int getCode() {
        return code;
    }
}
