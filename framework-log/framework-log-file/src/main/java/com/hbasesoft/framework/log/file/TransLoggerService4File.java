/**
 * 
 */
package com.hbasesoft.framework.log.file;

import java.util.Arrays;

import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import com.hbasesoft.framework.common.utils.CommonUtil;
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
            MDC.put("stackId", stackId);
            MDC.put("parentStackId", parentStackId);
            MDC.put("method", method);
            MDC.put("params", Arrays.toString(params));
            logger.info("BEFORE");
            MDC.clear();
        }
        else {
            super.before(stackId, parentStackId, beginTime, method, params);
        }
    }

    @Override
    public void afterReturn(String stackId, long endTime, long consumeTime, String method, Object returnValue) {
        if (alwaysLog) {
            MDC.put("stackId", stackId);
            MDC.put("consumeTime", consumeTime + "");
            MDC.put("method", method);
            MDC.put("returnValue", CommonUtil.getString(returnValue));
            logger.info("AFTER");
            MDC.clear();
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
    public void afterThrow(String stackId, long endTime, long consumeTime, String method, Throwable e) {
        if (alwaysLog) {
            MDC.put("stackId", stackId);
            MDC.put("consumeTime", consumeTime + "");
            MDC.put("method", method);
            MDC.put("exception", e.getMessage());
            logger.error(e, "ERROR");
            MDC.clear();
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
        Throwable e) {
        TransManager manager = TransManager.getInstance();
        if (alwaysLog) {
            MDC.put("stackId", stackId);
            MDC.put("consumeTime", consumeTime + "");
            MDC.put("method", method);
            MDC.put("returnValue", CommonUtil.getString(returnValue));

            if (manager.isError()) {
                MDC.put("exception", e.getMessage());
                logger.error(e, "FAIL");
            }
            else if (manager.isTimeout()) {
                logger.warn("TIMEOUT");
            }
            else {
                logger.info("SUCCESS");
            }
            MDC.clear();
        }
        else {
            try {
                for (String key : manager.getIdSet()) {
                    if (manager.isError() || manager.isTimeout()) {
                        logger.warn(CommonUtil.getString(getTransBean(key)));
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
