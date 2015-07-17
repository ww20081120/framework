/**
 * 
 */
package com.fccfc.framework.log.file;

import org.springframework.stereotype.Service;

import com.fccfc.framework.cache.core.CacheConstant;
import com.fccfc.framework.cache.core.CacheException;
import com.fccfc.framework.cache.core.CacheHelper;
import com.fccfc.framework.cache.core.IStringCache;
import com.fccfc.framework.common.utils.logger.Logger;
import com.fccfc.framework.log.core.AbstractTransLoggerService;
import com.fccfc.framework.log.core.TransManager;

/**
 * <Description> <br>
 * 
 * @author wangwei<br>
 * @version 1.0<br>
 * @CreateDate 2015年6月27日 <br>
 * @see com.fccfc.framework.log.file <br>
 */
@Service
public class TransLoggerService4File extends AbstractTransLoggerService {

    /**
     * logger
     */
    private Logger logger = new Logger(TransLoggerService4File.class);

    /*
     * (non-Javadoc)
     * @see com.fccfc.framework.log.core.TransLoggerService#end(java.lang.String, long, long, long, java.lang.Object,
     * java.lang.Exception)
     */
    @Override
    public void end(String stackId, long beginTime, long endTime, long consumeTime, Object returnValue, Exception e) {
        TransManager manager = TransManager.getInstance();
        IStringCache cache = CacheHelper.getStringCache();
        try {
            for (String key : manager.getIdSet()) {
                if (manager.isError() || manager.isTimeout()) {
                    logger.warn(cache.getValue(CacheConstant.CACHE_LOGS, key));
                }

            }
        }
        catch (CacheException ex) {
            logger.warn(ex);
        }

    }

    /*
     * (non-Javadoc)
     * @see com.fccfc.framework.log.core.TransLoggerService#sql(java.lang.String, java.lang.String)
     */
    @Override
    public void sql(String stackId, String sql) {
    }

}
