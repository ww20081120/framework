/**
 * 
 */
package com.fccfc.framework.web.dao.resource;

import java.util.List;

import com.fccfc.framework.db.core.DaoException;
import com.fccfc.framework.db.core.annotation.Dao;
import com.fccfc.framework.db.core.annotation.Param;
import com.fccfc.framework.db.core.annotation.Sql;
import com.fccfc.framework.db.hibernate.IGenericBaseDao;

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
public interface UrlResourceDao extends IGenericBaseDao {

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param moduleCode <br>
     * @return <br>
     * @throws DaoException <br>
     */
    @Sql(bean = String.class)
    List<String> selectAllModuleUrlResource(@Param("moduleCode") List<String> moduleCode) throws DaoException;

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param moduleCode <br>
     * @param clazz <br>
     * @param method <br>
     * @return <br>
     * @throws DaoException <br>
     */
    String selectUrlByClassAndName(@Param("moduleCode") List<String> moduleCode, @Param("class") String clazz,
        @Param("method") String method) throws DaoException;
}
