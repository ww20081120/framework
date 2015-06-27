/**
 * 
 */
package com.fccfc.framework.log.db.service;

import org.springframework.stereotype.Service;

import com.fccfc.framework.log.core.TransLoggerService;

/**
 * <Description> <br>
 * 
 * @author wangwei<br>
 * @version 1.0<br>
 * @CreateDate 2015年6月27日 <br>
 * @see com.fccfc.framework.log.db.service <br>
 */
@Service
public class TransLoggerService4db implements TransLoggerService {

    /*
     * (non-Javadoc)
     * @see com.fccfc.framework.log.core.TransLoggerService#before(java.lang.String, java.lang.String, long,
     * java.lang.String, java.lang.Object[])
     */
    @Override
    public void before(String stackId, String parentStackId, long beginTime, String method, Object[] params) {
    }

    /*
     * (non-Javadoc)
     * @see com.fccfc.framework.log.core.TransLoggerService#afterReturn(java.lang.String, long, long, java.lang.Object)
     */
    @Override
    public void afterReturn(String stackId, long endTime, long consumeTime, Object retrunValue) {
    }

    /*
     * (non-Javadoc)
     * @see com.fccfc.framework.log.core.TransLoggerService#afterThrow(java.lang.String, long, long,
     * java.lang.Exception)
     */
    @Override
    public void afterThrow(String stackId, long endTime, long consumeTime, Exception e) {
    }

    /*
     * (non-Javadoc)
     * @see com.fccfc.framework.log.core.TransLoggerService#end(java.lang.String, long, long, long, java.lang.Object,
     * java.lang.Exception)
     */
    @Override
    public void end(String stackId, long beginTime, long endTime, long consumeTime, Object returnValue, Exception e) {
    }

    /*
     * (non-Javadoc)
     * @see com.fccfc.framework.log.core.TransLoggerService#sql(java.lang.String, java.lang.String)
     */
    @Override
    public void sql(String stackId, String sql) {
    }

}
