package service.task.api;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.thrift.TException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.fccfc.framework.task.api.CronTrigger;
import com.fccfc.framework.task.api.SimpleTrigger;
import com.fccfc.framework.task.api.Task;
import com.fccfc.framework.task.api.TaskService;

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
     * Description: 启动所有任务<br> 
     *  
     * @author shao.dinghui<br>
     * @taskId <br>
     * @throws TException <br>
     */
    @Test
    public void testScheduleAllTask() throws TException {
        iface.scheduleAllTask();
    }
    
    /**
     * Description: 添加一个简单任务<br> 
     *  
     * @author shao.dinghui<br>
     * @taskId <br>
     * @throws TException <br>
     */
    @Test
    public void testSimpleScheduleTask() throws TException {
    	Task task = new Task("task01", "com.fccfc.framework.bootstrap.JobTest", "testJob01");
    	SimpleTrigger trigger = new SimpleTrigger();
    	trigger.setTriggerId(1001);
    	trigger.setTriggerName("simpleTrigger01");
    	
    	// 从beginTime到endTime每隔5秒钟执行一次，要执行10000次
    	Long beginTime = new Date().getTime();
    	Long endTime = beginTime + 7 * 24 * 60 * 60 * 1000;
    	trigger.setBeginTime(beginTime);// 开始执行时间
    	trigger.setEndTime(endTime);	// 结束执行时间
    	trigger.setExecuteInterval(10);	// 间隔时间
    	trigger.setIntervalUnit("ss");	// 间隔单位
    	trigger.setTimes(10000);		// 执行次数
    	iface.simpleScheduleTask(task, trigger);
    }
    
    /**
     * Description: 添加一个Cron表达式任务<br> 
     *  
     * @author shao.dinghui<br>
     * @throws TException 
     * @taskId <br> <br>
     */
    @Test
    public void testCronScheduleTask() throws TException{
    	List<Task> tasks = new ArrayList<Task>();
    	tasks.add(new Task("task01", "com.fccfc.framework.bootstrap.JobTest", "testJob01"));
    	tasks.add(new Task("task02", "com.fccfc.framework.bootstrap.JobTest", "testJob02"));
    	tasks.add(new Task("task03", "com.fccfc.framework.bootstrap.JobTest", "testJob03"));
    	
    	CronTrigger trigger = new CronTrigger("cronTrigger", "0/10 * * * * ?");
    	
    	for (Task tmpTask : tasks) {
			iface.cronScheduleTask(tmpTask, trigger);
		}
    }
    
    /**
     * Description: 暂停任务<br> 
     *  
     * @author shao.dinghui<br>
     * @throws TException 
     * @taskId <br> <br>
     */
    @Test
    public void testPause() throws TException{
    	Task task = new Task("task01", "com.fccfc.framework.bootstrap.JobTest", "testJob01");
    	iface.pause(task);
    }
    
    /**
     * Description: 恢复任务<br> 
     *  
     * @author shao.dinghui<br>
     * @throws TException 
     * @taskId <br> <br>
     */
    @Test
    public void testResume() throws TException {
    	Task task = new Task("task01", "com.fccfc.framework.bootstrap.JobTest", "testJob01");
    	iface.resume(task);
    }
    
    /**
     * Description: 移除任务<br> 
     *  
     * @author shao.dinghui<br>
     * @throws TException 
     * @taskId <br> <br>
     */
    @Test
    public void testRemove() throws TException {
    	Task task = new Task("task01", "com.fccfc.framework.bootstrap.JobTest", "testJob01");
    	iface.remove(task);
    }
}
