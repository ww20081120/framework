/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.shell.core.cmd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.beust.jcommander.JCommander;
import com.hbasesoft.framework.common.utils.ContextHolder;
import com.hbasesoft.framework.shell.core.CommandHandler;
import com.hbasesoft.framework.shell.core.Shell;
import com.hbasesoft.framework.shell.core.cmd.Help.Option;
import com.hbasesoft.framework.shell.core.vo.AbstractOption;

import lombok.Getter;
import lombok.Setter;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2019年7月3日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.vcc.tools.command <br>
 */
@Component
public class Help implements CommandHandler<Option> {

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param option <br>
     */
    @Override
    @SuppressWarnings("rawtypes")
    public void execute(final JCommander cmd, final Option option, final Shell shell) {
        // 加载所有命令
        Map<String, CommandHandler> commandHolder = ContextHolder.getContext().getBeansOfType(CommandHandler.class);
        String cmdStr = getCommands(commandHolder);
        shell.getOut().println(cmdStr);
    }

    @SuppressWarnings("rawtypes")
    private static String getCommands(final Map<String, CommandHandler> commandHolder) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n您可以输入以下命令:");
        List<String> cmds = new ArrayList<>(commandHolder.keySet());
        Collections.sort(cmds);
        for (String key : cmds) {
            sb.append('\n').append(key).append(' ').append(commandHolder.get(key));
        }
        return sb.toString();
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    @Override
    public String toString() {
        return "查看支持的命令";
    }

    @Getter
    @Setter
    public static class Option extends AbstractOption {
    }
}
