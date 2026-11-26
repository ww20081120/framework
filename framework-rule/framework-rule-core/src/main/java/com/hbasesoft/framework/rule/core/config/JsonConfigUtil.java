/****************************************************************************************
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.rule.core.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.utils.Assert;
import com.hbasesoft.framework.common.utils.ContextHolder;
import com.hbasesoft.framework.common.utils.JsonUtil;
import com.hbasesoft.framework.rule.core.FlowComponent;

import lombok.NoArgsConstructor;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;

/**
 * <Description> <br>
 *
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2018年8月24日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.rule.core.config <br>
 */
@NoArgsConstructor
public final class JsonConfigUtil {

    /**
     * Description: 从 ObjectNode 解析流程配置 <br>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @param obj ObjectNode
     * @return FlowConfig
     */
    @SuppressWarnings("rawtypes")
    public static FlowConfig getFlowConfig(final ObjectNode obj) {

        TreeFlowConfig config = new TreeFlowConfig();

        String component = obj.has("component") ? obj.get("component").asText() : null;
        if (StringUtils.isNotEmpty(component)) {
            FlowComponent flowComponent = ContextHolder.getContext().getBean(component, FlowComponent.class);
            Assert.notNull(flowComponent, ErrorCodeDef.FLOW_COMPONENT_NOT_FOUND, component);
            config.setComponent(flowComponent);
        }

        String name = obj.has("name") ? obj.get("name").asText() : null;
        if (StringUtils.isEmpty(name)) {
            name = component;
        }
        config.setName(name);

        String version = obj.has("version") ? obj.get("version").asText() : null;
        if (StringUtils.isEmpty(version)) {
            version = "1.0";
        }

        if (obj.has("children") && obj.get("children").isArray()) {
            ArrayNode children = (ArrayNode) obj.get("children");
            if (children != null && children.size() > 0) {
                List<FlowConfig> childConfigList = new ArrayList<FlowConfig>();
                for (int i = 0, size = children.size(); i < size; i++) {
                    childConfigList.add(getFlowConfig((ObjectNode) children.get(i)));
                }
                config.setChildrenConfigList(childConfigList);
            }
        }

        // 构建剩余属性 Map（排除已知字段）
        Map<String, Object> attrMap = new HashMap<>();
        for (String key : obj.propertyNames()) {
            if (!"component".equals(key) && !"name".equals(key) && !"version".equals(key) && !"children".equals(key)) {
                attrMap.put(key, JsonUtil.treeToValue(obj.get(key), Object.class));
            }
        }
        config.setConfigAttrMap(attrMap);
        return config;
    }

    /**
     * Description: 从 Map 解析流程配置 <br>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @param map Map
     * @return FlowConfig
     */
    public static FlowConfig getFlowConfig(final Map<String, Object> map) {
        ObjectNode node = (ObjectNode) JsonUtil.valueToTree(map);
        return getFlowConfig(node);
    }
}
