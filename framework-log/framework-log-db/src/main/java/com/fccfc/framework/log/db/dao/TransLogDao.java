/**
 * 
 */
package com.fccfc.framework.log.db.dao;

import java.util.List;

import com.fccfc.framework.db.core.DaoException;
import com.fccfc.framework.db.core.annotation.Dao;
import com.fccfc.framework.db.core.annotation.Param;
import com.fccfc.framework.db.core.annotation.Sql;
import com.fccfc.framework.db.hibernate.IGenericBaseDao;
import com.fccfc.framework.log.db.bean.TransLogPojo;
import com.fccfc.framework.log.db.bean.TransLogStackPojo;

/**
 * <Description> <br>
 * 
 * @author Administrator<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2014年11月29日 <br>
 * @since V7.3<br>
 * @see com.fccfc.framework.log.dao <br>
 */
@Dao
public interface TransLogDao extends IGenericBaseDao {

    @Sql(bean = TransLogPojo.class)
    List<TransLogPojo> listTransLog(@Param(Param.pageIndex) int pageIndex, @Param(Param.pageSize) int pageSize)
        throws DaoException;

    @Sql(value = "SELECT COUNT(*) FROM TRANS_LOG", bean = Integer.class)
    int getTransLogListSize() throws DaoException;

    @Sql(
        value = "SELECT STACK_ID, SEQ, TRANS_ID, PARENT_STACK_ID, METHOD, BEGIN_TIME, END_TIME, CONSUME_TIME FROM TRANS_LOG_STACK WHERE TRANS_ID = :transId",
        bean = TransLogStackPojo.class)
    List<TransLogStackPojo> listTransLogStack(@Param("transId") String transId) throws DaoException;

    @Sql(value = "SELECT * FROM TRANS_LOG_STACK WHERE TRANS_ID = :transId AND STACK_ID = :stackId",
        bean = TransLogStackPojo.class)
    TransLogStackPojo getTransLogStackPojo(@Param("transId") String transId, @Param("stackId") String stackId)
        throws DaoException;
}
