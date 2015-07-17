package com.fccfc.framework.web.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.fccfc.framework.common.ServiceException;
import com.fccfc.framework.db.core.DaoException;
import com.fccfc.framework.web.bean.operator.OperateLogPojo;
import com.fccfc.framework.web.dao.operator.OperateLogDao;
import com.fccfc.framework.web.service.OperateLogService;

/***
 * <Description> <br>
 * 
 * @author bai.wenlong<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年7月13日 <br>
 * @since V6.11<br>
 * @see com.fccfc.framework.web.service.impl <br>
 */
@Service
public class OperateLogServiceImpl implements OperateLogService {
    /**
     * operateLogDao
     */
    @Resource
    private OperateLogDao operateLogDao;

    /***
     * Description: <br>
     * 
     * @author bai.wenlong<br>
     * @taskId <br>
     * @param operateLogPojo <br>
     * @throws ServiceException <br>
     * <br>
     */
    @Override
    public void save(OperateLogPojo operateLogPojo) throws ServiceException {
        try {
            operateLogDao.save(operateLogPojo);
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }

    }

}
