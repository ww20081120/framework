package com.fccfc.framework.web.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.fccfc.framework.api.ServiceException;
import com.fccfc.framework.api.bean.config.ConfigPojo;
import com.fccfc.framework.core.db.DaoException;
import com.fccfc.framework.web.dao.ConfigDao;
import com.fccfc.framework.web.service.ConfigService;

/**
 * 参数配置Service 实现类
 * 
 * @author skysun
 */
@Service
public class ConfigServiceImpl implements ConfigService {

    @Resource
    private ConfigDao configDao;

    @Override
    public List<Map<String, Object>> queryConfigCatalogs() throws ServiceException {
        try {
            return configDao.selectConfigs();
        }
        catch (DaoException e) {
            throw new ServiceException("query config catalogs error", e);
        }
    }

    @Override
    public List<Map<String, Object>> queryConfigItems(Map<String, Object> paramMap) throws ServiceException {
        try {
            String directory = (String) paramMap.get("directory");
            return configDao.selectConfigItems(directory);
        }
        catch (DaoException e) {
            throw new ServiceException("query config items error", e);
        }
    }

    @Override
    public List<Map<String, Object>> queryDirectorys() throws ServiceException {
        try {
            return configDao.selectDirectorys();
        }
        catch (DaoException e) {
            throw new ServiceException("query directorys error", e);
        }
    }

    @Override
    public List<Map<String, Object>> queryModules() throws ServiceException {
        try {
            return configDao.selectModules();
        }
        catch (DaoException e) {
            throw new ServiceException("query modules error", e);
        }
    }

    @Override
    public List<Map<String, Object>> queryInputTypes() throws ServiceException {
        try {
            return configDao.selectInputTypes();
        }
        catch (DaoException e) {
            throw new ServiceException("query input types error", e);
        }
    }

    @Override
    public List<Map<String, Object>> queryDataTypes() throws ServiceException {
        try {
            return configDao.selectDataTypes();
        }
        catch (DaoException e) {
            throw new ServiceException("query data types error", e);
        }
    }

    @Override
    public void addConfigItem(Map<String, Object> paramMap) throws ServiceException {
        try {
            String directory = (String) paramMap.get("directory");
            String module = (String) paramMap.get("module");
            String name = (String) paramMap.get("configName");
            String vasiable = (String) paramMap.get("vasiable");
            String code = (String) paramMap.get("configCode");
            String remark = null;
            if (null != paramMap.get("remark")) {
                remark = (String) paramMap.get("remark");
            }
            configDao.addConfigItem(directory, module, code, name, vasiable, remark);
        }
        catch (DaoException e) {
            throw new ServiceException("query modules error", e);
        }
    }

    @Override
    public void addParams(ConfigPojo config) throws ServiceException {
        try {
            configDao.addParams(config);
        }
        catch (DaoException e) {
            throw new ServiceException("query addParams error", e);
        }
    }

    @Override
    public void addParam(Map<String, Object> paramMap) throws ServiceException {
        try {
            String itemId = paramMap.get("itemId").toString();
            String paramCode = paramMap.get("paramCode").toString();
            String paramName = paramMap.get("paramName").toString();
            String paramValue = paramMap.get("paramValue").toString();
            String defaultValue = paramMap.get("defaultValue").toString();
            String dataType = paramMap.get("dataType").toString();
            String inputType = paramMap.get("inputType").toString();
            String valueScript = paramMap.get("valueScript").toString();
            String paramRemark = paramMap.get("paramRemark").toString();
            configDao.addParam(itemId, paramCode, paramName, paramValue, defaultValue, dataType, inputType,
                valueScript, paramRemark);
        }
        catch (DaoException e) {
            throw new ServiceException("query addParams error", e);
        }
    }

    @Override
    public List<Map<String, Object>> queryParams(Map<String, Object> paramMap) throws ServiceException {
        try {
            String itemId = paramMap.get("itemId").toString();
            return configDao.selectParams(itemId);
        }
        catch (DaoException e) {
            throw new ServiceException("query params error", e);
        }
    }

}
