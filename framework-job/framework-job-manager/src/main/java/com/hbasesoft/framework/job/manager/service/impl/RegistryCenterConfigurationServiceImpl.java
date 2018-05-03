/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.job.manager.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.MapUtils;
import org.springframework.stereotype.Service;

import com.hbasesoft.framework.cache.core.CacheHelper;
import com.hbasesoft.framework.cache.core.ICache;
import com.hbasesoft.framework.common.utils.CommonUtil;
import com.hbasesoft.framework.job.manager.bean.RegistryCenterConfiguration;
import com.hbasesoft.framework.job.manager.service.RegistryCenterConfigurationService;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2018年4月14日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.vcc.sgp.plat.job.manager.service <br>
 */
@Service
public class RegistryCenterConfigurationServiceImpl implements RegistryCenterConfigurationService {

    private static final String JOB_REGISTRY_CENTER_CONFIGURATION_NODE = "JOB_REGISTRY_CENTER_CONFIGURATION_NODE";

    private volatile ICache cache = CacheHelper.getCache();

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    @Override
    public List<RegistryCenterConfiguration> queryAll() {
        Map<String, RegistryCenterConfiguration> nodeMap = cache.getNode(JOB_REGISTRY_CENTER_CONFIGURATION_NODE,
            RegistryCenterConfiguration.class);

        List<RegistryCenterConfiguration> entityList = new ArrayList<>();
        if (MapUtils.isNotEmpty(nodeMap)) {
            for (Entry<String, RegistryCenterConfiguration> node : nodeMap.entrySet()) {
                entityList.add(node.getValue());
            }
            Collections.sort(entityList, (e1, e2) -> (e1.getName().compareTo(e2.getName())));
        }
        return entityList;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param name
     * @return <br>
     */
    @Override
    public RegistryCenterConfiguration get(String id) {
        return cache.get(JOB_REGISTRY_CENTER_CONFIGURATION_NODE, id);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param entity
     * @return <br>
     */
    @Override
    public RegistryCenterConfiguration add(RegistryCenterConfiguration entity) {
        entity.setId(CommonUtil.getTransactionID());
        cache.put(JOB_REGISTRY_CENTER_CONFIGURATION_NODE, entity.getId(), entity);
        return entity;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param name <br>
     */
    @Override
    public void delete(String id) {
        cache.evict(JOB_REGISTRY_CENTER_CONFIGURATION_NODE, id);
    }

}
