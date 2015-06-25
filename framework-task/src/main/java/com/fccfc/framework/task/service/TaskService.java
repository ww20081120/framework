/**
 * 
 */
package com.fccfc.framework.task.service;

import com.fccfc.framework.common.ServiceException;
import com.fccfc.framework.task.bean.CronTriggerPojo;
import com.fccfc.framework.task.bean.SimpleTriggerPojo;
import com.fccfc.framework.task.bean.TaskPojo;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2014年11月5日 <br>
 * @since V1.0<br>
 * @see com.fccfc.framework.api.task <br>
 */
public interface TaskService {

    /**
     * 触发所有Job
     * 
     * @throws ServiceException
     */
    void scheduleAllTask() throws ServiceException;

    /**
     * 触发Job
     * 
     * @param taskPojo
     * @param simpleTriggerPojo
     * @throws ServiceException
     */
    void schedule(TaskPojo taskPojo, SimpleTriggerPojo simpleTriggerPojo) throws ServiceException;

    /**
     * 触发Job
     * 
     * @param taskPojo
     * @param cronTriggerPojo
     * @throws ServiceException
     */
    void schedule(TaskPojo taskPojo, CronTriggerPojo cronTriggerPojo) throws ServiceException;

    /**
     * 暂停Job
     * 
     * @param taskPojo
     * @throws ServiceException
     */
    void pause(TaskPojo taskPojo) throws ServiceException;

    /**
     * 恢复Job
     * 
     * @param taskPojo
     * @throws ServiceException
     */
    void resume(TaskPojo taskPojo) throws ServiceException;

    /**
     * 删除Job
     * 
     * @param taskPojo
     * @throws ServiceException
     */
    void remove(TaskPojo taskPojo) throws ServiceException;
}
