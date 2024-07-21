/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.db.demo.util;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.hbasesoft.framework.common.utils.io.IOUtil;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2024年7月20日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.db.demo.util <br>
 */
public class DataUtil {

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param name
     * @return <br>
     */
    public static String loadData(final String name) {
        return IOUtil.readString(DataUtil.class.getClassLoader().getResourceAsStream(name));
    }

    /**
     * Description: 对比两个bean<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param <T> T
     * @param obj1
     * @param obj2
     * @param fieldStrs 需要对比的内容，如果不填则对比所有内容
     * @return <br>
     */
    public static <T> boolean equals(final T obj1, final T obj2, final String... fieldStrs) {
        if (obj1 == null || obj2 == null) {
            return false;
        }
        List<String> filedList = Arrays.asList(fieldStrs);
        return Arrays.stream(obj1.getClass().getDeclaredFields())
            .filter(field -> !"serialVersionUID".equals(field.getName()) && CollectionUtils.isEmpty(filedList)
                || filedList.contains(field.getName())) // 排除序列化ID
            .allMatch(field -> match(field, obj1, obj2));
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param <T> T
     * @param obj1
     * @param obj2
     * @param fieldStrs 不需要对比的内容
     * @return <br>
     */
    public static <T> boolean equalsWithoutFields(final T obj1, final T obj2, final String... fieldStrs) {
        if (obj1 == null || obj2 == null) {
            return false;
        }
        List<String> filedList = Arrays.asList(fieldStrs);
        return Arrays.stream(obj1.getClass().getDeclaredFields())
            // 排除序列化ID
            .filter(field -> !"serialVersionUID".equals(field.getName()) && !filedList.contains(field.getName()))
            .allMatch(field -> match(field, obj1, obj2));
    }

    private static <T> boolean match(final Field field, final T obj1, final T obj2) {
        field.setAccessible(true);
        try {
            Object v1 = field.get(obj1);
            Object v2 = field.get(obj2);
            boolean result;
            if (v1 != null) {
                result = v1.equals(v2);
            }
            else if (v2 != null) {
                result = v2.equals(v1);
            }
            else {
                result = true;
            }
            if (!result) {
                LoggerUtil.info("{0}.{1}匹配失败,value1={2},value2={3}", obj1.getClass().getClass().getName(),
                    field.getName(), v1, v2);
            }
            return result;
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
