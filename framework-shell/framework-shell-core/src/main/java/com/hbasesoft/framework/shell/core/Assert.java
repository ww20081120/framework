/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.shell.core;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

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
     * @param params <br>
     * @throws RuntimeException <br>
     */
    public static void notNull(final Object obj, final Object... params) {
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
     * @param params <br>
     * @throws RuntimeException <br>
     */
    public static void isNull(final Object obj, final Object... params) throws RuntimeException {
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
     * @param params <br>
     * @throws RuntimeException <br>
     */
    public static void notEmpty(final String str, final Object... params) throws RuntimeException {
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
     * @param params <br>
     * @throws RuntimeException <br>
     */
    public static void isEmpty(final String str, final Object... params) throws RuntimeException {
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
     * @param params <br>
     * @throws RuntimeException <br>
     */
    public static <T> void notEmpty(final T[] str, final Object... params) throws RuntimeException {
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
     * @param params <br>
     * @throws RuntimeException <br>
     */
    public static <T> void isEmpty(final T[] str, final Object... params) throws RuntimeException {
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
     * @param params <br>
     * @throws RuntimeException <br>
     */
    public static void notEmpty(final Collection<?> str, final Object... params) throws RuntimeException {
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
     * @param params <br>
     * @throws RuntimeException <br>
     */
    public static void isEmpty(final Collection<?> str, final Object... params) throws RuntimeException {
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
     * @param params <br>
     * @throws RuntimeException <br>
     */
    public static void notEmpty(final Map<?, ?> str, final Object... params) throws RuntimeException {
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
     * @param params <br>
     * @throws RuntimeException <br>
     */
    public static void isEmpty(final Map<?, ?> str, final Object... params) throws RuntimeException {
        if (MapUtils.isNotEmpty(str)) {
            throw new RuntimeException(Arrays.toString(params));
        }
    }

    /**
     * @Method equals
     * @param obj1
     * @param obj2
     * @param params
     * @Author 李煜龙
     * @Description TODD
     * @Date 2023/1/29 10:43
    */
    public static void equals(final Object obj1, final Object obj2, final Object... params) {
        if (!(obj1 != null && obj1.equals(obj2))) {
            throw new RuntimeException(Arrays.toString(params));
        }
    }

    /**
     * @Method notEquals
     * @param obj1
     * @param obj2
     * @param params
     * @Author 李煜龙
     * @Description TODD
     * @Date 2023/1/29 10:44
    */
    public static void notEquals(final Object obj1, final Object obj2, final Object... params) {
        if ((obj1 != null && obj1.equals(obj2)) || (obj2 != null && obj2.equals(obj1))) {
            throw new RuntimeException(Arrays.toString(params));
        }
    }

    /**
     * @Method isTrue
     * @param result
     * @param params
     * @Author 李煜龙
     * @Description TODD
     * @Date 2023/1/29 10:44
    */
    public static void isTrue(final boolean result, final Object... params) {
        if (!result) {
            throw new RuntimeException(Arrays.toString(params));
        }
    }

    /**
     * @Method isFalse
     * @param result
     * @param params
     * @Author 李煜龙
     * @Description TODD
     * @Date 2023/1/29 10:44
    */
    public static void isFalse(final boolean result, final Object... params) {
        if (result) {
            throw new RuntimeException(Arrays.toString(params));
        }
    }
}
