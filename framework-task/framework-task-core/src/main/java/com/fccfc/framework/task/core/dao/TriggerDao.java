/**
 * 
 */
package com.fccfc.framework.task.core.dao;

import java.util.List;

import com.fccfc.framework.db.core.DaoException;
import com.fccfc.framework.db.core.annotation.Dao;
import com.fccfc.framework.db.core.annotation.Param;
import com.fccfc.framework.db.core.annotation.Sql;
import com.fccfc.framework.task.core.bean.CronTriggerPojo;
import com.fccfc.framework.task.core.bean.SimpleTriggerPojo;
import com.fccfc.framework.task.core.bean.TriggerPojo;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2014年11月5日 <br>
 * @since V1.0<br>
 * @see com.fccfc.framework.task.core.dao <br>
 */
@Dao
public interface TriggerDao {

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param taskId <br>
     * @return <br>
     * @throws DaoException <br>
     */
    @Sql(bean = TriggerPojo.class)
    List<TriggerPojo> selectTriggerByTaskId(@Param("taskId") int taskId) throws DaoException;

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param trigger <br>
     * @return <br>
     * @throws DaoException <br>
     */
    int saveCronTrigger(@Param("t") CronTriggerPojo trigger) throws DaoException;

    /**
     * Description: <br> 
     *  
     * @author shao.dinghui<br>
     * @taskId <br>
     * @param triggerId <br>
     * @throws DaoException <br>
     */
    void delCronTrigger(@Param("triggerId") int triggerId) throws DaoException;
    
    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param trigger <br>
     * @return <br>
     * @throws DaoException <br>
     */
    int saveSimpleTrigger(@Param("t") SimpleTriggerPojo trigger) throws DaoException;

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param triggerId <br>
     * @return <br>
     * @throws DaoException <br>
     */
    @Sql(bean = CronTriggerPojo.class)
    CronTriggerPojo getCronTriggerById(@Param("id") int triggerId) throws DaoException;

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param triggerId <br>
     * @return <br>
     * @throws DaoException <br>
     */
    @Sql(bean = CronTriggerPojo.class)
    SimpleTriggerPojo getSimpleTriggerById(@Param("id") int triggerId) throws DaoException;
}
