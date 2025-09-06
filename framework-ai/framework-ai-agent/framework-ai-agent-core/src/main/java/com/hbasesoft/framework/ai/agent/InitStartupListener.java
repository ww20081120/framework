/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.agent;

import org.springframework.context.ApplicationContext;

import com.hbasesoft.framework.ai.agent.config.IConfigService;
import com.hbasesoft.framework.ai.agent.dynamic.mcp.service.McpCacheManager;
import com.hbasesoft.framework.ai.agent.dynamic.prompt.model.enums.PromptEnum;
import com.hbasesoft.framework.ai.agent.dynamic.prompt.service.impl.PromptDataInitializer;
import com.hbasesoft.framework.ai.agent.llm.LlmService;
import com.hbasesoft.framework.ai.agent.prompt.PromptDescriptionLoader;
import com.hbasesoft.framework.ai.agent.tool.filesystem.UnifiedDirectoryManager;
import com.hbasesoft.framework.common.StartupListener;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月23日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.agent.init <br>
 */
public class InitStartupListener implements StartupListener {

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param context <br>
     */
    @Override
    public void complete(ApplicationContext context) {
        UnifiedDirectoryManager udm = context.getBean(UnifiedDirectoryManager.class);
        udm.init();

        IConfigService configService = context.getBean(IConfigService.class);
        configService.init();

        McpCacheManager mm = context.getBean(McpCacheManager.class);
        mm.init();

        LlmService llmService = context.getBean(LlmService.class);
        llmService.init();

        PromptDataInitializer pdi = context.getBean(PromptDataInitializer.class);
        pdi.init();

        PromptDescriptionLoader promptDescriptionLoader = context.getBean(PromptDescriptionLoader.class);
        PromptEnum.setDescriptionLoader(promptDescriptionLoader);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    @Override
    public LoadOrder getOrder() {
        return LoadOrder.MIDDLE;
    }
}
