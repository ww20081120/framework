package com.hbasesoft.framework.web.permission.service;

import java.util.List;
import java.util.Map;

import com.hbasesoft.framework.web.permission.bean.ResourcesConfigPojo;
import com.hbasesoft.framework.common.ServiceException;

public interface ResourcesConfigService {

    List<ResourcesConfigPojo> selectResourcesConfig(String moduleCode, String resourceId) throws ServiceException;

    List<Map<String, Object>> query(String sql) throws ServiceException;

}
