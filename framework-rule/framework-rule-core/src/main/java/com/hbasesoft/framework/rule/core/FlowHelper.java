/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.rule.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ServiceLoader;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;

import com.alibaba.fastjson.JSONObject;
import com.hbasesoft.framework.common.ErrorCode;
import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.FrameworkException;
import com.hbasesoft.framework.common.utils.Assert;
import com.hbasesoft.framework.common.utils.ContextHolder;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;
import com.hbasesoft.framework.rule.core.config.FlowConfig;
import com.hbasesoft.framework.rule.core.config.FlowLoader;
import com.hbasesoft.framework.rule.core.config.JsonConfigUtil;
import com.hbasesoft.framework.rule.core.config.TreeFlowConfig;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2017年9月2日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.workflow.core <br>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FlowHelper {

    /** serviceLoader */
    private ServiceLoader<FlowLoader> serviceLoader;

    /** interceptors */
    private List<FlowComponentInterceptor> interceptors;

    /** reverseInterceptors */
    private List<FlowComponentInterceptor> reverseInterceptors;

    /** FlowHelper */
    private static FlowHelper flowHelper;

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public static FlowHelper getFlowHelper() {
        if (flowHelper == null) {
            flowHelper = new FlowHelper();
        }
        return flowHelper;
    }

    /**
     * Description: 支持通过json方式启动流程<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param bean
     * @param flowConfig
     * @param throwable
     * @param <T> T
     * @return <br>
     */
    public static <T extends Serializable> ErrorCode flowStart(final T bean, final JSONObject flowConfig,
        final boolean throwable) {

        Assert.notNull(bean, ErrorCodeDef.PARAM_NOT_NULL, "FlowBean");

        ErrorCode result = ErrorCodeDef.SUCCESS;

        Assert.notNull(flowConfig, ErrorCodeDef.FLOW_NOT_MATCH, bean);

        FlowConfig config = JsonConfigUtil.getFlowConfig(flowConfig);
        Assert.notNull(config, ErrorCodeDef.FLOW_NOT_MATCH, bean);

        try {
            execute(bean, new FlowContext(config));
        }
        catch (Exception e) {
            LoggerUtil.error("flow process error.", e);
            FrameworkException fe = e instanceof FrameworkException ? (FrameworkException) e
                : new FrameworkException(e);
            if (throwable) {
                throw fe;
            }
            result = fe.getCode();
        }
        return result;

    }

    /**
     * Description: 支持读取本地配置文件的方式启动流程 <br>
     * <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param bean
     * @param flowName
     * @param throwable
     * @param <T> T
     * @return <br>
     */
    public static <T extends Serializable> ErrorCode flowStart(final T bean, final String flowName,
        final boolean throwable) {
        Assert.notNull(bean, ErrorCodeDef.PARAM_NOT_NULL, "FlowBean");

        ErrorCode result = ErrorCodeDef.SUCCESS;

        // match flow config
        FlowConfig config = getFlowHelper().match(flowName);
        Assert.notNull(config, ErrorCodeDef.FLOW_NOT_MATCH, bean);

        try {
            execute(bean, new FlowContext(config));
        }
        catch (Exception e) {
            LoggerUtil.error("flow process error.", e);
            FrameworkException fe = e instanceof FrameworkException ? (FrameworkException) e
                : new FrameworkException(e);
            if (throwable) {
                throw fe;
            }
            result = fe.getCode();
        }
        return result;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param bean
     * @param flowName
     * @param <T> T
     * @return <br>
     */
    public static <T extends Serializable> ErrorCode flowStart(final T bean, final String flowName) {
        return flowStart(bean, flowName, false);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param bean
     * @param flowConfig
     * @param <T> T
     * @return <br>
     */
    public static <T extends Serializable> ErrorCode flowStart(final T bean, final JSONObject flowConfig) {
        return flowStart(bean, flowConfig, false);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param flowBean
     * @param flowContext
     * @param <T> T
     * @throws Exception <br>
     */
    @SuppressWarnings("unchecked")
    public static <T extends Serializable> void execute(final T flowBean, final FlowContext flowContext)
        throws Exception {
        TreeFlowConfig flowConfig = (TreeFlowConfig) flowContext.getFlowConfig();
        Assert.notNull(flowConfig, ErrorCodeDef.FLOW_COMPONENT_NOT_FOUND);

        FlowComponent<T> component = null;

        FlowHelper helper = getFlowHelper();

        // 执行拦截器前置拦截
        if (helper.before(flowBean, flowContext)) {
            try {

                // 获取流程组件，存在则执行流程组件
                component = flowConfig.getComponent();
                boolean flag = true;
                if (component != null) {
                    // 执行流程组件
                    flag = component.process(flowBean, flowContext);
                }

                // 是否执行流程组件中的子流程
                if (flag) {
                    List<FlowConfig> list = flowConfig.getChildrenConfigList();
                    try {
                        if (CollectionUtils.isNotEmpty(list)) {
                            for (FlowConfig childConfig : list) {
                                flowContext.setFlowConfig(childConfig);
                                execute(flowBean, flowContext);

                            }
                        }
                        if (component != null) {
                            // 执行组件后置拦截
                            component.afterProcess(flowBean, flowContext, null);
                        }
                    }
                    catch (Exception e) {
                        if (component != null) {
                            component.afterProcess(flowBean, flowContext, e);
                        }
                        throw e;
                    }

                }
                flowContext.setFlowConfig(flowConfig);

                // 执行后置拦截
                helper.after(flowBean, flowContext);
            }
            catch (Exception e) {
                flowContext.setFlowConfig(flowConfig);
                // 执行异常拦截
                helper.error(e, flowBean, flowContext);
                throw e;
            }
        }
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param flowName
     * @return <br>
     */
    protected FlowConfig match(final String flowName) {
        if (serviceLoader == null) {
            serviceLoader = ServiceLoader.load(FlowLoader.class);
        }

        FlowConfig flowConfig = null;
        for (FlowLoader flowLoader : serviceLoader) {
            flowConfig = flowLoader.load(flowName);
            if (flowConfig != null) {
                break;
            }
        }
        return flowConfig;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param fb
     * @param fc
     * @return <br>
     */
    public boolean before(final Serializable fb, final FlowContext fc) {
        List<FlowComponentInterceptor> its = loadInterceptor(false);
        if (CollectionUtils.isNotEmpty(its)) {
            for (FlowComponentInterceptor interceptor : its) {
                if (!interceptor.before(fb, fc)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param fb
     * @param fc <br>
     */
    public void after(final Serializable fb, final FlowContext fc) {
        List<FlowComponentInterceptor> its = loadInterceptor(true);
        if (CollectionUtils.isNotEmpty(its)) {
            for (FlowComponentInterceptor interceptor : its) {
                interceptor.after(fb, fc);
            }
        }
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param e
     * @param fb
     * @param fc <br>
     */
    public void error(final Exception e, final Serializable fb, final FlowContext fc) {
        List<FlowComponentInterceptor> its = loadInterceptor(true);
        if (CollectionUtils.isNotEmpty(its)) {
            for (FlowComponentInterceptor interceptor : its) {
                interceptor.error(e, fb, fc);
            }
        }
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param reverse
     * @return <br>
     */
    protected List<FlowComponentInterceptor> loadInterceptor(final boolean reverse) {
        if (CollectionUtils.isEmpty(interceptors)) {
            Map<String, FlowComponentInterceptor> interceptorMap = ContextHolder.getContext()
                .getBeansOfType(FlowComponentInterceptor.class);

            if (MapUtils.isNotEmpty(interceptorMap)) {
                interceptors = new ArrayList<FlowComponentInterceptor>();
                for (Entry<String, FlowComponentInterceptor> entry : interceptorMap.entrySet()) {
                    interceptors.add(entry.getValue());
                }
                Collections.sort(interceptors);
                reverseInterceptors = new ArrayList<FlowComponentInterceptor>();
                reverseInterceptors.addAll(interceptors);
                Collections.reverse(reverseInterceptors);
            }
        }
        return reverse ? reverseInterceptors : interceptors;
    }
}
