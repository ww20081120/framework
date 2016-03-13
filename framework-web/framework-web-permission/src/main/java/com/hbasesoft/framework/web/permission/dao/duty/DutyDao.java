package com.hbasesoft.framework.web.permission.dao.duty;

import java.util.List;

import com.hbasesoft.framework.web.permission.bean.DutyPojo;
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
 * @CreateDate 2015/10/29 <br>
 * @see com.hbasesoft.framework.web.permission.dao.duty <br>
 * @since V1.0<br>
 */
@Dao
public interface DutyDao extends IGenericBaseDao {

    @Sql(bean = DutyPojo.class)
    List<DutyPojo> selectDuty(@Param("orgId") Long orgId, @Param(Param.PAGE_INDEX) Integer pageIndex,
        @Param(Param.PAGE_SIZE) Integer pageSize) throws DaoException;

    DutyPojo selectDuty(@Param("dutyId") Long dutyId) throws DaoException;

    @Sql(value = "UPDATE DUTY SET STATE = 'X' WHERE DUTY_ID IN :ids")
    int deleteByIds(@Param("ids") Long[] ids) throws DaoException;

    @Sql(value = "UPDATE DUTY SET DUTY_NAME = :pojo.dutyName WHERE DUTY_ID = :pojo.dutyId")
    int update(@Param("pojo") DutyPojo pojo) throws DaoException;

    int insertDutyRole(@Param("dutyId") Long dutyId, @Param("roleIds") Long[] roleIds) throws DaoException;

    @Sql(value = "DELETE FROM DUTY_ROLE WHERE DUTY_ID IN :dutyIds")
    int deleteDutyRole(@Param("dutyIds") Long[] dutyIds) throws DaoException;

    @Sql(bean = Long.class, value = "SELECT ROLE_ID FROM DUTY_ROLE WHERE DUTY_ID = :dutyId")
    List<Long> selectListDutyRole(@Param("dutyId") Long dutyId) throws DaoException;
}
