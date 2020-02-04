/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.rule.plugin.statemachine;

import java.io.Serializable;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2019年7月2日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.rule.plugin.statemachine <br>
 */
public interface StateFlowBean extends Serializable {

    /**
     * 
     * Description: 获取最后组件<br> 
     *  
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    String getLastComponent();

    /**
     * 
     * Description: 设置最后的组件<br> 
     *  
     * @author 王伟<br>
     * @taskId <br>
     * @param lastComponent <br>
     */
    void setLastComponent(String lastComponent);
}
