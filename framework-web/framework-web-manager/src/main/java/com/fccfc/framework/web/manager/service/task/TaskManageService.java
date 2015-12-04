package com.fccfc.framework.web.manager.service.task;

import java.util.List;

import com.fccfc.framework.common.ServiceException;
import com.fccfc.framework.config.core.bean.ModulePojo;
import com.fccfc.framework.task.core.bean.TaskPojo;

/**
 * <Description> <br>
 * 
 * @author liu.baiyang<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年11月18日 <br>
 * @since bps<br>
 * @see com.fccfc.framework.web.manager.service.task <br>
 */
public interface TaskManageService {

    /**
     * Description: <br>
     * 
     * @author liu.baiyang<br>
     * @taskId <br>
     * @param taskPojo <br>
     * @param pageIndex <br>
     * @param pageSize <br>
     * @return <br>
     * @throws ServiceException <br>
     */
    List<TaskPojo> queryAllTask(TaskPojo taskPojo, int pageIndex, int pageSize) throws ServiceException;

    /**
     * Description: <br>
     * 
     * @author liu.baiyang<br>
     * @taskId <br>
     * @param pojo <br>
     * @throws ServiceException <br>
     */
    void addTask(TaskPojo pojo) throws ServiceException;

    /**
     * Description: <br>
     * 
     * @author liu.baiyang<br>
     * @taskId <br>
     * @param taskId <br>
     * @throws ServiceException <br>
     */
    void deleteTask(Integer taskId) throws ServiceException;

    /**
     * Description:导出任务 <br>
     * 
     * @author liu.baiyang<br>
     * @taskId <br>
     * @param mediaId <br>
     * @param mediaName <br>
     * @throws ServiceException <br>
     */
    void importTaskData(String mediaId, String mediaName) throws ServiceException;

    /**
     * Description: <br>
     * 
     * @author liu.baiyang<br>
     * @taskId <br>
     * @param pojo <br>
     * @throws ServiceException <br>
     */
    void modifyTask(TaskPojo pojo) throws ServiceException;

    /**
     * Description: <br>
     * 
     * @author liu.baiyang
     * @taskId <br>
     * @param taskIds <br>
     * @param state <br>
     * @throws ServiceException <br>
     */
    void modifyTaskState(String[] taskIds, String state) throws ServiceException;

    /**
     * Description: 根据条件查询任务信息<br>
     * 
     * @author liu.baiyang<br>
     * @taskId <br>
     * @param taskPojo <br>
     * @return <br>
     * @throws ServiceException <br>
     */
    List<TaskPojo> queryTaskById(TaskPojo taskPojo) throws ServiceException;

    /**
     * Description: 查询模块信息<br>
     * 
     * @author liu.baiyang<br>
     * @taskId <br>
     * @param pojo <br>
     * @return <br>
     * @throws ServiceException <br>
     */
    List<ModulePojo> queryModule(ModulePojo pojo) throws ServiceException;

    /**
     * Description:插入历史表 <br>
     * 
     * @author liu.baiyang<br>
     * @taskId <br>
     * @param taskPojo taskPojo
     * @throws ServiceException <br>
     */
    void insertTaskHis(TaskPojo taskPojo) throws ServiceException;

    /**
     * Description: <br>
     * 
     * @author liu.baiyang<br>
     * @taskId <br>
     * @param taskPojo <br>
     * @return <br>
     * @throws ServiceException <br>
     */
    List<TaskPojo> checkTask(TaskPojo taskPojo) throws ServiceException;

    /**
     * Description: <br>
     * 
     * @author liu.baiyang<br>
     * @taskId <br>
     * @param taskIds <br>
     * @param taskStates <br>
     * @return <br>
     * @throws ServiceException <br>
     */
    List<TaskPojo> queryTaskByState(String[] taskIds, String[] taskStates) throws ServiceException;
}
