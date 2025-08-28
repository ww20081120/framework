/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.agent.file.model;

import java.util.List;
import java.util.ArrayList;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.hbasesoft.framework.ai.agent.dynamic.model.model.vo.ModelConfig;

/**
 * 模型配置属性类<br>
 * 用于从application.yml文件中读取模型配置信息
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月27日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.agent.file.model <br>
 */
@Component
@ConfigurationProperties(prefix = "ai.models")
public class ModelProperties {

    /** 模型配置列表 */
    private List<ModelConfig> configs = new ArrayList<>();

    /**
     * 获取模型配置列表
     * 
     * @return 模型配置列表
     */
    public List<ModelConfig> getConfigs() {
        return configs;
    }

    /**
     * 设置模型配置列表
     * 
     * @param configs 模型配置列表
     */
    public void setConfigs(List<ModelConfig> configs) {
        this.configs = configs;
    }
}
