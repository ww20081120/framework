/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.shell.core;

import com.beust.jcommander.JCommander;
import com.hbasesoft.framework.shell.core.vo.AbstractOption;

/**
 * <Description> <br>
 * 
 * @param <T>
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2019年7月3日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.vcc.tools.command <br>
 */
public interface CommandHandler<T extends AbstractOption> {

    /**
     * @Method execute
     * @param cmd
     * @param option
     * @param shell
     * @Author 李煜龙
     * @Description TODD
     * @Date 2023/1/29 10:49
     */
    void execute(JCommander cmd, T option, Shell shell);

    /**
     * Description: 是否有参数<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    default boolean hasParam() {
        return true;
    }
}
