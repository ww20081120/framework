package com.fccfc.framework.task.core.service.impl;

import com.fccfc.framework.common.ServiceException;


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
	 * @author shao.dinghui<br>
	 * @taskId <br>
	 * @param taskId
	 * @param operatorId
	 * @param state <br>
	 */
	public void insertTaskHisAndTaskState(int taskId, int operatorId, String state) throws ServiceException ;
	
	/**
	 * Description: <br> 
	 *  
	 * @author shao.dinghui<br>
	 * @taskId <br>
	 * @param taskId
	 * @param operatorId
	 * @param clz <br>
	 */
	public void insertTaskHisAndDeleteTaskById(int taskId, int operatorId, Class clz) throws ServiceException ;
}
