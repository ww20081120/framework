/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.job.manager.controller;

import java.util.Collection;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dangdang.ddframe.job.lite.lifecycle.domain.JobBriefInfo;
import com.dangdang.ddframe.job.lite.lifecycle.domain.ShardingInfo;
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
@RequestMapping("/api/jobManager/job")
public class JobOperationController {

    @Resource
    private JobAPIService jobAPIService;

    /**
     * 获取作业总数.
     * 
     * @return 作业总数
     */
    @GetMapping("/{centerId}/count")
    public int getJobsTotalCount(@PathVariable("centerId") String centerId) {
        return jobAPIService.getJobStatisticsAPI(centerId).getJobsTotalCount();
    }

    /**
     * 获取作业详情.
     * 
     * @return 作业详情集合
     */
    @GetMapping("/{centerId}")
    public Collection<JobBriefInfo> getAllJobsBriefInfo(@PathVariable("centerId") String centerId) {
        return jobAPIService.getJobStatisticsAPI(centerId).getAllJobsBriefInfo();
    }

    /**
     * 触发作业.
     * 
     * @param jobName 作业名称
     */
    @PostMapping("/{centerId}/{jobName}/trigger")
    public void triggerJob(@PathVariable("centerId") String centerId, @PathVariable("jobName") final String jobName) {
        jobAPIService.getJobOperatorAPI(centerId).trigger(Optional.of(jobName), Optional.<String> absent());

    }

    /**
     * 禁用作业.
     * 
     * @param jobName 作业名称
     */
    @PatchMapping("/{centerId}/{jobName}/disable")
    public void disableJob(@PathVariable("centerId") String centerId, @PathVariable("jobName") final String jobName) {
        jobAPIService.getJobOperatorAPI(centerId).disable(Optional.of(jobName), Optional.<String> absent());

    }

    /**
     * 启用作业.
     *
     * @param jobName 作业名称
     */
    @PatchMapping("/{centerId}/{jobName}/enable")
    public void enableJob(@PathVariable("centerId") String centerId, @PathVariable("jobName") final String jobName) {
        jobAPIService.getJobOperatorAPI(centerId).enable(Optional.of(jobName), Optional.<String> absent());
    }

    /**
     * 终止作业.
     * 
     * @param jobName 作业名称
     */
    @PatchMapping("/{centerId}/{jobName}/shutdown")
    public void shutdownJob(@PathVariable("centerId") String centerId, @PathVariable("jobName") final String jobName) {
        jobAPIService.getJobOperatorAPI(centerId).shutdown(Optional.of(jobName), Optional.<String> absent());
    }

    /**
     * 获取分片信息.
     * 
     * @param jobName 作业名称
     * @return 分片信息集合
     */
    @GetMapping("/{centerId}/{jobName}/sharding")
    public Collection<ShardingInfo> getShardingInfo(@PathVariable("centerId") String centerId,
        @PathVariable("jobName") final String jobName) {
        return jobAPIService.getShardingStatisticsAPI(centerId).getShardingInfo(jobName);

    }

    /**
     * Description: 禁用分片信息 <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param jobName
     * @param item <br>
     */
    @PatchMapping("/{centerId}/{jobName}/sharding/{item}/disable")
    public void disableSharding(@PathVariable("centerId") String centerId,
        @PathVariable("jobName") final String jobName, @PathVariable("item") final String item) {

        jobAPIService.getShardingOperateAPI(centerId).disable(jobName, item);

    }

    /**
     * Description: 启用分片信息<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param jobName
     * @param item <br>
     */
    @PatchMapping("/{centerId}/{jobName}/sharding/{item}/enable")
    public void enableSharding(@PathVariable("centerId") String centerId, @PathVariable("jobName") final String jobName,
        @PathVariable("item") final String item) {

        jobAPIService.getShardingOperateAPI(centerId).enable(jobName, item);

    }
}
