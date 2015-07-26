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
import com.fccfc.framework.web.bean.resource.UrlResourcePojo;

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
     * Description: selectAllModuleUrlResource<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param moduleCode
     * @return
     * @throws DaoException <br>
     */
    @Sql(bean = UrlResourcePojo.class)
    List<UrlResourcePojo> selectAllModuleUrlResource(@Param("moduleCode") List<String> moduleCode) throws DaoException;

    /**
     * Description: selectUrlResourceByPermision<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param operateId
     * @param moduleCode
     * @return
     * @throws DaoException <br>
     */
    @Sql(bean = UrlResourcePojo.class)
    List<UrlResourcePojo> selectUrlResourceByPermision(@Param("operateId") Integer operateId,
        @Param("moduleCode") List<String> moduleCode) throws DaoException;
    
    List<Integer> selectResourceIdByPermission(@Param("operateId") Integer operateId,
        @Param("moduleCode") List<String> moduleCode) throws DaoException;
}
