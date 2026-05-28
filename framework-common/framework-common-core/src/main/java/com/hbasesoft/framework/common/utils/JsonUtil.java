/****************************************************************************************
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.common.utils;

import java.text.SimpleDateFormat;
import java.util.Map;

import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.FrameworkException;
import com.hbasesoft.framework.common.utils.date.DateUtil;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;

/**
 * JSON 工具类，基于 Jackson 3 (tools.jackson) <br>
 *
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2026年5月27日 <br>
 * @since V1.0<br>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JsonUtil {

    /** 共享的 ObjectMapper 实例 */
    private static final ObjectMapper OBJECT_MAPPER = tools.jackson.databind.json.JsonMapper.builder()
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        .defaultDateFormat(new SimpleDateFormat(DateUtil.DATETIME_FORMAT_19)).build();

    /**
     * 将对象序列化为 JSON 字符串
     *
     * @param obj 要序列化的对象
     * @return JSON 字符串
     */
    public static String toJson(final Object obj) {
        try {
            return OBJECT_MAPPER.writeValueAsString(obj);
        }
        catch (Exception e) {
            throw new FrameworkException(e, ErrorCodeDef.FAILURE);
        }
    }

    /**
     * 将 JSON 字符串反序列化为指定类型
     *
     * @param json JSON 字符串
     * @param clazz 目标类型
     * @param <T> 目标类型泛型
     * @return 反序列化后的对象
     */
    public static <T> T fromJson(final String json, final Class<T> clazz) {
        try {
            return OBJECT_MAPPER.readValue(json, clazz);
        }
        catch (Exception e) {
            throw new FrameworkException(e, ErrorCodeDef.FAILURE);
        }
    }

    /**
     * 将 JSON 字符串解析为 Map
     *
     * @param json JSON 字符串
     * @return Map 表示
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> parseMap(final String json) {
        try {
            return OBJECT_MAPPER.readValue(json, Map.class);
        }
        catch (Exception e) {
            throw new FrameworkException(e, ErrorCodeDef.FAILURE);
        }
    }

    /**
     * 将 JSON 字符串解析为 ObjectNode（Jackson 树模型）
     *
     * @param json JSON 字符串
     * @return ObjectNode
     */
    public static ObjectNode parseObject(final String json) {
        try {
            JsonNode node = OBJECT_MAPPER.readTree(json);
            if (node.isObject()) {
                return (ObjectNode) node;
            }
            throw new FrameworkException(ErrorCodeDef.FAILURE, "JSON is not an object: " + json);
        }
        catch (FrameworkException e) {
            throw e;
        }
        catch (Exception e) {
            throw new FrameworkException(e, ErrorCodeDef.FAILURE);
        }
    }

    /**
     * 将 JSON 字符串解析为 ArrayNode
     *
     * @param json JSON 字符串
     * @return ArrayNode
     */
    public static ArrayNode parseArray(final String json) {
        try {
            JsonNode node = OBJECT_MAPPER.readTree(json);
            if (node.isArray()) {
                return (ArrayNode) node;
            }
            throw new FrameworkException(ErrorCodeDef.FAILURE, "JSON is not an array: " + json);
        }
        catch (FrameworkException e) {
            throw e;
        }
        catch (Exception e) {
            throw new FrameworkException(e, ErrorCodeDef.FAILURE);
        }
    }

    /**
     * 将 JsonNode 树节点转换为指定类型的 Java 对象
     *
     * @param node JsonNode 节点
     * @param clazz 目标类型
     * @param <T> 目标类型泛型
     * @return 转换后的对象
     */
    public static <T> T treeToValue(final JsonNode node, final Class<T> clazz) {
        try {
            return OBJECT_MAPPER.treeToValue(node, clazz);
        }
        catch (Exception e) {
            throw new FrameworkException(e, ErrorCodeDef.FAILURE);
        }
    }

    /**
     * 将 Java 对象转换为 JsonNode 树节点
     *
     * @param value Java 对象
     * @return JsonNode 树节点
     */
    public static JsonNode valueToTree(final Object value) {
        return OBJECT_MAPPER.valueToTree(value);
    }

    /**
     * 将一个 Java 对象转换为另一个类型（通过 JSON 中间表示）
     *
     * @param fromValue 源对象
     * @param toValueType 目标类型
     * @param <T> 目标类型泛型
     * @return 转换后的对象
     */
    public static <T> T convertValue(final Object fromValue, final Class<T> toValueType) {
        return OBJECT_MAPPER.convertValue(fromValue, toValueType);
    }
}
