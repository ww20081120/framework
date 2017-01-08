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
    private static final long serialVersionUID = -6513454148109803923L;

    /**
     * @param exception
     */
    public RemoteServiceException(FrameworkException exception) {
        super(exception);
    }

    /**
     * @param code
     * @param params
     */
    public RemoteServiceException(int code, Object... params) {
        super(code, params);
    }

    /**
     * @param code
     * @param t
     */
    public RemoteServiceException(int code, Throwable t) {
        super(code, t);
    }

    /**
     * @param code
     */
    public RemoteServiceException(int code) {
        super(code);
    }

    /**
     * @param t
     * @param code
     * @param params
     */
    public RemoteServiceException(Throwable t, int code, Object... params) {
        super(t, code, params);
    }

    /**
     * @param t
     */
    public RemoteServiceException(Throwable t) {
        super(t);
    }
}
