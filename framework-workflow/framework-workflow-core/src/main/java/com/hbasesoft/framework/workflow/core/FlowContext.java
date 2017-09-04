/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.workflow.core;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.hbasesoft.framework.workflow.core.config.FlowConfig;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2017年9月4日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.workflow.core <br>
 */
public class FlowContext implements Serializable {

    /**
     * serialVersionUID <br>
     */
    private static final long serialVersionUID = 7704217056875708410L;

    private FlowConfig flowConfig;

    private Map<String, Object> paramMap;

    public FlowContext(FlowConfig flowConfig) {
        this.flowConfig = flowConfig;
        this.paramMap = new HashMap<String, Object>();
    }

    public void setAttribute(String key, Object value) {
        this.paramMap.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T getAttribute(String key) {
        return (T) this.paramMap.get(key);
    }

    public void setFlowConfig(FlowConfig flowConfig) {
        this.flowConfig = flowConfig;
    }

    public FlowConfig getFlowConfig() {
        return this.flowConfig;
    }
    

    @Override
    public String toString() {
        return JSONObject.toJSONStringWithDateFormat(this, "yyyy-MM-dd HH:mm:ss", new SerializerFeature[0]);
    }
}
