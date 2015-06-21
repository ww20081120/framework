/**************************************************************************************** 
 Copyright © 2003-2012 ZTEsoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.fccfc.framework.task.core.job;

import java.lang.reflect.Method;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.springframework.context.ApplicationContext;

import com.fccfc.framework.task.core.TaskConstants;

/**
 * <Description> <br>
 * 
 * @author 王伟 <br>
 * @version 1.0 <br>
 * @CreateDate 2014年11月7日 <br>
 * @see com.fccfc.framework.task.job <br>
 */
@PersistJobDataAfterExecution
public class JobExcutor implements Job {

    /**
     * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
     */
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            JobDataMap dataMap = context.getMergedJobDataMap();
            ApplicationContext application = (ApplicationContext) context.getScheduler().getContext()
                .get(TaskConstants.APPLICATION_CONTEXT_KEY);
            Class<?> targetClass = Class.forName(dataMap.getString(TaskConstants.TASK_CLASS_NAME));
            Object target = application.getAutowireCapableBeanFactory().createBean(targetClass);
            Method method = targetClass.getDeclaredMethod(dataMap.getString(TaskConstants.TASK_EXCUTE_METHOD_NAME));
            method.invoke(target);
        }
        catch (Exception e) {
            throw new JobExecutionException("执行Job失败", e);
        }
    }

}
