/**
 * 
 */
package com.fccfc.framework.log.db.service;

import org.springframework.stereotype.Service;

import com.fccfc.framework.log.core.AbstractTransLoggerService;

/**
 * <Description> <br>
 * 
 * @author wangwei<br>
 * @version 1.0<br>
 * @CreateDate 2015年6月27日 <br>
 * @see com.fccfc.framework.log.db.service <br>
 */
@Service
public class TransLoggerService4db extends AbstractTransLoggerService {

    /**
     * @see com.fccfc.framework.log.core.TransLoggerService#end(java.lang.String, long, long, long, java.lang.Object,
     *      java.lang.Exception)
     */
    @Override
    public void end(String stackId, long beginTime, long endTime, long consumeTime, Object returnValue, Exception e) {
    }
}
