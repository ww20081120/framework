/**************************************************************************************** 
 Copyright © 2022-2027 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.job.xxl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
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

    /** 是否启用job */
    @Value("${job.enable:true}")
    private boolean enable;

    /** 调度中心部署根地址 [选填]：如调度中心集群部署存在多个地址则用逗号分隔。执行器将会使用该地址进行"执行器心跳注册"和"任务结果回调"；为空则关闭自动注册 */
    @Value("${job.xxl.admin.addresses:http://127.0.0.1:8080/xxl-job-admin}")
    private String adminAddresses;

    /** 执行器通讯TOKEN [选填]：非空时启用； */
    @Value("${job.xxl.accessToken:}")
    private String accessToken;

    /** 执行器AppName [选填]：执行器心跳注册分组依据；为空则关闭自动注册 */
    @Value("${job.xxl.executor.appname:xxl-job-executor-sample}")
    private String appname;

    /** 执行器注册 [选填]：优先使用该配置作为注册地址，为空时使用内嵌服务 ”IP:PORT“ 作为注册地址。从而更灵活的支持容器类型执行器动态IP和动态映射端口问题 */
    @Value("${job.xxl.executor.address:}")
    private String address;

    /** 执行器IP [选填]：默认为空表示自动获取IP，多网卡时可手动设置指定IP，该IP不会绑定Host仅作为通讯实用；地址信息用于 "执行器注册" 和 "调度中心请求并触发任务"； */
    @Value("${job.xxl.executor.ip:}")
    private String ip;

    /** 执行器端口号 [选填]：小于等于0则自动获取；默认端口为9999，单机部署多个执行器时，注意要配置不同执行器端口； */
    @Value("${job.xxl.executor.port:9999}")
    private int port;

    /** 执行器运行日志文件存储磁盘路径 [选填] ：需要对该路径拥有读写权限；为空则使用默认路径； */
    @Value("${job.xxl.executor.logpath:}")
    private String logPath;

    /** 执行器日志文件保存天数 [选填] ： 过期日志自动清理, 限制值大于等于3时生效; 否则, 如-1, 关闭自动清理功能； */
    @Value("${job.xxl.executor.logretentiondays:30}")
    private int logRetentionDays;

    /**
     * @Method xxlJobExecutor
     * @param
     * @return com.xxl.job.core.executor.impl.XxlJobSpringExecutor
     * @Author 李煜龙
     * @Description TODD
     * @Date 2023/1/29 16:13
     */
    @Bean
    public XxlJobSpringExecutor xxlJobExecutor() {

        // 未开启Job则不进行扫描
        if (!enable) {
            return null;
        }

        XxlJobSpringExecutor xxlJobSpringExecutor = new XxlJobSpringExecutor();
        xxlJobSpringExecutor.setAdminAddresses(adminAddresses);

        // 执行器通讯TOKEN [选填]：非空时启用；
        if (StringUtils.isNotEmpty(accessToken)) {
            xxlJobSpringExecutor.setAccessToken(accessToken);
        }

        // 执行器AppName [选填]：执行器心跳注册分组依据；为空则关闭自动注册
        xxlJobSpringExecutor.setAppname(appname);

        if (StringUtils.isNotEmpty(ip)) {
            xxlJobSpringExecutor.setIp(ip);
        }
        else {
            xxlJobSpringExecutor.setAddress(address);
        }
        // 执行器端口号 [选填]：小于等于0则自动获取；默认端口为9999，单机部署多个执行器时，注意要配置不同执行器端口；
        xxlJobSpringExecutor.setPort(port);

        // 执行器运行日志文件存储磁盘路径 [选填] ：需要对该路径拥有读写权限；为空则使用默认路径；
        xxlJobSpringExecutor.setLogPath(StringUtils.isNotEmpty(logPath) ? logPath
            : new StringBuilder().append(System.getProperty("user.home")).append("/log/hbasesoft/")
                .append(PropertyHolder.getProjectName()).toString());

        // 执行器日志文件保存天数 [选填] ： 过期日志自动清理, 限制值大于等于3时生效; 否则, 如-1, 关闭自动清理功能；
        xxlJobSpringExecutor.setLogRetentionDays(logRetentionDays);
        return xxlJobSpringExecutor;

    }
}
