package com.fccfc.framework.web.manager.dao.permission.admin;

import com.fccfc.framework.db.core.DaoException;
import com.fccfc.framework.db.core.annotation.Dao;
import com.fccfc.framework.db.core.annotation.Param;
import com.fccfc.framework.db.core.annotation.Sql;
import com.fccfc.framework.db.hibernate.IGenericBaseDao;
import com.fccfc.framework.web.manager.bean.permission.AdminPojo;

import java.util.Date;
import java.util.List;

@Dao
public interface AdminDao extends IGenericBaseDao {

    @Sql(bean = AdminPojo.class)
    List<AdminPojo> selectList(@Param("dutyId") Long dutyId, @Param("name") String name, @Param(Param.PAGE_INDEX) Integer pageIndex, @Param(Param.PAGE_SIZE) Integer pageSize) throws DaoException;

    void deleteAdminById(@Param("ids") Integer[] ids,@Param("state") String state) throws DaoException;

    @Sql(value = "")
    int update4Attr(@Param("adminPojo") AdminPojo adminPojo) throws DaoException;
    
    void saveHistory(@Param("id") Integer id,@Param("operatorId") Integer operatorId,
        @Param("updateDate") Date updateDate
        ); 
    
    @Sql(bean = AdminPojo.class)
    AdminPojo getAdminByName(@Param("name") String name) throws DaoException;
}
