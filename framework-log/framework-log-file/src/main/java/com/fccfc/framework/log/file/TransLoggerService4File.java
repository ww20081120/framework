/**
 * 
 */
package com.fccfc.framework.log.file;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.fccfc.framework.cache.core.CacheConstant;
import com.fccfc.framework.cache.core.CacheException;
import com.fccfc.framework.cache.core.CacheHelper;
import com.fccfc.framework.cache.core.IStringCache;
import com.fccfc.framework.common.GlobalConstants;
import com.fccfc.framework.common.utils.CommonUtil;
import com.fccfc.framework.common.utils.logger.Logger;
import com.fccfc.framework.log.core.TransLoggerService;
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
public class TransLoggerService4File implements TransLoggerService {

    private Logger logger = new Logger(TransLoggerService4File.class);

    /*
     * (non-Javadoc)
     * @see com.fccfc.framework.log.core.TransLoggerService#before(java.lang.String, java.lang.String, long,
     * java.lang.String, java.lang.Object[])
     */
    @Override
    public void before(String stackId, String parentStackId, long beginTime, String method, Object[] params) {
        try {
            String data = CacheHelper.getStringCache().getValue(CacheConstant.CACHE_LOGS, stackId);
            if (CommonUtil.isEmpty(data)) {
                JSONObject json = new JSONObject();
                json.put("stackId", stackId);
                json.put("parentStackId", parentStackId);
                json.put("beginTime", beginTime);
                json.put("method", method);
                json.put("params", CommonUtil.isEmpty(params) ? "" : Arrays.toString(params));
                CacheHelper.getStringCache().putValue(CacheConstant.CACHE_LOGS, stackId, json.toJSONString());
            }
        }
        catch (CacheException e) {
            logger.warn(e);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.fccfc.framework.log.core.TransLoggerService#afterReturn(java.lang.String, long, long, java.lang.Object)
     */
    @Override
    public void afterReturn(String stackId, long endTime, long consumeTime, Object returnValue) {
        try {
            String data = CacheHelper.getStringCache().getValue(CacheConstant.CACHE_LOGS, stackId);
            if (CommonUtil.isNotEmpty(data)) {
                JSONObject json = JSONObject.parseObject(data);
                json.put("endTime", endTime);
                json.put("consumeTime", consumeTime);
                json.put("returnValue", returnValue == null ? "" : returnValue.toString());
                json.put("result", 0);
                CacheHelper.getStringCache().updateValue(CacheConstant.CACHE_LOGS, stackId, json.toJSONString());
            }
        }
        catch (CacheException e) {
            logger.warn(e);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.fccfc.framework.log.core.TransLoggerService#afterThrow(java.lang.String, long, long,
     * java.lang.Exception)
     */
    @Override
    public void afterThrow(String stackId, long endTime, long consumeTime, Exception e) {
        try {
            String data = CacheHelper.getStringCache().getValue(CacheConstant.CACHE_LOGS, stackId);
            if (CommonUtil.isNotEmpty(data)) {
                JSONObject json = JSONObject.parseObject(data);
                json.put("endTime", endTime);
                json.put("consumeTime", consumeTime);
                json.put("exception", getExceptionMsg(e));
                json.put("result", 1);
                CacheHelper.getStringCache().updateValue(CacheConstant.CACHE_LOGS, stackId, json.toJSONString());
            }
        }
        catch (Exception ex) {
            logger.warn(ex);
        }
    }

    private String getExceptionMsg(Exception ex) throws UnsupportedEncodingException {
        String result = null;
        PrintWriter writer = null;
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            writer = new PrintWriter(new OutputStreamWriter(out, GlobalConstants.DEFAULT_CHARSET));
            ex.printStackTrace(writer);
            result = out.toString(GlobalConstants.DEFAULT_CHARSET);
        }
        finally {
            if (writer != null) {
                writer.close();
            }
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * @see com.fccfc.framework.log.core.TransLoggerService#end(java.lang.String, long, long, long, java.lang.Object,
     * java.lang.Exception)
     */
    @Override
    public void end(String stackId, long beginTime, long endTime, long consumeTime, Object returnValue, Exception e) {
        TransManager manager = TransManager.getInstance();
        IStringCache cache = CacheHelper.getStringCache();
        for (String key : manager.getIdSet()) {
            try {
                if (manager.isError() || manager.isTimeout()) {
                    logger.warn(cache.getValue(CacheConstant.CACHE_LOGS, key));
                }
                cache.removeValue(CacheConstant.CACHE_LOGS, key);
            }
            catch (CacheException ex) {
                logger.warn(ex);
            }
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
