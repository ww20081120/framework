/**
 * 
 */
package com.fccfc.framework.task.core.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.apache.thrift.TException;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.ListenerManager;
import org.quartz.Scheduler;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;

import com.fccfc.framework.common.ErrorCodeDef;
import com.fccfc.framework.common.ServiceException;
import com.fccfc.framework.common.utils.Assert;
import com.fccfc.framework.common.utils.CommonUtil;
import com.fccfc.framework.task.api.CronTrigger;
import com.fccfc.framework.task.api.SimpleTrigger;
import com.fccfc.framework.task.api.Task;
import com.fccfc.framework.task.api.TaskService;
import com.fccfc.framework.task.core.TaskConstants;
import com.fccfc.framework.task.core.bean.CronTriggerPojo;
import com.fccfc.framework.task.core.bean.SimpleTriggerPojo;
import com.fccfc.framework.task.core.bean.TaskPojo;
import com.fccfc.framework.task.core.bean.TriggerPojo;
import com.fccfc.framework.task.core.dao.JobDao;
import com.fccfc.framework.task.core.dao.TriggerDao;
import com.fccfc.framework.task.core.job.JobExcutor;
import com.fccfc.framework.task.core.job.SynchronizedJobExcutor;
import com.fccfc.framework.task.core.listener.TaskListener;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2014年11月5日 <br>
 * @since V1.0<br>
 * @see com.fccfc.framework.task.core.listener <br>
 */
public class TaskServiceImpl implements TaskService.Iface {
    
    /**
     * jobDao
     */
    @Resource
    private JobDao jobDao;
    
    /**
     * triggerDao
     */
    @Resource
    private TriggerDao triggerDao;
    
    /**
     * scheduler
     */
    @Resource
    private Scheduler scheduler;
    
    /**
     * taskListener
     */
    @Resource
    private TaskListener taskListener;

