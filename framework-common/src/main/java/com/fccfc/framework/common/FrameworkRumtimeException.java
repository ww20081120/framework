/**
 * 
 */
package com.fccfc.framework.common;

import java.text.MessageFormat;

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
public class FrameworkRumtimeException extends RuntimeException {

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
    public FrameworkRumtimeException(int code) {
        this.code = code;
    }

    /**
     * FrameworkException
     * 
     * @param code <br>
     * @param msg <br>
     */
    public FrameworkRumtimeException(int code, String msg) {
        super(msg);
        this.code = code;
    }

    /**
     * FrameworkException
     * 
     * @param code <br>
     * @param msg <br>
     * @param params <br>
     */
    public FrameworkRumtimeException(int code, String msg, Object... params) {
        this(code, MessageFormat.format(msg, params));
    }

    /**
     * FrameworkException
     * 
     * @param exception <br>
     */
    public FrameworkRumtimeException(FrameworkException exception) {
        this(exception.getCode(), exception);
    }

    /**
     * FrameworkException
     * 
     * @param code <br>
     * @param arg0 <br>
     */
    public FrameworkRumtimeException(int code, Throwable arg0) {
        super(arg0);
        this.code = code;
    }

    /**
     * FrameworkException
     * 
     * @param arg0 <br>
     * @param exception <br>
     */
    public FrameworkRumtimeException(String arg0, FrameworkException exception) {
        this(exception.getCode(), arg0, exception);
    }

    /**
     * FrameworkException
     * 
     * @param code <br>
     * @param arg0 <br>
     * @param arg1 <br>
     */
    public FrameworkRumtimeException(int code, String arg0, Throwable arg1) {
        super(arg0, arg1);
        this.code = code;
    }

    /**
     * FrameworkException
     * 
     * @param arg0 <br>
     * @param arg1 <br>
     * @param params <br>
     */
    public FrameworkRumtimeException(String arg0, FrameworkException arg1, Object... params) {
        this(arg1.getCode(), MessageFormat.format(arg0, params), arg1);
    }

    /**
     * FrameworkException
     * 
     * @param code <br>
     * @param arg0 <br>
     * @param arg1 <br>
     * @param params <br>
     */
    public FrameworkRumtimeException(int code, String arg0, Throwable arg1, Object... params) {
        this(code, MessageFormat.format(arg0, params), arg1);
    }

    public int getCode() {
        return code;
    }
}
