/**
 * 
 */
package com.fccfc.framework.task.core.listener;

import javax.annotation.Resource;

import com.fccfc.framework.common.ErrorCodeDef;
import com.fccfc.framework.common.Initialization;
import com.fccfc.framework.common.InitializationException;
import com.fccfc.framework.common.utils.logger.Logger;
import com.fccfc.framework.task.api.TaskService;

/**
 * <Description> <br>
 * 
 * @author 伟<br>
 * @version 1.0<br>
 * @CreateDate 2014-11-8 <br>
 * @see com.fccfc.framework.task.core.listener <br>
 */
public class TaskInitialization implements Initialization {

    /**
     * logger
     */
    private static Logger logger = new Logger(TaskInitialization.class);

    /**
     * taskService
     */
    @Resource
    private TaskService.Iface taskService;

    /*
     * (non-Javadoc)
     * @see com.fccfc.framework.core.Initialization#init()
     */
    @Override
    public void afterPropertiesSet() throws InitializationException {
        logger.debug("---------------task start up listener stop ------------------");
        try {
            taskService.scheduleAllTask();
        }
        catch (Exception e) {
            throw new InitializationException(ErrorCodeDef.EXECUTE_ALL_TAKS_ERROR, e);
        }
        logger.debug("---------------task start up listener stop ------------------");
    }

    /*
     * (non-Javadoc)
     * @see com.fccfc.framework.core.Initialization#destory()
     */
    @Override
    public void destroy() throws InitializationException {
        logger.debug("---------------task start up listener stop ------------------");
    }
}
