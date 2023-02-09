/**
 * 
 */
package com.hbasesoft.framework.common;

import com.hbasesoft.framework.common.utils.CommonUtil;

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
    private ErrorCode code;

    /**
     * FrameworkException
     * 
     * @param ocode <br>
     */
    public FrameworkException(final ErrorCode ocode) {
        super(ocode.getMsg());
        this.code = ocode;
    }

    /**
     * FrameworkException
     * 
     * @param ocode
     * @param params
     */
    public FrameworkException(final ErrorCode ocode, final Object... params) {
        super(CommonUtil.messageFormat(ocode.getMsg(), params));
        this.code = ocode;
    }

    /**
     * FrameworkException
     * 
     * @param t
     */
    public FrameworkException(final Throwable t) {
        super(t);
        if (t instanceof FrameworkException fe) {
            this.code = fe.getCode();
        }
        else {
            this.code = ErrorCodeDef.SYSTEM_ERROR;
        }

    }

    /**
     * FrameworkException
     * 
     * @param tempCode
     * @param t
     */
    public FrameworkException(final ErrorCode tempCode, final Throwable t) {
        super(tempCode.getMsg(), t);
        this.code = tempCode;
    }

    /**
     * FrameworkException
     * 
     * @param t
     * @param ocode
     * @param params
     */
    public FrameworkException(final Throwable t, final ErrorCode ocode, final Object... params) {
        super(CommonUtil.messageFormat(ocode.getMsg(), params), t);
        this.code = ocode;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public ErrorCode getCode() {
        return code;
    }
}
