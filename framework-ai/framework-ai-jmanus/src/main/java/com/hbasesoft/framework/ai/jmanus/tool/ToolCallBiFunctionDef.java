/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.jmanus.tool;

import java.util.function.BiFunction;

import org.springframework.ai.chat.model.ToolContext;

import com.hbasesoft.framework.ai.jmanus.tool.code.ToolExecuteResult;

/**
 * <Description> 工具定义接口，提供统一的工具定义方法 <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年7月1日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.jmanus.tool <br>
 */
public interface ToolCallBiFunctionDef<T> extends BiFunction<T, ToolContext, ToolExecuteResult> {

    /**
     * 获取工具组的名称
     * 
     * @return 返回工具的唯一标识名称
     */
    String getServiceGroup();

    /**
     * 获取工具的名称
     * 
     * @return 返回工具的唯一标识名称
     */
    String getName();

    /**
     * 获取工具的描述信息
     * 
     * @return 返回工具的功能描述
     */
    String getDescription();

    /**
     * 获取工具的参数定义模式
     * 
     * @return 返回 JSON 格式的参数定义模式
     */
    String getParameters();

    /**
     * 获取工具的输入类型
     * 
     * @return 返回工具接受的输入参数类型 Class
     */
    Class<T> getInputType();

    /**
     * 判断工具是否直接返回结果
     * 
     * @return 如果工具直接返回结果则返回 true，否则返回 false
     */
    boolean isReturnDirect();

    /**
     * 设置关联的 Agent 实例
     * 
     * @param planId 要关联的计划 ID
     */
    public void setPlanId(String planId);

    /**
     * 获取工具的当前状态字符串
     * 
     * @return 返回描述工具当前状态的字符串
     */
    String getCurrentToolStateString();

    /**
     * 清理指定 planId 的所有相关资源
     * 
     * @param planId 计划 ID
     */
    void cleanup(String planId);
}
