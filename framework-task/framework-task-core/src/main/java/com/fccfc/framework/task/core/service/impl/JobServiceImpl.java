package com.fccfc.framework.task.core.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.fccfc.framework.common.ErrorCodeDef;
import com.fccfc.framework.common.ServiceException;
import com.fccfc.framework.db.core.DaoException;
import com.fccfc.framework.task.core.dao.JobDao;

@Service
public class JobServiceImpl implements JobService {
	
	@Resource
	private JobDao jobDao;
	
	/**
	 * Description: <br> 
	 *  
	 * @author shao.dinghui<br>
	 * @taskId <br>
	 * @param taskId
	 * @param operatorId
	 * @param state <br>
	 * @throws ServiceException 
	 */
	@Override
	public void insertTaskHisAndTaskState(int taskId, int operatorId, String state) throws ServiceException {
		try {
			jobDao.insertTaskHistory(taskId, operatorId);
			jobDao.updateTaskState(taskId, state);
		} catch (DaoException e) {
			throw new ServiceException(ErrorCodeDef.SAVE_HIS_ERROR_20026, "保存TASK历史记录失败", e);
		}
	}

	/**
	 * Description: <br> 
	 *  
	 * @author shao.dinghui<br>
	 * @taskId <br>
	 * @param taskId
	 * @param operatorId
	 * @param clz <br>
	 */
	@Override
	public void insertTaskHisAndDeleteTaskById(int taskId, int operatorId, Class clz) throws ServiceException {
		try {
			jobDao.insertTaskHistory(taskId, -1);
			jobDao.deleteById(clz, taskId);
		} catch (DaoException e) {
			throw new ServiceException(ErrorCodeDef.SAVE_HIS_ERROR_20026, "保存TASK历史记录失败", e);
		}
	}
}
