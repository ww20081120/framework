/**
 * 
 */
package com.fccfc.framework.web.service;

import com.fccfc.framework.common.ServiceException;
import com.fccfc.framework.web.bean.operator.OperatorPojo;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2014年11月30日 <br>
 * @since V1.0<br>
 * @see com.fccfc.framework.api.operator <br>
 */
public interface OperatorService {

    OperatorPojo getOperator(Integer id, Integer code) throws ServiceException;

    OperatorPojo getOperatorByAccount(String type, String username) throws ServiceException;

    OperatorPojo checkOperator(String type, String username, String password) throws ServiceException;

    OperatorPojo login(String type, String username, String password, String ip) throws ServiceException;

    OperatorPojo addOperator(String username, String password, String accountType, String operatorType, String registIp)
        throws ServiceException;

    int updateOperatorCode(int id, int code) throws ServiceException;

    void updatePassword(OperatorPojo operator, String password) throws ServiceException;
}
