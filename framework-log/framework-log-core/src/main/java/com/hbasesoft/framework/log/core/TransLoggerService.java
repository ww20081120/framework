/**
 * 
 */
package com.hbasesoft.framework.log.core;

/**
 * <Description> <br>
 * 
 * @author wangwei<br>
 * @version 1.0<br>
 * @CreateDate 2015年6月27日 <br>
 * @see com.hbasesoft.framework.log.core <br>
 */
public interface TransLoggerService {

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param stackId <br>
     * @param parentStackId <br>
     * @param beginTime <br>
     * @param method <br>
     * @param params <br>
     */
    void before(String stackId, String parentStackId, long beginTime, String method, Object[] params);

    /**
     * 
     * Description: <br> 
     *  
     * @author 王伟<br>
     * @taskId <br>
     * @param stackId
     * @param endTime
     * @param consumeTime
     * @param method
     * @param returnValue <br>
     */
    void afterReturn(String stackId, long endTime, long consumeTime, String method, Object returnValue);

    /**
     * 
     * Description: <br> 
     *  
     * @author 王伟<br>
     * @taskId <br>
     * @param stackId
     * @param endTime
     * @param consumeTime
     * @param method
     * @param e <br>
     */
    void afterThrow(String stackId, long endTime, long consumeTime, String method, Throwable e);

    /**
     * 
     * Description: <br> 
     *  
     * @author 王伟<br>
     * @taskId <br>
     * @param stackId
     * @param beginTime
     * @param endTime
     * @param consumeTime
     * @param method
     * @param returnValue
     * @param e <br>
     */
    void end(String stackId, long beginTime, long endTime, long consumeTime, String method, Object returnValue,
        Throwable e);

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     *         <br>
     */
    void clean();

    /**
     * 
     * Description: <br> 
     *  
     * @author 王伟<br>
     * @taskId <br>
     * @param alwaysLog <br>
     */
    void setAlwaysLog(boolean alwaysLog);
}
