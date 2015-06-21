/**
 * 
 */
package com.fccfc.framework.log.core.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.fccfc.framework.common.ServiceException;
import com.fccfc.framework.db.core.DaoException;
import com.fccfc.framework.log.core.bean.TransLogPojo;
import com.fccfc.framework.log.core.bean.TransLogStackPojo;
import com.fccfc.framework.log.core.dao.TransLogDao;
import com.fccfc.framework.log.core.service.TransLogService;

/**
 * <Description> <br>
 * 
 * @author Administrator<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2014年12月6日 <br>
 * @since V7.3<br>
 * @see com.fccfc.framework.log.service.impl <br>
 */
@Service
public class TransLogServiceImpl implements TransLogService {

    @Resource
    private TransLogDao transLogDao;

    @Override
    public void addTransactionLog(TransLogPojo transLogPo) throws ServiceException {
        try {
            transLogDao.save(transLogPo);
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public void addTransactionStackLog(TransLogStackPojo logPo) throws ServiceException {
        try {
            transLogDao.save(logPo);
        }
        catch (DaoException e) {
            e.printStackTrace();
            throw new ServiceException(e);
        }
    }

    @Override
    public Map<String, Object> listTransLogPojo(int pageIndex, int pageSize) throws ServiceException {
        try {
            Map<String, Object> map = new HashMap<String, Object>();
            List<TransLogPojo> list = this.transLogDao.listTransLog(pageIndex, pageSize);
            map.put("list", JSONArray.toJSONStringWithDateFormat(list, "yyyy-MM-dd HH:mm:ss", new SerializerFeature[0]));
            map.put("total", this.transLogDao.getTransLogListSize());
            return map;
        }
        catch (DaoException e) {
            e.printStackTrace();
            throw new ServiceException(e);
        }
    }

    @Override
    public List<TransLogStackPojo> listTransLogStackPojo(String transId) throws ServiceException {
        try {
            return this.transLogDao.listTransLogStack(transId);
        }
        catch (DaoException e) {
            e.printStackTrace();
            throw new ServiceException(e);
        }
    }

    @Override
    public TransLogStackPojo queryTransLogStackPojo(String transId, String stackId) throws ServiceException {
        try {
            return this.transLogDao.getTransLogStackPojo(transId, stackId);
        }
        catch (DaoException e) {
            e.printStackTrace();
            throw new ServiceException(e);
        }
    }
}
