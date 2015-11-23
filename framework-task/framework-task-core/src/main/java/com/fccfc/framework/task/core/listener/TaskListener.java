/**
 * 
 */
package com.fccfc.framework.task.core.listener;

import javax.annotation.Resource;

import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.JobListener;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerListener;
import org.quartz.Trigger;
import org.quartz.Trigger.CompletedExecutionInstruction;
import org.quartz.TriggerKey;
import org.quartz.TriggerListener;

import com.fccfc.framework.common.utils.logger.Logger;
import com.fccfc.framework.task.core.TaskConstants;
import com.fccfc.framework.task.core.bean.TaskPojo;
import com.fccfc.framework.task.core.service.JobService;

/**
 * <Description> <br>
 * 
 * @author 伟<br>
 * @version 1.0<br>
 * @CreateDate 2014-11-8 <br>
 * @see com.fccfc.framework.task.core.listener <br>
 */
public class TaskListener implements JobListener, TriggerListener, SchedulerListener {

    /**
     * logger
     */
    private static Logger logger = new Logger(TaskListener.class);

    /**
     * jobService
     */
    @Resource
    private JobService jobService;

    /**
     * scheduler
     */
    @Resource
    private Scheduler scheduler;

    /*
     * (non-Javadoc)
     * @see org.quartz.TriggerListener#triggerComplete(org.quartz.Trigger, org.quartz.JobExecutionContext,
     * org.quartz.Trigger.CompletedExecutionInstruction)
     */
    @Override
    public void triggerComplete(Trigger trigger, JobExecutionContext context,
        CompletedExecutionInstruction instruction) {
        try {
            JobDataMap dataMap = context.getMergedJobDataMap();
            Integer taskId = dataMap.getInt(TaskConstants.TASK_ID);
            if (taskId != null) {
                jobService.insertTaskHisAndTaskState(taskId, -1, TaskPojo.TASK_STATE_COMPLETE);
            }
            logger.debug("Task[{0}] Complete", taskId);
        }
        catch (Exception e) {
            logger.warn("triggerComplete", e);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.quartz.TriggerListener#triggerFired(org.quartz.Trigger, org.quartz.JobExecutionContext)
     */
    @Override
    public void triggerFired(Trigger trigger, JobExecutionContext context) {
        try {
            JobDataMap dataMap = context.getMergedJobDataMap();
            Integer taskId = dataMap.getInt(TaskConstants.TASK_ID);
            if (taskId != null) {
                jobService.insertTaskHisAndTaskState(taskId, -1, TaskPojo.TASK_STATE_ACQUIRED);
            }
            logger.debug("Task[{0}] Complete", taskId);
        }
        catch (Exception e) {
            logger.warn("triggerFired", e);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.quartz.TriggerListener#triggerMisfired(org.quartz.Trigger)
     */
    @Override
    public void triggerMisfired(Trigger trigger) {
        try {
            JobDataMap dataMap = trigger.getJobDataMap();
            Integer taskId = dataMap.getInt(TaskConstants.TASK_ID);
            if (taskId != null) {
                jobService.insertTaskHisAndTaskState(taskId, -1, TaskPojo.TASK_STATE_BLOCKED);
            }
            logger.debug("Task[{0}] Complete", taskId);
        }
        catch (Exception e) {
            logger.warn("triggerMisfired", e);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.quartz.TriggerListener#vetoJobExecution(org.quartz.Trigger, org.quartz.JobExecutionContext)
     */
    @Override
    public boolean vetoJobExecution(Trigger trigger, JobExecutionContext context) {
        try {
            JobDataMap dataMap = context.getMergedJobDataMap();
            Integer taskId = dataMap.getInt(TaskConstants.TASK_ID);
            if (taskId != null) {
                jobService.insertTaskHisAndTaskState(taskId, -1, TaskPojo.TASK_STATE_WAITING);
            }
            logger.debug("Task[{0}] vetoJobExecution", taskId);
        }
        catch (Exception e) {
            logger.warn("vetoJobExecution", e);
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * @see org.quartz.JobListener#getName()
     */
    @Override
    public String getName() {
        return TaskConstants.JOB_LISTENER_NAME;
    }

    /*
     * (non-Javadoc)
     * @see org.quartz.JobListener#jobExecutionVetoed(org.quartz.JobExecutionContext)
     */
    @Override
    public void jobExecutionVetoed(JobExecutionContext context) {
        try {
            JobDataMap dataMap = context.getMergedJobDataMap();
            Integer taskId = dataMap.getInt(TaskConstants.TASK_ID);
            if (taskId != null) {
                jobService.insertTaskHisAndTaskState(taskId, -1, TaskPojo.TASK_STATE_WAITING);

            }
            logger.debug("Task[{0}] jobExecutionVetoed", taskId);
        }
        catch (Exception e) {
            logger.warn("jobExecutionVetoed", e);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.quartz.JobListener#jobToBeExecuted(org.quartz.JobExecutionContext)
     */
    @Override
    public void jobToBeExecuted(JobExecutionContext context) {
        try {
            JobDataMap dataMap = context.getMergedJobDataMap();
            Integer taskId = dataMap.getInt(TaskConstants.TASK_ID);
            if (taskId != null) {
                jobService.insertTaskHisAndTaskState(taskId, -1, TaskPojo.TASK_STATE_ACQUIRED);
            }
            logger.debug("Task[{0}] jobToBeExecuted", taskId);
        }
        catch (Exception e) {
            logger.warn("jobToBeExecuted", e);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.quartz.JobListener#jobWasExecuted(org.quartz.JobExecutionContext, org.quartz.JobExecutionException)
     */
    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException exception) {
        try {
            JobDataMap dataMap = context.getMergedJobDataMap();
            Integer taskId = dataMap.getInt(TaskConstants.TASK_ID);
            if (taskId != null) {
                jobService.insertTaskHisAndTaskState(taskId, -1, TaskPojo.TASK_STATE_COMPLETE);
            }
            logger.debug("Task[{0}] jobWasExecuted", taskId);
        }
        catch (Exception e) {
            logger.warn("jobWasExecuted", e);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.quartz.SchedulerListener#jobScheduled(org.quartz.Trigger)
     */
    @Override
    public void jobScheduled(Trigger trigger) {
        logger.info("---------------->jobScheduled trigger[{0}]<---------------", trigger.getKey());
    }

    /*
     * (non-Javadoc)
     * @see org.quartz.SchedulerListener#jobUnscheduled(org.quartz.TriggerKey)
     */
    @Override
    public void jobUnscheduled(TriggerKey triggerKey) {
        logger.info("---------------->jobUnscheduled trigger[{0}]<---------------", triggerKey);
    }

    /*
     * (non-Javadoc)
     * @see org.quartz.SchedulerListener#triggerFinalized(org.quartz.Trigger)
     */
    @Override
    public void triggerFinalized(Trigger trigger) {
        logger.info("---------------->triggerFinalized trigger[{0}]<---------------", trigger.getKey());
    }

    /*
     * (non-Javadoc)
     * @see org.quartz.SchedulerListener#triggerPaused(org.quartz.TriggerKey)
     */
    @Override
    public void triggerPaused(TriggerKey triggerKey) {
        try {
            Trigger trigger = scheduler.getTrigger(triggerKey);
            JobDataMap dataMap = trigger.getJobDataMap();
            Integer taskId = dataMap.getInt(TaskConstants.TASK_ID);
            if (taskId != null) {
                jobService.insertTaskHisAndTaskState(taskId, -1, TaskPojo.TASK_STATE_PAUSED);
            }
        }
        catch (Exception e) {
            logger.warn("恢复Job失败", e);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.quartz.SchedulerListener#triggersPaused(java.lang.String)
     */
    @Override
    public void triggersPaused(String triggerGroup) {
        logger.info("---------------->triggersPaused group[{0}]<---------------", triggerGroup);
    }

    /*
     * (non-Javadoc)
     * @see org.quartz.SchedulerListener#triggerResumed(org.quartz.TriggerKey)
     */
    @Override
    public void triggerResumed(TriggerKey triggerKey) {
        try {
            Trigger trigger = scheduler.getTrigger(triggerKey);
            JobDataMap dataMap = trigger.getJobDataMap();
            Integer taskId = dataMap.getInt(TaskConstants.TASK_ID);
            if (taskId != null) {
                jobService.insertTaskHisAndTaskState(taskId, -1, TaskPojo.TASK_STATE_WAITING);
            }
        }
        catch (Exception e) {
            logger.warn("恢复Job失败", e);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.quartz.SchedulerListener#triggersResumed(java.lang.String)
     */
    @Override
    public void triggersResumed(String triggerGroup) {
        logger.info("---------------->triggersResumed group[{0}]<---------------", triggerGroup);
    }

    /*
     * (non-Javadoc)
     * @see org.quartz.SchedulerListener#jobAdded(org.quartz.JobDetail)
     */
    @Override
    public void jobAdded(JobDetail jobDetail) {
        logger.info("---------------->jobAdded jobDetail[{0}]<---------------", jobDetail.getKey());
    }

    /*
     * (non-Javadoc)
     * @see org.quartz.SchedulerListener#jobDeleted(org.quartz.JobKey)
     */
    @Override
    public void jobDeleted(JobKey jobKey) {
        try {
            JobDetail jobDetail = scheduler.getJobDetail(jobKey);
            JobDataMap dataMap = jobDetail.getJobDataMap();
            Integer taskId = dataMap.getInt(TaskConstants.TASK_ID);
            if (taskId != null) {
                jobService.insertTaskHisAndDeleteTaskById(taskId, -1);
            }
        }
        catch (Exception e) {
            logger.warn("恢复Job失败", e);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.quartz.SchedulerListener#jobPaused(org.quartz.JobKey)
     */
    @Override
    public void jobPaused(JobKey jobKey) {
        try {
            JobDetail jobDetail = scheduler.getJobDetail(jobKey);
            JobDataMap dataMap = jobDetail.getJobDataMap();
            Integer taskId = dataMap.getInt(TaskConstants.TASK_ID);
            if (taskId != null) {
                jobService.insertTaskHisAndTaskState(taskId, -1, TaskPojo.TASK_STATE_PAUSED);
            }
        }
        catch (Exception e) {
            logger.warn("恢复Job失败", e);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.quartz.SchedulerListener#jobsPaused(java.lang.String)
     */
    @Override
    public void jobsPaused(String jobGroup) {
        logger.info("---------------->jobsPaused group[{0}]<---------------", jobGroup);
    }

    /*
     * (non-Javadoc)
     * @see org.quartz.SchedulerListener#jobResumed(org.quartz.JobKey)
     */
    @Override
    public void jobResumed(JobKey jobKey) {
        try {
            JobDetail jobDetail = scheduler.getJobDetail(jobKey);
            JobDataMap dataMap = jobDetail.getJobDataMap();
            Integer taskId = dataMap.getInt(TaskConstants.TASK_ID);
            if (taskId != null) {
                jobService.insertTaskHisAndTaskState(taskId, -1, TaskPojo.TASK_STATE_WAITING);
            }
        }
        catch (Exception e) {
            logger.warn("恢复Job失败", e);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.quartz.SchedulerListener#jobsResumed(java.lang.String)
     */
    @Override
    public void jobsResumed(String jobGroup) {
        logger.info("---------------->jobsResumed group[{0}]<---------------", jobGroup);
    }

    /*
     * (non-Javadoc)
     * @see org.quartz.SchedulerListener#schedulerError(java.lang.String, org.quartz.SchedulerException)
     */
    @Override
    public void schedulerError(String msg, SchedulerException cause) {
        logger.error(msg, cause);
    }

    /*
     * (non-Javadoc)
     * @see org.quartz.SchedulerListener#schedulerInStandbyMode()
     */
    @Override
    public void schedulerInStandbyMode() {
        logger.info("---------------->schedulerInStandbyMode<---------------");
    }

    /*
     * (non-Javadoc)
     * @see org.quartz.SchedulerListener#schedulerStarted()
     */
    @Override
    public void schedulerStarted() {
        logger.info("---------------->schedulerStarted<---------------");
    }

    /*
     * (non-Javadoc)
     * @see org.quartz.SchedulerListener#schedulerStarting()
     */
    @Override
    public void schedulerStarting() {
        logger.info("---------------->schedulerStarting<---------------");
    }

    /*
     * (non-Javadoc)
     * @see org.quartz.SchedulerListener#schedulerShutdown()
     */
    @Override
    public void schedulerShutdown() {
        logger.info("---------------->schedulerShutdown<---------------");
    }

    /*
     * (non-Javadoc)
     * @see org.quartz.SchedulerListener#schedulerShuttingdown()
     */
    @Override
    public void schedulerShuttingdown() {
        logger.info("---------------->schedulerShuttingdown<---------------");
    }

    /*
     * (non-Javadoc)
     * @see org.quartz.SchedulerListener#schedulingDataCleared()
     */
    @Override
    public void schedulingDataCleared() {
        logger.info("---------------->schedulingDataCleared<---------------");
    }

}
