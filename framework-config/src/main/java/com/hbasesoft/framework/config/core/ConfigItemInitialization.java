/**
 * 
 */
package com.hbasesoft.framework.config.core;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.hbasesoft.framework.common.FrameworkException;
import com.hbasesoft.framework.common.StartupListener;
import com.hbasesoft.framework.common.utils.Assert;
import com.hbasesoft.framework.common.utils.logger.Logger;
import com.hbasesoft.framework.config.core.service.ConfigService;
import com.hbasesoft.framework.config.core.service.DictionaryDataService;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2014年11月16日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.core.config <br>
 */
@Component
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
    public LoadOrder getOrder() {
        return StartupListener.LoadOrder.FIRST;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param context <br>
     */
    @Override
    public void complete(ApplicationContext context) {
        logger.debug("---------------begin ConfigItem init ------------------");
        ConfigService configService = context.getBean(ConfigService.class);
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

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @throws FrameworkException <br>
     */
    @Override
    public void init() throws FrameworkException {
    }
}
