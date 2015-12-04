package com.fccfc.framework.web.manager.service.system.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.fccfc.framework.common.ServiceException;
import com.fccfc.framework.db.core.DaoException;
import com.fccfc.framework.web.manager.bean.common.EventPojo;
import com.fccfc.framework.web.manager.bean.system.OperateLogPojo;
import com.fccfc.framework.web.manager.dao.system.log.OperateLogDao;
import com.fccfc.framework.web.manager.service.system.OperateLogService;

/***
 * <Description> <br>
 * 
 * @author bai.wenlong<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年7月13日 <br>
 * @since V6.11<br>
 * @see  com.fccfc.framework.web.manager.service.log.impl <br>
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

    /**
     * Description: <br> 
     *  
     * @author xu.jun<br>
     * @taskId <br>
     * @param operatorid 管理员id
     * @param starttime 开始日期
     * @param endtime 结束日期
     * @param eventid 事件id
     * @param pageIndex 当前页数
     * @param pageSize 每页记录数
     * @return List<OperateLogPojo>
     * @throws ServiceException <br>
     */
	@Override
	public List<OperateLogPojo> queryOperateLog(Integer operatorid,
			String starttime, String endtime, Integer eventid, int pageIndex,
			int pageSize) throws ServiceException {
        try {
            String startdate = null;
            String enddate = null;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            if ((starttime == null || starttime.trim().isEmpty()) && (endtime == null || endtime.trim().isEmpty())) {
                Date end = new Date();
                long l = (end.getTime() / 1000 - 60 * 60 * 24 * 60) * 1000;
                Date start = new Date(l);
                enddate = sdf.format(end);
                startdate = sdf.format(start);
            } 
            else if (starttime != null && (endtime == null || endtime.trim().isEmpty())) {
                Date start = sdf.parse(starttime);
                long l = (start.getTime() / 1000 + 60 * 60 * 24 * 60) * 1000;
                Date end = new Date(l);
                enddate = sdf.format(end);
                startdate = sdf.format(start);
            }
            else if ((starttime == null || starttime.trim().isEmpty()) && endtime != null) {
                Date end = sdf.parse(endtime);
                long l = (end.getTime() / 1000 - 60 * 60 * 24 * 60) * 1000;
                Date start = new Date(l);
                enddate = sdf.format(end);
                startdate = sdf.format(start);
            }
            else {
                enddate = endtime;
                startdate = starttime;
            }
            return operateLogDao.selectServiceRecord(operatorid, startdate, enddate, eventid, pageIndex, pageSize);
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }
        catch (ParseException e) {
            throw new RuntimeException(e);
        }
	}
	
	 /**
     * Description: <br> 
     *  
     * @author xu.jun<br>
     * @taskId <br>
     * @param operatorid 管理员id
     * @param starttime 开始日期
     * @param endtime 结束日期
     * @param eventid 事件id
     * @return List<OperateLogPojo>
     * @throws ServiceException <br>
     */
	@Override
	public List<OperateLogPojo> queryOperateLog(Integer operatorid,
			String starttime, String endtime, Integer eventid) throws ServiceException {
        try {
            String startdate = null;
            String enddate = null;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            if ((starttime == null || starttime.trim().isEmpty()) && (endtime == null || endtime.trim().isEmpty())) {
                Date end = new Date();
                long l = (end.getTime() / 1000 - 60 * 60 * 24 * 60) * 1000;
                Date start = new Date(l);
                enddate = sdf.format(end);
                startdate = sdf.format(start);
            } 
            else if (starttime != null && (endtime == null || endtime.trim().isEmpty())) {
                Date start = sdf.parse(starttime);
                long l = (start.getTime() / 1000 + 60 * 60 * 24 * 60) * 1000;
                Date end = new Date(l);
                enddate = sdf.format(end);
                startdate = sdf.format(start);
            }
            else if ((starttime == null || starttime.trim().isEmpty()) && endtime != null) {
                Date end = sdf.parse(endtime);
                long l = (end.getTime() / 1000 - 60 * 60 * 24 * 60) * 1000;
                Date start = new Date(l);
                enddate = sdf.format(end);
                startdate = sdf.format(start);
            }
            else {
                enddate = endtime;
                startdate = starttime;
            }
            return operateLogDao.selectServiceRecord(operatorid, startdate, enddate, eventid);
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }
        catch (ParseException e) {
            throw new RuntimeException(e);
        }
	}

	/**
     * Description: <br> 
     *  
     * @author xu.jun<br>
     * @return List
     * @throws ServiceException <br>
     */
	@Override
	public List<EventPojo> qryEventList() throws ServiceException {
		try {
            return operateLogDao.qryEventList();
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }
	}
}
