/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.common.utils;

import java.util.Collection;
import java.util.Map;

import com.hbasesoft.framework.common.ErrorCodeDef;

/**
 * <Description> <br>
 * 
 * @author 王伟 <br>
 * @version 1.0 <br>
 * @CreateDate 2014年11月7日 <br>
 * @see com.hbasesoft.framework.core.utils <br>
 */
public final class Assert {

    /**
     * 默认构造函数
     */
    private Assert() {
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param obj <br>
     * @param message <br>
     * @param params <br>
     * @throws AssertException <br>
     */
    public static void notNull(Object obj, String message, Object... params) throws AssertException {
        if (obj == null) {
            throw new AssertException(ErrorCodeDef.IS_NULL, message, params);
        }
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param obj <br>
     * @param message <br>
     * @param params <br>
     * @throws AssertException <br>
     */
    public static void isNull(Object obj, String message, Object... params) throws AssertException {
        if (obj != null) {
            throw new AssertException(ErrorCodeDef.NOT_NULL, message, params);
        }
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param str <br>
     * @param message <br>
     * @param params <br>
     * @throws AssertException <br>
     */
    public static void notEmpty(String str, String message, Object... params) throws AssertException {
        if (CommonUtil.isEmpty(str)) {
            throw new AssertException(ErrorCodeDef.IS_NULL, message, params);
        }
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param str <br>
     * @param message <br>
     * @param params <br>
     * @throws AssertException <br>
     */
    public static void isEmpty(String str, String message, Object... params) throws AssertException {
        if (CommonUtil.isNotEmpty(str)) {
            throw new AssertException(ErrorCodeDef.NOT_NULL, message, params);
        }
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param <T> <br>
     * @param str <br>
     * @param message <br>
     * @param params <br>
     * @throws AssertException <br>
     */
    public static <T> void notEmpty(T[] str, String message, Object... params) throws AssertException {
        if (CommonUtil.isEmpty(str)) {
            throw new AssertException(ErrorCodeDef.IS_NULL, message, params);
        }
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param <T> <br>
     * @param str <br>
     * @param message <br>
     * @param params <br>
     * @throws AssertException <br>
     */
    public static <T> void isEmpty(T[] str, String message, Object... params) throws AssertException {
        if (CommonUtil.isNotEmpty(str)) {
            throw new AssertException(ErrorCodeDef.NOT_NULL, message, params);
        }
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param str <br>
     * @param message <br>
     * @param params <br>
     * @throws AssertException <br>
     */
    public static void notEmpty(Collection<?> str, String message, Object... params) throws AssertException {
        if (CommonUtil.isEmpty(str)) {
            throw new AssertException(ErrorCodeDef.IS_NULL, message, params);
        }
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param str <br>
     * @param message <br>
     * @param params <br>
     * @throws AssertException <br>
     */
    public static void isEmpty(Collection<?> str, String message, Object... params) throws AssertException {
        if (CommonUtil.isNotEmpty(str)) {
            throw new AssertException(ErrorCodeDef.NOT_NULL, message, params);
        }
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param str <br>
     * @param message <br>
     * @param params <br>
     * @throws AssertException <br>
     */
    public static void notEmpty(Map<?, ?> str, String message, Object... params) throws AssertException {
        if (CommonUtil.isEmpty(str)) {
            throw new AssertException(ErrorCodeDef.IS_NULL, message, params);
        }
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param str <br>
     * @param message <br>
     * @param params <br>
     * @throws AssertException <br>
     */
    public static void isEmpty(Map<?, ?> str, String message, Object... params) throws AssertException {
        if (CommonUtil.isNotEmpty(str)) {
            throw new AssertException(ErrorCodeDef.NOT_NULL, message, params);
        }
    }

    public static void isTrue(boolean result, String message, Object... params) {
        if (!result) {
            throw new AssertException(ErrorCodeDef.IS_FALSE, message, params);
        }
    }

    public static void isFalse(boolean result, String message, Object... params) {
        if (result) {
            throw new AssertException(ErrorCodeDef.IS_TRUE, message, params);
        }
    }
}
