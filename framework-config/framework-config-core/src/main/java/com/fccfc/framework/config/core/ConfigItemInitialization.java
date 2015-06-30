/**
 * 
 */
package com.fccfc.framework.config.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;

import com.fccfc.framework.cache.core.CacheHelper;
import com.fccfc.framework.cache.core.redis.RedisCache;
import com.fccfc.framework.cache.core.redis.RedisStringCache;
import com.fccfc.framework.cache.simple.SimpleCache;
import com.fccfc.framework.cache.simple.SimpleStringCache;
import com.fccfc.framework.common.FrameworkException;
import com.fccfc.framework.common.Initialization;
import com.fccfc.framework.common.InitializationException;
import com.fccfc.framework.common.utils.logger.Logger;
import com.fccfc.framework.config.api.ConfigService;
import com.fccfc.framework.config.core.service.ConfigurationService;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2014年11月16日 <br>
 * @since V1.0<br>
 * @see com.fccfc.framework.core.config <br>
 */
public class ConfigItemInitialization implements Initialization {

    /**
     * logger
     */
    private static Logger logger = new Logger(ConfigItemInitialization.class);

    /**
     * params
     */
    private Map<String, String> params;

    /**
     * configurationService
     */
    @Resource
    private ConfigurationService configurationService;

    /**
     * configService
     */
    @Resource
    private ConfigService.Iface configService;

    /*
     * (non-Javadoc)
     * @see com.fccfc.framework.core.Initialization#init()
     */
    @SuppressWarnings({
        "rawtypes", "unchecked"
    })
    @Override
    public void afterPropertiesSet() throws FrameworkException {
        logger.debug("---------------begin ConfigItem init ------------------");

        if (RedisCache.CACHE_MODEL.equals(params.get("CACHE_MODULE"))) {
            String host = params.get("CACHE_REDIS_HOST");
            Integer port = Integer.valueOf(params.get("CACHE_REDIS_PORT"));
            CacheHelper.setCache(new RedisCache(host, port));
            CacheHelper.setStringCache(new RedisStringCache(host, port));
        }
        else {
            Map map = new ConcurrentHashMap();
            CacheHelper.setCache(new SimpleCache(map));
            CacheHelper.setStringCache(new SimpleStringCache(map));
        }

        Configuration.setConfigService(configService);
        Configuration.setCache(params);
        Configuration.setAllModules(configurationService.selectAllModule());

        Configuration.reloadCache();
        logger.debug("---------------end ConfigItem int ------------------");
    }

    /*
     * (non-Javadoc)
     * @see com.fccfc.framework.core.Initialization#destory()
     */
    @Override
    public void destroy() throws InitializationException {
        logger.debug("---------------ConfigItem destory ------------------");
        params.clear();
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }
}
