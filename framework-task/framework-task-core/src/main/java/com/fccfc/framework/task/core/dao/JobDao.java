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
 * @see com.fccfc.framework.task.core.dao <br>
 */
@Dao
public interface JobDao extends IGenericBaseDao {

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param taskPojo <br>
     * @param pageIndex <br>
     * @param pageSize <br>
     * @return <br>
     * @throws DaoException <br>
     */
    @Sql(bean = TaskPojo.class)
    List<TaskPojo> selectTaskList(@Param("task") TaskPojo taskPojo, @Param(Param.PAGE_INDEX) int pageIndex,
        @Param(Param.PAGE_SIZE) int pageSize) throws DaoException;

    /**
	 * Description: <br> 
	 *  
	 * @author shao.dinghui<br>
	 * @taskId <br>
	 * @param taskTrigger
	 * @return
	 * @throws DaoException <br>
	 */
	int insertTask(@Param("t") TaskPojo taskPojo) throws DaoException;
    
    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param taskId <br>
     * @param operatorId <br>
     * @return <br>
     * @throws DaoException <br>
     */
    int insertTaskHistory(@Param("taskId") int taskId, @Param("operatorId") Integer operatorId) throws DaoException;

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param taskTrigger <br>
     * @return <br>
     * @throws DaoException <br>
     */
    int insertTaskTrigger(@Param("t") TaskTriggerPojo taskTrigger) throws DaoException;

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param taskId <br>
     * @param state <br>
     * @throws DaoException <br>
     */
    void updateTaskState(@Param("taskId") int taskId, @Param("state") String state) throws DaoException;
}
