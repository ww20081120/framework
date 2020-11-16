/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.job.core;

import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.shardingsphere.elasticjob.api.ElasticJob;
import org.apache.shardingsphere.elasticjob.api.JobConfiguration;
import org.apache.shardingsphere.elasticjob.lite.api.bootstrap.impl.ScheduleJobBootstrap;
import org.apache.shardingsphere.elasticjob.reg.base.CoordinatorRegistryCenter;
import org.apache.shardingsphere.elasticjob.reg.zookeeper.ZookeeperConfiguration;
import org.apache.shardingsphere.elasticjob.reg.zookeeper.ZookeeperRegistryCenter;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;

import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.GlobalConstants;
import com.hbasesoft.framework.common.InitializationException;
import com.hbasesoft.framework.common.StartupListener;
import com.hbasesoft.framework.common.utils.Assert;
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
    private final String[] packagesToScan = new String[] {
        "com.hbasesoft.*"
    };

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
            final CoordinatorRegistryCenter regCenter = setUpRegistryCenter();

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

                            // 分片大小
                            int shardingTotalCount = 1;

                            String shardingItemParameters = getPropery(job.shardingParam());
                            if (StringUtils.isNotEmpty(shardingItemParameters)) {
                                String[] params = StringUtils.split(shardingItemParameters, GlobalConstants.SPLITOR);
                                shardingTotalCount = params.length;
                                StringBuilder sb = new StringBuilder();
                                for (int i = 0; i < shardingTotalCount; i++) {
                                    sb.append(i).append(GlobalConstants.EQUAL_SPLITER).append(params[i]);
                                    if (i < shardingTotalCount - 1) {
                                        sb.append(GlobalConstants.SPLITOR);
                                    }
                                }
                                shardingItemParameters = sb.toString();
                            }

                            JobConfiguration coreConfig = JobConfiguration.newBuilder(name, shardingTotalCount)
                                .cron(getPropery(job.cron())).shardingItemParameters(shardingItemParameters).build();

                            ScheduleJobBootstrap bootstrap = new ScheduleJobBootstrap(regCenter,
                                (ElasticJob) clazz.newInstance(), coreConfig);
                            bootstrap.schedule();

                            LoggerUtil.info("    success create job [{0}] with name {1}", clazz.getName(), name);
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            throw new InitializationException(e);
        }

    }

    private static CoordinatorRegistryCenter setUpRegistryCenter() {
        String url = PropertyHolder.getProperty("job.register.url");
        Assert.notEmpty(url, ErrorCodeDef.JOB_REGISTER_URL_IS_NULL);

        String jobNamespace = PropertyHolder.getProperty("job.register.namespace",
            PropertyHolder.getProperty("project.name"));
        Assert.notEmpty(jobNamespace, ErrorCodeDef.JOB_REGISTER_NAMESPACE_IS_NULL);

        ZookeeperConfiguration zkConfig = new ZookeeperConfiguration(url, jobNamespace);
        CoordinatorRegistryCenter result = new ZookeeperRegistryCenter(zkConfig);
        result.init();
        return result;
    }

    private static String getPropery(final String propery) {
        if (StringUtils.isNotEmpty(propery) && propery.startsWith("${") && propery.endsWith("}")) {
            return PropertyHolder.getProperty(propery.substring(2, propery.length() - 1));
        }
        return propery;
    }

}
