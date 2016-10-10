/**
 * 
 */
package com.hbasesoft.framework.log.file;

import java.util.Arrays;

import org.springframework.stereotype.Service;

import com.hbasesoft.framework.common.GlobalConstants;
import com.hbasesoft.framework.common.utils.logger.Logger;
import com.hbasesoft.framework.log.core.AbstractTransLoggerService;
import com.hbasesoft.framework.log.core.TransManager;

/**
 * <Description> <br>
 * 
 * @author wangwei<br>
 * @version 1.0<br>
 * @CreateDate 2015年6月27日 <br>
 * @see com.hbasesoft.framework.log.file <br>
 */
@Service
public class TransLoggerService4File extends AbstractTransLoggerService {

    /**
     * logger
     */
    private Logger logger = new Logger(TransLoggerService4File.class);

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param stackId
     * @param parentStackId
     * @param beginTime
     * @param method
     * @param params <br>
     */
    @Override
    public void before(String stackId, String parentStackId, long beginTime, String method, Object[] params) {
        if (alwaysLog) {
            logger.debug("{0}|{1}|before|{2}|{3}", stackId, parentStackId, method, Arrays.toString(params));
        }
        else {
            super.before(stackId, parentStackId, beginTime, method, params);
        }
    }

    @Override
    public void afterReturn(String stackId, long endTime, long consumeTime, String method, Object returnValue) {
        if (alwaysLog) {
            logger.debug("{0}|after|{1}|{2}|{3}", stackId, method, consumeTime, returnValue);
        }
        else {
            super.afterReturn(stackId, endTime, consumeTime, method, returnValue);
        }
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param stackId
     * @param endTime
     * @param consumeTime
     * @param e <br>
     */
    @Override
    public void afterThrow(String stackId, long endTime, long consumeTime, String method, Exception e) {
        if (alwaysLog) {
            logger.error(e, "{0}|error|{1}|{2}", stackId, method, consumeTime, getExceptionMsg(e));
        }
        else {
            super.afterThrow(stackId, endTime, consumeTime, method, e);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.hbasesoft.framework.log.core.TransLoggerService#end(java.lang.String, long, long, long,
     * java.lang.Object, java.lang.Exception)
     */
    @Override
    public void end(String stackId, long beginTime, long endTime, long consumeTime, String method, Object returnValue,
        Exception e) {
        TransManager manager = TransManager.getInstance();
        if (alwaysLog) {
            logger.debug("{0}|end|{1}|{2}|{3}|{4}|{5}", stackId, method, consumeTime,
                manager.isError() || manager.isTimeout() ? "FAIL" : "SUCCESS", returnValue,
                e == null ? GlobalConstants.BLANK : e.getMessage());
        }
        else {
            try {
                for (String key : manager.getIdSet()) {
                    if (manager.isError() || manager.isTimeout()) {
                        logger.warn(getTransBean(key));
                    }

                }
            }
            catch (Exception ex) {
                logger.error(ex);
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see com.hbasesoft.framework.log.core.TransLoggerService#sql(java.lang.String, java.lang.String)
     */
    @Override
    public void sql(String stackId, String sql) {
    }

}
