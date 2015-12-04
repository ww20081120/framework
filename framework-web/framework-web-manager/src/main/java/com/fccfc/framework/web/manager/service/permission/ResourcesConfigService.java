package com.fccfc.framework.web.manager.service.permission;

import java.util.List;
import java.util.Map;

import com.fccfc.framework.common.ServiceException;
import com.fccfc.framework.web.manager.bean.permission.ResourcesConfigPojo;

public interface ResourcesConfigService {

    List<ResourcesConfigPojo> selectResourcesConfig(String moduleCode, String resourceId) throws ServiceException;

    List<Map<String, Object>> query(String sql) throws ServiceException;

}
