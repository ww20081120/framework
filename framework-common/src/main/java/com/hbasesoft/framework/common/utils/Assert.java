/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.common.utils;

import java.util.Collection;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.hbasesoft.framework.common.ErrorCode;

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
     * @author 王伟<br>
     * @taskId <br>
     * @param obj
     * @param errorCode
     * @param params <br>
     */
    public static void notNull(final Object obj, final ErrorCode errorCode, final Object... params) {
        if (obj == null) {
            throw new AssertException(errorCode, params);
        }
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param obj
     * @param errorCode
     * @param params
     * @throws AssertException <br>
     */
    public static void isNull(final Object obj, final ErrorCode errorCode, final Object... params)
        throws AssertException {
        if (obj != null) {
            throw new AssertException(errorCode, params);
        }
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param str
     * @param errorCode
     * @param params
     * @throws AssertException <br>
     */
    public static void notEmpty(final String str, final ErrorCode errorCode, final Object... params)
        throws AssertException {
        if (StringUtils.isEmpty(str)) {
            throw new AssertException(errorCode, params);
        }
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param str
     * @param errorCode
     * @param params
     * @throws AssertException <br>
     */
    public static void isEmpty(final String str, final ErrorCode errorCode, final Object... params)
        throws AssertException {
        if (StringUtils.isNotEmpty(str)) {
            throw new AssertException(errorCode, params);
        }
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param str
     * @param errorCode
     * @param params
     * @param <T> T
     * @throws AssertException <br>
     */
    public static <T> void notEmpty(final T[] str, final ErrorCode errorCode, final Object... params)
        throws AssertException {
        if (ArrayUtils.isEmpty(str)) {
            throw new AssertException(errorCode, params);
        }
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param str
     * @param errorCode
     * @param params
     * @param <T> T
     * @throws AssertException <br>
     */
    public static <T> void isEmpty(final T[] str, final ErrorCode errorCode, final Object... params)
        throws AssertException {
        if (!ArrayUtils.isEmpty(str)) {
            throw new AssertException(errorCode, params);
        }
    }

    /**
     * Description: notEmpty<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param str
     * @param errorCode
     * @param params
     * @throws AssertException <br>
     */
    public static void notEmpty(final Collection<?> str, final ErrorCode errorCode, final Object... params)
        throws AssertException {
        if (CollectionUtils.isEmpty(str)) {
            throw new AssertException(errorCode, params);
        }
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param str
     * @param errorCode
     * @param params
     * @throws AssertException <br>
     */
    public static void isEmpty(final Collection<?> str, final ErrorCode errorCode, final Object... params)
        throws AssertException {
        if (CollectionUtils.isNotEmpty(str)) {
            throw new AssertException(errorCode, params);
        }
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param str
     * @param errorCode
     * @param params
     * @throws AssertException <br>
     */
    public static void notEmpty(final Map<?, ?> str, final ErrorCode errorCode, final Object... params)
        throws AssertException {
        if (MapUtils.isEmpty(str)) {
            throw new AssertException(errorCode, params);
        }
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param str
     * @param errorCode
     * @param params
     * @throws AssertException <br>
     */
    public static void isEmpty(final Map<?, ?> str, final ErrorCode errorCode, final Object... params)
        throws AssertException {
        if (MapUtils.isNotEmpty(str)) {
            throw new AssertException(errorCode, params);
        }
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param obj1
     * @param obj2
     * @param errorCode
     * @param params <br>
     */
    public static void equals(final Object obj1, final Object obj2, final ErrorCode errorCode, final Object... params) {
        if (!(obj1 != null && obj1.equals(obj2))) {
            throw new AssertException(errorCode, params);
        }
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param obj1
     * @param obj2
     * @param errorCode
     * @param params <br>
     */
    public static void notEquals(final Object obj1, final Object obj2, final ErrorCode errorCode,
        final Object... params) {
        if ((obj1 != null && obj1.equals(obj2)) || (obj2 != null && obj2.equals(obj1))) {
            throw new AssertException(errorCode, params);
        }
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param result
     * @param errorCode
     * @param params <br>
     */
    public static void isTrue(final boolean result, final ErrorCode errorCode, final Object... params) {
        if (!result) {
            throw new AssertException(errorCode, params);
        }
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param result
     * @param errorCode
     * @param params <br>
     */
    public static void isFalse(final boolean result, final ErrorCode errorCode, final Object... params) {
        if (result) {
            throw new AssertException(errorCode, params);
        }
    }
}
