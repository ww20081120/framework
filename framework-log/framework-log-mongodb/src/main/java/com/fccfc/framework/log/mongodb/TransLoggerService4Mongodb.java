/**
 * 
 */
package com.fccfc.framework.log.mongodb;

import org.springframework.stereotype.Service;

import com.fccfc.framework.log.core.AbstractTransLoggerService;

/**
 * <Description> <br>
 * 
 * @author wangwei<br>
 * @version 1.0<br>
 * @CreateDate 2015年6月27日 <br>
 * @see com.fccfc.framework.log.mongodb <br>
 */
@Service
public class TransLoggerService4Mongodb extends AbstractTransLoggerService {

    /**
     * 
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
    @Override
    public void end(String stackId, long beginTime, long endTime, long consumeTime, Object returnValue, Exception e) {
    }
}
