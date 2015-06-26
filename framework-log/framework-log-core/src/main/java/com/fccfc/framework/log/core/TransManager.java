/**
 * 
 */
package com.fccfc.framework.log.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.fccfc.framework.cache.core.CacheConstant;
import com.fccfc.framework.cache.core.CacheException;
import com.fccfc.framework.cache.core.CacheHelper;
import com.fccfc.framework.common.utils.CommonUtil;
import com.fccfc.framework.log.core.bean.TransLogPojo;
import com.fccfc.framework.log.core.bean.TransLogStackPojo;

/**
 * <Description> <br>
 * 
 * @author Administrator<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2014年11月22日 <br>
 * @since V7.3<br>
 * @see com.fccfc.framework.core.threadlocal <br>
 */
public final class TransManager implements Serializable {

    /**
     * serialVersionUID <br>
     */
    private static final long serialVersionUID = 2162951151671381710L;

    /**
     * 序列
     */
    private int seq;

    /**
     * 流程是否出错
     */
    private boolean error;

    /**
     * 日志栈
     */
    private Stack<TransLogStackPojo> logStack;

    /**
     * 流程日志集合
     */
    private List<TransLogStackPojo> logList;

    /**
     * 事务日志信息
     */
    private TransLogPojo transLog;

    /**
     * 私有构造, 不需要外部实例化
     */
    private TransManager() {
    }

    /**
     * 本地线程对象, 保证对象在同一个线程内共享
     */
    private static ThreadLocal<TransManager> sessionThread = new ThreadLocal<TransManager>() {
        protected synchronized TransManager initialValue() {
            return new TransManager();
        }
    };

    /**
     * 得到用户会话信息
     * 
     * @return UserSessionInfo
     */
    public static TransManager getInstance() {
        return sessionThread.get();
    }

    /**
     * 获取栈顶
     * 
     * @return
     */
    public TransLogStackPojo getLastTransLogStack() {
        return getLogStack().isEmpty() ? null : getLogStack().peek();
    }

    public void setTransLog(TransLogPojo transLog) {
        this.transLog = transLog;
    }

    public void addTransLogStack(TransLogStackPojo pojo) {
        getLogStack().push(pojo);
    }

    public TransLogStackPojo removeLast() {
        return getLogStack().isEmpty() ? null : getLogStack().pop();
    }

    public int getStackSize() {
        return getLogStack().size();
    }

    private Stack<TransLogStackPojo> getLogStack() {
        if (logStack == null) {
            logStack = new Stack<TransLogStackPojo>();
        }
        return logStack;
    }

    public void addSqlLog(String msg) throws CacheException {
        if (CommonUtil.isNull(transLog) || msg == null) {
            return;
        }
        String key = transLog.getTransId() + "_sql";
        String value = CacheHelper.getStringCache().getValue(CacheConstant.CACHE_LOGS, key);
        value = value + msg + "<br/>";
        CacheHelper.getStringCache().updateValue(CacheConstant.CACHE_LOGS, key, value);
    }

    public String getSqlLog() throws CacheException {
        if (CommonUtil.isNull(transLog)) {
            return null;
        }
        String key = transLog.getTransId() + "_sql";
        return CacheHelper.getStringCache().getValue(CacheConstant.CACHE_LOGS, key);
    }

    public void removeSqlLog() throws CacheException {
        if (CommonUtil.isNull(transLog)) {
            return;
        }
        String key = transLog.getTransId() + "_sql";
        CacheHelper.getStringCache().removeValue(CacheConstant.CACHE_LOGS, key);
    }

    public TransLogPojo getTransLog() {
        return transLog;
    }

    public List<TransLogStackPojo> getLogList() {
        if (logList == null) {
            logList = new ArrayList<TransLogStackPojo>();
        }
        return logList;
    }

    public void setLogList(List<TransLogStackPojo> logList) {
        this.logList = logList;
    }

    public int getSeq() {
        return ++seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

}
