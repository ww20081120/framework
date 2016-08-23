package com.hbasesoft.framework.common.utils.bean;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.hbasesoft.framework.common.ServiceException;

/**
 * json,file互转方法常用类(基于jackson)
 * 
 * @author meiguiyang
 * @version [版本号, Mar 30, 2013]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public final class JsonUtil {
    /**
     * 日志log对象
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonUtil.class);

    /**
     * objectMapper转换
     */
    private static ObjectMapper objectMapper = new ObjectMapper();
    static {
        // 设置序列化的null值处理
        objectMapper.setSerializationInclusion(Include.NON_NULL);
        // 设置日期处理格式
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        // json转换对象忽略找不到属性的对象
        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        // 设置去掉null值和对应的属性
        objectMapper.disable(SerializationFeature.WRITE_NULL_MAP_VALUES);
        // 设置数字按字符串处理
        objectMapper.configure(JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS, true);
    }

    private JsonUtil() {
    }

    /**
     * 文件json数据转换数据
     * 
     * @param path json文件对象路径
     * @return Map<String, Object>
     * @see [类、类#方法、类#成员]
     */
    public static Map<String, Object> getJsonFromFile(String path) {
        if (StringUtils.isNotEmpty(path)) {
            File file = new File(path);
            try {
                return getJsonFromFile(file, new TypeReference<Map<String, Object>>() {
                });
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        return null;
    }

    /**
     * 文件对象json数据转换数据
     * 
     * @param <T> 任意类型
     * @param file File对象
     * @param typeReference 转换器
     * @return T
     * @throws ServiceException 业务异常
     * @see [类、类#方法、类#成员]
     */
    public static <T> T getJsonFromFile(File file, TypeReference<T> typeReference) {
        if (file != null) {
            try {
                return objectMapper.readValue(file, typeReference);
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        return null;
    }

    /**
     * 将JSON数组转换成 List<T>
     * 
     * @param jsonData json字符串
     * @return List<T>
     */
    public static <T> List<T> readJson2List(String jsonData, Class<T> clazz) {
        return readJson2Object(jsonData, new TypeReference<List<T>>() {
        });
    }

    /**
     * 将JSON数组转换成 Map<String, Object>
     * 
     * @param jsonData json字符串
     * @return Map<String, Object>
     */
    public static Map<String, Object> readJson2Object(String jsonData) {
        return readJson2Object(jsonData, new TypeReference<Map<String, Object>>() {
        });
    }

    /**
     * 将JSON数组转换成obj(支持泛型)
     * 
     * @param <T> 任意类型
     * @param jsonData json字符串
     * @param typeReference 类型转换器
     * @return T
     */
    public static <T> T readJson2Object(String jsonData, TypeReference<T> typeReference) {
        if (jsonData != null) {
            try {
                return objectMapper.readValue(jsonData, typeReference);
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        return null;
    }

    /**
     * 将JSON数组转换成obj(支持泛型)
     * 
     * @param <T> 任意类型
     * @param jsonData json字符串
     * @param c 复杂对象类型
     * @return T 对象
     * @throws ServiceException 业务异常
     * @see [类、类#方法、类#成员]
     */
    public static <T> T readJson2Object(String jsonData, Class<T> c) {
        if (jsonData != null) {
            try {
                return objectMapper.readValue(jsonData, c);
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        return null;
    }

    /**
     * obj换成JSON
     * 
     * @param obj obj
     * @return JSON字符串
     * @throws ServiceException ServiceException
     * @see [类、类#方法、类#成员]
     */
    public static String writeObj2Json(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * obj换成JSON(带json格式)
     * 
     * @param obj obj
     * @return JSON字符串 (带json格式)
     * @throws ServiceException ServiceException
     * @see [类、类#方法、类#成员]
     */
    public static String writeObj2FormatJson(Object obj) {
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 单一对象的转化
     * 
     * @param json json 对象(也可以是map对象)
     * @param c 泛型对象
     * @return 泛型对象
     * @throws CommonException
     */
    public static <T> T convertValue(Object json, Class<T> c) {
        if (json == null) {
            return null;
        }
        return objectMapper.convertValue(json, c);
    }

    /**
     * 单一对象的转化
     * 
     * @param json json 对象(也可以是map对象)
     * @param c 泛型对象
     * @return 泛型对象
     * @throws CommonException
     */
    public static <T> T convertValue(Object obj, TypeReference<T> typeReference) {
        if (obj == null) {
            return null;
        }
        return objectMapper.convertValue(obj, typeReference);
    }

    /**
     * 对象转换成<T>的集合
     * 
     * @param json json对象(也可以是map对象)
     * @param c 泛型对象
     * @return 对象集合
     * @throws CommonException
     */
    public static <T> List<T> convertValue2List(Object json, Class<T> c) {
        if (json == null) {
            return null;
        }
        return objectMapper.convertValue(json, new TypeReference<List<T>>() {
        });
    }

    /**
     * 对象转换成<T>的集合
     * 
     * @param json json对象(也可以是map对象)
     * @param c 泛型对象
     * @return 对象集合
     * @throws CommonException
     */
    public static <T> T[] convertValue2Array(Object json, Class<T> c) {
        if (json == null) {
            return null;
        }
        return objectMapper.convertValue(json, new TypeReference<T[]>() {
        });
    }

    /**
     * writeValue
     * 
     * @param jgen
     * @param obj
     */
    public static void writeValue(JsonGenerator jgen, Object obj) {
        try {
            objectMapper.writeValue(jgen, obj);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}