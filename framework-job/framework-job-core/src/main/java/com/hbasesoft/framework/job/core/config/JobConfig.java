/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.job.core.config;

import org.apache.shardingsphere.elasticjob.reg.base.CoordinatorRegistryCenter;
import org.apache.shardingsphere.elasticjob.reg.zookeeper.ZookeeperConfiguration;
import org.apache.shardingsphere.elasticjob.reg.zookeeper.ZookeeperRegistryCenter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.utils.Assert;
import com.hbasesoft.framework.common.utils.PropertyHolder;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2018年4月14日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.job.core.config <br>
 */
@Configuration
public class JobConfig {

    @Bean
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

}
