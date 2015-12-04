package com.fccfc.framework.web.manager.service.system;

import java.util.List;

import com.fccfc.framework.common.ServiceException;
import com.fccfc.framework.web.manager.bean.common.EventPojo;
import com.fccfc.framework.web.manager.bean.system.OperateLogPojo;

/***
 * <Description> <br>
 * 
 * @author bai.wenlong<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年7月13日 <br>
 * @since V6.11<br>
 * @see com.fccfc.framework.web.service <br>
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
    List<OperateLogPojo> queryOperateLog(Integer operatorid, String startdate, String enddate,
        Integer eventid, int pageIndex, int pageSize) throws ServiceException;
    
    /**
     * Description: <br> 
     * @author xu.jun<br>
     * @taskId <br>
     * @param operatorid 操作员id
     * @param startdate 开始日期
     * @param enddate 结束日期
     * @param eventid 事件id
     * @return List<OperateLogPojo>
     * @throws ServiceException <br>
     */
    List<OperateLogPojo> queryOperateLog(Integer operatorid, String startdate, String enddate,
        Integer eventid) throws ServiceException;
    
    /**
     * Description: <br> 
     * @author xu.jun<br>
     * @taskId <br>
     * @return List
     * @throws ServiceException <br>
     */
    List<EventPojo> qryEventList() throws ServiceException;
}
