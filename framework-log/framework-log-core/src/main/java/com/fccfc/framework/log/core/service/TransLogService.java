/**
 * 
 */
package com.fccfc.framework.log.core.service;

import java.util.List;
import java.util.Map;

import com.fccfc.framework.common.ServiceException;
import com.fccfc.framework.log.core.bean.TransLogPojo;
import com.fccfc.framework.log.core.bean.TransLogStackPojo;

/**
 * <Description> <br>
 * 
 * @author Administrator<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2014年11月29日 <br>
 * @since V7.3<br>
 * @see com.fccfc.framework.api.log <br>
 */

public interface TransLogService {

    /**
     * 添加事务日志
     * 
     * @param transLogPo 事务日志信息
     * @throws ServiceException 异常
     */
    void addTransactionLog(TransLogPojo transLogPo) throws ServiceException;

    /**
     * 添加事务栈日志信息
     * 
     * @param logPo 事务栈日志信息
     * @throws ServiceException 异常
     */
    void addTransactionStackLog(TransLogStackPojo logPo) throws ServiceException;

    /**
     * @param pageIndex
     * @param pageSize
     * @return
     * @throws ServiceException
     */
    Map<String, Object> listTransLogPojo(int pageIndex, int pageSize) throws ServiceException;

    /**
     * @param transId
     * @return
     * @throws ServiceException
     */
    List<TransLogStackPojo> listTransLogStackPojo(String transId) throws ServiceException;

    TransLogStackPojo queryTransLogStackPojo(String transId, String stackId) throws ServiceException;
}
