package com.fccfc.framework.common.utils.bean;

import com.alibaba.fastjson.JSONObject;

/**
 * Json转换工具类
 *
 * @author skysun
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class JsonUtil {

    /**
     * 将对象转化为JSON
     *
     * @param obj 任意类型对象
     * @return JSON字符串
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    public static String writeObj2JSON(Object obj) {
        return JSONObject.toJSONString(obj);
    }

}
