/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.web.system.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.hbasesoft.framework.web.system.bean.ConfigItemParamPojo;
import com.hbasesoft.framework.web.system.bean.ConfigItemParamValuePojo;
import com.hbasesoft.framework.common.ServiceException;
import com.hbasesoft.framework.config.core.bean.ConfigItemPojo;
import com.hbasesoft.framework.config.core.bean.DirectoryPojo;
import com.hbasesoft.framework.config.core.bean.ModulePojo;

/**
 * <Description> <br>
 * 
 * @author yang.zhipeng <br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年11月24日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.web.manager.service.configitem <br>
 */
@Service
public interface ConfigItemService {

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param pageIndex
     * @param pageSize
     * @return
     * @throws ServiceException <br>
     */
    List<ConfigItemPojo> queryConfigItemPager(Integer pageIndex, Integer pageSize) throws ServiceException;

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param pageIndex
     * @param pageSize
     * @return
     * @throws ServiceException <br>
     */
    List<ConfigItemParamPojo> queryConfigItemParamPager(Integer configItemId, Integer pageIndex, Integer pageSize)
        throws ServiceException;

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param configItemId
     * @param paramCode
     * @param pageIndex
     * @param pageSize
     * @return
     * @throws ServiceException <br>
     */
    List<ConfigItemParamValuePojo> queryConfigItemParamValuePager(Integer configItemId, String paramCode,
        Integer pageIndex, Integer pageSize) throws ServiceException;

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param pojo
     * @throws ServiceException <br>
     */
    void addConfigItem(ConfigItemPojo pojo) throws ServiceException;

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param configItemId
     * @return
     * @throws ServiceException <br>
     */
    ConfigItemPojo queryConfigItem(Integer configItemId) throws ServiceException;

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param pojo
     * @return
     * @throws ServiceException <br>
     */
    ConfigItemPojo queryConfigItem(ConfigItemPojo pojo) throws ServiceException;

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param pojo
     * @throws ServiceException <br>
     */
    void modifyConfigItem(ConfigItemPojo pojo) throws ServiceException;

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param configItemPojo
     * @throws ServiceException <br>
     */
    void addConfigItemHistory(ConfigItemPojo configItemPojo) throws ServiceException;

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param configItemParamPojo
     * @throws ServiceException <br>
     */
    void addConfigItemParamHistory(ConfigItemParamPojo configItemParamPojo) throws ServiceException;

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param configItemId
     * @throws ServiceException <br>
     */
    void deleteConfigItem(Integer configItemId) throws ServiceException;

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param pojo
     * @throws ServiceException <br>
     */
    void addConfigItemParam(ConfigItemParamPojo pojo) throws ServiceException;

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param configItemId
     * @param paramCode
     * @return
     * @throws ServiceException <br>
     */
    ConfigItemParamPojo queryConfigItemParam(Integer configItemId, String paramCode) throws ServiceException;

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param pojo
     * @param oldParamCode
     * @throws ServiceException <br>
     */
    void modifyConfigItemParam(ConfigItemParamPojo pojo, String oldParamCode) throws ServiceException;

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param configItemId
     * @param paramCode
     * @throws ServiceException <br>
     */
    void deleteConfigItemParams(Integer configItemId, String paramCode) throws ServiceException;

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param pojo
     * @throws ServiceException <br>
     */
    void addConfigItemParamValue(ConfigItemParamValuePojo pojo) throws ServiceException;

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param paramValueId
     * @return
     * @throws ServiceException <br>
     */
    ConfigItemParamValuePojo queryConfigItemParamValue(Integer paramValueId) throws ServiceException;

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param pojo
     * @throws ServiceException <br>
     */
    void modifyConfigItemParamValue(ConfigItemParamValuePojo pojo) throws ServiceException;

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param paramValueId
     * @throws ServiceException <br>
     */
    void deleteConfigItemParamValue(Integer paramValueId) throws ServiceException;

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @return
     * @throws ServiceException <br>
     */
    List<DirectoryPojo> queryDirectoryCode() throws ServiceException;

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @return
     * @throws ServiceException <br>
     */
    List<ModulePojo> queryModuleCode() throws ServiceException;

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param mediaId
     * @param mediaName
     * @throws ServiceException <br>
     */
    void importConfigItem(String mediaId, String mediaName) throws ServiceException;

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param mediaId
     * @param mediaName
     * @throws ServiceException <br>
     */
    void importConfigItemParam(String mediaId, String mediaName) throws ServiceException;

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param mediaId
     * @param mediaName
     * @throws ServiceException <br>
     */
    void importConfigItemParamValue(String mediaId, String mediaName) throws ServiceException;

    /**
     * 配置map
     */
    List<Map<String, String>> getMap(String dictCode) throws ServiceException;

}
