/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.jmanus.dynamic.agent;

import java.util.Map;

import com.hbasesoft.framework.ai.jmanus.planning.IPlanningFactory.ToolCallBackContext;


/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年7月2日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.jmanus.dynamic.agent <br>
 */
public interface ToolCallbackProvider {

    /**
     * 获取工具回调上下文的映射。 该映射的键为工具的标识字符串，值为 PlanningFactory 类中定义的 ToolCallBackContext 对象， 每个 ToolCallBackContext
     * 对象包含了与工具相关的回调信息。
     * 
     * @return 一个包含工具标识和对应工具回调上下文的映射
     */
    Map<String, ToolCallBackContext> getToolCallBackContext();

}
