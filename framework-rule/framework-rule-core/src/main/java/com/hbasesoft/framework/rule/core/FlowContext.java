/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.rule.core;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.hbasesoft.framework.rule.core.config.FlowConfig;

import lombok.AllArgsConstructor;

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

    private Map<String, Object> extendUtils;

    private Map<String, Object> paramMap;

    private Exception exception;

    public FlowContext(FlowConfig flowConfig, Map<String, Object> extendUtils, Map<String, Object> paramMap) {
        this.flowConfig = flowConfig;
        this.extendUtils = extendUtils;
        this.paramMap = paramMap;
    }

    public FlowContext(FlowConfig flowConfig) {
        this.flowConfig = flowConfig;
        this.paramMap = new HashMap<String, Object>();
        this.extendUtils = new HashMap<String, Object>();
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

    public void addExtendUtil(String utilName, Object util) {
        this.extendUtils.put(utilName, util);
    }

    public FlowConfig getFlowConfig() {
        return this.flowConfig;
    }

    public Map<String, Object> getParamMap() {
        return this.paramMap;
    }

    public Map<String, Object> getExtendUtils() {
        return this.extendUtils;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }
}
