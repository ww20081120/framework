package com.fccfc.framework.web.dao;

import java.util.List;
import java.util.Map;

import com.fccfc.framework.db.core.DaoException;
import com.fccfc.framework.db.core.annotation.Dao;
import com.fccfc.framework.db.core.annotation.Param;
import com.fccfc.framework.db.core.annotation.Sql;

/**
 * 
 * <Description> <br> 
 *  
 * @author yang.zhipeng <br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年7月2日 <br>
 * @since V1.0<br>
 * @see com.fccfc.framework.web.dao <br>
 */
@Dao
public interface ConfigDao {

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @return <br>
     * @throws DaoException <br>
     */
    @Sql(bean = Map.class)
    List<Map<String, Object>> selectConfigs() throws DaoException;

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param directory <br>
     * @return <br>
     * @throws DaoException <br>
     */
    @Sql(bean = Map.class)
    List<Map<String, Object>> selectConfigItems(@Param("directory") String directory) throws DaoException;

    
    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @return <br>
     * @throws DaoException <br>
     */
    @Sql(bean = Map.class)
    List<Map<String, Object>> selectDirectorys() throws DaoException;

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @return <br>
     * @throws DaoException <br>
     */
    @Sql(bean = Map.class)
    List<Map<String, Object>> selectModules() throws DaoException;

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @return <br>
     * @throws DaoException <br>
     */
    @Sql(bean = Map.class)
    List<Map<String, Object>> selectInputTypes() throws DaoException;

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @return <br>
     * @throws DaoException <br>
     */
    @Sql(bean = Map.class)
    List<Map<String, Object>> selectDataTypes() throws DaoException;

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param itemId <br>
     * @return <br>
     * @throws DaoException <br>
     */
    @Sql(bean = Map.class)
    List<Map<String, Object>> selectParams(@Param("itemId") String itemId) throws DaoException;

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param directory <br>
     * @param module <br>
     * @param code <br>
     * @param name <br>
     * @param vasiable <br>
     * @param remark <br>
     * @throws DaoException <br>
     */

    void addConfigItem(@Param("directory") String directory, @Param("module") String module,
        @Param("code") String code, @Param("name") String name, @Param("vasiable") String vasiable,
        @Param("remark") String remark) throws DaoException;

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param itemId <br>
     * @param paramCode <br>
     * @param paramName <br>
     * @param paramValue <br>
     * @param defaultValue <br>
     * @param dataType <br>
     * @param inputType <br>
     * @param valueScript <br>
     * @param paramRemark <br>
     * @throws DaoException <br>
     */
    void addParam(@Param("itemId") String itemId, @Param("paramCode") String paramCode,
        @Param("paramName") String paramName, @Param("paramValue") String paramValue,
        @Param("defaultValue") String defaultValue, @Param("dataType") String dataType,
        @Param("inputType") String inputType, @Param("valueScript") String valueScript,
        @Param("paramRemark") String paramRemark) throws DaoException;
}
