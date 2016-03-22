package com.hbasesoft.framework.web.permission.dao.org;

import java.util.List;

import com.hbasesoft.framework.web.permission.bean.OrgPojo;
import com.hbasesoft.framework.db.core.DaoException;
import com.hbasesoft.framework.db.core.annotation.Dao;
import com.hbasesoft.framework.db.core.annotation.Param;
import com.hbasesoft.framework.db.core.annotation.Sql;
import com.hbasesoft.framework.db.hibernate.IGenericBaseDao;

/**
 * <Description> <br>
 *
 * @author wk <br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015/10/25 <br>
 * @see com.hbasesoft.framework.web.permission.dao.org <br>
 * @since V1.0<br>
 */
@Dao
public interface OrgDao extends IGenericBaseDao {

    @Sql(bean = OrgPojo.class)
    List<OrgPojo> selectList(@Param("parentOrgId") Long parentOrgIds, @Param(Param.PAGE_INDEX) Integer pageIndex,
        @Param(Param.PAGE_SIZE) Integer pageSize) throws DaoException;

    @Sql(value = "UPDATE T_MANAGER_ORG SET STATE = 'X' WHERE ORG_ID = :orgId")
    int deleteById(@Param("orgId") Long orgId);
}
