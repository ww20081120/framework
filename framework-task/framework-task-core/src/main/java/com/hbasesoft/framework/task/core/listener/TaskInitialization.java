/**
 * 
 */
package com.hbasesoft.framework.task.core.listener;

import javax.annotation.Resource;

import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.Initialization;
import com.hbasesoft.framework.common.InitializationException;
import com.hbasesoft.framework.common.utils.logger.Logger;
import com.hbasesoft.framework.task.api.TaskService;

/**
 * <Description> <br>
 * 
 * @author 伟<br>
 * @version 1.0<br>
 * @CreateDate 2014-11-8 <br>
 * @see com.hbasesoft.framework.task.core.listener <br>
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
     * @see com.hbasesoft.framework.core.Initialization#init()
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
     * @see com.hbasesoft.framework.core.Initialization#destory()
     */
    @Override
    public void destroy() throws InitializationException {
        logger.debug("---------------task start up listener stop ------------------");
    }
}
