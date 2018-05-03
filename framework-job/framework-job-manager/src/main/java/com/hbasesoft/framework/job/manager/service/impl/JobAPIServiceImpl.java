/*
 * Copyright 1999-2015 dangdang.com.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </p>
 */

package com.hbasesoft.framework.job.manager.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.dangdang.ddframe.job.lite.lifecycle.api.JobAPIFactory;
import com.dangdang.ddframe.job.lite.lifecycle.api.JobOperateAPI;
import com.dangdang.ddframe.job.lite.lifecycle.api.JobSettingsAPI;
import com.dangdang.ddframe.job.lite.lifecycle.api.JobStatisticsAPI;
import com.dangdang.ddframe.job.lite.lifecycle.api.ServerStatisticsAPI;
import com.dangdang.ddframe.job.lite.lifecycle.api.ShardingOperateAPI;
import com.dangdang.ddframe.job.lite.lifecycle.api.ShardingStatisticsAPI;
import com.google.common.base.Optional;
import com.hbasesoft.framework.job.manager.bean.RegistryCenterConfiguration;
import com.hbasesoft.framework.job.manager.service.JobAPIService;
import com.hbasesoft.framework.job.manager.service.RegistryCenterConfigurationService;

/**
 * 作业API服务实现类.
 * 
 * @author zhangliang
 */
@Service
public class JobAPIServiceImpl implements JobAPIService {

    @Resource
    private RegistryCenterConfigurationService registryCenterConfigurationService;

    @Override
    public JobSettingsAPI getJobSettingsAPI(String centerId) {
        RegistryCenterConfiguration regCenterConfig = registryCenterConfigurationService.get(centerId);
        return JobAPIFactory.createJobSettingsAPI(regCenterConfig.getZkAddressList(), regCenterConfig.getNamespace(),
            Optional.fromNullable(regCenterConfig.getDigest()));
    }

    @Override
    public JobOperateAPI getJobOperatorAPI(String centerId) {
        RegistryCenterConfiguration regCenterConfig = registryCenterConfigurationService.get(centerId);
        return JobAPIFactory.createJobOperateAPI(regCenterConfig.getZkAddressList(), regCenterConfig.getNamespace(),
            Optional.fromNullable(regCenterConfig.getDigest()));
    }

    @Override
    public ShardingOperateAPI getShardingOperateAPI(String centerId) {
        RegistryCenterConfiguration regCenterConfig = registryCenterConfigurationService.get(centerId);
        return JobAPIFactory.createShardingOperateAPI(regCenterConfig.getZkAddressList(),
            regCenterConfig.getNamespace(), Optional.fromNullable(regCenterConfig.getDigest()));
    }

    @Override
    public JobStatisticsAPI getJobStatisticsAPI(String centerId) {
        RegistryCenterConfiguration regCenterConfig = registryCenterConfigurationService.get(centerId);
        return JobAPIFactory.createJobStatisticsAPI(regCenterConfig.getZkAddressList(), regCenterConfig.getNamespace(),
            Optional.fromNullable(regCenterConfig.getDigest()));
    }

    @Override
    public ServerStatisticsAPI getServerStatisticsAPI(String centerId) {
        RegistryCenterConfiguration regCenterConfig = registryCenterConfigurationService.get(centerId);
        return JobAPIFactory.createServerStatisticsAPI(regCenterConfig.getZkAddressList(),
            regCenterConfig.getNamespace(), Optional.fromNullable(regCenterConfig.getDigest()));
    }

    @Override
    public ShardingStatisticsAPI getShardingStatisticsAPI(String centerId) {
        RegistryCenterConfiguration regCenterConfig = registryCenterConfigurationService.get(centerId);
        return JobAPIFactory.createShardingStatisticsAPI(regCenterConfig.getZkAddressList(),
            regCenterConfig.getNamespace(), Optional.fromNullable(regCenterConfig.getDigest()));
    }

}
