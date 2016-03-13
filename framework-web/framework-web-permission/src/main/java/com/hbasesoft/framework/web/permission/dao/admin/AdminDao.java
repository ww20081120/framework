package com.hbasesoft.framework.web.permission.dao.admin;

import java.util.Date;
import java.util.List;

import com.hbasesoft.framework.web.permission.bean.AdminPojo;
import com.hbasesoft.framework.db.core.DaoException;
import com.hbasesoft.framework.db.core.annotation.Dao;
import com.hbasesoft.framework.db.core.annotation.Param;
import com.hbasesoft.framework.db.core.annotation.Sql;
import com.hbasesoft.framework.db.hibernate.IGenericBaseDao;

@Dao
public interface AdminDao extends IGenericBaseDao {

    @Sql(bean = AdminPojo.class)
    List<AdminPojo> selectList(@Param("orgId") Long orgId, @Param("dutyId") Long dutyId, @Param("name") String name,
        @Param(Param.PAGE_INDEX) Integer pageIndex, @Param(Param.PAGE_SIZE) Integer pageSize) throws DaoException;

    void deleteAdminById(@Param("ids") Integer[] ids, @Param("state") String state) throws DaoException;

    @Sql(value = "")
    int update4Attr(@Param("adminPojo") AdminPojo adminPojo) throws DaoException;

    void saveHistory(@Param("id") Integer id, @Param("operatorId") Integer operatorId,
        @Param("updateDate") Date updateDate);

    @Sql(bean = AdminPojo.class)
    AdminPojo getAdminByName(@Param("name") String name) throws DaoException;

    /** 查询当前登录用户 */
    @Sql(bean = AdminPojo.class)
    AdminPojo getOne(@Param("operatorId") Integer operatorId) throws DaoException;

    String checkPwd(@Param("operatorId") Integer operatorId) throws DaoException;
}
