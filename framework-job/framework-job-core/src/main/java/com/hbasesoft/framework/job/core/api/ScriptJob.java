/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.job.core.api;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2018年4月14日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.job.core.api <br>
 */
public interface ScriptJob extends com.dangdang.ddframe.job.api.script.ScriptJob {

    /**
     * Description: 加载脚本 <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    String loadScript();
}
