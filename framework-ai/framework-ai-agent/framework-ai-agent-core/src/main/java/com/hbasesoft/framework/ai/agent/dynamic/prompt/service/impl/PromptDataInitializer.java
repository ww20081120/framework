/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.agent.dynamic.prompt.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.hbasesoft.framework.ai.agent.dynamic.prompt.service.IPromptDataInitializer;
import com.hbasesoft.framework.ai.agent.dynamic.prompt.service.PromptInitializationService;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月21日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.agent.dynamic.prompt.service.impl <br>
 */
@Component
public class PromptDataInitializer implements IPromptDataInitializer {

    private final PromptInitializationService promptInitializationService;

    @Value("${namespace.value:default}")
    private String namespace;

    public PromptDataInitializer(PromptInitializationService promptInitializationService) {
        this.promptInitializationService = promptInitializationService;
    }

    public void init() {
        try {
            promptInitializationService.initializePromptsForNamespace(namespace);
        }
        catch (Exception e) {
            LoggerUtil.error("Failed to initialize prompt data", e);
            throw e;
        }
    }

}
