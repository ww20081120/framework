package com.hbasesoft.framework.web.system.service;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.hbasesoft.framework.web.system.bean.EventPojo;
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

public interface EventService {

    /***
     * Description: <br>
     * 
     * @author bai.wenlong<br>
     * @taskId <br>
     * @return List<EventPojo> <br>
     * @throws ServiceException <br>
     */
    List<EventPojo> selectList(int pageIndex, int pageSize) throws ServiceException;

    void saveEvent(EventPojo event) throws ServiceException;

    boolean checkUserName(String eventId, String checkUserName) throws ServiceException;

    EventPojo getEventByCode(String eventCode) throws ServiceException;

    EventPojo getEventById(Integer eventId) throws ServiceException;

    void modify(EventPojo event) throws ServiceException;

    void delete(Integer[] ids) throws ServiceException;

    void exportEvent(HttpServletResponse response, int pageIndex, int pageSize) throws ServiceException;

    void importEvent(String mediaId, String mediaName) throws ServiceException;
}
