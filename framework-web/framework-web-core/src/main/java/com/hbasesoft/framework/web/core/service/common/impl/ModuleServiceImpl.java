/**
 * 
 */
package com.hbasesoft.framework.web.core.service.common.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.hbasesoft.framework.cache.core.CacheConstant;
import com.hbasesoft.framework.cache.core.annotation.Cache;
import com.hbasesoft.framework.cache.core.annotation.CacheType;
import com.hbasesoft.framework.common.ServiceException;
import com.hbasesoft.framework.common.utils.CommonUtil;
import com.hbasesoft.framework.config.core.bean.ModulePojo;
import com.hbasesoft.framework.config.core.dao.ModuleDao;
import com.hbasesoft.framework.db.core.DaoException;
import com.hbasesoft.framework.web.core.service.common.ModuleService;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2014年11月16日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.core.config.service.impl <br>
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
     * @see com.hbasesoft.framework.core.config.service.ConfigurationService#selectAllModule()
     */
    @Cache(node = CacheConstant.MODULE_DATA, type = CacheType.NODE, bean = ModulePojo.class)
    public Map<String, ModulePojo> selectAllModule() throws ServiceException {
        Map<String, ModulePojo> moduleMap = null;
        try {
            List<ModulePojo> moduleList = moduleDao.selectList(ModulePojo.class);
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
