/**
 * 
 */
package com.fccfc.framework.web.dao;

import java.util.List;

import com.fccfc.framework.db.core.DaoException;
import com.fccfc.framework.db.core.annotation.Dao;
import com.fccfc.framework.db.core.annotation.Param;
import com.fccfc.framework.db.core.annotation.Sql;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2014年11月17日 <br>
 * @since V1.0<br>
 * @see com.fccfc.framework.web.dao <br>
 */
@Dao
public interface UrlResourceDao {

    @Sql(value = "SELECT U.EXECUTE_CLASS FROM URL_RESOURCE U WHERE U.MODULE_CODE IN (:moduleCode)", bean = String.class)
    List<String> selectAllModuleUrlResource(@Param("moduleCode") List<String> moduleCode) throws DaoException;

    @Sql("SELECT U.URL FROM URL_RESOURCE U WHERE U.MODULE_CODE IN (:moduleCode) AND U.EXECUTE_CLASS = :class AND U.EXECUTE_METHOD = :method")
    String selectUrlByClassAndName(@Param("moduleCode") List<String> moduleCode, @Param("class") String clazz,
        @Param("method") String method) throws DaoException;
}
