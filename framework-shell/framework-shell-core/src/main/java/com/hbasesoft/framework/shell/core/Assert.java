/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.shell.core;

import java.util.Arrays;
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
     * @throws RuntimeException <br>
     */
    public static void notNull(Object obj, Object... params) {
        if (obj == null) {
            throw new RuntimeException(Arrays.toString(params));
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
     * @throws RuntimeException <br>
     */
    public static void isNull(Object obj, Object... params) throws RuntimeException {
        if (obj != null) {
            throw new RuntimeException(Arrays.toString(params));
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
     * @throws RuntimeException <br>
     */
    public static void notEmpty(String str, Object... params) throws RuntimeException {
        if (StringUtils.isEmpty(str)) {
            throw new RuntimeException(Arrays.toString(params));
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
     * @throws RuntimeException <br>
     */
    public static void isEmpty(String str, Object... params) throws RuntimeException {
        if (StringUtils.isNotEmpty(str)) {
            throw new RuntimeException(Arrays.toString(params));
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
     * @throws RuntimeException <br>
     */
    public static <T> void notEmpty(T[] str, Object... params) throws RuntimeException {
        if (ArrayUtils.isEmpty(str)) {
            throw new RuntimeException(Arrays.toString(params));
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
     * @throws RuntimeException <br>
     */
    public static <T> void isEmpty(T[] str, Object... params) throws RuntimeException {
        if (!ArrayUtils.isEmpty(str)) {
            throw new RuntimeException(Arrays.toString(params));
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
     * @throws RuntimeException <br>
     */
    public static void notEmpty(Collection<?> str, Object... params) throws RuntimeException {
        if (CollectionUtils.isEmpty(str)) {
            throw new RuntimeException(Arrays.toString(params));
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
     * @throws RuntimeException <br>
     */
    public static void isEmpty(Collection<?> str, Object... params) throws RuntimeException {
        if (CollectionUtils.isNotEmpty(str)) {
            throw new RuntimeException(Arrays.toString(params));
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
     * @throws RuntimeException <br>
     */
    public static void notEmpty(Map<?, ?> str, Object... params) throws RuntimeException {
        if (MapUtils.isEmpty(str)) {
            throw new RuntimeException(Arrays.toString(params));
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
     * @throws RuntimeException <br>
     */
    public static void isEmpty(Map<?, ?> str, Object... params) throws RuntimeException {
        if (MapUtils.isNotEmpty(str)) {
            throw new RuntimeException(Arrays.toString(params));
        }
    }

    public static void equals(Object obj1, Object obj2, Object... params) {
        if (!(obj1 != null && obj1.equals(obj2))) {
            throw new RuntimeException(Arrays.toString(params));
        }
    }

    public static void notEquals(Object obj1, Object obj2, Object... params) {
        if ((obj1 != null && obj1.equals(obj2)) || (obj2 != null && obj2.equals(obj1))) {
            throw new RuntimeException(Arrays.toString(params));
        }
    }

    public static void isTrue(boolean result, Object... params) {
        if (!result) {
            throw new RuntimeException(Arrays.toString(params));
        }
    }

    public static void isFalse(boolean result, Object... params) {
        if (result) {
            throw new RuntimeException(Arrays.toString(params));
        }
    }
}
