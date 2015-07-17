/**************************************************************************************** 
 Copyright © 2003-2012 fccfc Corporation. All rights reserved. Reproduction or       <br>
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
     * serialVersionUID
     */
    private static final long serialVersionUID = -863830498282709480L;

    /**
     * CacheException
     * @param exception <br>
     */
    public CacheException(FrameworkException exception) {
        super(exception);
    }

    /**
     * CacheException
     * @param code <br>
     * @param msg <br>
     * @param params <br>
     */
    public CacheException(int code, String msg, Object... params) {
        super(code, msg, params);
    }

    /**
     * CacheException
     * @param code <br>
     * @param arg0 <br>
     * @param arg1 <br>
     * @param params <br>
     */
    public CacheException(int code, String arg0, Throwable arg1, Object... params) {
        super(code, arg0, arg1, params);
    }

    /**
     * CacheException
     * @param code <br>
     * @param arg0 <br>
     * @param arg1 <br>
     */
    public CacheException(int code, String arg0, Throwable arg1) {
        super(code, arg0, arg1);
    }

    /**
     * CacheException
     * @param code <br>
     * @param msg <br>
     */
    public CacheException(int code, String msg) {
        super(code, msg);
    }

    /**
     * CacheException
     * @param code <br>
     * @param arg0 <br>
     */
    public CacheException(int code, Throwable arg0) {
        super(code, arg0);
    }

    /**
     * CacheException
     * @param code <br>
     */
    public CacheException(int code) {
        super(code);
    }

    /**
     * CacheException
     * @param arg0 <br>
     * @param arg1 <br>
     * @param params <br>
     */
    public CacheException(String arg0, FrameworkException arg1, Object... params) {
        super(arg0, arg1, params);
    }

    /**
     * CacheException
     * @param arg0 <br>
     * @param exception <br>
     */
    public CacheException(String arg0, FrameworkException exception) {
        super(arg0, exception);
    }

}
