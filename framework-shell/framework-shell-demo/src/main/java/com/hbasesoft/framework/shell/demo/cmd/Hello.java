/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.shell.demo.cmd;

import org.springframework.stereotype.Component;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.hbasesoft.framework.shell.core.CommandHandler;
import com.hbasesoft.framework.shell.core.Shell;
import com.hbasesoft.framework.shell.core.vo.AbstractOption;
import com.hbasesoft.framework.shell.demo.cmd.Hello.HelloWordOption;

import lombok.Getter;
import lombok.Setter;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate Mar 4, 2022 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.shell.demo.cmd <br>
 */
@Component
public class Hello implements CommandHandler<HelloWordOption> {

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    @Override
    public String toString() {
        return "演示命令";
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param cmd
     * @param option
     * @param shell <br>
     */
    @Override
    public void execute(JCommander cmd, HelloWordOption option, Shell shell) {
        shell.out.println("hello world! " + option.testParam);
    }

    @Getter
    @Setter
    public static class HelloWordOption extends AbstractOption {

        @Parameter(names = {
            "--test", "-t"
        }, help = true, order = 1, description = "测试参数")
        private String testParam;
    }
}
