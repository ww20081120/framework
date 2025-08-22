/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.demo.embabel;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import com.embabel.agent.api.common.autonomy.AgentProcessExecution;
import com.embabel.agent.api.common.autonomy.Autonomy;
import com.embabel.agent.core.ProcessOptions;
import com.embabel.agent.core.Verbosity;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月21日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.demo.embabel <br>
 */
@ShellComponent
public class AgentCommands {

    private final Autonomy autonomy;

    public AgentCommands(Autonomy autonomy) {
        this.autonomy = autonomy;
    }

    @ShellMethod(key = "solve", value = "使用 Embabel 代理解决编码任务")
    public String solveCodingTask(@ShellOption(help = "编码任务描述") String task) {
        try {
            // 创建 ProcessOptions
            ProcessOptions processOptions = ProcessOptions.builder()
                .verbosity(Verbosity.builder()
                    .showPlanning(true)
                    .build())
                .build();
            
            // 使用 Autonomy 执行任务
            AgentProcessExecution result = autonomy.chooseAndRunAgent(task, processOptions);

            // 返回结果
            return "任务处理完成:\n" + result.toString();
        } catch (Exception e) {
            return "处理任务时出错: " + e.getMessage();
        }
    }

    @ShellMethod(key = "help", value = "显示可用命令")
    public String help() {
        return """
                可用命令:
                solve --task "<description>"  - 解决编码任务
                help                          - 显示此帮助信息
                exit                          - 退出 shell
                
                示例:
                solve --task "创建一个简单的计算器 Java 类"
                """;
    }
}

