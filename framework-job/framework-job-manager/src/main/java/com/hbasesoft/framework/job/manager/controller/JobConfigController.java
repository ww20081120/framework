/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.job.manager.controller;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dangdang.ddframe.job.lite.lifecycle.domain.JobSettings;
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
@RequestMapping("/api/jobManager/jobConfig")
public class JobConfigController {

    @Resource
    private JobAPIService jobAPIService;

    /**
     * 获取作业配置.
     * 
     * @param jobName 作业名称
     * @return 作业配置
     */
    @GetMapping("/{centerId}/{jobName}")
    public JobSettings getJobSettings(@PathVariable("centerId") String centerId,
        @PathVariable("jobName") final String jobName) {
        return jobAPIService.getJobSettingsAPI(centerId).getJobSettings(jobName);

    }

    /**
     * 修改作业配置.
     * 
     * @param jobSettings 作业配置
     */
    @PutMapping("/{centerId}/{jobName}")
    public void updateJobSettings(@PathVariable("centerId") String centerId,
        @RequestBody final JobSettings jobSettings) {
        jobAPIService.getJobSettingsAPI(centerId).updateJobSettings(jobSettings);

    }

    /**
     * 删除作业配置.
     * 
     * @param jobName 作业名称
     */
    @DeleteMapping("/{centerId}/{jobName}")
    public void removeJob(@PathVariable("centerId") String centerId, @PathVariable("jobName") final String jobName) {
        jobAPIService.getJobSettingsAPI(centerId).removeJobSettings(jobName);
    }
}
