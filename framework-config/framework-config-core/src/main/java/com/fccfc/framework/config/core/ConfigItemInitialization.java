/**
 * 
 */
package com.fccfc.framework.config.core;

import javax.annotation.Resource;

import com.fccfc.framework.common.FrameworkException;
import com.fccfc.framework.common.Initialization;
import com.fccfc.framework.common.InitializationException;
import com.fccfc.framework.common.utils.logger.Logger;
import com.fccfc.framework.config.api.ConfigService;
import com.fccfc.framework.config.core.service.ConfigurationService;
import com.fccfc.framework.config.core.service.DictionaryDataService;

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
     * configurationService
     */
    @Resource
    private ConfigurationService configurationService;

    /**
     * configService
     */
    @Resource
    private ConfigService.Iface configService;

    /**
     * dictionaryDataService
     */
    @Resource
    private DictionaryDataService dictionaryDataService;

    @Override
    public void afterPropertiesSet() throws FrameworkException {
        logger.debug("---------------begin ConfigItem init ------------------");

        Configuration.setConfigService(configService);
        Configuration.reloadCache();

        DictionaryHelper.setdictDataService(dictionaryDataService);
        DictionaryHelper.reloadCache(); // 加载字典数据
        logger.debug("---------------end ConfigItem int ------------------");
    }

    /*
     * (non-Javadoc)
     * @see com.fccfc.framework.core.Initialization#destory()
     */
    @Override
    public void destroy() throws InitializationException {
        logger.debug("---------------ConfigItem destory ------------------");
    }
}
