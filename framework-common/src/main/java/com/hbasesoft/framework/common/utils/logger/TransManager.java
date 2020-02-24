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

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
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
    public void push(final String id, final long beginTime) {
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

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public int getSeq() {
        return seq++;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public boolean isError() {
        return error;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param e <br>
     */
    public void setError(final boolean e) {
        this.error = e;
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param id <br>
     * @return <br>
     */
    public long getBeginTime(final String id) {
        return executeTimeMap.get(id);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public boolean isTimeout() {
        return timeout;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param t <br>
     */
    public void setTimeout(final boolean t) {
        this.timeout = t;
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
    }

    /**
     * Description: getIdSet<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public Set<String> getIdSet() {
        return executeTimeMap.keySet();
    }

    /**
     * Description: getTopStackId<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public String getTopStackId() {
        return executeStack.isEmpty() ? null : executeStack.firstElement();
    }

    /**
     * Description: getStackId<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public String getStackId() {
        return executeStack.isEmpty() ? null : executeStack.lastElement();
    }
}
