/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.job.quartz;

import java.util.Calendar;
import java.util.Date;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;

import com.hbasesoft.framework.common.GlobalConstants;
import com.hbasesoft.framework.common.InitializationException;
import com.hbasesoft.framework.common.StartupListener;
import com.hbasesoft.framework.common.utils.PropertyHolder;
import com.hbasesoft.framework.common.utils.bean.BeanUtil;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;
import com.hbasesoft.framework.job.core.annotation.Job;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2018年4月14日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.job.core <br>
 */
public class JobStartupLinstener implements StartupListener {

    /**
     * packages to scan
     */
    private final String[] packagesToScan = StringUtils
        .split(PropertyHolder.getProperty("job.basepackage", "com.hbasesoft.*"), GlobalConstants.SPLITOR);

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param context <br>
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    @Override
    public void complete(final ApplicationContext context) {

        // 未开启Job则不进行扫描
        if (!PropertyHolder.getBooleanProperty("job.enable", true)) {
            return;
        }

        try {

            SchedulerFactory schedulerFactory = new StdSchedulerFactory();
            Scheduler scheduler = schedulerFactory.getScheduler();

            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MINUTE, 1);
            Date startTime = calendar.getTime();

            for (String pack : packagesToScan) {
                if (StringUtils.isNotEmpty(pack)) {
                    Set<Class<?>> clazzSet = BeanUtil.getClasses(pack);
                    for (Class<?> clazz : clazzSet) {
                        if (clazz.isAnnotationPresent(Job.class)) {
                            Job job = AnnotationUtils.findAnnotation(clazz, Job.class);

                            String isJobEnable = job.enable();
                            isJobEnable = getPropery(isJobEnable);
                            if (!"true".equalsIgnoreCase(isJobEnable)) {
                                continue;
                            }

                            // Job名称
                            String name = getPropery(job.name());
                            if (StringUtils.isEmpty(name)) {
                                name = StringUtils.uncapitalize(clazz.getSimpleName());
                            }

                            String jobNamespace = PropertyHolder.getProperty("job.register.namespace",
                                PropertyHolder.getProperty("project.name"));
                            JobDetail jobDetail = JobBuilder.newJob(ProxyJob.class).withIdentity(name, jobNamespace)
                                .build();
                            jobDetail.getJobDataMap().put(JobConstants.JOB_INSTANCE_CLASS, clazz.getName());
                            jobDetail.getJobDataMap().put(JobConstants.JOB_SHARDING_PARAM,
                                getPropery(job.shardingParam()));
                            jobDetail.getJobDataMap().put(JobConstants.JOB_NAME, name);

                            CronTrigger trigger = (CronTrigger) TriggerBuilder.newTrigger()
                                .withIdentity(name, jobNamespace) // 创建一个标识符
                                .startAt(startTime)
                                // 每秒钟触发一次任务
                                .withSchedule(CronScheduleBuilder.cronSchedule(job.cron())).build();

                            scheduler.scheduleJob(jobDetail, trigger);

                            LoggerUtil.info("    success create job [{0}] with name {1}", clazz.getName(), name);
                        }
                    }
                }
            }
            if (!scheduler.isShutdown()) {
                scheduler.start();
            }
        }
        catch (Exception e) {
            throw new InitializationException(e);
        }

    }

    private static String getPropery(final String propery) {
        if (StringUtils.isNotEmpty(propery) && propery.startsWith("${") && propery.endsWith("}")) {
            return PropertyHolder.getProperty(propery.substring(2, propery.length() - 1));
        }
        return propery;
    }

}
