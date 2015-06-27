/**
 * 
 */
package com.fccfc.framework.log.file;

import java.util.Arrays;

import org.springframework.stereotype.Service;

import com.fccfc.framework.common.utils.CommonUtil;
import com.fccfc.framework.common.utils.logger.Logger;
import com.fccfc.framework.log.core.TransLoggerService;

/**
 * <Description> <br>
 * 
 * @author wangwei<br>
 * @version 1.0<br>
 * @CreateDate 2015年6月27日 <br>
 * @see com.fccfc.framework.log.file <br>
 */
@Service
public class TransLoggerService4File implements TransLoggerService {

    private Logger logger = new Logger(TransLoggerService4File.class);

    /*
     * (non-Javadoc)
     * @see com.fccfc.framework.log.core.TransLoggerService#before(java.lang.String, java.lang.String, long,
     * java.lang.String, java.lang.Object[])
     */
    @Override
    public void before(String stackId, String parentStackId, long beginTime, String method, Object[] params) {
        logger.debug("[{0}]进入[{1}]方法，参数为[{2}],statckId[{3}],parentStackId[{4}]", beginTime, method,
            (CommonUtil.isEmpty(params) ? "" : Arrays.toString(params)), stackId, parentStackId);
    }

    /*
     * (non-Javadoc)
     * @see com.fccfc.framework.log.core.TransLoggerService#afterReturn(java.lang.String, long, long, java.lang.Object)
     */
    @Override
    public void afterReturn(String stackId, long endTime, long consumeTime, Object returnValue) {
        logger.debug("[{0}]执行statckId[{1}]完毕，共执行[{2}],返回值为[{3}]", endTime, stackId, consumeTime, returnValue);
    }

    /*
     * (non-Javadoc)
     * @see com.fccfc.framework.log.core.TransLoggerService#afterThrow(java.lang.String, long, long,
     * java.lang.Exception)
     */
    @Override
    public void afterThrow(String stackId, long endTime, long consumeTime, Exception e) {
        logger.warn(e, "[{0}]执行statckId[{1}]失败，共执行[{2}]", endTime, stackId, consumeTime);
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
