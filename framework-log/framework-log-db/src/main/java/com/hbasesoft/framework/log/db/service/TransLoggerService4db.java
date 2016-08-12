/**
 * 
 */
package com.hbasesoft.framework.log.db.service;

import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import javax.annotation.Resource;

import org.hibernate.cache.CacheException;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.hbasesoft.framework.cache.core.CacheConstant;
import com.hbasesoft.framework.cache.core.CacheHelper;
import com.hbasesoft.framework.common.utils.PropertyHolder;
import com.hbasesoft.framework.common.utils.logger.Logger;
import com.hbasesoft.framework.db.core.DaoException;
import com.hbasesoft.framework.log.core.AbstractTransLoggerService;
import com.hbasesoft.framework.log.core.TransManager;
import com.hbasesoft.framework.log.db.bean.TransLogPojo;
import com.hbasesoft.framework.log.db.bean.TransLogStackPojo;
import com.hbasesoft.framework.log.db.dao.TransLogDao;

/**
 * <Description> <br>
 * 
 * @author wangwei<br>
 * @version 1.0<br>
 * @CreateDate 2015年6月27日 <br>
 * @see com.hbasesoft.framework.log.db.service <br>
 */
@Service
public class TransLoggerService4db extends AbstractTransLoggerService {

    /**
     * logger
     */
    private Logger logger = new Logger(TransLoggerService4db.class);

    /**
     * transLogDao
     */
    @Resource
    private TransLogDao transLogDao;

    /***
     * Description: <br>
     * 
     * @author bai.wenlong<br>
     * @taskId <br>
     * @param stackId <br>
     * @param beginTime <br>
     * @param endTime <br>
     * @param consumeTime <br>
     * @param returnValue <br>
     * @param e <br>
     */
    @Override
    public void end(String stackId, long beginTime, long endTime, long consumeTime, String method, Object returnValue,
        Exception e) {
        TransManager manager = TransManager.getInstance();
        try {
            boolean printFlag = PropertyHolder.getBooleanProperty("log.trans.db.print");
            if (manager.isError() || manager.isTimeout() || printFlag) {
                // 插入数据,TRANS_LOG
                saveTransLog(stackId, beginTime, endTime, consumeTime, returnValue, e);

                // // 插入数据,TRANS_LOG_STACK
                saveTransLogStack(manager);
            }
        }
        catch (Exception ex) {
            logger.error(ex);
        }

    }

    /***
     * Description: <br>
     * 
     * @author bai.wenlong<br>
     * @param manager <br>
     * @throws DaoException <br>
     * @taskId <br>
     *         <br>
     */
    private void saveTransLogStack(TransManager manager) throws DaoException {
        Set<String> keySet = manager.getIdSet();
        Iterator<String> it = keySet.iterator();
        TransLogStackPojo transLogStackPojo = null;
        Integer seq = 0;
        while (it.hasNext()) {
            transLogStackPojo = new TransLogStackPojo();
            String stackId = it.next();
            transLogStackPojo.setStackId(stackId);
            transLogStackPojo.setSeq(seq);
            transLogStackPojo.setTransId(manager.getStackId());
            JSONObject logJson = (JSONObject) JSONObject
                .parse(CacheHelper.getCache().get(CacheConstant.CACHE_LOGS, stackId));
            transLogStackPojo.setMethod(logJson.getString("method"));
            transLogStackPojo.setParentStackId(logJson.getString("parentStackId"));
            transLogStackPojo.setBeginTime(new Date(logJson.getLong("beginTime")));
            transLogStackPojo.setEndTime(new Date(logJson.getLong("endTime")));
            transLogStackPojo.setConsumeTime(logJson.getInteger("consumeTime"));
            transLogStackPojo.setInputParam(logJson.getString("params"));
            transLogStackPojo.setOutputParam(logJson.getString("returnValue"));
            transLogStackPojo.setIsSuccess("N");
            seq++;
            transLogDao.save(transLogStackPojo);
        }
    }

    /***
     * Description: <br>
     * 保存错误日志数据
     * 
     * @author bai.wenlong<br>
     * @taskId <br>
     * @param stackId <br>
     * @param beginTime <br>
     * @param endTime <br>
     * @param consumeTime <br>
     * @param returnValue <br>
     * @param e <br>
     * @throws CacheException <br>
     * @throws IOException <br>
     */
    private void saveTransLog(String stackId, long beginTime, long endTime, long consumeTime, Object returnValue,
        Exception e) throws DaoException, IOException {
        TransLogPojo transLogPojo = new TransLogPojo();
        transLogPojo.setTransId(TransManager.getInstance().getStackId());
        Date beginDate = new Date(beginTime);
        transLogPojo.setBeginTime(beginDate);
        Date endDate = new Date(endTime);
        transLogPojo.setEndTime(endDate);
        transLogPojo.setConsumeTime(Integer.valueOf(String.valueOf(consumeTime)));

        // 获取异常堆栈信息
        transLogPojo.setExceptionLog(getExceptionMsg(e));
        // SQL日志
        int seq = TransManager.getInstance().getSeq();
        StringBuffer sqlSb = new StringBuffer();
        for (int i = 0; i < seq; i++) {
            sqlSb.append(CacheHelper.getCache().get(CacheConstant.CACHE_LOGS, stackId + "_SQL_" + i));
            sqlSb.append("\n");
        }
        transLogPojo.setSqlLog(sqlSb.toString());

        // 渠道标示comtachChannelId
        String contactChannelId = PropertyHolder.getProperty("contact.channel.id");
        transLogPojo.setContactChannelId(Integer.valueOf(contactChannelId));

        // 输入参数
        JSONObject inputParam = (JSONObject) JSONObject
            .parse(CacheHelper.getCache().get(CacheConstant.CACHE_LOGS, stackId));
        transLogPojo.setInputParam(inputParam.getString("params"));

        // 输出参数
        if (returnValue != null) {
            transLogPojo.setOutputParam(returnValue.toString());
        }

        // 模块编码
        String moduleCode = PropertyHolder.getProperty("project.code");
        transLogPojo.setModuleCode(moduleCode);
        transLogDao.save(transLogPojo);
    }

}
