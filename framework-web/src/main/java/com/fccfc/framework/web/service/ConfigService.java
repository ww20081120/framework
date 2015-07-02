package com.fccfc.framework.web.service;

import java.util.List;
import java.util.Map;

import com.fccfc.framework.common.ServiceException;

/**
 * 参数配置Service
 * 
 * @author skysun'
 */
public interface ConfigService {

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @return <br>
     * @throws ServiceException <br>
     */
    List<Map<String, Object>> queryConfigCatalogs() throws ServiceException;

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param paramMap <br>
     * @return <br>
     * @throws ServiceException <br>
     */
    List<Map<String, Object>> queryConfigItems(Map<String, Object> paramMap) throws ServiceException;

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @return <br>
     * @throws ServiceException <br>
     */
    List<Map<String, Object>> queryDirectorys() throws ServiceException;

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @return <br>
     * @throws ServiceException <br>
     */
    List<Map<String, Object>> queryModules() throws ServiceException;

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @return <br>
     * @throws ServiceException <br>
     */
    List<Map<String, Object>> queryInputTypes() throws ServiceException;

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @return <br>
     * @throws ServiceException <br>
     */
    List<Map<String, Object>> queryDataTypes() throws ServiceException;

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param paramMap <br>
     * @throws ServiceException <br>
     */
    void addConfigItem(Map<String, Object> paramMap) throws ServiceException;

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param paramMap <br>
     * @throws ServiceException <br>
     */
    void addParam(Map<String, Object> paramMap) throws ServiceException;

    /**
     * 查询配置项参数数据
     * 
     * @param paramMap Map<String,Object>
     * @return List<Map<String,Object>>
     * @throws ServiceException ServiceException
     */
    List<Map<String, Object>> queryParams(Map<String, Object> paramMap) throws ServiceException;

}
