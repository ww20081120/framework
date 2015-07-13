package com.fccfc.framework.web.service;

import com.fccfc.framework.common.ServiceException;
import com.fccfc.framework.web.bean.operator.OperateLogPojo;

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
}
