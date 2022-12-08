/**************************************************************************************** 
 Copyright © 2022-2027 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.job.xxl;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.hbasesoft.framework.common.utils.PropertyHolder;
import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2022年12月8日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.job.quartz <br>
 */
@Configuration
public class XxlJobConfig {

    @Bean
    public XxlJobSpringExecutor xxlJobExecutor() {

        // 未开启Job则不进行扫描
        if (!PropertyHolder.getBooleanProperty("job.enable", true)) {
            return null;
        }

        XxlJobSpringExecutor xxlJobSpringExecutor = new XxlJobSpringExecutor();
        // 调度中心部署根地址 [选填]：如调度中心集群部署存在多个地址则用逗号分隔。执行器将会使用该地址进行"执行器心跳注册"和"任务结果回调"；为空则关闭自动注册
        xxlJobSpringExecutor.setAdminAddresses(
            PropertyHolder.getProperty("xxl.job.admin.addresses", "http://127.0.0.1:8080/xxl-job-admin"));

        // 执行器通讯TOKEN [选填]：非空时启用；
        String accessToken = PropertyHolder.getProperty("xxl.job.accessToken");
        if (StringUtils.isNotEmpty(accessToken)) {
            xxlJobSpringExecutor.setAccessToken(accessToken);
        }

        // 执行器AppName [选填]：执行器心跳注册分组依据；为空则关闭自动注册
        xxlJobSpringExecutor
            .setAppname(PropertyHolder.getProperty("xxl.job.executor.appname", PropertyHolder.getProjectName()));
        String ip = PropertyHolder.getProperty("xxl.job.executor.ip");
        if (StringUtils.isNotEmpty(ip)) {
            xxlJobSpringExecutor.setIp(ip);
        }
        // 执行器端口号 [选填]：小于等于0则自动获取；默认端口为9999，单机部署多个执行器时，注意要配置不同执行器端口；
        xxlJobSpringExecutor.setPort(PropertyHolder.getIntProperty("xxl.job.executor.port", 9999));

        // 执行器运行日志文件存储磁盘路径 [选填] ：需要对该路径拥有读写权限；为空则使用默认路径；
        xxlJobSpringExecutor.setLogPath(PropertyHolder.getProperty("xxl.job.executor.logpath",
            new StringBuilder().append(System.getProperty("user.home")).append("/log/hbasesoft/")
                .append(PropertyHolder.getProjectName()).toString()));

        // 执行器日志文件保存天数 [选填] ： 过期日志自动清理, 限制值大于等于3时生效; 否则, 如-1, 关闭自动清理功能；
        xxlJobSpringExecutor
            .setLogRetentionDays(PropertyHolder.getIntProperty("xxl.job.executor.logretentiondays", 30));
        return xxlJobSpringExecutor;

    }
}
