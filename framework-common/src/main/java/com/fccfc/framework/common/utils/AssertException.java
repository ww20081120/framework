/**************************************************************************************** 
 Copyright © 2003-2012 ZTEsoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.fccfc.framework.common.utils;

import com.fccfc.framework.common.FrameworkException;

/**
 * <Description> <br>
 * 
 * @author 王伟 <br>
 * @version 1.0 <br>
 * @CreateDate 2014年11月7日 <br>
 * @see com.fccfc.framework.core.utils <br>
 */
public class AssertException extends FrameworkException {

    /**
     * serialVersionUID <br>
     */
    private static final long serialVersionUID = 2704052920464076367L;

    /**
     * 默认构造函数
     */
    public AssertException(FrameworkException exception) {
        super(exception);
    }

    /**
     * 默认构造函数
     */
    public AssertException(int code, String msg, Object... params) {
        super(code, msg, params);
    }

    /**
     * 默认构造函数
     */
    public AssertException(int code, String arg0, Throwable arg1, Object... params) {
        super(code, arg0, arg1, params);
    }

    /**
     * 默认构造函数
     */
    public AssertException(int code, String arg0, Throwable arg1) {
        super(code, arg0, arg1);
    }

    /**
     * 默认构造函数
     */
    public AssertException(int code, String msg) {
        super(code, msg);
    }

    /**
     * 默认构造函数
     */
    public AssertException(int code, Throwable arg0) {
        super(code, arg0);
    }

    /**
     * 默认构造函数
     */
    public AssertException(int code) {
        super(code);
    }

    /**
     * 默认构造函数
     */
    public AssertException(String arg0, FrameworkException arg1, Object... params) {
        super(arg0, arg1, params);
    }

    /**
     * 默认构造函数
     */
    public AssertException(String arg0, FrameworkException exception) {
        super(arg0, exception);
    }
}
