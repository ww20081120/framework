/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.rule.core.config;

import java.io.Serializable;

import com.alibaba.fastjson2.JSONObject;
import com.hbasesoft.framework.rule.core.FlowComponent;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2017年9月2日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.workflow.core.config <br>
 */
public interface FlowConfig extends Serializable {

    /**
     * Description: 获取配置Json <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    JSONObject getConfigAttrMap();

    /**
     * Description: 节点名称<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    String getName();

    /**
     * Description: 获取组件<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param <T>
     * @return <br>
     */
    <T> FlowComponent<T> getComponent();
}
