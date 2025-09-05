/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.agent.dynamic.agent.vo;

import java.util.List;

import com.hbasesoft.framework.ai.agent.dynamic.model.model.vo.ModelConfig;

import lombok.Data;

/**
 * Agent配置类，用于定义和管理智能代理的各项配置信息<br>
 * 该类包含了代理的基本信息、提示词、可用工具等配置项
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年7月1日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.agent.dynamic.agent.vo <br>
 */
@Data
public class AgentConfig {

    /** 代理的唯一标识符 */
    private String id;

    /** 代理名称 */
    private String name;

    /** 代理描述信息 */
    private String description;

    /** 系统提示词，定义代理的行为和角色 */
    private String systemPrompt;

    /** 下一步操作提示词 */
    private String nextStepPrompt;

    /** 代理可用的工具列表 */
    private List<String> availableTools;

    /** 代理实现类的完整类名 */
    private String className;

    /** 代理使用的模型配置 */
    private ModelConfig model;

    /** 代理所属的命名空间 */
    private String namespace;

    /** 是否为内置代理 */
    private Boolean builtIn = false;

    /** 是否为内置代理（备用字段） */
    private Boolean isBuiltIn = false;
}