/**************************************************************************************** 
 Copyright © 2003-2012 fccfc Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.fccfc.framework.web.manager.dao.system.configitem;

import java.util.List;

import com.fccfc.framework.config.core.bean.ConfigItemPojo;
import com.fccfc.framework.config.core.bean.ModulePojo;
import com.fccfc.framework.db.core.DaoException;
import com.fccfc.framework.db.core.annotation.Dao;
import com.fccfc.framework.db.core.annotation.Param;
import com.fccfc.framework.db.core.annotation.Sql;
import com.fccfc.framework.db.hibernate.IGenericBaseDao;
import com.fccfc.framework.web.manager.bean.system.ConfigItemHistoryPojo;
import com.fccfc.framework.web.manager.bean.system.ConfigItemParamHistoryPojo;
import com.fccfc.framework.web.manager.bean.system.ConfigItemParamPojo;
import com.fccfc.framework.web.manager.bean.system.ConfigItemParamValuePojo;
import com.fccfc.framework.web.manager.bean.system.DirectoryPojo;

/** 
 * <Description> <br> 
 *  
 * @author yang.zhipeng <br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年11月24日 <br>
 * @since V1.0<br>
 * @see com.fccfc.framework.web.manager.dao.system.configitem <br>
 */
@Dao
public interface ConfigItemParamDao extends IGenericBaseDao{

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param pageIndex
     * @param pageSize
     * @return
     * @throws DaoException <br>
     */
    @Sql(bean = ConfigItemPojo.class)
    List<ConfigItemPojo> queryConfigItem(@Param(Param.PAGE_INDEX) Integer pageIndex, @Param(Param.PAGE_SIZE) Integer pageSize) throws DaoException;
    
    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param pageIndex
     * @param pageSize
     * @return
     * @throws DaoException <br>
     */
    @Sql(bean = ConfigItemParamPojo.class)
    List<ConfigItemParamPojo> queryConfigItemParam(@Param("configItemId") Integer configItemId, @Param(Param.PAGE_INDEX) Integer pageIndex, @Param(Param.PAGE_SIZE) Integer pageSize) throws DaoException;
    

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param configItemId
     * @param paramCode
     * @param pageIndex
     * @param pageSize
     * @return
     * @throws DaoException <br>
     */
    @Sql(bean = ConfigItemParamValuePojo.class)
    List<ConfigItemParamValuePojo> queryConfigItemParamValue(@Param("configItemId") Integer configItemId, @Param("paramCode") String paramCode, @Param(Param.PAGE_INDEX) Integer pageIndex, @Param(Param.PAGE_SIZE) Integer pageSize) throws DaoException;
    
    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param configItemIds
     * @throws DaoException <br>
     */
    void deleteConfigItems(@Param("configItemIds") Integer[] configItemIds) throws DaoException;
    
    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param configItemId
     * @param paramCode
     * @return
     * @throws DaoException <br>
     */
    @Sql(bean = ConfigItemParamPojo.class)
    ConfigItemParamPojo queryConfigItemParam(@Param("configItemId") Integer configItemId, @Param("paramCode") String paramCode) throws DaoException;
    
    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param configItemId
     * @param paramCode
     * @throws DaoException <br>
     */
    void deleteConfigItemParams(@Param("configItemId") Integer configItemId, @Param("paramCode") String paramCode) throws DaoException;
    
    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param paramValueIds
     * @throws DaoException <br>
     */
    void deleteConfigItemParamValues(@Param("paramValueIds") Integer[] paramValueIds) throws DaoException;
    
    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @return
     * @throws DaoException <br>
     */
    @Sql(bean = DirectoryPojo.class)
    List<DirectoryPojo> queryDirectoryCode() throws DaoException;
    
    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @return
     * @throws DaoException <br>
     */
    @Sql(bean = ModulePojo.class)
    List<ModulePojo> queryModuleCode() throws DaoException;
    
    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param pojo
     * @throws DaoException <br>
     */
    void insertConfigItemParam(@Param("pojo") ConfigItemParamPojo pojo) throws DaoException;
    
    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param pojo
     * @param oldParamCode
     * @throws DaoException <br>
     */
    void updateConfigItemParam(@Param("pojo") ConfigItemParamPojo pojo, @Param("oldParamCode") String oldParamCode) throws DaoException;
    
    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param pojoList
     * @throws DaoException <br>
     */
    int batchInsertItem(@Param("pojoList") List<ConfigItemPojo> pojoList) throws DaoException;
    
    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param pojoList
     * @return
     * @throws DaoException <br>
     */
    int batchInsertParam(@Param("pojoList") List<ConfigItemParamPojo> pojoList) throws DaoException;
    
    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param pojoList
     * @return
     * @throws DaoException <br>
     */
    int batchInsertParamValue(@Param("pojoList") List<ConfigItemParamValuePojo> pojoList) throws DaoException;
    
    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param pojoList
     * @throws DaoException <br>
     */
    void insertConfigItemHistory(@Param("pojoList") List<ConfigItemHistoryPojo> pojoList) throws DaoException;
    
    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param pojoList
     * @throws DaoException <br>
     */
    void insertConfigItemParamHistory(@Param("pojoList") List<ConfigItemParamHistoryPojo> pojoList) throws DaoException;
    
    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param configItemId
     * @return
     * @throws DaoException <br>
     */
    @Sql(bean = Integer.class)
    Integer getMaxSeqConfigItemHis(@Param("configItemId") Integer configItemId) throws DaoException;
    
    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param configItemId
     * @param paramCode
     * @return
     * @throws DaoException <br>
     */
    @Sql(bean = Integer.class)
    Integer getMaxSeqConfigItemParamHis(@Param("configItemId") Integer configItemId, @Param("paramCode") String paramCode) throws DaoException;
}
