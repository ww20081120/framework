/**
 * 
 */
package com.fccfc.framework.task.core.dao;

import java.util.List;

import com.fccfc.framework.db.core.DaoException;
import com.fccfc.framework.db.core.annotation.Dao;
import com.fccfc.framework.db.core.annotation.Param;
import com.fccfc.framework.db.core.annotation.Sql;
import com.fccfc.framework.db.hibernate.IGenericBaseDao;
import com.fccfc.framework.task.core.bean.TaskPojo;
import com.fccfc.framework.task.core.bean.TaskTriggerPojo;

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
public interface TaskDao extends IGenericBaseDao {

    @Sql(bean = TaskPojo.class)
    List<TaskPojo> selectTaskList(@Param("task") TaskPojo taskPojo, @Param(Param.pageIndex) int pageIndex,
        @Param(Param.pageSize) int pageSize) throws DaoException;

    int insertTaskHistory(@Param("taskId") int taskId, @Param("operatorId") Integer operatorId) throws DaoException;

    @Sql("INSERT INTO TASK_TRIGGER (TASK_ID, TRIGGER_TYPE, TRIGGER_ID) VALUES (:t.taskId, :t.triggerType, :t.triggerId)")
    int insertTaskTrigger(@Param("t") TaskTriggerPojo taskTrigger) throws DaoException;

    @Sql("UPDATE TASK T SET T.TASK_STATE = :state WHERE T.TASK_ID = :taskId")
    void updateTaskState(@Param("taskId") int taskId, @Param("state") String state) throws DaoException;
}
