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

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param id <br>
     * @param code <br>
     * @return <br>
     * @throws ServiceException <br>
     */
    OperatorPojo getOperator(Integer id, Integer code) throws ServiceException;

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param type <br>
     * @param username <br>
     * @return <br>
     * @throws ServiceException <br>
     */
    OperatorPojo getOperatorByAccount(String type, String username) throws ServiceException;

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param type <br>
     * @param username <br>
     * @param password <br>
     * @return <br>
     * @throws ServiceException <br>
     */
    OperatorPojo checkOperator(String type, String username, String password) throws ServiceException;

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param type <br>
     * @param username <br>
     * @param password <br>
     * @param ip <br>
     * @return <br>
     * @throws ServiceException <br>
     */
    OperatorPojo login(String type, String username, String password, String ip) throws ServiceException;

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param username <br>
     * @param password <br>
     * @param accountType <br>
     * @param operatorType <br>
     * @param registIp <br>
     * @return <br>
     * @throws ServiceException <br>
     */
    OperatorPojo addOperator(String username, String password, String accountType, String operatorType, String registIp)
        throws ServiceException;

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param id <br>
     * @param code <br>
     * @return <br>
     * @throws ServiceException <br>
     */
    int updateOperatorCode(int id, int code) throws ServiceException;

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param operator <br>
     * @param password <br>
     * @throws ServiceException <br>
     */
    void updatePassword(OperatorPojo operator, String password) throws ServiceException;
}
