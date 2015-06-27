/**
 * 
 */
package com.fccfc.framework.log.core;

/**
 * <Description> <br>
 * 
 * @author wangwei<br>
 * @version 1.0<br>
 * @CreateDate 2015年6月27日 <br>
 * @see com.fccfc.framework.log.core <br>
 */
public interface TransLoggerService {

    void before(String stackId, String parentStackId, long beginTime, String method, Object[] params);

    void afterReturn(String stackId, long endTime, long consumeTime, Object retrunValue);

    void afterThrow(String stackId, long endTime, long consumeTime, Exception e);

    void end(String stackId, long beginTime, long endTime, long consumeTime, Object returnValue, Exception e);

    void sql(String stackId, String sql);
}
