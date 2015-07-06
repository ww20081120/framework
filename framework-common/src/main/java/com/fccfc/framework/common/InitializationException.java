/**************************************************************************************** 
 Copyright © 2003-2012 fccfc Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.fccfc.framework.common;


/**
 * <Description> <br>
 * 
 * @author 王伟 <br>
 * @version 1.0 <br>
 * @CreateDate 2014年10月24日 <br>
 * @see com.fccfc.framework.common <br>
 */
public class InitializationException extends FrameworkException {
    /**
     * serialVersionUID <br>
     */
    private static final long serialVersionUID = 7630275353803653896L;

    /**
     * InitializationException
     * @param exception <br>
     */
    public InitializationException(FrameworkException exception) {
        super(exception);
    }

    /**
     * InitializationException
     * @param code <br>
     * @param msg <br>
     * @param params <br>
     */
    public InitializationException(int code, String msg, Object... params) {
        super(code, msg, params);
    }

    /**
     * InitializationException
     * @param code <br>
     * @param arg0 <br>
     * @param arg1 <br>
     * @param params <br>
     */
    public InitializationException(int code, String arg0, Throwable arg1, Object... params) {
        super(code, arg0, arg1, params);
    }

    /**
     * InitializationException
     * @param code <br>
     * @param arg0 <br>
     * @param arg1 <br>
     */
    public InitializationException(int code, String arg0, Throwable arg1) {
        super(code, arg0, arg1);
    }

    /**
     * InitializationException
     * @param code <br>
     * @param msg <br>
     */
    public InitializationException(int code, String msg) {
        super(code, msg);
    }

    /**
     * InitializationException
     * @param code <br>
     * @param arg0 <br>
     */
    public InitializationException(int code, Throwable arg0) {
        super(code, arg0);
    }

    /**
     * InitializationException
     * @param code <br>
     */
    public InitializationException(int code) {
        super(code);
    }

    /**
     * InitializationException
     * @param arg0 <br>
     * @param arg1 <br>
     * @param params <br>
     */
    public InitializationException(String arg0, FrameworkException arg1, Object... params) {
        super(arg0, arg1, params);
    }

    /**
     * InitializationException
     * @param arg0 <br>
     * @param exception <br>
     */
    public InitializationException(String arg0, FrameworkException exception) {
        super(arg0, exception);
    }
}
