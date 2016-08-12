/**
 * 
 */
package com.hbasesoft.framework.log.mongodb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.hbasesoft.framework.cache.core.CacheConstant;
import com.hbasesoft.framework.cache.core.CacheHelper;
import com.hbasesoft.framework.common.utils.PropertyHolder;
import com.hbasesoft.framework.common.utils.logger.Logger;
import com.hbasesoft.framework.log.core.AbstractTransLoggerService;
import com.hbasesoft.framework.log.core.TransManager;
import com.hbasesoft.framework.log.mongodb.model.TransLog;
import com.hbasesoft.framework.log.mongodb.repositoryimpl.LogRepository;

/**
 * <Description> <br>
 * 
 * @author wangwei<br>
 * @version 1.0<br>
 * @CreateDate 2015年6月27日 <br>
 * @see com.hbasesoft.framework.log.mongodb <br>
 */
@Service
public class TransLoggerService4Mongodb extends AbstractTransLoggerService {

    /** logger */
    private Logger logger = new Logger(TransLoggerService4Mongodb.class);

    /** logRepository */
    private LogRepository logRepository;

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
                // 插入数据
                saveData(manager, beginTime, endTime, consumeTime, returnValue, e);

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
     * @taskId <br>
     * @param manager <br>
     * @param beginTime <br>
     * @param endTime <br>
     * @param consumeTime <br>
     * @param returnValue <br>
     * @param e <br>
     * @throws CacheException <br>
     * @throws IOException <br>
     */
    private void saveData(TransManager manager, long beginTime, long endTime, long consumeTime, Object returnValue,
        Exception e) throws IOException {
        logger.debug("向数据集中插入数据开始：");
        TransLog transLogPojo = new TransLog();
        transLogPojo.setTransId(manager.getStackId());
        Date beginDate = new Date(beginTime);
        transLogPojo.setBeginTime(beginDate);
        Date endDate = new Date(endTime);
        transLogPojo.setEndTime(endDate);
        transLogPojo.setConsumeTime(Integer.valueOf(String.valueOf(consumeTime)));

        transLogPojo.setExceptionLog(getExceptionMsg(e));
        // SQL日志
        int seq = TransManager.getInstance().getSeq();
        StringBuffer sqlSb = new StringBuffer();
        for (int i = 0; i < seq; i++) {
            sqlSb.append(CacheHelper.getCache().get(CacheConstant.CACHE_LOGS, manager.getStackId() + "_SQL_" + i));
            sqlSb.append("\n");
        }
        transLogPojo.setSqlLog(sqlSb.toString());

        // 渠道标示comtachChannelId
        String contactChannelId = PropertyHolder.getProperty("contact.channel.id");
        transLogPojo.setContactChannelId(Integer.valueOf(contactChannelId));

        // 输入参数
        JSONObject inputParam = (JSONObject) JSONObject
            .parse(CacheHelper.getCache().get(CacheConstant.CACHE_LOGS, manager.getStackId()));
        transLogPojo.setInputParam(inputParam.getString("params"));

        // 输出参数
        if (returnValue != null) {
            transLogPojo.setOutputParam(returnValue.toString());
        }

        // 模块编码
        String moduleCode = PropertyHolder.getProperty("project.code");
        transLogPojo.setModuleCode(moduleCode);
        // 堆栈信息
        Set<String> keySet = manager.getIdSet();
        Iterator<String> it = keySet.iterator();
        List<String> stackJson = new ArrayList<String>();
        while (it.hasNext()) {
            String childrenStackId = it.next();
            String logJson = CacheHelper.getCache().get(CacheConstant.CACHE_LOGS, childrenStackId);
            stackJson.add(logJson);
        }
        transLogPojo.setStackJson(stackJson);
        logRepository.insert(transLogPojo);
        logger.debug("向数据集中插入数据完成！");
    }

    public void setLogRepository(LogRepository logRepository) {
        this.logRepository = logRepository;
    }
}
