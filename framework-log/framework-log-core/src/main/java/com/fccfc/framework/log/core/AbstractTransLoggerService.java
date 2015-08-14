/**************************************************************************************** 
 Copyright © 2003-2012 ZTEsoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.fccfc.framework.log.core;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;

import com.alibaba.fastjson.JSONObject;
import com.fccfc.framework.cache.core.CacheConstant;
import com.fccfc.framework.cache.core.CacheException;
import com.fccfc.framework.cache.core.CacheHelper;
import com.fccfc.framework.cache.core.IStringCache;
import com.fccfc.framework.common.GlobalConstants;
import com.fccfc.framework.common.utils.CommonUtil;
import com.fccfc.framework.common.utils.logger.Logger;

/**
 * <Description> <br>
 * 
 * @author 王伟 <br>
 * @version 1.0 <br>
 * @CreateDate 2015年6月29日 <br>
 * @see com.fccfc.framework.log.core <br>
 */
public abstract class AbstractTransLoggerService implements TransLoggerService {

    /**
     * logger
     */
    private Logger logger = new Logger(AbstractTransLoggerService.class);

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
            logger.warn("缓存保存失败", e);
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
            logger.warn("return更新缓存失败", e);
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
            logger.warn("更新缓存失败", ex);
        }
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param ex <br>
     * @return <br>
     * @throws IOException
     */
    protected String getExceptionMsg(Exception ex) throws IOException {
        if (ex == null) {
            return GlobalConstants.BLANK;
        }

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        StringBuilder sb = new StringBuilder();
        try {
            ex.printStackTrace(pw);
            pw.flush();
            LineNumberReader reader = new LineNumberReader(new StringReader(sw.toString()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n\t");
            }
        }
        finally {
            if (sw != null) {
                sw.close();
            }

            if (pw != null) {
                pw.close();
            }
        }

        return sb.toString();
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param stackId <br>
     * @param sql <br>
     */
    @Override
    public void sql(String stackId, String sql) {
        try {
            CacheHelper.getStringCache().putValue(CacheConstant.CACHE_LOGS,
                stackId + "_SQL_" + TransManager.getInstance().getSeq(), sql);
        }
        catch (CacheException e) {
            logger.warn("缓存sql失败", e);
        }
    }

    @Override
    public void clean() {
        TransManager manager = TransManager.getInstance();
        IStringCache cache = CacheHelper.getStringCache();

        String stackId = manager.peek();
        if (CommonUtil.isNotEmpty(stackId)) {
            for (int i = 0, size = manager.getSeq(); i < size; i++) {
                try {
                    cache.removeValue(CacheConstant.CACHE_LOGS, stackId + "_SQL_" + i);
                }
                catch (Exception e) {
                    logger.warn("删除cache日志失败", e);
                }
            }
        }

        for (String key : manager.getIdSet()) {
            try {
                cache.removeValue(CacheConstant.CACHE_LOGS, key);
            }
            catch (CacheException ex) {
                logger.warn("删除cache日志失败2", ex);
            }
        }
    }
}
