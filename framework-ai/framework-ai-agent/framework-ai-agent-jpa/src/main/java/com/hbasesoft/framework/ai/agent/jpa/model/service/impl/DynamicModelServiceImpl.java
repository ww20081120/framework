/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.agent.jpa.model.service.impl;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hbasesoft.framework.ai.agent.dynamic.model.model.vo.ModelConfig;
import com.hbasesoft.framework.ai.agent.dynamic.model.service.DynamicModelService;
import com.hbasesoft.framework.ai.agent.jpa.model.dao.DynamicModelDao;
import com.hbasesoft.framework.ai.agent.jpa.model.po.DynamicModelPo4Jpa;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月22日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.agent.jpa.model.service.impl <br>
 */
@Service
public class DynamicModelServiceImpl implements DynamicModelService {

    @Autowired
    private DynamicModelDao dynamicModelRepository;

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    @Transactional(readOnly = true)
    @Override
    public ModelConfig getDefault() {
        return convert(dynamicModelRepository.getByLambda(q -> q.eq(DynamicModelPo4Jpa::getIsDefault, 1)));
    }

    private ModelConfig convert(DynamicModelPo4Jpa po) {
        if (po == null) {
            return null;
        }
        ModelConfig modelConfig = new ModelConfig();
        BeanUtils.copyProperties(po, modelConfig);
        return modelConfig;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    @Transactional(readOnly = true)
    @Override
    public List<ModelConfig> queryAll() {
        return dynamicModelRepository.queryAll().stream().map(this::convert).toList();
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param modelId
     * @return <br>
     */
    @Transactional(readOnly = true)
    @Override
    public ModelConfig get(Long modelId) {
        return convert(dynamicModelRepository.get(modelId));
    }
}
