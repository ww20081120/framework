/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.rule.core;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ServiceLoader;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;

import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.FrameworkException;
import com.hbasesoft.framework.common.utils.Assert;
import com.hbasesoft.framework.common.utils.CommonUtil;
import com.hbasesoft.framework.common.utils.ContextHolder;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;
import com.hbasesoft.framework.log.core.TransLogUtil;
import com.hbasesoft.framework.rule.core.config.FlowConfig;
import com.hbasesoft.framework.rule.core.config.FlowLoader;

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
public final class FlowHelper {

    private static ServiceLoader<FlowLoader> serviceLoader;

    private static List<FlowComponentInterceptor> interceptors;

    private static List<FlowComponentInterceptor> reverseInterceptors;

    private static Method processMethod;

    public static int flowStart(FlowBean bean, String flowName) {
        Assert.notNull(bean, ErrorCodeDef.NOT_NULL, "FlowBean");

        int result = ErrorCodeDef.SUCCESS;
        String transId = bean.getTransId();
        if (StringUtils.isEmpty(transId)) {
            transId = CommonUtil.getTransactionID();
            bean.setTransId(transId);
        }

        // match flow config
        FlowConfig config = match(flowName);
        Assert.notNull(config, ErrorCodeDef.FLOW_NOT_MATCH, bean);

        try {
            execute(bean, new FlowContext(config));
        }
        catch (Exception e) {
            LoggerUtil.error("flow process error.", e);
            result = (e instanceof FrameworkException) ? ((FrameworkException) e).getCode()
                : ErrorCodeDef.SYSTEM_ERROR_10001;
        }
        return result;
    }

    private static void execute(FlowBean flowBean, FlowContext flowContext) throws Exception {
        FlowConfig flowConfig = flowContext.getFlowConfig();
        FlowComponent component = null;

        // 执行拦截器前置拦截
        if (before(flowBean, flowContext)) {
            try {

                // 获取流程组件，存在则执行流程组件
                component = flowConfig.getComponent();
                boolean flag = true;
                if (component != null) {
                    // 打印前置日志
                    TransLogUtil.before(component, getMethod(), new Object[] {
                        flowBean, flowContext
                    });

                    // 执行流程组件
                    flag = component.process(flowBean, flowContext);
                }

                // 是否执行流程组件中的子流程
                if (flag) {
                    List<FlowConfig> list = flowConfig.getChildrenConfigList();
                    if (CollectionUtils.isNotEmpty(list)) {
                        for (FlowConfig childConfig : list) {
                            flowContext.setFlowConfig(childConfig);
                            execute(flowBean, flowContext);
                        }
                    }
                }
                flowContext.setFlowConfig(flowConfig);

                if (component != null) {
                    // 打印后置日志
                    TransLogUtil.afterReturning(component, getMethod(), flag);
                }

                // 执行后置拦截
                after(flowBean, flowContext);
            }
            catch (Exception e) {
                flowContext.setFlowConfig(flowConfig);
                if (component != null) {

                    // 打印错误日志
                    TransLogUtil.afterThrowing(component, getMethod(), e);
                }

                // 执行异常拦截
                error(e, flowBean, flowContext);
                throw e;
            }
        }
    }

    private static FlowConfig match(String flowName) {
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

    private static boolean before(FlowBean flowBean, FlowContext flowContext) {
        List<FlowComponentInterceptor> interceptors = loadInterceptor(false);
        if (CollectionUtils.isNotEmpty(interceptors)) {
            for (FlowComponentInterceptor interceptor : interceptors) {
                if (!interceptor.before(flowBean, flowContext)) {
                    return false;
                }
            }
        }
        return true;
    }

    private static void after(FlowBean flowBean, FlowContext flowContext) {
        List<FlowComponentInterceptor> interceptors = loadInterceptor(true);
        if (CollectionUtils.isNotEmpty(interceptors)) {
            for (FlowComponentInterceptor interceptor : interceptors) {
                interceptor.after(flowBean, flowContext);
            }
        }
    }

    private static void error(Exception e, FlowBean flowBean, FlowContext flowContext) {
        List<FlowComponentInterceptor> interceptors = loadInterceptor(true);
        if (CollectionUtils.isNotEmpty(interceptors)) {
            for (FlowComponentInterceptor interceptor : interceptors) {
                interceptor.error(e, flowBean, flowContext);
            }
        }
    }

    private static List<FlowComponentInterceptor> loadInterceptor(boolean reverse) {
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

    private static Method getMethod() throws NoSuchMethodException, SecurityException {
        if (processMethod == null) {
            processMethod = FlowComponent.class.getDeclaredMethod("process", FlowBean.class, FlowContext.class);
        }
        return processMethod;
    }

}
