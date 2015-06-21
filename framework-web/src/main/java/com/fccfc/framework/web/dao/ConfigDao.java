package com.fccfc.framework.web.dao;

import java.util.List;
import java.util.Map;

import com.fccfc.framework.api.ServiceException;
import com.fccfc.framework.api.bean.config.ConfigPojo;
import com.fccfc.framework.core.db.DaoException;
import com.fccfc.framework.core.db.annotation.DAO;
import com.fccfc.framework.core.db.annotation.Param;
import com.fccfc.framework.core.db.annotation.Sql;

@DAO
public interface ConfigDao {

    @Sql(value = "select DIRECTORY_CODE as  id,DIRECTORY_NAME as name,PARENT_DIRECTORY_CODE as pId from directory",
        bean = Map.class)
    List<Map<String, Object>> selectConfigs() throws DaoException;

    /**
     * 查询系统参数配置
     * 
     * @return
     * @throws DaoException
     */
    @Sql(
        value = "select CONFIG_ITEM_ID as id,CONFIG_ITEM_NAME,DIRECTORY_CODE,MODULE_CODE,CONFIG_ITEM_CODE,IS_VISIABLE,UPDATE_TIME,REMARK from"
            + " config_item where DIRECTORY_CODE =:directory", bean = Map.class)
    List<Map<String, Object>> selectConfigItems(@Param("directory") String directory) throws DaoException;

    /**
     * 查询所有目录数据
     * 
     * @return
     * @throws DaoException
     */
    @Sql(value = "select DIRECTOR_CODE,DIRECTOR_NAME from moduleY_CODE,DIRECTORY_NAME from directory", bean = Map.class)
    List<Map<String, Object>> selectDirectorys() throws DaoException;

    /**
     * 查询所有模块数据
     * 
     * @return
     * @throws DaoException
     */
    @Sql(value = "select MODULE_CODE,MODULE_NAME from module", bean = Map.class)
    List<Map<String, Object>> selectModules() throws DaoException;

    /**
     * 查询输入方式
     * 
     * @return
     * @throws DaoException
     */
    @Sql(value = "select INPUT_TYPE,INPUT_TYPE_NAME from input_type", bean = Map.class)
    List<Map<String, Object>> selectInputTypes() throws DaoException;

    /**
     * 查询数据 类型
     * 
     * @return
     * @throws DaoException
     */
    @Sql(value = "select DATA_TYPE,DATA_TYPE_NAME from data_type", bean = Map.class)
    List<Map<String, Object>> selectDataTypes() throws DaoException;

    @Sql(value = "select A.PARAM_CODE,A.PARAM_NAME,A.PARAM_VALUE,A.DEFAULT_PARAM_VALUE,A.REMARK,A.UPDATE_TIME,"
        + "B.DATA_TYPE_NAME,C.INPUT_TYPE_NAME from config_item_param A,data_type B,"
        + "input_type C WHERE A.CONFIG_ITEM_ID=:itemId AND A.INPUT_TYPE=C.INPUT_TYPE AND A.DATA_TYPE = B.DATA_TYPE",
        bean = Map.class)
    List<Map<String, Object>> selectParams(@Param("itemId") String itemId) throws DaoException;

    /**
     * 新增参数配置项
     * 
     * @param directory
     * @param module
     * @param name
     * @param vasiable
     * @param remark
     * @throws DaoException
     */
    @Sql(
        value = "insert into  config_item(MODULE_CODE,DIRECTORY_CODE,CONFIG_ITEM_CODE,CONFIG_ITEM_NAME,IS_VISIABLE,UPDATE_TIME,REMARK) "
            + "VALUES(:module,:directory,:code,:name,:vasiable,current_timestamp(),:remark)")
    void addConfigItem(@Param("directory") String directory, @Param("module") String module,
        @Param("code") String code, @Param("name") String name, @Param("vasiable") String vasiable,
        @Param("remark") String remark) throws DaoException;

    @Sql(
        value = "insert into  config_item_param(CONFIG_ITEM_ID,PARAM_CODE,PARAM_NAME,PARAM_VALUE,DEFAULT_PARAM_VALUE,DATA_TYPE,INPUT_TYPE,VALUE_SCRIPT,UPDATE_TIME,REMARK) "
            + "VALUES(:config.itemId,:config.paramCode,:config.paramName,:config.paramValue,:config.defaultValue,:config.dataType,:config.inputType,:config.valueScript,current_timestamp(),:config.paramRemark)")
    void addParams(@Param("config") ConfigPojo config) throws DaoException;

    @Sql(
        value = "insert into  config_item_param(CONFIG_ITEM_ID,PARAM_CODE,PARAM_NAME,PARAM_VALUE,DEFAULT_PARAM_VALUE,DATA_TYPE,INPUT_TYPE,VALUE_SCRIPT,UPDATE_TIME,REMARK) "
            + "VALUES(:itemId,:paramCode,:paramName,:paramValue,:defaultValue,:dataType,:inputType,:valueScript,current_timestamp(),:paramRemark)")
    void addParam(@Param("itemId") String itemId, @Param("paramCode") String paramCode,
        @Param("paramName") String paramName, @Param("paramValue") String paramValue,
        @Param("defaultValue") String defaultValue, @Param("dataType") String dataType,
        @Param("inputType") String inputType, @Param("valueScript") String valueScript,
        @Param("paramRemark") String paramRemark) throws DaoException;
}
