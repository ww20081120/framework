/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.shell.core.vo;

import com.beust.jcommander.Parameter;

import lombok.Getter;
import lombok.Setter;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2019年11月8日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.vcc.tools.vo <br>
 */
@Getter
@Setter
public abstract class AbstractOption {

    /** */
    @Parameter(names = {
        "--help", "-h"
    }, help = true, order = 0, description = "帮助")

    private boolean help;

    /** 默认参数 */
    private String defaultParams;

}
