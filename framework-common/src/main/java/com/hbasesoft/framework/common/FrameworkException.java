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
     * @param ocode <br>
     */
    public FrameworkException(final int ocode) {
        super(ocode + GlobalConstants.BLANK);
        this.code = ocode;
    }

    /**
     * FrameworkException
     * 
     * @param ocode
     * @param params
     */
    public FrameworkException(final int ocode, final Object... params) {
        super(PropertyHolder.getErrorMessage(ocode, params));
        this.code = ocode;
    }

    /**
     * FrameworkException
     * 
     * @param t
     */
    public FrameworkException(final Throwable t) {
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
     * @param tempCode
     * @param t
     */
    public FrameworkException(final int tempCode, final Throwable t) {
        super(PropertyHolder.getErrorMessage(tempCode), t);
        this.code = tempCode;
    }

    /**
     * FrameworkException
     * 
     * @param t
     * @param ocode
     * @param params
     */
    public FrameworkException(final Throwable t, final int ocode, final Object... params) {
        super(PropertyHolder.getErrorMessage(ocode, params), t);
        this.code = ocode;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public int getCode() {
        return code;
    }
}
