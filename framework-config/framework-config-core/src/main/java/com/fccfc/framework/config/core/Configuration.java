/**
 * 
 */
package com.fccfc.framework.config.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;

import com.fccfc.framework.cache.core.CacheConstant;
import com.fccfc.framework.cache.core.CacheHelper;
import com.fccfc.framework.common.ErrorCodeDef;
import com.fccfc.framework.common.ServiceException;
import com.fccfc.framework.common.utils.CommonUtil;
import com.fccfc.framework.common.utils.logger.Logger;
import com.fccfc.framework.config.api.Config;
import com.fccfc.framework.config.api.ConfigService;
import com.fccfc.framework.config.core.bean.ModulePojo;

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

    private static final long CACHE_TIME = 3600 * 1000;

    private static Logger logger = new Logger(Configuration.class);

    private static Map<String, String> cache;

    private static Map<String, ModulePojo> allModules;

    private static ConfigService.Iface configService;

    /**
     * 获取模块代码
     * 
     * @return 模块代码
     */
    public static List<String> getModuleCode(String modelCode) {
        List<String> moduleCodeList = new ArrayList<String>();
        while (CommonUtil.isNotEmpty(modelCode)) {
            ModulePojo module = allModules.get(modelCode);
            if (module != null) {
                moduleCodeList.add(module.getModuleCode());
                modelCode = module.getParentModuleCode();
            }
            else {
                modelCode = null;
            }
        }
        return moduleCodeList;
    }

    public static List<String> getModuleCode() {
        return getModuleCode(cache.get(CacheConstant.LOCAL_MODULE_CODE));
    }

    public static Object get(String key) {
        String value = cache == null ? null : cache.get(key);
        if (value == null) {
            try {
                String cacheTime = CacheHelper.getStringCache().getValue(CacheConstant.CACHE_KEY_CONFIGITEM,
                    CacheConstant.CONFIG_CACHE_TIME);
                if (CommonUtil.isEmpty(cacheTime)
                    || (Long.valueOf(cacheTime) + CACHE_TIME > System.currentTimeMillis())) {
                    logger.info("开始重新加载配置项");
                    reloadCache();
                    CacheHelper.getStringCache().putValue(CacheConstant.CACHE_KEY_CONFIGITEM,
                        CacheConstant.CONFIG_CACHE_TIME, String.valueOf(System.currentTimeMillis()));
                    logger.info("重新加载配置项结束");
                }
                value = CacheHelper.getStringCache().getValue(CacheConstant.CACHE_KEY_CONFIGITEM, key);
            }
            catch (Exception e) {
                logger.warn("get cache error. key is [{0}]", key);
                logger.warn(e);
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

    public static void setCache(Map<String, String> cache) {
        Configuration.cache = cache;
    }

    public static void setAllModules(List<ModulePojo> allModules) {
        Map<String, ModulePojo> map = new ConcurrentHashMap<String, ModulePojo>();
        for (ModulePojo pojo : allModules) {
            map.put(pojo.getModuleCode(), pojo);
        }
        Configuration.allModules = map;
    }

    public static void reloadCache() throws ServiceException {
        String moduleCode = cache.get(CacheConstant.LOCAL_MODULE_CODE);
        try {
            List<Config> configs = configService.queryAllConfig(moduleCode);
            if (CommonUtil.isNotEmpty(configs)) {
                Map<String, String> cacheMap = new HashMap<String, String>();
                for (Config conf : configs) {
                    cacheMap.put(conf.getConfigItemCode() + "." + conf.getParamCode(), conf.getParamValue());
                }
                CacheHelper.getStringCache().putNode(CacheConstant.CACHE_KEY_CONFIGITEM, cacheMap);
            }
        }
        catch (Exception e) {
            throw new ServiceException(ErrorCodeDef.CACHE_ERROR_10002, e);
        }
    }

    public static void setConfigService(ConfigService.Iface configService) {
        Configuration.configService = configService;
    }
}
