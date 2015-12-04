/**
 * 
 */
package com.fccfc.framework.web.manager.service.common.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.fccfc.framework.cache.core.CacheConstant;
import com.fccfc.framework.cache.core.annotation.Cache;
import com.fccfc.framework.common.ServiceException;
import com.fccfc.framework.common.utils.CommonUtil;
import com.fccfc.framework.config.core.bean.ModulePojo;
import com.fccfc.framework.config.core.dao.ModuleDao;
import com.fccfc.framework.db.core.DaoException;
import com.fccfc.framework.web.manager.service.common.ModuleService;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2014年11月16日 <br>
 * @since V1.0<br>
 * @see com.fccfc.framework.core.config.service.impl <br>
 */
@Service
public class ModuleServiceImpl implements ModuleService {

    /**
     * moduleDao
     */
    @Resource
    private ModuleDao moduleDao;

    /*
     * (non-Javadoc)
     * @see com.fccfc.framework.core.config.service.ConfigurationService#selectAllModule()
     */
    @Cache(node = CacheConstant.MODULE_DATA)
    public Map<String, ModulePojo> selectAllModule() throws ServiceException {
        Map<String, ModulePojo> moduleMap = null;
        try {
            List<ModulePojo> moduleList = moduleDao.selectAllModule();
            if (CommonUtil.isNotEmpty(moduleList)) {
                moduleMap = new HashMap<String, ModulePojo>();
                for (ModulePojo module : moduleList) {
                    moduleMap.put(module.getModuleCode(), module);
                }
            }
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }
        return moduleMap;
    }

}
