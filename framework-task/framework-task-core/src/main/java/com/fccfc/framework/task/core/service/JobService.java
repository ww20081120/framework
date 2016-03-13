package com.hbasesoft.framework.task.core.service;

import com.hbasesoft.framework.common.ServiceException;

/**
 * <Description> <br>
 * 
 * @author shao.dinghui<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年7月11日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.task.core.service.impl <br>
 */
public interface JobService {

    /**
     * Description: <br>
     * 
     * @author shao.dinghui<br>
     * @taskId <br>
     * @param taskId <br>
     * @param operatorId <br>
     * @param state <br>
     * @throws ServiceException <br>
     */
    public void insertTaskHisAndTaskState(int taskId, int operatorId, String state) throws ServiceException;

    /**
     * Description: <br>
     * 
     * @author shao.dinghui<br>
     * @taskId <br>
     * @param taskId <br>
     * @param operatorId <br>
     * @throws ServiceException <br>
     */
    public void insertTaskHisAndDeleteTaskById(int taskId, int operatorId)
        throws ServiceException;
}
