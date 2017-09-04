/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.workflow.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ServiceLoader;

import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.FrameworkException;
import com.hbasesoft.framework.common.utils.Assert;
import com.hbasesoft.framework.common.utils.CommonUtil;
import com.hbasesoft.framework.common.utils.ContextHolder;
import com.hbasesoft.framework.common.utils.logger.Logger;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;
import com.hbasesoft.framework.workflow.core.config.FlowConfig;
import com.hbasesoft.framework.workflow.core.config.FlowLoader;

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

    private static Logger logger = new Logger("workflowLogger");

    private static ServiceLoader<FlowLoader> serviceLoader;

    private static List<FlowComponentInterceptor> interceptors;

    public static int flowStart(FlowBean bean, String flowName) {
        Assert.notNull(bean, ErrorCodeDef.NOT_NULL, "FlowBean");

        long beginTime = System.currentTimeMillis();
        int result = ErrorCodeDef.SUCCESS;
        String transId = bean.getTransId();
        if (CommonUtil.isEmpty(transId)) {
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
        if (before(flowBean, flowContext)) {
            try {
                FlowComponent component = flowConfig.getComponent();

                boolean flag = true;
                if (component != null) {
                    flag = component.process(flowBean, flowContext);
                }

                if (flag) {
                    List<FlowConfig> list = flowConfig.getChildrenConfigList();
                    if (CommonUtil.isNotEmpty(list)) {
                        for (FlowConfig childConfig : list) {
                            flowContext.setFlowConfig(childConfig);
                            execute(flowBean, flowContext);
                        }
                    }
                }
                flowContext.setFlowConfig(flowConfig);
                after(flowBean, flowContext);
            }
            catch (Exception e) {
                flowContext.setFlowConfig(flowConfig);
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
        List<FlowComponentInterceptor> interceptors = loadInterceptor();
        if (CommonUtil.isNotEmpty(interceptors)) {
            for (FlowComponentInterceptor interceptor : interceptors) {
                if (!interceptor.before(flowBean, flowContext)) {
                    return false;
                }
            }
        }
        return true;
    }

    private static void after(FlowBean flowBean, FlowContext flowContext) {
        List<FlowComponentInterceptor> interceptors = loadInterceptor();
        if (CommonUtil.isNotEmpty(interceptors)) {
            for (FlowComponentInterceptor interceptor : interceptors) {
                interceptor.after(flowBean, flowContext);
            }
        }
    }

    private static void error(Exception e, FlowBean flowBean, FlowContext flowContext) {
        List<FlowComponentInterceptor> interceptors = loadInterceptor();
        if (CommonUtil.isNotEmpty(interceptors)) {
            for (FlowComponentInterceptor interceptor : interceptors) {
                interceptor.error(e, flowBean, flowContext);
            }
        }
    }

    private static List<FlowComponentInterceptor> loadInterceptor() {
        if (CommonUtil.isEmpty(interceptors)) {
            Map<String, FlowComponentInterceptor> interceptorMap = ContextHolder.getContext()
                .getBeansOfType(FlowComponentInterceptor.class);

            if (CommonUtil.isNotEmpty(interceptorMap)) {
                interceptors = new ArrayList<FlowComponentInterceptor>();
                for (Entry<String, FlowComponentInterceptor> entry : interceptorMap.entrySet()) {
                    interceptors.add(entry.getValue());
                }
                Collections.sort(interceptors, (s1, s2) -> {
                    return s1.order() - s2.order();
                });
            }
        }
        return interceptors;
    }

}
