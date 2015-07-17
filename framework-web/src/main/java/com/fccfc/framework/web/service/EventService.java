package com.fccfc.framework.web.service;

import java.util.List;

import com.fccfc.framework.common.ServiceException;
import com.fccfc.framework.web.bean.event.EventPojo;

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
    List<EventPojo> selectList() throws ServiceException;
}
