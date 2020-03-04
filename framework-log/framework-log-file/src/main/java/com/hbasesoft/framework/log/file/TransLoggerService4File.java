/**
 * 
 */
package com.hbasesoft.framework.log.file;

import java.util.Arrays;

import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import com.hbasesoft.framework.common.utils.CommonUtil;
import com.hbasesoft.framework.common.utils.logger.Logger;
import com.hbasesoft.framework.common.utils.logger.TransManager;
import com.hbasesoft.framework.log.core.AbstractTransLoggerService;

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
    public void before(final String stackId, final String parentStackId, final long beginTime, final String method,
        final Object[] params) {
        if (this.isAlwaysLog()) {
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

    /**
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
    @Override
    public void afterReturn(final String stackId, final long endTime, final long consumeTime, final String method,
        final Object returnValue) {
        if (this.isAlwaysLog()) {
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
    public void afterThrow(final String stackId, final long endTime, final long consumeTime, final String method,
        final Throwable e) {
        if (this.isAlwaysLog()) {
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

    /**
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
    @Override
    public void end(final String stackId, final long beginTime, final long endTime, final long consumeTime,
        final String method, final Object returnValue, final Throwable e) {
        TransManager manager = TransManager.getInstance();
        if (this.isAlwaysLog()) {
            MDC.put("stackId", stackId);
            MDC.put("consumeTime", consumeTime + "");
            MDC.put("method", method);
            MDC.put("returnValue", CommonUtil.getString(returnValue));

            if (manager.isError()) {
                if (e != null) {
                    MDC.put("exception", e.getMessage());
                    logger.error(e, "FAIL");
                }
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

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param stackId
     * @param sql <br>
     */
    @Override
    public void sql(final String stackId, final String sql) {
    }

}
