/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.common.utils;

import com.hbasesoft.framework.common.FrameworkException;
import com.hbasesoft.framework.common.FrameworkRumtimeException;

/**
 * <Description> <br>
 * 
 * @author 王伟 <br>
 * @version 1.0 <br>
 * @CreateDate 2014年11月7日 <br>
 * @see com.hbasesoft.framework.core.utils <br>
 */
public class AssertException extends FrameworkRumtimeException {

    /**
     * serialVersionUID <br>
     */
    private static final long serialVersionUID = 2704052920464076367L;

    /**
     * 默认构造函数
     * 
     * @param exception <br>
     */
    public AssertException(FrameworkException exception) {
        super(exception);
    }

    /**
     * 默认构造函数
     * 
     * @param code <br>
     * @param msg <br>
     * @param params <br>
     */
    public AssertException(int code, String msg, Object... params) {
        super(code, msg, params);
    }

    /**
     * 默认构造函数
     * 
     * @param code <br>
     * @param arg0 <br>
     * @param arg1 <br>
     * @param params <br>
     */
    public AssertException(int code, String arg0, Throwable arg1, Object... params) {
        super(code, arg0, arg1, params);
    }

    /**
     * 默认构造函数
     * 
     * @param code <br>
     * @param arg0 <br>
     * @param arg1 <br>
     */
    public AssertException(int code, String arg0, Throwable arg1) {
        super(code, arg0, arg1);
    }

    /**
     * 默认构造函数
     * 
     * @param code <br>
     * @param msg <br>
     */
    public AssertException(int code, String msg) {
        super(code, msg);
    }

    /**
     * 默认构造函数
     * 
     * @param code <br>
     * @param arg0 <br>
     */
    public AssertException(int code, Throwable arg0) {
        super(code, arg0);
    }

    /**
     * 默认构造函数
     * 
     * @param code <br>
     */
    public AssertException(int code) {
        super(code);
    }

    /**
     * 默认构造函数
     * 
     * @param arg0 <br>
     * @param arg1 <br>
     * @param params <br>
     */
    public AssertException(String arg0, FrameworkException arg1, Object... params) {
        super(arg0, arg1, params);
    }

    /**
     * 默认构造函数
     * 
     * @param arg0 <br>
     * @param exception <br>
     */
    public AssertException(String arg0, FrameworkException exception) {
        super(arg0, exception);
    }
}
