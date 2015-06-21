/**************************************************************************************** 
 Copyright © 2003-2012 ZTEsoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.fccfc.framework.cache.core;

import com.fccfc.framework.common.FrameworkException;

/**
 * <Description> <br>
 * 
 * @author 王伟 <br>
 * @version 1.0 <br>
 * @CreateDate 2014年10月24日 <br>
 * @see com.fccfc.framework.core.cache <br>
 */
public class CacheException extends FrameworkException {

    /**
     * 
     */
    private static final long serialVersionUID = -863830498282709480L;

    /**
     * @param exception
     */
    public CacheException(FrameworkException exception) {
        super(exception);
    }

    /**
     * @param code
     * @param msg
     * @param params
     */
    public CacheException(int code, String msg, Object... params) {
        super(code, msg, params);
    }

    /**
     * @param code
     * @param arg0
     * @param arg1
     * @param params
     */
    public CacheException(int code, String arg0, Throwable arg1, Object... params) {
        super(code, arg0, arg1, params);
    }

    /**
     * @param code
     * @param arg0
     * @param arg1
     */
    public CacheException(int code, String arg0, Throwable arg1) {
        super(code, arg0, arg1);
    }

    /**
     * @param code
     * @param msg
     */
    public CacheException(int code, String msg) {
        super(code, msg);
    }

    /**
     * @param code
     * @param arg0
     */
    public CacheException(int code, Throwable arg0) {
        super(code, arg0);
    }

    /**
     * @param code
     */
    public CacheException(int code) {
        super(code);
    }

    /**
     * @param arg0
     * @param arg1
     * @param params
     */
    public CacheException(String arg0, FrameworkException arg1, Object... params) {
        super(arg0, arg1, params);
    }

    /**
     * @param arg0
     * @param exception
     */
    public CacheException(String arg0, FrameworkException exception) {
        super(arg0, exception);
    }

}
