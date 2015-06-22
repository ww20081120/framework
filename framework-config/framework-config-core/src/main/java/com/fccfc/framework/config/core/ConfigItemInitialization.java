/**
 * 
 */
package com.fccfc.framework.config.core;

import java.util.Map;

import javax.annotation.Resource;

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

    private static Logger logger = new Logger(ConfigItemInitialization.class);

    private Map<String, String> params;

    @Resource
    private ConfigurationService configurationService;

    @Resource
    private ConfigService.Iface configService;

    /*
     * (non-Javadoc)
     * @see com.fccfc.framework.core.Initialization#init()
     */
    @Override
    public void afterPropertiesSet() throws FrameworkException {
        logger.debug("---------------begin ConfigItem init ------------------");
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
