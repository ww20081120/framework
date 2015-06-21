/**
 * 
 */
package com.fccfc.framework.config.core;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.fccfc.framework.cache.core.CacheConstant;
import com.fccfc.framework.cache.core.CacheHelper;
import com.fccfc.framework.common.utils.logger.Logger;

/**
 * <Description> <br>
 * 
 * @author xgf<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2014年10月26日 <br>
 * @since V7.3<br>
 * @see com.fccfc.framework.log.config <br>
 */

public final class Configuration {

    private static Logger logger = new Logger(Configuration.class);

    private static Map<String, Object> cache;

    /**
     * 获取模块代码
     * 
     * @return 模块代码
     */
    @SuppressWarnings("unchecked")
    public static List<String> getModuleCode() {
        return (List<String>) get(CacheConstant.MODULE_CODE);
    }

    /**
     * 获取模块代码
     * 
     * @return 模块代码
     */
    public static String getLocalModuleCode() {
        return getString(CacheConstant.LOCAL_MODULE_CODE);
    }

    public static Object get(String key) {
        Object value = cache == null ? null : cache.get(key);
        if (value == null) {
            try {
                value = CacheHelper.getStringCache().getValue(CacheConstant.CACHE_KEY_CONFIGITEM, key);
            }
            catch (Exception e) {
                logger.warn("get cache error. key is [{0}]", e, key);
            }
        }
        return value;
    }

    /**
     * 根据key获取配置值：字符串类型
     * 
     * @param key key
     * @return 值
     */
    public static String getString(String key) {
        Object value = get(key);
        return value == null ? null : value.toString();
    }

    /**
     * 根据key获取配置值：布尔型
     * 
     * @param key key
     * @return 值
     */
    public static boolean getBoolean(String key) {
        return StringUtils.equalsIgnoreCase("true", getString(key));
    }

    /**
     * 根据key获取配置值：整型
     * 
     * @param key key
     * @return 值
     */
    public static int getInt(String key) {
        return Integer.valueOf(getString(key));
    }

    /**
     * 根据key获取配置值：长整型
     * 
     * @param key key
     * @return 值
     */
    public static long getLong(String key) {
        return Long.valueOf(getString(key));
    }

    public static void setCache(Map<String, Object> cache) {
        Configuration.cache = cache;
    }

    public static void clear() {
        if (cache != null) {
            cache.clear();
        }
        cache = null;
    }
}
