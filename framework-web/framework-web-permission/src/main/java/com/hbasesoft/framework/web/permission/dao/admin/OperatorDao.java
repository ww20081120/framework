
package com.hbasesoft.framework.web.permission.dao.admin;

import com.hbasesoft.framework.web.core.bean.OperatorPojo;
import com.hbasesoft.framework.db.core.DaoException;
import com.hbasesoft.framework.db.core.annotation.Dao;
import com.hbasesoft.framework.db.core.annotation.Param;
import com.hbasesoft.framework.db.core.annotation.Sql;
import com.hbasesoft.framework.db.hibernate.IGenericBaseDao;

import java.util.List;


@Dao
public interface OperatorDao extends IGenericBaseDao {

    //根据账户值，类型，查找操作员所有数据
    @Sql(bean = OperatorPojo.class)
    OperatorPojo getOperatorByAccount(@Param("account") String account) throws DaoException;

    //根据操作员id修改密码
    int updateOperatorPassword(@Param("id") Integer id, @Param("password") String password) throws DaoException;

    void deleteOperatorById(@Param("ids") Integer[] ids,@Param("state") String state);

    @Sql(bean = OperatorPojo.class)
    List<OperatorPojo> selectList(@Param("dutyId") Long dutyId, @Param(Param.PAGE_INDEX) Integer pageIndex
            , @Param(Param.PAGE_SIZE) Integer pageSize) throws DaoException;
    
}
