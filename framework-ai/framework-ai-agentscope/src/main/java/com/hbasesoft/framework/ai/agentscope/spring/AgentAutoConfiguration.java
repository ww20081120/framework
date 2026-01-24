/****************************************************************************************
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.agentscope.spring;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;

import com.hbasesoft.framework.ai.core.Agent;
import com.hbasesoft.framework.common.utils.logger.Logger;

import io.agentscope.core.ReActAgent;

/**
 * Agent自动配置类<br>
 * 负责注册Agent扫描器到Spring容器<br>
 *
 * @author 王伟<br>
 * @version 1.0<br>
 * @CreateDate 2026年1月23日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.agentscope.spring <br>
 */
@AutoConfiguration
@ConditionalOnClass({ReActAgent.class, Agent.class})
public class AgentAutoConfiguration {

    /** 日志记录器 */
    private static final Logger LOGGER = new Logger(AgentAutoConfiguration.class);

    /**
     * 注册Agent扫描器<br>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @return AgentDefinitionProcessor<br>
     */
    @Bean
    public static AgentDefinitionProcessor agentScannerRegistrar() {
        LOGGER.info("注册Agent扫描器到Spring容器");
        return new AgentDefinitionProcessor();
    }
}
