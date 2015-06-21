/**
 * 
 */
package com.fccfc.framework.task.core.listener;

import javax.annotation.Resource;

import com.fccfc.framework.common.Initialization;
import com.fccfc.framework.common.InitializationException;
import com.fccfc.framework.common.ServiceException;
import com.fccfc.framework.core.utils.LogUtil;

/**
 * <Description> <br>
 * 
 * @author 伟<br>
 * @version 1.0<br>
 * @CreateDate 2014-11-8 <br>
 * @see com.fccfc.framework.task.listener <br>
 */
public class TaskInitialization implements Initialization {

    @Resource
    private TaskService taskService;

    /*
     * (non-Javadoc)
     * @see com.fccfc.framework.core.Initialization#init()
     */
    @Override
    public void afterPropertiesSet() throws InitializationException {
        LogUtil.debug("---------------task start up listener stop ------------------");
        try {
            taskService.scheduleAllTask();
        }
        catch (ServiceException e) {
            throw new InitializationException(e);
        }
        LogUtil.debug("---------------task start up listener stop ------------------");
    }

    /*
     * (non-Javadoc)
     * @see com.fccfc.framework.core.Initialization#destory()
     */
    @Override
    public void destroy() throws InitializationException {
        LogUtil.debug("---------------task start up listener stop ------------------");
    }
}
