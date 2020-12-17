/**************************************************************************************** 
 Copyright © 2003-2012 ZTEsoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.log.core;

import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import com.hbasesoft.framework.common.GlobalConstants;
import com.hbasesoft.framework.common.utils.CommonUtil;
import com.hbasesoft.framework.common.utils.logger.Logger;
import com.hbasesoft.framework.common.utils.logger.TransManager;

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

    /** always log */
    private boolean alwaysLog;

    private static Map<String, TransBean> transBeanHolder = new HashMap<>();

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
        try {
            TransBean bean = getTransBean(stackId);
            if (bean == null) {
                bean = new TransBean();
                bean.setStackId(stackId);
                bean.setParentStackId(parentStackId);
                bean.setBeginTime(beginTime);
                bean.setMethod(method);
                bean.setParams(CommonUtil.isEmpty(params) ? GlobalConstants.BLANK : Arrays.toString(params));
                putTransBean(bean);
            }
        }
        catch (Exception e) {
            logger.warn("缓存保存失败", e);
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
        try {
            TransBean bean = getTransBean(stackId);
            if (bean != null) {
                bean.setEndTime(endTime);
                bean.setConsumeTime(consumeTime);
                bean.setReturnValue(returnValue == null ? GlobalConstants.BLANK : returnValue.toString());
                putTransBean(bean);
            }
        }
        catch (Exception e) {
            logger.warn("return更新缓存失败", e);
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
     * @param e <br>
     */
    @Override
    public void afterThrow(final String stackId, final long endTime, final long consumeTime, final String method,
        final Throwable e) {
        try {
            TransBean bean = getTransBean(stackId);
            if (bean != null) {
                bean.setEndTime(endTime);
                bean.setConsumeTime(consumeTime);
                bean.setException(getExceptionMsg(e));
                bean.setResult("1");
                putTransBean(bean);
            }
        }
        catch (Exception ex) {
            logger.warn("更新缓存失败", ex);
        }
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param stackId
     * @return <br>
     */
    protected TransBean getTransBean(final String stackId) {
        return transBeanHolder.get(stackId);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param bean <br>
     */
    protected void putTransBean(final TransBean bean) {
        transBeanHolder.put(bean.getStackId(), bean);
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param ex <br>
     * @return <br>
     */
    protected String getExceptionMsg(final Throwable ex) {
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
        catch (Exception e) {
            logger.error(e);
        }
        finally {
            IOUtils.closeQuietly(sw);
            IOUtils.closeQuietly(pw);
        }

        return sb.toString();
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Override
    public void clean() {
        TransManager manager = TransManager.getInstance();
        for (String key : manager.getIdSet()) {
            transBeanHolder.remove(key);
        }
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return alwaysLog <br>
     */
    public boolean isAlwaysLog() {
        return alwaysLog;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param alwaysLog <br>
     */
    @Override
    public void setAlwaysLog(final boolean alwaysLog) {
        this.alwaysLog = alwaysLog;
    }
}
