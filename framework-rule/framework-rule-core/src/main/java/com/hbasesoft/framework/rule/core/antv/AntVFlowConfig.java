/****************************************************************************************
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.rule.core.antv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.apache.commons.lang3.StringUtils;

import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.ServiceException;
import com.hbasesoft.framework.common.utils.Assert;
import com.hbasesoft.framework.common.utils.ContextHolder;
import com.hbasesoft.framework.common.utils.JsonUtil;
import com.hbasesoft.framework.common.utils.PropertyHolder;
import com.hbasesoft.framework.rule.core.FlowComponent;
import com.hbasesoft.framework.rule.core.config.FlowConfig;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;

/**
 * <Description> <br>
 *
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2023年12月1日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.rule.core.antv <br>
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AntVFlowConfig implements FlowConfig {

    /** 流程最大深度 */
    private static final int DEFAULT_MAX_DEPTH = 100000;

    /**
     * serialVersionUID <br>
     */
    private static final long serialVersionUID = -5870298427253572010L;

    /** 配置表 */
    private transient Map<String, Component> nodeMap;

    /** 线 */
    private transient Map<String, List<String>> edgeMap;

    /** 当前的节点 */
    private transient String currentNode;

    /** 栈 */
    private transient Stack<String> stack;

    /** 深度 */
    private transient int depth;

    /** 最大深度 */
    private transient int maxDepth;

    /**
     * Description: <br>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @param jsonRule
     * @return <br>
     */
    @SuppressWarnings("rawtypes")
    public static AntVFlowConfig parse(final ObjectNode jsonRule) {
        AntVFlowConfig config = new AntVFlowConfig();
        config.nodeMap = new HashMap<>();
        config.edgeMap = new HashMap<>();
        config.stack = new Stack<>();
        config.depth = 1;
        config.maxDepth = PropertyHolder.getIntProperty("antvFlow.maxDepth", DEFAULT_MAX_DEPTH);

        JsonNode nodesNode = jsonRule.get("nodes");
        Assert.isTrue(nodesNode != null && nodesNode.isArray() && nodesNode.size() > 0,
            ErrorCodeDef.PARAM_NOT_NULL, "nodes节点");
        ArrayNode nodes = (ArrayNode) nodesNode;
        for (int i = 0, len = nodes.size(); i < len; i++) {
            ObjectNode node = (ObjectNode) nodes.get(i);
            if (node != null) {
                String component = node.has("component") ? node.get("component").asText() : null;
                String id = node.has("id") ? node.get("id").asText() : null;
                Assert.isTrue(StringUtils.isNotEmpty(component) && StringUtils.isNotEmpty(id),
                    ErrorCodeDef.PARAM_NOT_NULL, JsonUtil.toJson(node) + "中component或者id节点");

                FlowComponent flowComponent = ContextHolder.getContext().getBean(component, FlowComponent.class);
                Assert.notNull(flowComponent, ErrorCodeDef.FLOW_COMPONENT_NOT_FOUND, component);
                config.nodeMap.put(id, new Component(flowComponent, node));

                boolean isStart = node.has("isStart") && node.get("isStart").asBoolean(false);
                if (isStart) {
                    if (config.currentNode == null) {
                        config.currentNode = id;
                    }
                    else {
                        throw new ServiceException(ErrorCodeDef.PARAM_REPEAT, "开始节点");
                    }
                }
            }
        }

        JsonNode edgesNode = jsonRule.get("edges");
        Assert.isTrue(edgesNode != null && edgesNode.isArray() && edgesNode.size() > 0,
            ErrorCodeDef.PARAM_NOT_NULL, "edges节点");
        ArrayNode edges = (ArrayNode) edgesNode;
        for (int i = 0, len = edges.size(); i < len; i++) {
            ObjectNode edge = (ObjectNode) edges.get(i);
            if (edge != null) {
                String source = edge.has("source") ? edge.get("source").asText() : null;
                String target = edge.has("target") ? edge.get("target").asText() : null;
                Assert.isTrue(StringUtils.isNotEmpty(source) && StringUtils.isNotEmpty(target),
                    ErrorCodeDef.PARAM_NOT_NULL, JsonUtil.toJson(edge) + "中source或者target节点");

                if (StringUtils.isEmpty(config.currentNode)) {
                    config.currentNode = source;
                }

                List<String> list = config.edgeMap.get(source);
                if (list == null) {
                    list = new ArrayList<>();
                    config.edgeMap.put(source, list);
                }
                list.add(target);
            }
        }
        return config;
    }

    /**
     * Description: <br>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> getConfigAttrMap() {
        Component node = nodeMap.get(currentNode);
        if (node == null) {
            return null;
        }
        ObjectNode attrs = node.getAttrs();
        return JsonUtil.convertValue(attrs, Map.class);
    }

    /**
     * Description: <br>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    @Override
    public String getName() {
        return currentNode;
    }

    /**
     * Description: <br>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @param currentNode <br>
     */
    public void setCurrentNode(final String currentNode) {
        this.currentNode = currentNode;
    }

    /**
     * Description: <br>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @param <T>
     * @return <br>
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> FlowComponent<T> getComponent() {
        if (currentNode != null) {
            Component node = nodeMap.get(currentNode);
            if (node != null) {
                return node.getFlowComponent();
            }
        }
        return null;
    }

    /**
     * Description: <br>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @param lastSuccess <br>
     */
    public void setNext(final boolean lastSuccess) {
        Assert.isTrue(++depth <= maxDepth, ErrorCodeDef.FLOW_STACK_OVERFLOW, maxDepth);
        if (lastSuccess) {
            List<String> leges = edgeMap.get(currentNode);
            if (leges != null) {
                currentNode = leges.get(0);
                if (leges.size() > 1) {
                    stack.addAll(leges.subList(1, leges.size()));
                }
                return;
            }
        }
        if (stack.size() > 0) {
            currentNode = stack.pop();
        }
        else {
            currentNode = null;
        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    private static final class Component {
        /** flowComponent */
        @SuppressWarnings("rawtypes")
        private FlowComponent flowComponent;

        /** attrs */
        private ObjectNode attrs;
    }
}
