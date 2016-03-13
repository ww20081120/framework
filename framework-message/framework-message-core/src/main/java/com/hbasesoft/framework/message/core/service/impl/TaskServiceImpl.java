/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.message.core.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.hbasesoft.framework.common.ServiceException;
import com.hbasesoft.framework.db.core.DaoException;
import com.hbasesoft.framework.message.core.dao.MessageBoxDao;
import com.hbasesoft.framework.message.core.service.TaskService;

/**
 * <Description> <br>
 * 
 * @author 王伟 <br>
 * @version 1.0 <br>
 * @CreateDate 2014年12月11日 <br>
 * @see com.hbasesoft.framework.message.service <br>
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
