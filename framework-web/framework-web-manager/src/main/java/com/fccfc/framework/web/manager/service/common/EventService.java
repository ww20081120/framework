package com.fccfc.framework.web.manager.service.common;

import java.util.List;






import javax.servlet.http.HttpServletResponse;

import com.fccfc.framework.common.ServiceException;
import com.fccfc.framework.web.manager.bean.common.EventPojo;

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
    
    boolean checkUserName(String eventId,String checkUserName) throws ServiceException;
    
    EventPojo getEvent(Integer eventId) throws ServiceException;

    void modify(EventPojo event) throws ServiceException;
    
    void delete(Integer[] ids) throws ServiceException;
    
    void exportEvent(HttpServletResponse response,int pageIndex, int pageSize) throws ServiceException;

    void importEvent(String mediaId,String mediaName) throws ServiceException;
}
