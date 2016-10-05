/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.common;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2016年10月5日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.common <br>
 */
public class RemoteServiceException extends FrameworkException {
    /**
     * serialVersionUID <br>
     */
    private static final long serialVersionUID = 1615477208457076971L;

    /**
     * ServiceException
     * 
     * @param exception <br>
     */
    public RemoteServiceException(FrameworkException exception) {
        super(exception.getCode(), exception.getMessage());
    }

    /**
     * ServiceException
     * 
     * @param exception <br>
     */
    public RemoteServiceException(Exception exception) {
        super(ErrorCodeDef.SYSTEM_ERROR_10001, exception.getMessage());
    }

    /**
     * ServiceException
     * 
     * @param code <br>
     * @param msg <br>
     */
    public RemoteServiceException(int code, String msg) {
        super(code, msg);
    }

    /**
     * ServiceException
     * 
     * @param code <br>
     * @param msg <br>
     * @param params <br>
     */
    public RemoteServiceException(int code, String msg, Object... params) {
        super(code, msg, params);
    }

    /**
     * ServiceException
     * 
     * @param code <br>
     * @param arg0 <br>
     */
    public RemoteServiceException(int code, Throwable arg0) {
        super(code, arg0.getMessage());
    }

    /**
     * ServiceException
     * 
     * @param code <br>
     */
    public RemoteServiceException(int code) {
        super(code);
    }

    /**
     * ServiceException
     * 
     * @param arg0 <br>
     * @param arg1 <br>
     * @param params <br>
     */
    public RemoteServiceException(FrameworkException e, String message, Object... params) {
        super(e.getCode(), message, params);
    }

    /**
     * ServiceException
     * 
     * @param arg0 <br>
     * @param exception <br>
     */
    public RemoteServiceException(String arg0, FrameworkException exception) {
        super(exception.getCode(), arg0);
    }
}
