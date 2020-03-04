/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.rule.core.config;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
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
public class FlowConfig implements Serializable {

    /**
     * serialVersionUID <br>
     */
    private static final long serialVersionUID = 6738624692458576179L;

    /** name */
    private String name;

    /** version */
    private String version;

    /** component */
    @SuppressWarnings("rawtypes")
    private FlowComponent component;

    /** configAttrMap */
    private Map<String, Object> configAttrMap;

    /** hildren config list */
    private List<FlowConfig> childrenConfigList;

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    @SuppressWarnings("rawtypes")
    public FlowComponent getComponent() {
        return component;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param component <br>
     */
    public void setComponent(final FlowComponent<?> component) {
        this.component = component;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public List<FlowConfig> getChildrenConfigList() {
        return childrenConfigList;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param childrenConfigList <br>
     */
    public void setChildrenConfigList(final List<FlowConfig> childrenConfigList) {
        this.childrenConfigList = childrenConfigList;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public Map<String, Object> getConfigAttrMap() {
        return configAttrMap;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param configAttrMap <br>
     */
    public void setConfigAttrMap(final Map<String, Object> configAttrMap) {
        this.configAttrMap = configAttrMap;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public String getName() {
        return name;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param name <br>
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public String getVersion() {
        return version;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param version <br>
     */
    public void setVersion(final String version) {
        this.version = version;
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
        return JSONObject.toJSONStringWithDateFormat(this, "yyyy-MM-dd HH:mm:ss", new SerializerFeature[0]);
    }
}
