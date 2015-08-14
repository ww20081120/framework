package service.task.api;

import java.util.Date;

import javax.annotation.Resource;

import org.apache.thrift.TException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.fccfc.framework.cache.core.CacheException;
import com.fccfc.framework.cache.core.ICache;
import com.fccfc.framework.cache.core.redis.RedisCache;
import com.fccfc.framework.task.api.CronTrigger;
import com.fccfc.framework.task.api.Task;
import com.fccfc.framework.task.api.TaskService;
import com.fccfc.framework.task.core.bean.ChangeNotifRedisPojo;

/**
 * <Description> <br> 
 *  
 * @author shao.dinghui<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年7月14日 <br>
 * @since V1.0<br>
 * @see service.task.api <br>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({
	"classpath:/META-INF/spring/*.xml"
})
public class RedisCacheServiceTest {
	
	@Resource
    private TaskService.Iface iface;
	
	/**
	 * Description: <br> 
	 *  
	 * @author shao.dinghui<br>
	 * @throws TException 
	 * @taskId <br> <br>
	 */
	@Test
	public void testPutDataToRedis() throws TException {
		Task task = new Task();
    	task.setTaskId(1003);
    	task.setTaskName("task03");
    	task.setModuleCode("TASK");
    	task.setTaskState("A");
    	task.setClassName("com.fccfc.framework.task.core.job.JobRedisCache");
    	task.setMethod("jobPutDataToRedis");
    	task.setCreateTime(new Date().getTime());
    	task.setOperatorId(-1);
    	task.setPriority(5);
    	
    	CronTrigger trigger = new CronTrigger();
    	trigger.setTriggerId(1003);
    	trigger.setTriggerName("Trigger1003");
    	trigger.setCreateTime(new Date().getTime());
    	trigger.setOperatorId(-1);
    	trigger.setTriggerType("2");
    	trigger.setCronExpression("0/10 * * * * ?");
    	
		iface.cronScheduleTask(task, trigger);
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
    	Task task = new Task();
    	task.setTaskId(1003);
    	task.setTaskName("task03");
    	task.setModuleCode("TASK");
    	task.setTaskState("A");
    	task.setClassName("com.fccfc.framework.task.core.job.JobRedisCache");
    	task.setMethod("jobPutDataToRedis");
    	task.setCreateTime(new Date().getTime());
    	task.setOperatorId(-1);
    	task.setPriority(5);
    	
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
    	Task task = new Task();
    	task.setTaskId(1003);
    	task.setTaskName("task03");
    	task.setModuleCode("TASK");
    	task.setTaskState("A");
    	task.setClassName("com.fccfc.framework.task.core.job.JobRedisCache");
    	task.setMethod("jobPutDataToRedis");
    	task.setCreateTime(new Date().getTime());
    	task.setOperatorId(-1);
    	task.setPriority(5);
    	
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
    	Task task = new Task();
    	task.setTaskId(1003);
    	task.setTaskName("task03");
    	task.setModuleCode("TASK");
    	task.setTaskState("A");
    	task.setClassName("com.fccfc.framework.task.core.job.JobRedisCache");
    	task.setMethod("jobPutDataToRedis");
    	task.setCreateTime(new Date().getTime());
    	task.setOperatorId(-1);
    	task.setPriority(5);
    	
    	iface.remove(task);
    }
	
	public static void main(String[] args) throws CacheException {
		ICache cache = new RedisCache("127.0.0.1", 6379);
		ChangeNotifRedisPojo pojo = (ChangeNotifRedisPojo)cache.getValue("nodeName", "key5");
		
		System.out.println(pojo.getChangeNotifId());
		System.out.println(pojo.getActionType());
		System.out.println(pojo.getKeyValue());
		System.out.println(pojo.getTableName());
		System.out.println(pojo.getCreatedDate());
	}
}
