package com.hbasesoft.framework.web.system.service;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.hbasesoft.framework.web.system.bean.EventPojo;
import com.hbasesoft.framework.web.system.bean.OperateLogPojo;
import com.hbasesoft.framework.common.ServiceException;

/***
 * <Description> <br>
 * 
 * @author bai.wenlong<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年7月13日 <br>
 * @since V6.11<br>
 * @see com.hbasesoft.framework.web.service <br>
 */
public interface OperateLogService {
    /***
     * Description: <br>
     * 
     * @author bai.wenlong<br>
     * @taskId <br>
     * @param operateLogPojo <br>
     * @throws ServiceException <br>
     */
    void save(OperateLogPojo operateLogPojo) throws ServiceException;

    /**
     * Description: <br>
     * 
     * @author xu.jun<br>
     * @taskId <br>
     * @param operatorid 操作员id
     * @param startdate 开始日期
     * @param enddate 结束日期
     * @param eventid 事件id
     * @param pageIndex 当前页数
     * @param pageSize 每页条目数
     * @return List<OperateLogPojo>
     * @throws ServiceException <br>
     */
    List<OperateLogPojo> queryOperateLog(Integer operatorid, String startdate, String enddate, Integer eventid,
        int pageIndex, int pageSize) throws ServiceException;

    /**
     * Description: <br>
     * 
     * @author xu.jun<br>
     * @taskId <br>
     * @param operatorid 操作员id
     * @param startdate 开始日期
     * @param enddate 结束日期
     * @param eventid 事件id
     * @return List<OperateLogPojo>
     * @throws ServiceException <br>
     */
    List<OperateLogPojo> queryOperateLog(Integer operatorid, String startdate, String enddate, Integer eventid)
        throws ServiceException;

    /**
     * Description: <br>
     * 
     * @author xu.jun<br>
     * @taskId <br>
     * @return List
     * @throws ServiceException <br>
     */
    List<EventPojo> qryEventList() throws ServiceException;

    List<OperateLogPojo> getAll(Integer pageIndex, Integer pageSize) throws ServiceException;

    void export(HttpServletResponse response, int pageIndex, int pageSize) throws ServiceException;
}
