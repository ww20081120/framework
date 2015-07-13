package com.fccfc.framework.task.core.service.impl;

/**
 * <Description> <br>
 * 
 * @author shao.dinghui<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年7月11日 <br>
 * @since V1.0<br>
 * @see com.fccfc.framework.task.core.service.impl <br>
 */
public interface JobService {

    /**
     * Description: <br>
     * 
     * @author liu.baiyang<br>
     * @taskId <br>
     * @param taskId <br>
     * @param operatorId <br>
     * @param state <br>
     */
    public void insertTaskHisAndTaskState(int taskId, int operatorId, String state);

    /**
     * Description: <br>
     * 
     * @author shao.dinghui<br>
     * @taskId <br>
     * @param taskId <br>
     * @param operatorId <br>
     * @param clz <br>
     */
    public void insertTaskHisAndDeleteTaskById(int taskId, int operatorId, Class clz);
}
