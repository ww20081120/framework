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

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.ServiceException;
import com.hbasesoft.framework.common.utils.Assert;
import com.hbasesoft.framework.common.utils.ContextHolder;
import com.hbasesoft.framework.common.utils.PropertyHolder;
import com.hbasesoft.framework.rule.core.FlowComponent;
import com.hbasesoft.framework.rule.core.config.FlowConfig;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private Map<String, Component> nodeMap;

    /** 线 */
    private Map<String, List<String>> edgeMap;

    /** 当前的节点 */
    private String currentNode;

    /** 栈 */
    private Stack<String> stack;

    /** 深度 */
    private int depth;

    /** 最大深度 */
    private int maxDepth;

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param jsonRule
     * @return <br>
     */
    @SuppressWarnings("rawtypes")
    public static AntVFlowConfig parse(final JSONObject jsonRule) {
        AntVFlowConfig config = new AntVFlowConfig();
        config.nodeMap = new HashMap<>();
        config.edgeMap = new HashMap<>();
        config.stack = new Stack<>();
        config.depth = 1;
        config.maxDepth = PropertyHolder.getIntProperty("antvFlow.maxDepth", DEFAULT_MAX_DEPTH);

        JSONArray nodes = jsonRule.getJSONArray("nodes");
        Assert.isTrue(nodes != null && nodes.size() > 0, ErrorCodeDef.PARAM_NOT_NULL, "nodes节点");
        for (int i = 0, len = nodes.size(); i < len; i++) {
            JSONObject node = nodes.getJSONObject(i);
            if (node != null) {
                String component = node.getString("component");
                String id = node.getString("id");
                Assert.isTrue(StringUtils.isNotEmpty(component) && StringUtils.isNotEmpty(id),
                    ErrorCodeDef.PARAM_NOT_NULL, node.toJSONString() + "中component或者id节点");

                FlowComponent flowComponent = ContextHolder.getContext().getBean(component, FlowComponent.class);
                Assert.notNull(flowComponent, ErrorCodeDef.FLOW_COMPONENT_NOT_FOUND, component);
                config.nodeMap.put(id, new Component(flowComponent, node));

                boolean isStart = node.getBooleanValue("isStart");
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

        JSONArray edges = jsonRule.getJSONArray("edges");
        Assert.isTrue(edges != null && edges.size() > 0, ErrorCodeDef.PARAM_NOT_NULL, "edges节点");
        for (int i = 0, len = edges.size(); i < len; i++) {
            JSONObject edge = edges.getJSONObject(i);
            if (edge != null) {
                String source = edge.getString("source");
                String target = edge.getString("target");
                Assert.isTrue(StringUtils.isNotEmpty(source) && StringUtils.isNotEmpty(target),
                    ErrorCodeDef.PARAM_NOT_NULL, edge.toJSONString() + "中source或者target节点");

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
    public JSONObject getConfigAttrMap() {
        Component node = nodeMap.get(currentNode);
        return node == null ? null : node.getAttrs();
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
        private JSONObject attrs;
    }
}
