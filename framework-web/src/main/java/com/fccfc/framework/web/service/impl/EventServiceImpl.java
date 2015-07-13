package com.fccfc.framework.web.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.fccfc.framework.common.ServiceException;
import com.fccfc.framework.db.core.DaoException;
import com.fccfc.framework.web.bean.event.EventPojo;
import com.fccfc.framework.web.dao.event.EventDao;
import com.fccfc.framework.web.service.EventService;

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
public class EventServiceImpl implements EventService {
    /**
     * operateLogDao
     */
    @Resource
    private EventDao eventDao;

    /***
     * Description: <br>
     * 
     * @author bai.wenlong<br>
     * @taskId <br>
     * @return List<EventPojo> <br>
     * @throws ServiceException <br>
     */
    @Override
    public List<EventPojo> selectList() throws ServiceException {
        // TODO Auto-generated method stub
        try {
            return eventDao.selectList(EventPojo.class);
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

}
