package com.fccfc.framework.web.manager.service.permission.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.fccfc.framework.common.ServiceException;
import com.fccfc.framework.db.core.DaoException;
import com.fccfc.framework.web.manager.bean.permission.ResourcesConfigPojo;
import com.fccfc.framework.web.manager.dao.permission.resources.ResourcesConfigDao;
import com.fccfc.framework.web.manager.dao.permission.role.RoleResourceCfgDao;
import com.fccfc.framework.web.manager.service.permission.ResourcesConfigService;

@Service
public class ResourcesConfigServiceImpl implements ResourcesConfigService {

    /** resourcesConfigDao */
    @Resource
    private ResourcesConfigDao resourcesConfigDao;

    @Resource
    private RoleResourceCfgDao roleResourceCfgDao;

    /**
     * Description: 查询所有的属性数据<br>
     * 
     * @author shao.dinghui<br>
     * @taskId <br>
     * @return
     * @throws ServiceException <br>
     */
    @Override
    public List<ResourcesConfigPojo> selectResourcesConfig(String moduleCode, String resourceId)
        throws ServiceException {
        try {
            return resourcesConfigDao.selectResourcesConfig(moduleCode, resourceId);
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Description: 查询所有的属性数据<br>
     * 
     * @author shao.dinghui<br>
     * @taskId <br>
     * @return
     * @throws ServiceException <br>
     */
    @Override
    public List<Map<String, Object>> query(String sql) throws ServiceException {
        try {
            return roleResourceCfgDao.query(sql);
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

}
