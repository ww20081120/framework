package com.fccfc.framework.task.api;

import javax.annotation.Resource;

import org.apache.thrift.TException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * <Description> <br> 
 *  
 * @author shao.dinghui<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年7月3日 <br>
 * @since V1.0<br>
 * @see com.fccfc.framework.task.api <br>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({
    "classpath:/META-INF/spring/*.xml"
})
public class TaskServiceTest {

    /**
     * iface
     */
    @Resource
    private TaskService.Iface iface;
    
    /**
     * Description: <br> 
     *  
     * @author shao.dinghui<br>
     * @taskId <br>
     * @throws TException <br>
     */
    @Test
    public void testScheduleAllTask() throws TException {
        iface.scheduleAllTask();
    }
}
