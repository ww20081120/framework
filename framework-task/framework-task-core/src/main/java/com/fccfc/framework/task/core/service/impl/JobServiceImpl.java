package com.fccfc.framework.task.core.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.fccfc.framework.db.core.DaoException;
import com.fccfc.framework.task.core.dao.JobDao;

/**
 * <Description> <br>
 * 
 * @author liu.baiyang<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年7月13日 <br>
 * @since migu<br>
 * @see com.fccfc.framework.task.core.service.impl <br>
 */
@Service
public class JobServiceImpl implements JobService {

    /**
     * jobDao
     */
    @Resource
    private JobDao jobDao;

    /**
     * Description: <br>
     * 
     * @author liu.baiyang<br>
     * @taskId <br>
     * @param taskId <br>
     * @param operatorId <br>
     * @param state <br>
     */
    @Override
    public void insertTaskHisAndTaskState(int taskId, int operatorId, String state) {
        try {
            jobDao.insertTaskHistory(taskId, operatorId);
            jobDao.updateTaskState(taskId, state);
        }
        catch (DaoException e) {
            e.printStackTrace();
        }
    }

    /**
     * Description: <br>
     * 
     * @author shao.dinghui<br>
     * @taskId <br>
     * @param taskId <br>
     * @param operatorId <br>
     * @param clz <br>
     */
    @Override
    public void insertTaskHisAndDeleteTaskById(int taskId, int operatorId, Class clz) {
        try {
            jobDao.insertTaskHistory(taskId, -1);
            jobDao.deleteById(clz, taskId);
        }
        catch (DaoException e) {
            e.printStackTrace();
        }
    }

}
