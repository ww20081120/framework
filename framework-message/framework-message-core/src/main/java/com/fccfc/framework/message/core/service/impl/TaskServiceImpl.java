/**************************************************************************************** 
 Copyright © 2003-2012 fccfc Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.fccfc.framework.message.core.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.fccfc.framework.common.ServiceException;
import com.fccfc.framework.db.core.DaoException;
import com.fccfc.framework.message.core.dao.MessageBoxDao;
import com.fccfc.framework.message.core.service.TaskService;

/**
 * <Description> <br>
 * 
 * @author 王伟 <br>
 * @version 1.0 <br>
 * @CreateDate 2014年12月11日 <br>
 * @see com.fccfc.framework.message.service <br>
 */
@Service
public class TaskServiceImpl implements TaskService {

    /**
     * messageBoxDao
     */
    @Resource
    private MessageBoxDao messageBoxDao;

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @return <br>
     * @throws ServiceException <br>
     */
    public List<Long> selectResendMessage() throws ServiceException {
        try {
            return messageBoxDao.selectMessageBox(new Date());
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

}
