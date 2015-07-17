/**
 * 
 */
package com.fccfc.framework.config.core.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.fccfc.framework.common.ServiceException;
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
            return moduleDao.selectAllModule();
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

}
