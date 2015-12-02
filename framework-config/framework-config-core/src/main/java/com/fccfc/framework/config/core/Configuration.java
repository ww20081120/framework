/**
 * 
 */
package com.fccfc.framework.config.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.fccfc.framework.cache.core.CacheConstant;
import com.fccfc.framework.cache.core.CacheHelper;
import com.fccfc.framework.common.ErrorCodeDef;
import com.fccfc.framework.common.GlobalConstants;
import com.fccfc.framework.common.ServiceException;
import com.fccfc.framework.common.utils.CommonUtil;
import com.fccfc.framework.common.utils.PropertyHolder;
import com.fccfc.framework.common.utils.logger.Logger;
import com.fccfc.framework.config.api.Config;
import com.fccfc.framework.config.api.ConfigService;

/**
 * <Description> <br>
 * 
 * @author xgf<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2014年10月26日 <br>
 * @since V1.0<br>
 * @see com.fccfc.framework.log.config <br>
 */

public final class Configuration {

    /**
     * logger
     */
    private static Logger logger = new Logger(Configuration.class);

    /**
     * configService
     */
    private static ConfigService.Iface configService;

    public static String getModuleCode() {
        return getString(CacheConstant.MODULE_CODE);
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param key <br>
     * @return <br>
     */
    public static Object get(String key) {
        String value = PropertyHolder.getProperty(key);
        if (value == null) {
            try {
                value = CacheHelper.getCache().getValue(CacheConstant.CACHE_KEY_CONFIGITEM, key);
            }
            catch (Exception e) {
                logger.warn(e, "get cache error. key is [{0}]", key);
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
     * Description: 根据key获取配置值：字符串类型 <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param key key
     * @param defaultValue 默认值
     * @return 值<br>
     */
    public static String getString(String key, String defaultValue) {
        String value = getString(key);
        return CommonUtil.isEmpty(value) ? defaultValue : value;
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

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param key <br>
     * @param defaultValue <br>
     * @return <br>
     */
    public static long getLong(String key, Long defaultValue) {
        String value = getString(key);
        return CommonUtil.isEmpty(value) ? defaultValue : Long.valueOf(value);
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @throws ServiceException <br>
     */
    public static void reloadCache() throws ServiceException {
        String moduleCode = getString(CacheConstant.MODULE_CODE);
        try {
            List<Config> configs = configService.queryAllConfig(moduleCode);
            if (CommonUtil.isNotEmpty(configs)) {
                Map<String, String> cacheMap = new HashMap<String, String>();
                for (Config conf : configs) {
                    if (CommonUtil.isNotEmpty(conf.getParamValue())) {
                        cacheMap.put(conf.getConfigItemCode() + "." + conf.getParamCode(), conf.getParamValue());
                    }
                }
                CacheHelper.getCache().putNode(CacheConstant.CACHE_KEY_CONFIGITEM, cacheMap);
            }
        }
        catch (Exception e) {
            throw new ServiceException(ErrorCodeDef.CACHE_ERROR_10002, e);
        }
    }

    public static void setConfigService(ConfigService.Iface configService) {
        Configuration.configService = configService;
    }

    /**
     * Description: 匹配配置项中是否含有matchValue<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param key <br>
     * @param matchValue <br>
     * @return <br>
     */
    public static boolean match(String key, String matchValue) {
        boolean ismatch = false;
        String value = getString(key);
        if (CommonUtil.isNotEmpty(value) && CommonUtil.isNotEmpty(matchValue)) {
            ismatch = new StringBuilder().append(GlobalConstants.SPLITOR).append(value).append(GlobalConstants.SPLITOR)
                .indexOf(new StringBuilder().append(GlobalConstants.SPLITOR).append(matchValue)
                    .append(GlobalConstants.SPLITOR).toString()) != -1;
        }
        return ismatch;
    }
}
