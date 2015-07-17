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
 * @since V1.0<br>
 * @see com.fccfc.framework.log.dao <br>
 */
@Dao
public interface TransLogDao extends IGenericBaseDao {

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param pageIndex <br>
     * @param pageSize <br>
     * @return <br>
     * @throws DaoException <br>
     */
    @Sql(bean = TransLogPojo.class)
    List<TransLogPojo> listTransLog(@Param(Param.PAGE_INDEX) int pageIndex, @Param(Param.PAGE_SIZE) int pageSize)
        throws DaoException;

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @return <br>
     * @throws DaoException <br>
     */
    @Sql(bean = Integer.class)
    int getTransLogListSize() throws DaoException;

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param transId <br>
     * @return <br>
     * @throws DaoException <br>
     */
    @Sql(bean = TransLogStackPojo.class)
    List<TransLogStackPojo> listTransLogStack(@Param("transId") String transId) throws DaoException;

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param transId <br>
     * @param stackId <br>
     * @return <br>
     * @throws DaoException <br>
     */
    @Sql(bean = TransLogStackPojo.class)
    TransLogStackPojo getTransLogStackPojo(@Param("transId") String transId, @Param("stackId") String stackId)
        throws DaoException;
}
