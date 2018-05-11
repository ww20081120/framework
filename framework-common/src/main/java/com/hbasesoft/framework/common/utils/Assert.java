/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.common.utils;

import java.util.Collection;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * <Description> <br>
 * 
 * @author 王伟 <br>
 * @version 1.0 <br>
 * @CreateDate 2014年11月7日 <br>
 * @see com.hbasesoft.framework.core.utils <br>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Assert {

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
    public static void notNull(Object obj, int errorCode, Object... params) {
        if (obj == null) {
            throw new AssertException(errorCode, params);
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
    public static void isNull(Object obj, int errorCode, Object... params) throws AssertException {
        if (obj != null) {
            throw new AssertException(errorCode, params);
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
    public static void notEmpty(String str, int errorCode, Object... params) throws AssertException {
        if (StringUtils.isEmpty(str)) {
            throw new AssertException(errorCode, params);
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
    public static void isEmpty(String str, int errorCode, Object... params) throws AssertException {
        if (StringUtils.isNotEmpty(str)) {
            throw new AssertException(errorCode, params);
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
    public static <T> void notEmpty(T[] str, int errorCode, Object... params) throws AssertException {
        if (ArrayUtils.isEmpty(str)) {
            throw new AssertException(errorCode, params);
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
    public static <T> void isEmpty(T[] str, int errorCode, Object... params) throws AssertException {
        if (!ArrayUtils.isEmpty(str)) {
            throw new AssertException(errorCode, params);
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
    public static void notEmpty(Collection<?> str, int errorCode, Object... params) throws AssertException {
        if (CollectionUtils.isEmpty(str)) {
            throw new AssertException(errorCode, params);
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
    public static void isEmpty(Collection<?> str, int errorCode, Object... params) throws AssertException {
        if (CollectionUtils.isNotEmpty(str)) {
            throw new AssertException(errorCode, params);
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
    public static void notEmpty(Map<?, ?> str, int errorCode, Object... params) throws AssertException {
        if (MapUtils.isEmpty(str)) {
            throw new AssertException(errorCode, params);
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
    public static void isEmpty(Map<?, ?> str, int errorCode, Object... params) throws AssertException {
        if (MapUtils.isNotEmpty(str)) {
            throw new AssertException(errorCode, params);
        }
    }

    public static void equals(Object obj1, Object obj2, int errorCode, Object... params) {
        if (!(obj1 != null && obj1.equals(obj2))) {
            throw new AssertException(errorCode, params);
        }
    }

    public static void notEquals(Object obj1, Object obj2, int errorCode, Object... params) {
        if ((obj1 != null && obj1.equals(obj2)) || (obj2 != null && obj2.equals(obj1))) {
            throw new AssertException(errorCode, params);
        }
    }

    public static void isTrue(boolean result, int errorCode, Object... params) {
        if (!result) {
            throw new AssertException(errorCode, params);
        }
    }

    public static void isFalse(boolean result, int errorCode, Object... params) {
        if (result) {
            throw new AssertException(errorCode, params);
        }
    }
}
