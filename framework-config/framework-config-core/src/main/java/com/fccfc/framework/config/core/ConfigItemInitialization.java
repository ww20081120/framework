/**
 * 
 */
package com.fccfc.framework.config.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;

import com.fccfc.framework.cache.core.CacheConstant;
import com.fccfc.framework.cache.core.CacheHelper;
import com.fccfc.framework.common.FrameworkException;
import com.fccfc.framework.common.Initialization;
import com.fccfc.framework.common.InitializationException;
import com.fccfc.framework.common.utils.CommonUtil;
import com.fccfc.framework.common.utils.logger.Logger;
import com.fccfc.framework.config.core.bean.ModulePojo;
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

    private Map<String, Object> params;

    private boolean loadFromDatabase = true;

    @Resource
    private ConfigurationService configurationService;

    /*
     * (non-Javadoc)
     * @see com.fccfc.framework.core.Initialization#init()
     */
    @Override
    public void afterPropertiesSet() throws FrameworkException {
        logger.debug("---------------begin ConfigItem init ------------------");
        Configuration.setCache(params);
        if (loadFromDatabase) {
            List<Map<String, Object>> paramList = configurationService.loadAll();
            if (CommonUtil.isNotEmpty(paramList)) {
                for (Map<String, Object> param : paramList) {
                    CacheHelper.getStringCache().putValue(CacheConstant.CACHE_KEY_CONFIGITEM,
                        param.get("CONFIG_ITEM_CODE") + "." + param.get("PARAM_CODE"),
                        (String) param.get("PARAM_VALUE"));
                }
            }

            String moduleCode = Configuration.getLocalModuleCode();
            List<ModulePojo> moduleList = configurationService.selectAllModule();
            if (CommonUtil.isNotEmpty(moduleList)) {
                params.put(CacheConstant.MODULE_CODE, selectAllModule(moduleCode, new ArrayList<String>(), moduleList));
            }
        }
        logger.debug("---------------end ConfigItem int ------------------");
    }

    public List<String> selectAllModule(String moduleCode, List<String> moduleList, List<ModulePojo> modulePojoList) {
        for (ModulePojo modulePojo : modulePojoList) {
            if (StringUtils.equals(moduleCode, modulePojo.getModuleCode())) {
                moduleList.add(moduleCode);
                if (CommonUtil.isNotEmpty(modulePojo.getParentModuleCode())) {
                    selectAllModule(modulePojo.getParentModuleCode(), moduleList, modulePojoList);
                }
                break;
            }
        }
        return moduleList;
    }

    /*
     * (non-Javadoc)
     * @see com.fccfc.framework.core.Initialization#destory()
     */
    @Override
    public void destroy() throws InitializationException {
        logger.debug("---------------ConfigItem destory ------------------");
        Configuration.clear();
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public void setLoadFromDatabase(boolean loadFromDatabase) {
        this.loadFromDatabase = loadFromDatabase;
    }
}
