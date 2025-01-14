/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.cache.core.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.hbasesoft.framework.common.FrameworkException;
import com.hbasesoft.framework.common.GlobalConstants;
import com.hbasesoft.framework.common.annotation.Key;
import com.hbasesoft.framework.common.utils.engine.VelocityParseFactory;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2018年3月23日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.cache.core.redis.util <br>
 */
public final class KeyUtil {

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param template
     * @param method
     * @param args
     * @return String
     * @throws FrameworkException <br>
     */
    public static String getLockKey(final String template, final Method method, final Object[] args)
        throws FrameworkException {
        if (ArrayUtils.isNotEmpty(args)) {
            Annotation[][] parameterAnnotations = method.getParameterAnnotations();
            Map<String, Object> paramMap = new TreeMap<String, Object>();
            for (int i = 0; i < parameterAnnotations.length; i++) {
                for (Annotation annotation : parameterAnnotations[i]) {
                    if (annotation instanceof Key) {
                        Key key = (Key) annotation;
                        paramMap.put(key.value(), args[i]);
                        break;
                    }
                }
            }

            if (MapUtils.isNotEmpty(paramMap)) {
                StringBuilder sb = new StringBuilder();
                if (StringUtils.isNotEmpty(template)) {
                    sb.append(GlobalConstants.UNDERLINE)
                        .append(VelocityParseFactory.parse(template, paramMap));
                }
                else {
                    for (Entry<String, Object> entry : paramMap.entrySet()) {
                        sb.append(GlobalConstants.UNDERLINE)
                            .append(entry.getValue() == null ? GlobalConstants.BLANK : entry.getValue());
                    }
                }
                return sb.toString();
            }
        }
        return GlobalConstants.BLANK;
    }

}
