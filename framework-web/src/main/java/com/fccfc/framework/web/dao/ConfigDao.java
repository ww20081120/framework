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
    @Sql(value = "select DIRECTORY_CODE as  id,DIRECTORY_NAME as name,PARENT_DIRECTORY_CODE as pId from directory",
        bean = Map.class)
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
    @Sql(
        value = "select CONFIG_ITEM_ID as id,CONFIG_ITEM_NAME,DIRECTORY_CODE,MODULE_CODE,CONFIG_ITEM_CODE,IS_VISIABLE,UPDATE_TIME,REMARK from"
            + " config_item where DIRECTORY_CODE =:directory", bean = Map.class)
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
    @Sql(value = "select DIRECTOR_CODE,DIRECTOR_NAME from moduleY_CODE,DIRECTORY_NAME from directory", bean = Map.class)
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
    @Sql(value = "select MODULE_CODE,MODULE_NAME from module", bean = Map.class)
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
    @Sql(value = "select INPUT_TYPE,INPUT_TYPE_NAME from input_type", bean = Map.class)
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
    @Sql(value = "select DATA_TYPE,DATA_TYPE_NAME from data_type", bean = Map.class)
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
    @Sql(value = "select A.PARAM_CODE,A.PARAM_NAME,A.PARAM_VALUE,A.DEFAULT_PARAM_VALUE,A.REMARK,A.UPDATE_TIME,"
            + "B.DATA_TYPE_NAME,C.INPUT_TYPE_NAME from config_item_param A,data_type B,"
            + "input_type C WHERE A.CONFIG_ITEM_ID=:itemId AND A.INPUT_TYPE=C.INPUT_TYPE AND A.DATA_TYPE = B.DATA_TYPE",
        bean = Map.class)
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
    @Sql(
        value = "insert into  config_item(MODULE_CODE,DIRECTORY_CODE,CONFIG_ITEM_CODE,CONFIG_ITEM_NAME,IS_VISIABLE,UPDATE_TIME,REMARK) "
            + "VALUES(:module,:directory,:code,:name,:vasiable,current_timestamp(),:remark)")
    void addConfigItem(@Param("directory") String directory, @Param("module") String module,
        @Param("code") String code, @Param("name") String name, @Param("vasiable") String vasiable,
        @Param("remark") String remark) throws DaoException;

    // @Sql(
    // value =
    // "insert into  config_item_param(CONFIG_ITEM_ID,PARAM_CODE,PARAM_NAME,
    // PARAM_VALUE,DEFAULT_PARAM_VALUE,DATA_TYPE,INPUT_TYPE,VALUE_SCRIPT,UPDATE_TIME,REMARK) "
    // +
    // "VALUES(:config.itemId,:config.paramCode,:config.paramName,:config.paramValue,
    // :config.defaultValue,:config.dataType,:config.inputType,:config.valueScript,current_timestamp(),:config.paramRemark)")
    // void addParams(@Param("config") ConfigPojo config) throws DaoException;

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
    @Sql(
        value = "insert into  config_item_param(CONFIG_ITEM_ID,PARAM_CODE,PARAM_NAME,"
            + "PARAM_VALUE,DEFAULT_PARAM_VALUE,DATA_TYPE,INPUT_TYPE,VALUE_SCRIPT,UPDATE_TIME,REMARK) "
            + "VALUES(:itemId,:paramCode,:paramName,:paramValue,:defaultValue,:dataType,:inputType,:valueScript,current_timestamp(),:paramRemark)")
    void addParam(@Param("itemId") String itemId, @Param("paramCode") String paramCode,
        @Param("paramName") String paramName, @Param("paramValue") String paramValue,
        @Param("defaultValue") String defaultValue, @Param("dataType") String dataType,
        @Param("inputType") String inputType, @Param("valueScript") String valueScript,
        @Param("paramRemark") String paramRemark) throws DaoException;
}
