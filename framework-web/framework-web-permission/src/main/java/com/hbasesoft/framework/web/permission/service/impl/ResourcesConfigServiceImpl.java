package com.hbasesoft.framework.web.permission.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.hbasesoft.framework.web.permission.bean.ResourcesConfigPojo;
import com.hbasesoft.framework.web.permission.dao.resources.ResourcesConfigDao;
import com.hbasesoft.framework.web.permission.dao.role.RoleResourceCfgDao;
import com.hbasesoft.framework.web.permission.service.ResourcesConfigService;
import com.hbasesoft.framework.common.ServiceException;
import com.hbasesoft.framework.db.core.DaoException;

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
