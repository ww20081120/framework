/**
 * 
 */
package com.hbasesoft.framework.common.utils.logger;

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
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param stackId <br>
     * @param endTime <br>
     * @param consumeTime <br>
     * @param returnValue <br>
     */
    void afterReturn(String stackId, long endTime, long consumeTime, String method, Object returnValue);

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param stackId <br>
     * @param endTime <br>
     * @param consumeTime <br>
     * @param e <br>
     */
    void afterThrow(String stackId, long endTime, long consumeTime, String method, Exception e);

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param stackId <br>
     * @param beginTime <br>
     * @param endTime <br>
     * @param consumeTime <br>
     * @param returnValue <br>
     * @param e <br>
     */
    void end(String stackId, long beginTime, long endTime, long consumeTime, String method, Object returnValue,
        Exception e);

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param stackId <br>
     * @param sql <br>
     */
    void sql(String stackId, String sql);

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     *         <br>
     */
    void clean();

    void setAlwaysLog(boolean alwaysLog);
}
