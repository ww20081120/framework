/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.rule.core.config;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.hbasesoft.framework.rule.core.FlowComponent;

import lombok.Getter;
import lombok.Setter;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2023年12月1日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.rule.core.config <br>
 */
@Getter
@Setter
public class TreeFlowConfig implements FlowConfig {

    /**
     * serialVersionUID <br>
     */
    private static final long serialVersionUID = 6738624692458576179L;

    /** configAttrMap */
    private JSONObject configAttrMap;

    /** hildren config list */
    private List<FlowConfig> childrenConfigList;

    /** name */
    private String name;

    /** version */
    private String version;

    /** component */
    @SuppressWarnings("rawtypes")
    private FlowComponent component;

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
