/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.shell.core.cmd;

import org.springframework.stereotype.Component;

import com.beust.jcommander.JCommander;
import com.hbasesoft.framework.shell.core.CommandHandler;
import com.hbasesoft.framework.shell.core.Shell;
import com.hbasesoft.framework.shell.core.cmd.Exit.Option;
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
public class Exit implements CommandHandler<Option> {

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param options <br>
     */
    @Override
    public void execute(JCommander cmd, Option option, Shell shell) {
        shell.exit();
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
        return "退出系统";
    }

    @Getter
    @Setter
    public static class Option extends AbstractOption {
    }
}
