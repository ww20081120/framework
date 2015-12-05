/**
 * 
 */
package com.fccfc.framework.config.core;

import org.springframework.context.ApplicationContext;

import com.fccfc.framework.common.StartupListener;
import com.fccfc.framework.common.utils.Assert;
import com.fccfc.framework.common.utils.logger.Logger;
import com.fccfc.framework.config.api.ConfigService;
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
public class ConfigItemInitialization implements StartupListener {

    /**
     * logger
     */
    private static Logger logger = new Logger(ConfigItemInitialization.class);

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    @Override
    public int getOrder() {
        return Integer.MAX_VALUE;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param context <br>
     */
    @Override
    public void init(ApplicationContext context) {
        logger.debug("---------------begin ConfigItem init ------------------");
        ConfigService.Iface configService = context.getBean(ConfigService.Iface.class);
        Assert.notNull(configService, "未设置ConfigService.Iface的实现类， 配置项不可以使用");
        DictionaryDataService dictionaryDataService = context.getBean(DictionaryDataService.class);
        Assert.notNull(dictionaryDataService, "未设置DictionaryDataService的实现类， 字典数据不可以使用");

        ConfigHelper.setConfigService(configService);
        ConfigHelper.setDictionaryDataService(dictionaryDataService);
        logger.debug("---------------end ConfigItem int ------------------");
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Override
    public void destory() {
        logger.debug("---------------ConfigItem destory ------------------");
    }
}
