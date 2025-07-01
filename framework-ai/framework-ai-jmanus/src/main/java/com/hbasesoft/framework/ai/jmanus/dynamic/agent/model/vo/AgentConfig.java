/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.jmanus.dynamic.agent.model.vo;

import java.util.List;

import lombok.Data;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年7月1日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.jmanus.dynamic.agent.model.vo <br>
 */
@Data
public class AgentConfig {

    /**
     * 智能代理的唯一标识符，用于在系统中唯一标识一个智能代理实例。
     */
    private String id;

    /**
     * 智能代理的名称，可用于在界面展示或日志记录中标识该代理。
     */
    private String name;

    /**
     * 智能代理的描述信息，用于简要说明该代理的功能、用途等。
     */
    private String description;

    /**
     * 系统提示信息，在与智能代理交互时，作为初始的引导信息发送给代理。
     */
    private String systemPrompt;

    /**
     * 下一步提示信息，用于引导智能代理执行下一步操作。
     */
    private String nextStepPrompt;

    /**
     * 智能代理可用的工具列表，每个元素为工具的唯一标识字符串。
     */
    private List<String> availableTools;

    /**
     * 智能代理对应的类名，可用于通过反射机制创建代理实例。
     */
    private String className;
}
