/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.message.rpc;

import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.FrameworkException;
import com.hbasesoft.framework.common.utils.PropertyHolder;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2016年10月5日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.message.rpc <br>
 */
public class RemoteServiceException extends Exception {

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
    public RemoteServiceException(int code) {
        super(PropertyHolder.getErrorMessage(code));
        this.code = code;
    }

    /**
     * FrameworkException
     * 
     * @param code <br>
     * @param msg <br>
     * @param params <br>
     */
    public RemoteServiceException(int code, Object... params) {
        super(PropertyHolder.getErrorMessage(code, params));
        this.code = code;
    }

    public RemoteServiceException(Throwable t) {
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
    public RemoteServiceException(int code, Throwable t) {
        super(PropertyHolder.getErrorMessage(code));
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
    public RemoteServiceException(Throwable t, int code, Object... params) {
        super(PropertyHolder.getErrorMessage(code, params));
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
