/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.workflow.core.config;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.hbasesoft.framework.workflow.core.FlowComponent;

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
public class FlowConfig implements Serializable {

    /**
     * serialVersionUID <br>
     */
    private static final long serialVersionUID = 6738624692458576179L;

    private String name;

    private String version;

    private FlowComponent component;

    private Map<String, String> configAttrMap;

    private List<FlowConfig> childrenConfigList;

    public FlowComponent getComponent() {
        return component;
    }

    public void setComponent(FlowComponent component) {
        this.component = component;
    }

    public List<FlowConfig> getChildrenConfigList() {
        return childrenConfigList;
    }

    public void setChildrenConfigList(List<FlowConfig> childrenConfigList) {
        this.childrenConfigList = childrenConfigList;
    }

    public Map<String, String> getConfigAttrMap() {
        return configAttrMap;
    }

    public void setConfigAttrMap(Map<String, String> configAttrMap) {
        this.configAttrMap = configAttrMap;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return JSONObject.toJSONStringWithDateFormat(this, "yyyy-MM-dd HH:mm:ss", new SerializerFeature[0]);
    }
}