    /*
     * (non-Javadoc)
     * @see com.fccfc.framework.api.task.TaskService#scheduleAllTask()
     */
    @Override
    public void scheduleAllTask() throws TException {
        try {
            TaskPojo pojo = new TaskPojo();
            pojo.setTaskState(TaskPojo.TASK_STATE_ACQUIRED);
            List<TaskPojo> taskList = jobDao.selectTaskList(pojo, -1, -1);
            if (CommonUtil.isNotEmpty(taskList)) {
                for (TaskPojo task : taskList) {
                    List<TriggerPojo> triggerList = triggerDao.selectTriggerByTaskId(task.getTaskId());
                    if (CommonUtil.isNotEmpty(triggerList)) {
                        for (TriggerPojo trigger : triggerList) {
                            if (TriggerPojo.TRIGGER_TYPE_SIMPLE.equals(trigger.getTriggerType())) {
                                SimpleTriggerPojo triggerPojo = triggerDao.getSimpleTriggerById(trigger.getTriggerId());
                                schedule(task, triggerPojo);
                            }
                            else {
                                CronTriggerPojo triggerPojo = triggerDao.getCronTriggerById(trigger.getTriggerId());
                                schedule(task, triggerPojo);
                            }
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            throw new TException("执行任务失败", e);
        }

    }

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param task <br>
     * @param simpleTrigger <br>
     * @throws TException <br>
     */
    @Override
    public void simpleScheduleTask(Task task, SimpleTrigger simpleTrigger) throws TException {
        try {
            schedule(new TaskPojo(task), new SimpleTriggerPojo(simpleTrigger));
        }
        catch (ServiceException e) {
            throw new TException(e);
        }

    }

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param taskPojo <br>
     * @param simpleTriggerPojo <br>
     * @throws ServiceException <br>
     */
    private void schedule(TaskPojo taskPojo, SimpleTriggerPojo simpleTriggerPojo) throws ServiceException {
        try {
            Assert.notNull(taskPojo, "任务不能为空");
            Assert.notNull(simpleTriggerPojo, "触发器不能为空");

            JobDetail jobDetail = getJobDetail(taskPojo);

            SimpleScheduleBuilder builder = SimpleScheduleBuilder.simpleSchedule()
                .withRepeatCount(simpleTriggerPojo.getTimes()).withMisfireHandlingInstructionNextWithExistingCount()
                .withIntervalInSeconds(simpleTriggerPojo.getExecuteInterval());

            TriggerKey triggerKey = new TriggerKey(simpleTriggerPojo.getTriggerName(), taskPojo.getTaskName());
            Trigger trigger = TriggerBuilder.newTrigger().startAt(simpleTriggerPojo.getBeginTime())
                .endAt(simpleTriggerPojo.getEndTime()).withIdentity(triggerKey).withSchedule(builder).build();

            if (taskListener != null) {
                ListenerManager listenerManager = scheduler.getListenerManager();
                listenerManager.addJobListener(taskListener);
                listenerManager.addTriggerListener(taskListener);
                listenerManager.addSchedulerListener(taskListener);
            }

            if (scheduler.checkExists(triggerKey)) {
                scheduler.rescheduleJob(triggerKey, trigger);
            }
            else {
                scheduler.scheduleJob(jobDetail, trigger);
            }
        }
        catch (Exception e) {
            throw new ServiceException(ErrorCodeDef.SCHEDULE_TASK_ERROR_10021, "执行任务失败", e);
        }
    }

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param task <br>
     * @param cronTrigger <br>
     * @throws TException <br>
     */
    @Override
    public void cronScheduleTask(Task task, CronTrigger cronTrigger) throws TException {
        try {
            schedule(new TaskPojo(task), new CronTriggerPojo(cronTrigger));
        }
        catch (ServiceException e) {
            throw new TException(e);
        }
    }

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param taskPojo <br>
     * @param cronTriggerPojo <br>
     * @throws ServiceException <br>
     */
    private void schedule(TaskPojo taskPojo, CronTriggerPojo cronTriggerPojo) throws ServiceException {
        try {
            Assert.notNull(taskPojo, "任务不能为空");
            Assert.notNull(cronTriggerPojo, "触发器不能为空");

            JobDetail jobDetail = getJobDetail(taskPojo);

            TriggerKey triggerKey = new TriggerKey(cronTriggerPojo.getTriggerName(), taskPojo.getTaskName());

            CronScheduleBuilder cronScheduleBuiler = CronScheduleBuilder.cronSchedule(cronTriggerPojo
                .getCronExpression());

            Trigger trigger = TriggerBuilder.newTrigger().withIdentity(triggerKey).withSchedule(cronScheduleBuiler)
                .build();

            if (taskListener != null) {
                ListenerManager listenerManager = scheduler.getListenerManager();
                listenerManager.addJobListener(taskListener);
                listenerManager.addTriggerListener(taskListener);
                listenerManager.addSchedulerListener(taskListener);
            }

            if (scheduler.checkExists(triggerKey)) {
                scheduler.rescheduleJob(triggerKey, trigger);
            }
            else {
                scheduler.scheduleJob(jobDetail, trigger);
            }
        }
        catch (Exception e) {
            throw new ServiceException(ErrorCodeDef.SCHEDULE_TASK_ERROR_10021, "执行任务失败", e);
        }
    }

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param task <br>
     * @throws TException <br>
     */
    @Override
    public void pause(Task task) throws TException {
        try {
            pause(new TaskPojo(task));
        }
        catch (ServiceException e) {
            throw new TException(e);
        }
    }

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param taskPojo <br>
     * @throws ServiceException <br>
     */
    private void pause(TaskPojo taskPojo) throws ServiceException {
        try {
            List<TriggerPojo> triggerList = triggerDao.selectTriggerByTaskId(taskPojo.getTaskId());
            if (CommonUtil.isNotEmpty(triggerList)) {
                for (TriggerPojo trigger : triggerList) {
                    scheduler.pauseTrigger(new TriggerKey(trigger.getTriggerName(), taskPojo.getTaskName()));
                }
            }
        }
        catch (Exception e) {
            throw new ServiceException(ErrorCodeDef.PAUSE_TASK_ERROR_10022, "暂停任务失败", e);
        }
    }

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param task <br>
     * @throws TException <br>
     */
    @Override
    public void resume(Task task) throws TException {
        try {
            resume(new TaskPojo(task));
        }
        catch (ServiceException e) {
            throw new TException(e);
        }
    }

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param taskPojo <br>
     * @throws ServiceException <br>
     */
    private void resume(TaskPojo taskPojo) throws ServiceException {
        try {
            List<TriggerPojo> triggerList = triggerDao.selectTriggerByTaskId(taskPojo.getTaskId());
            if (CommonUtil.isNotEmpty(triggerList)) {
                for (TriggerPojo trigger : triggerList) {
                    scheduler.resumeTrigger(new TriggerKey(trigger.getTriggerName(), taskPojo.getTaskName()));
                }
            }
        }
        catch (Exception e) {
            throw new ServiceException(ErrorCodeDef.RESUME_TASK_ERROR_10023, "暂停任务失败", e);
        }
    }

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param task <br>
     * @throws TException <br>
     */
    @Override
    public void remove(Task task) throws TException {
        try {
            remove(new TaskPojo(task));
        }
        catch (ServiceException e) {
            throw new TException(e);
        }
    }

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param taskPojo <br>
     * @throws ServiceException <br>
     */
    private void remove(TaskPojo taskPojo) throws ServiceException {
        try {
            List<TriggerPojo> triggerList = triggerDao.selectTriggerByTaskId(taskPojo.getTaskId());
            if (CommonUtil.isNotEmpty(triggerList)) {
                TriggerKey key = null;
                for (TriggerPojo trigger : triggerList) {
                    key = new TriggerKey(trigger.getTriggerName(), taskPojo.getTaskName());
                    scheduler.resumeTrigger(key);
                    scheduler.unscheduleJob(key);
                }
            }
        }
        catch (Exception e) {
            throw new ServiceException(ErrorCodeDef.REMOVE_TASK_ERROR_10024, "暂停任务失败", e);
        }
    }

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param taskPojo <br>
     * @return <br>
     */
    private JobDetail getJobDetail(TaskPojo taskPojo) {
        JobDetail detail = JobBuilder
            .newJob("Y".equals(taskPojo.getIsConcurrent()) ? JobExcutor.class : SynchronizedJobExcutor.class)
            .withIdentity(taskPojo.getTaskName(), taskPojo.getModuleCode()).build();
        detail.isDurable();
        JobDataMap dataMap = detail.getJobDataMap();
        dataMap.put(TaskConstants.TASK_CLASS_NAME, taskPojo.getClassName());
        dataMap.put(TaskConstants.TASK_EXCUTE_METHOD_NAME, taskPojo.getMethod());
        dataMap.put(TaskConstants.TASK_ID, taskPojo.getTaskId());
        return detail;
    }

    public void setTaskListener(TaskListener taskListener) {
        this.taskListener = taskListener;
    }

}
