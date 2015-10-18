/**
 * 
 */
package com.fccfc.framework.config.core.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.fccfc.framework.cache.core.CacheConstant;
import com.fccfc.framework.cache.core.CacheException;
import com.fccfc.framework.cache.core.CacheHelper;
import com.fccfc.framework.common.ServiceException;
import com.fccfc.framework.common.utils.CommonUtil;
import com.fccfc.framework.config.core.bean.ModulePojo;
import com.fccfc.framework.config.core.dao.ModuleDao;
import com.fccfc.framework.config.core.service.ConfigurationService;
import com.fccfc.framework.db.core.DaoException;

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
public class ConfigurationServiceImpl implements ConfigurationService {

    /**
     * moduleDao
     */
    @Resource
    private ModuleDao moduleDao;

    /*
     * (non-Javadoc)
     * @see com.fccfc.framework.core.config.service.ConfigurationService#selectAllModule()
     */
    @Override
    public List<ModulePojo> selectAllModule() throws ServiceException {

        try {
            Map<String, Object> moduleMap = CacheHelper.getCache().getNode(CacheConstant.MODULE_DATA);
            List<ModulePojo> moduleList = null;
            if (CommonUtil.isNotEmpty(moduleMap)) {
                moduleList = new ArrayList<ModulePojo>();
                for (Object obj : moduleMap.values()) {
                    moduleList.add((ModulePojo) obj);
                }
            }
            else {
                moduleList = moduleDao.selectAllModule();
                if (CommonUtil.isNotEmpty(moduleList)) {
                    moduleMap = new HashMap<String, Object>();
                    for (ModulePojo module : moduleList) {
                        moduleMap.put(module.getModuleCode(), module);
                    }
                    CacheHelper.getCache().putNode(CacheConstant.MODULE_DATA, moduleMap);
                }
            }
            return moduleList;
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }
        catch (CacheException e) {
            throw new ServiceException(e);
        }
    }

}
