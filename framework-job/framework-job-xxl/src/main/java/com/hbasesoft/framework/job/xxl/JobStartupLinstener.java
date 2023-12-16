/**************************************************************************************** 
 Copyright © 2022-2027 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.job.xxl;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;

import com.hbasesoft.framework.common.InitializationException;
import com.hbasesoft.framework.common.StartupListener;
import com.hbasesoft.framework.common.utils.PropertyHolder;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;
import com.hbasesoft.framework.job.core.SimpleJob;
import com.hbasesoft.framework.job.core.annotation.Job;
import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2022年12月8日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.job.xxl <br>
 */
public class JobStartupLinstener implements StartupListener {

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param context <br>
     */
    @Override
    public void complete(final ApplicationContext context) {

        // 未开启Job则不进行扫描
        if (!PropertyHolder.getBooleanProperty("job.enable", true)) {
            return;
        }

        String[] beans = context.getBeanNamesForAnnotation(Job.class);

        try {
            for (String bean : beans) {
                SimpleJob targetBean = context.getBean(bean, SimpleJob.class);
                Class<?> clazz = targetBean.getClass();
                Job job = AnnotationUtils.findAnnotation(clazz, Job.class);
                if (job != null) {
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

                    if (SimpleJob.class.isAssignableFrom(clazz)) {
                        XxlJobSpringExecutor.registJobHandler(name, new ProxyJob(name, targetBean));
                        LoggerUtil.info("    success create job [{0}] with name {1}", clazz.getName(), name);
                    }
                }

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
