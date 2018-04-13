/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.common.utils.bean;

import java.util.Map;

import com.alibaba.fastjson.JSONObject;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2017年10月16日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.common.utils.bean <br>
 */
@SuppressWarnings("rawtypes")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JSONUtil {

    public static String toJSONString(Object[][] array) {
        return toJSON(array).toJSONString();
    }

    public static JSONObject toJSON(Object[][] array) {

        if (array == null) {
            return null;
        }
        final JSONObject map = new JSONObject((int) (array.length * 1.5));
        for (int i = 0; i < array.length; i++) {
            Object object = array[i];
            if (object instanceof Map.Entry) {
                Map.Entry entry = (Map.Entry) object;
                map.put(entry.getKey().toString(), entry.getValue());
            }
            else if (object instanceof Object[]) {
                Object[] entry = (Object[]) object;
                if (entry.length < 2) {
                    throw new IllegalArgumentException(
                        "Array element " + i + ", '" + object + "', has a length less than 2");
                }
                map.put(entry[0].toString(), entry[1]);
            }
            else {
                throw new IllegalArgumentException(
                    "Array element " + i + ", '" + object + "', is neither of type Map.Entry nor an Array");
            }
        }
        return map;

    }

}
