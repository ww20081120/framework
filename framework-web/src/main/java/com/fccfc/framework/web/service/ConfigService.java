package com.fccfc.framework.web.service;

import java.util.List;
import java.util.Map;

import com.fccfc.framework.api.ServiceException;
import com.fccfc.framework.api.bean.config.ConfigPojo;

/**
 * 参数配置Service
 * 
 * @author skysun'
 */
public interface ConfigService {

    /**
     * 查询配置项目录
     * 
     * @return
     */
    List<Map<String, Object>> queryConfigCatalogs() throws ServiceException;

    /**
     * 查询参数配置项
     * 
     * @return
     * @throws ServiceException
     */
    List<Map<String, Object>> queryConfigItems(Map<String, Object> paramMap) throws ServiceException;

    /**
     * 查询所有目录
     * 
     * @return
     * @throws ServiceException
     */
    List<Map<String, Object>> queryDirectorys() throws ServiceException;

    /**
     * 查询所有模块
     * 
     * @return
     * @throws ServiceException
     */
    List<Map<String, Object>> queryModules() throws ServiceException;

    /**
     * 查询输入类型
     * 
     * @return
     * @throws ServiceException
     */
    List<Map<String, Object>> queryInputTypes() throws ServiceException;

    /**
     * 查询数据类型
     * 
     * @return
     * @throws ServiceException
     */
    List<Map<String, Object>> queryDataTypes() throws ServiceException;

    /**
     * 新增配置项
     * 
     * @param paramMap
     * @throws ServiceException
     */
    void addConfigItem(Map<String, Object> paramMap) throws ServiceException;

    /**
     * 新增配置参数
     * 
     * @param paramMap
     * @throws ServiceException
     */
    void addParams(ConfigPojo config) throws ServiceException;

    void addParam(Map<String, Object> paramMap) throws ServiceException;
    
    /**
     * 查询配置项参数数据
     * @param paramMap Map<String,Object>
     * @return  List<Map<String,Object>>
     * @throws ServiceException ServiceException
     */
    List<Map<String,Object>> queryParams(Map<String,Object> paramMap) throws ServiceException;

}
