/**
 * 
 */
package com.hbasesoft.framework.web.system.dao.task;

import java.util.List;

import com.hbasesoft.framework.task.core.bean.TaskPojo;
import com.hbasesoft.framework.config.core.bean.ModulePojo;
import com.hbasesoft.framework.db.core.DaoException;
import com.hbasesoft.framework.db.core.annotation.Dao;
import com.hbasesoft.framework.db.core.annotation.Param;
import com.hbasesoft.framework.db.core.annotation.Sql;

/**
 * <Description> <br>
 * 
 * @author XXX<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年11月24日 <br>
 * @since bps<br>
 * @see com.hbasesoft.framework.web.manager.dao.system.task <br>
 */
@Dao
public interface TaskDao {

    /**
     * Description: 查询任务<br>
     * 
     * @author XXX<br>
     * @taskId <br>
     * @param taskPojo <br>
     * @return <br>
     * @throws DaoException <br>
     */
    @Sql(bean = TaskPojo.class)
    List<TaskPojo> selectTaskList(@Param("task") TaskPojo taskPojo) throws DaoException;

    /**
     * Description: 添加任务信息<br>
     * 
     * @author XXX<br>
     * @taskId <br>
     * @param pojo <br>
     * @return <br>
     * @throws DaoException <br>
     */
    int insertTask(@Param("pojo") TaskPojo pojo) throws DaoException;

    /**
     * Description:修改任务信息<br>
     * 
     * @author XXX<br>
     * @taskId <br>
     * @param pojo <br>
     * @throws DaoException <br>
     */
    void updateTask(@Param("pojo") TaskPojo pojo) throws DaoException;

    /**
     * Description: 导入 新增 修改校验<br>
     * 
     * @author XXX<br>
     * @taskId <br>
     * @param taskPojo <br>
     * @return <br>
     * @throws DaoException <br>
     */
    @Sql(bean = TaskPojo.class)
    List<TaskPojo> checkTask(@Param("task") TaskPojo taskPojo) throws DaoException;

    /**
     * Description: 查询模块<br>
     * 
     * @author XXX<br>
     * @taskId <br>
     * @param pojo <br>
     * @return <br>
     * @throws DaoException <br>
     */
    @Sql(bean = ModulePojo.class)
    List<ModulePojo> selectAllModule(@Param("pojo") ModulePojo pojo) throws DaoException;

    /**
     * Description: <br>
     * 
     * @author liu.baiyang<br>
     * @taskId <br>
     * @param taskIds <br>
     * @param taskStates <br>
     * @return <br>
     * @throws DaoException <br>
     */
    @Sql(bean = TaskPojo.class)
    List<TaskPojo> queryTaskByState(@Param("taskIds") String[] taskIds, @Param("taskStates") String[] taskStates)
        throws DaoException;

    /**
     * Description: <br>
     * 
     * @author liu.baiyang<br>
     * @taskId <br>
     * @param taskId <br>
     * @param state <br>
     * @throws DaoException <br>
     */
    void modifyTaskState(@Param("taskId") String[] taskId, @Param("state") String state) throws DaoException;
}
