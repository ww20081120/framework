/**************************************************************************************** 
 Copyright © 2003-2012 ZTEsoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.log.core;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;

import com.alibaba.fastjson.JSONObject;
import com.hbasesoft.framework.cache.core.CacheConstant;
import com.hbasesoft.framework.cache.core.CacheHelper;
import com.hbasesoft.framework.common.GlobalConstants;
import com.hbasesoft.framework.common.utils.CommonUtil;
import com.hbasesoft.framework.common.utils.logger.Logger;

/**
 * <Description> <br>
 * 
 * @author 王伟 <br>
 * @version 1.0 <br>
 * @CreateDate 2015年6月29日 <br>
 * @see com.hbasesoft.framework.log.core <br>
 */
public abstract class AbstractTransLoggerService implements TransLoggerService {

    /**
     * logger
     */
    private Logger logger = new Logger(AbstractTransLoggerService.class);

    protected boolean alwaysLog;

    /*
     * (non-Javadoc)
     * @see com.hbasesoft.framework.log.core.TransLoggerService#before(java.lang.String, java.lang.String, long,
     * java.lang.String, java.lang.Object[])
     */
    @Override
    public void before(String stackId, String parentStackId, long beginTime, String method, Object[] params) {
        try {
            String data = CacheHelper.getCache().get(CacheConstant.CACHE_LOGS, stackId);
            if (CommonUtil.isEmpty(data)) {
                JSONObject json = new JSONObject();
                json.put("stackId", stackId);
                json.put("parentStackId", parentStackId);
                json.put("beginTime", beginTime);
                json.put("method", method);
                json.put("params", CommonUtil.isEmpty(params) ? "" : Arrays.toString(params));
                CacheHelper.getCache().put(CacheConstant.CACHE_LOGS, stackId, json.toJSONString());
            }
        }
        catch (Exception e) {
            logger.warn("缓存保存失败", e);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.hbasesoft.framework.log.core.TransLoggerService#afterReturn(java.lang.String, long, long,
     * java.lang.Object)
     */
    @Override
    public void afterReturn(String stackId, long endTime, long consumeTime, String method, Object returnValue) {
        try {
            String data = CacheHelper.getCache().get(CacheConstant.CACHE_LOGS, stackId);
            if (CommonUtil.isNotEmpty(data)) {
                JSONObject json = JSONObject.parseObject(data);
                json.put("endTime", endTime);
                json.put("consumeTime", consumeTime);
                json.put("returnValue", returnValue == null ? "" : returnValue);
                json.put("result", 0);
                CacheHelper.getCache().put(CacheConstant.CACHE_LOGS, stackId, json.toJSONString());
            }
        }
        catch (Exception e) {
            logger.warn("return更新缓存失败", e);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.hbasesoft.framework.log.core.TransLoggerService#afterThrow(java.lang.String, long, long,
     * java.lang.Exception)
     */
    @Override
    public void afterThrow(String stackId, long endTime, long consumeTime, String method, Exception e) {
        try {
            String data = CacheHelper.getCache().get(CacheConstant.CACHE_LOGS, stackId);
            if (CommonUtil.isNotEmpty(data)) {
                JSONObject json = JSONObject.parseObject(data);
                json.put("endTime", endTime);
                json.put("consumeTime", consumeTime);
                json.put("exception", getExceptionMsg(e));
                json.put("result", 1);
                CacheHelper.getCache().put(CacheConstant.CACHE_LOGS, stackId, json.toJSONString());
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
     * @throws IOException <br>
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
            CacheHelper.getCache().put(CacheConstant.CACHE_LOGS,
                stackId + "_SQL_" + TransManager.getInstance().getSeq(), sql);
        }
        catch (Exception e) {
            logger.warn("缓存sql失败", e);
        }
    }

    @Override
    public void clean() {
        TransManager manager = TransManager.getInstance();
        String stackId = manager.peek();
        if (CommonUtil.isNotEmpty(stackId)) {
            for (int i = 0, size = manager.getSeq(); i < size; i++) {
                try {
                    CacheHelper.getCache().evict(CacheConstant.CACHE_LOGS, stackId + "_SQL_" + i);
                }
                catch (Exception e) {
                    logger.warn("删除cache日志失败", e);
                }
            }
        }

        for (String key : manager.getIdSet()) {
            try {
                CacheHelper.getCache().evict(CacheConstant.CACHE_LOGS, key);
            }
            catch (Exception ex) {
                logger.warn("删除cache日志失败2", ex);
            }
        }
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param alwaysLog <br>
     */
    @Override
    public void setAlwaysLog(boolean alwaysLog) {
        this.alwaysLog = alwaysLog;
    }
}
