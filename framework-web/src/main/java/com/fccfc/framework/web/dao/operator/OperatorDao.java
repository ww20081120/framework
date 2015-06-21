/**
 * 
 */
package com.fccfc.framework.web.dao.operator;

import com.fccfc.framework.api.bean.operator.OperatorPojo;
import com.fccfc.framework.core.db.DaoException;
import com.fccfc.framework.core.db.annotation.DAO;
import com.fccfc.framework.core.db.annotation.Param;
import com.fccfc.framework.core.db.annotation.Sql;
import com.fccfc.framework.core.db.support.hibernate.IGenericBaseDao;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2014年11月30日 <br>
 * @since V1.0<br>
 * @see com.fccfc.framework.web.dao.operator <br>
 */
@DAO
public interface OperatorDao extends IGenericBaseDao {

    OperatorPojo getOperator(@Param("id") Integer id, @Param("code") Integer code) throws DaoException;

    OperatorPojo getOperatorByAccount(@Param("account") String account, @Param("type") String accountType)
        throws DaoException;

    @Sql("UPDATE OPERATOR O SET O.OPERATOR_CODE = :code WHERE O.OPERATOR_ID = :id")
    int updateOperatorCode(@Param("id") Integer id, @Param("code") Integer code) throws DaoException;

    @Sql("UPDATE OPERATOR O SET O.PASSWORD = :password WHERE O.OPERATOR_ID = :id")
    int updateOperatorPassword(@Param("id") int id, @Param("password") String password) throws DaoException;

    int insertOperatorHistory(@Param("id") Integer id, @Param("updateOperatorId") Integer updateOperatorId)
        throws DaoException;
}
