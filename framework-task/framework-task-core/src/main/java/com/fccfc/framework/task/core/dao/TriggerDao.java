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
 * @see com.fccfc.framework.task.dao <br>
 */
@Dao
public interface TriggerDao {

    @Sql(bean = TriggerPojo.class)
    List<TriggerPojo> selectTriggerByTaskId(@Param("taskId") int taskId) throws DaoException;

    int saveOrUpdateCronTrigger(@Param("trigger") CronTriggerPojo trigger) throws DaoException;

    int saveOrUpdateSimpleTrigger(@Param("trigger") SimpleTriggerPojo trigger) throws DaoException;

    @Sql(value = "SELECT T.* FROM CRON_TRIGGER T WHERE T.TRIGGER_ID = :id", bean = CronTriggerPojo.class)
    CronTriggerPojo getCronTriggerById(@Param("id") int triggerId) throws DaoException;

    @Sql(value = "SELECT T.* FROM SIMPLE_TRIGGER t WHERE T.TRIGGER_ID = :id", bean = CronTriggerPojo.class)
    SimpleTriggerPojo getSimpleTriggerById(@Param("id") int triggerId) throws DaoException;

    @Sql("SELECT LAST_INSERT_ID()")
    int getId() throws DaoException;
}
