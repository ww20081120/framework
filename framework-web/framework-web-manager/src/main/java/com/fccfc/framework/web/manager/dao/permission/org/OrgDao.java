package com.fccfc.framework.web.manager.dao.permission.org;

import com.fccfc.framework.db.core.DaoException;
import com.fccfc.framework.db.core.annotation.Dao;
import com.fccfc.framework.db.core.annotation.Param;
import com.fccfc.framework.db.core.annotation.Sql;
import com.fccfc.framework.db.hibernate.IGenericBaseDao;
import com.fccfc.framework.web.manager.bean.permission.OrgPojo;

import java.util.List;

/**
 * <Description> <br>
 *
 * @author wk <br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015/10/25 <br>
 * @see com.fccfc.framework.web.manager.dao.permission.org <br>
 * @since V1.0<br>
 */
@Dao
public interface OrgDao extends IGenericBaseDao {

    @Sql(bean = OrgPojo.class)
    List<OrgPojo> selectList(@Param("parentOrgIds") Long parentOrgIds, @Param(Param.PAGE_INDEX) Integer pageIndex
            , @Param(Param.PAGE_SIZE) Integer pageSize) throws DaoException;

    int update(@Param("pojo") OrgPojo orgPojo) throws DaoException;

    @Sql(value = "UPDATE ORG SET STATE = 'X' WHERE ORG_ID = :orgId")
    int deleteById(@Param("orgId") Long orgId);
}
