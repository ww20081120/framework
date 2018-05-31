/**
 * 
 */
package com.hbasesoft.framework.common.utils.logger;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

/**
 * <Description> <br>
 * 
 * @author Administrator<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2014年11月22日 <br>
 * @since V7.3<br>
 * @see com.hbasesoft.framework.core.threadlocal <br>
 */
public final class TransManager implements Serializable {

    /**
     * serialVersionUID <br>
     */
    private static final long serialVersionUID = 2162951151671381710L;

    /**
     * 序列
     */
    private int seq = 0;

    /**
     * 流程是否出错
     */
    private boolean error = false;

    /**
     * 流程是否执行超时
     */
    private boolean timeout = false;

    /**
     * 日志栈
     */
    private Stack<String> executeStack;

    /***
     * stactId
     */
    private String stackId;

    /**
     * executeTimeMap
     */
    private Map<String, Long> executeTimeMap;

    /**
     * 私有构造, 不需要外部实例化
     */
    private TransManager() {
        executeStack = new Stack<String>();
        executeTimeMap = new HashMap<String, Long>();
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

    public int getStackSize() {
        return executeStack.size();
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param id <br>
     * @param beginTime <br>
     */
    public void push(String id, long beginTime) {
        if (executeStack.isEmpty()) {
            stackId = id;
        }
        executeTimeMap.put(id, beginTime);
        executeStack.push(id);
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @return <br>
     */
    public String pop() {
        return executeStack.isEmpty() ? null : executeStack.pop();
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @return <br>
     */
    public String peek() {
        return executeStack.isEmpty() ? null : executeStack.peek();
    }

    public int getSeq() {
        return seq++;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param id <br>
     * @return <br>
     */
    public long getBeginTime(String id) {
        return executeTimeMap.get(id);
    }

    public boolean isTimeout() {
        return timeout;
    }

    public void setTimeout(boolean timeout) {
        this.timeout = timeout;
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     *         <br>
     */
    public void clean() {
        executeStack.clear();
        executeTimeMap.clear();
        seq = 0;
        error = false;
        timeout = false;
        stackId = null;
    }

    public Set<String> getIdSet() {
        return executeTimeMap.keySet();
    }

    public String getStackId() {
        return stackId;
    }
}
