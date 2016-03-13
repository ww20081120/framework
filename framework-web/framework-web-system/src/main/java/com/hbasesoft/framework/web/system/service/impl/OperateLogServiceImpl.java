package com.hbasesoft.framework.web.system.service.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.map.HashedMap;
import org.springframework.stereotype.Service;

import com.hbasesoft.framework.web.core.utils.excel.ExcelExportDto;
import com.hbasesoft.framework.web.core.utils.excel.ExcelUtil;
import com.hbasesoft.framework.web.system.bean.EventPojo;
import com.hbasesoft.framework.web.system.bean.OperateLogPojo;
import com.hbasesoft.framework.web.system.dao.log.OperateLogDao;
import com.hbasesoft.framework.web.system.service.OperateLogService;
import com.hbasesoft.framework.common.ServiceException;
import com.hbasesoft.framework.db.core.DaoException;

/***
 * <Description> <br>
 * 
 * @author bai.wenlong<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年7月13日 <br>
 * @since V6.11<br>
 * @see com.hbasesoft.framework.web.manager.service.log.impl <br>
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
     *             <br>
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
    public List<OperateLogPojo> queryOperateLog(Integer operatorid, String starttime, String endtime, Integer eventid,
        int pageIndex, int pageSize) throws ServiceException {
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
    public List<OperateLogPojo> queryOperateLog(Integer operatorid, String starttime, String endtime, Integer eventid)
        throws ServiceException {
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

    @Override
    public List<OperateLogPojo> getAll(Integer pageIndex, Integer pageSize) throws ServiceException {
        List<OperateLogPojo> list = new ArrayList<OperateLogPojo>();
        try {
            list = operateLogDao.getAll(pageIndex, pageSize);
        }
        catch (DaoException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public void export(HttpServletResponse response, int pageIndex, int pageSize) throws ServiceException {
        try {
            List<OperateLogPojo> list = operateLogDao.getAll(pageIndex, pageSize);
            String[] fields = {
                "operateLogId", "eventName", "ip", "adminName", "paramsValue", "createTime"
            };
            Map<String, String> fieldsHeader = new HashedMap();
            fieldsHeader.put("operateLogId", "操作日志标识");
            fieldsHeader.put("eventName", "事件名称");
            fieldsHeader.put("ip", "ip地址");
            fieldsHeader.put("adminName", "管理员姓名");
            fieldsHeader.put("paramsValue", "参数");
            fieldsHeader.put("createTime", "创建时间");
            ExcelExportDto<OperateLogPojo> exportDto = new ExcelExportDto<OperateLogPojo>(fields, fieldsHeader, list);
            OutputStream os = null;
            try {
                response.setHeader("Content-Disposition",
                    "attachment;filename=\"" + new String("操作日志.xls".getBytes(), "ISO8859-1") + "\"");
                os = response.getOutputStream();
                ExcelUtil.exportExcel(os, exportDto);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                try {
                    if (os != null) {
                        os.close();
                    }
                }
                catch (IOException e) {
                }
            }
        }
        catch (DaoException e) {
            e.printStackTrace();
        }

    }
}
