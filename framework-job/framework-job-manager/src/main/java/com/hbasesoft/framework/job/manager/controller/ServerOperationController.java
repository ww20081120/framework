/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.job.manager.controller;

import java.util.Collection;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dangdang.ddframe.job.lite.lifecycle.domain.JobBriefInfo;
import com.dangdang.ddframe.job.lite.lifecycle.domain.ServerBriefInfo;
import com.google.common.base.Optional;
import com.hbasesoft.framework.job.manager.service.JobAPIService;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2018年4月14日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.vcc.sgp.plat.job.manager.controller <br>
 */
@RestController
@RequestMapping("/api/jobManager/server")
public class ServerOperationController {

    @Resource
    private JobAPIService jobAPIService;

    /**
     * Description: 获取服务数量<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    @GetMapping("/{centerId}/count")
    public int getServersTotalCount(@PathVariable("centerId") String centerId) {
        return jobAPIService.getServerStatisticsAPI(centerId).getServersTotalCount();
    }

    /**
     * Description:获取服务器详情. <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    @GetMapping("/{centerId}")
    public Collection<ServerBriefInfo> getAllServersBriefInfo(@PathVariable("centerId") String centerId) {
        return jobAPIService.getServerStatisticsAPI(centerId).getAllServersBriefInfo();

    }

    /**
     * Description: 禁用作业.<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param serverIp 服务器IP地址<br>
     */
    @PatchMapping("/{centerId}/{serverIp}/disable")
    public void disableServer(@PathVariable("centerId") String centerId,
        @PathVariable("serverIp") final String serverIp) {
        jobAPIService.getJobOperatorAPI(centerId).disable(Optional.<String> absent(), Optional.of(serverIp));
    }

    /**
     * Description: 启用作业.<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param serverIp 服务器IP地址<br>
     */
    @PatchMapping("/{centerId}/{serverIp}/enable")
    public void enableServer(@PathVariable("centerId") String centerId,
        @PathVariable("serverIp") final String serverIp) {
        jobAPIService.getJobOperatorAPI(centerId).enable(Optional.<String> absent(), Optional.of(serverIp));

    }

    /**
     * 终止作业.
     *
     * @param serverIp 服务器IP地址
     */
    @PatchMapping("/{centerId}/{serverIp}/shutdown")
    public void shutdownServer(@PathVariable("centerId") String centerId,
        @PathVariable("serverIp") final String serverIp) {
        jobAPIService.getJobOperatorAPI(centerId).shutdown(Optional.<String> absent(), Optional.of(serverIp));

    }

    /**
     * 清理作业.
     *
     * @param serverIp 服务器IP地址
     */
    @DeleteMapping("/{centerId}/{serverIp}")
    public void removeServer(@PathVariable("centerId") String centerId,
        @PathVariable("serverIp") final String serverIp) {
        jobAPIService.getJobOperatorAPI(centerId).remove(Optional.<String> absent(), Optional.of(serverIp));

    }

    /**
     * 获取该服务器上注册的作业的简明信息.
     *
     * @param serverIp 服务器IP地址
     * @return 作业简明信息对象集合
     */
    @GetMapping("/{centerId}/{serverIp}/jobs")
    public Collection<JobBriefInfo> getJobs(@PathVariable("centerId") String centerId,
        @PathVariable("serverIp") final String serverIp) {
        return jobAPIService.getJobStatisticsAPI(centerId).getJobsBriefInfo(serverIp);

    }

    /**
     * Description: 禁用作业.<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param serverIp
     * @param jobName <br>
     */
    @PatchMapping("/{centerId}/{serverIp}/jobs/{jobName}/disable")
    public void disableServerJob(@PathVariable("centerId") String centerId, @PathVariable("serverIp") String serverIp,
        @PathVariable("jobName") String jobName) {
        jobAPIService.getJobOperatorAPI(centerId).disable(Optional.of(jobName), Optional.of(serverIp));

    }

    /**
     * Description: 启用作业.<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param serverIp
     * @param jobName <br>
     */
    @PatchMapping("/{centerId}/{serverIp}/jobs/{jobName}/enable")
    public void enableServerJob(@PathVariable("centerId") String centerId, @PathVariable("serverIp") String serverIp,
        @PathVariable("jobName") String jobName) {
        jobAPIService.getJobOperatorAPI(centerId).enable(Optional.of(jobName), Optional.of(serverIp));

    }

    /**
     * 终止作业.
     *
     * @param serverIp 服务器IP地址
     * @param jobName 作业名称
     */
    @PatchMapping("/{centerId}/{serverIp}/jobs/{jobName}/shutdown")
    public void shutdownServerJob(@PathVariable("centerId") String centerId,
        @PathVariable("serverIp") final String serverIp, @PathVariable("jobName") final String jobName) {
        jobAPIService.getJobOperatorAPI(centerId).shutdown(Optional.of(jobName), Optional.of(serverIp));

    }

    /**
     * 清理作业.
     *
     * @param serverIp 服务器IP地址
     * @param jobName 作业名称
     */
    @DeleteMapping("/{centerId}/{serverIp}/jobs/{jobName}")
    public void removeServerJob(@PathVariable("centerId") String centerId,
        @PathVariable("serverIp") final String serverIp, @PathVariable("jobName") final String jobName) {
        jobAPIService.getJobOperatorAPI(centerId).remove(Optional.of(jobName), Optional.of(serverIp));

    }
}
