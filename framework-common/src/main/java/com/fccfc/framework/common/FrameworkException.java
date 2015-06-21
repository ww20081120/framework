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
public class FrameworkException extends Exception {

    private static final long serialVersionUID = -7504811348332310234L;

    /**
     * 错误码
     */
    private int code;

    public FrameworkException(int code) {
        this.code = code;
    }

    public FrameworkException(int code, String msg) {
        super(msg);
        this.code = code;
    }

    public FrameworkException(int code, String msg, Object... params) {
        this(code, MessageFormat.format(msg, params));
    }

    public FrameworkException(FrameworkException exception) {
        this(exception.code, exception);
    }

    public FrameworkException(int code, Throwable arg0) {
        super(arg0);
        this.code = code;
    }

    public FrameworkException(String arg0, FrameworkException exception) {
        this(exception.code, arg0, exception);
    }

    public FrameworkException(int code, String arg0, Throwable arg1) {
        super(arg0, arg1);
        this.code = code;
    }

    public FrameworkException(String arg0, FrameworkException arg1, Object... params) {
        this(arg1.code, MessageFormat.format(arg0, params), arg1);
    }

    public FrameworkException(int code, String arg0, Throwable arg1, Object... params) {
        this(code, MessageFormat.format(arg0, params), arg1);
    }

    public int getCode() {
        return code;
    }
}
