/**
 * 
 */
package com.fccfc.framework.web.dao.operator;

import com.fccfc.framework.db.core.DaoException;
import com.fccfc.framework.db.core.annotation.Dao;
import com.fccfc.framework.db.core.annotation.Param;
import com.fccfc.framework.db.core.annotation.Sql;
import com.fccfc.framework.db.hibernate.IGenericBaseDao;
import com.fccfc.framework.web.bean.operator.OperatorPojo;

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
@Dao
public interface OperatorDao extends IGenericBaseDao {

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param id <br>
     * @param code <br>
     * @return <br>
     * @throws DaoException <br>
     */
    OperatorPojo getOperator(@Param("id") Integer id, @Param("code") Integer code) throws DaoException;

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param account <br>
     * @param accountType <br>
     * @return <br>
     * @throws DaoException <br>
     */
    OperatorPojo getOperatorByAccount(@Param("account") String account, @Param("type") String accountType)
        throws DaoException;

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param id <br>
     * @param code <br>
     * @return <br>
     * @throws DaoException <br>
     */
    @Sql("UPDATE OPERATOR O SET O.OPERATOR_CODE = :code WHERE O.OPERATOR_ID = :id")
    int updateOperatorCode(@Param("id") Integer id, @Param("code") Integer code) throws DaoException;

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param id <br>
     * @param password <br>
     * @return <br>
     * @throws DaoException <br>
     */
    @Sql("UPDATE OPERATOR O SET O.PASSWORD = :password WHERE O.OPERATOR_ID = :id")
    int updateOperatorPassword(@Param("id") int id, @Param("password") String password) throws DaoException;

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param id <br>
     * @param updateOperatorId <br>
     * @return <br>
     * @throws DaoException <br>
     */
    int insertOperatorHistory(@Param("id") Integer id, @Param("updateOperatorId") Integer updateOperatorId)
        throws DaoException;
}
