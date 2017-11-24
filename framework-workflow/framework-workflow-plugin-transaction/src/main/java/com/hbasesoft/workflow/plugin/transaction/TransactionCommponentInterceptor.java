/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.workflow.plugin.transaction;

import java.util.Map;
import java.util.Stack;

import javax.annotation.Resource;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.ServiceException;
import com.hbasesoft.framework.common.utils.CommonUtil;
import com.hbasesoft.framework.workflow.core.AbstractFlowCompnentInterceptor;
import com.hbasesoft.framework.workflow.core.FlowBean;
import com.hbasesoft.framework.workflow.core.FlowContext;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2017年9月4日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.workflow.plugin.transaction <br>
 */
public class TransactionCommponentInterceptor extends AbstractFlowCompnentInterceptor {

    @Resource(name = "transactionManager")
    private PlatformTransactionManager platformTransactionManager;

    private ThreadLocal<Stack<TransactionStatus>> statusHolder = new ThreadLocal<Stack<TransactionStatus>>() {
        protected synchronized Stack<TransactionStatus> initialValue() {
            return new Stack<TransactionStatus>();
        }
    };

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param flowBean
     * @param flowContext
     * @return <br>
     */
    @Override
    public boolean before(FlowBean flowBean, FlowContext flowContext) {
        Map<String, Object> configAttrMap = flowContext.getFlowConfig().getConfigAttrMap();

        String transactional = (String) configAttrMap.get("transactional");
        if ("true".equals(transactional)) {
            DefaultTransactionDefinition def = new DefaultTransactionDefinition();

            String propagation = (String) configAttrMap.get("propagation");
            if (CommonUtil.isNotEmpty(propagation)) {
                switch (propagation) {
                    case "PROPAGATION_REQUIRED":
                        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
                        break;
                    case "PROPAGATION_REQUIRES_NEW":
                        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
                        break;
                    case "PROPAGATION_SUPPORTS":
                        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_SUPPORTS);
                        break;
                    case "PROPAGATION_NOT_SUPPORTED":
                        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_NOT_SUPPORTED);
                        break;
                    case "PROPAGATION_MANDATORY":
                        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_MANDATORY);
                        break;
                    case "PROPAGATION_NESTED":
                        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_NESTED);
                        break;
                    default:
                        throw new ServiceException(ErrorCodeDef.UNSUPPORT_TRASACTION_TYPE, propagation);
                }
            }

            if ("true".equals(configAttrMap.get("readOnly"))) {
                def.setReadOnly(true);
            }

            statusHolder.get().push(platformTransactionManager.getTransaction(def));
        }
        return true;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param flowBean
     * @param flowContext <br>
     */
    @Override
    public void after(FlowBean flowBean, FlowContext flowContext) {
        Map<String, Object> configAttrMap = flowContext.getFlowConfig().getConfigAttrMap();

        String transactional = (String) configAttrMap.get("transactional");
        if ("true".equals(transactional)) {
            TransactionStatus status = statusHolder.get().isEmpty() ? null : statusHolder.get().pop();
            if (status != null) {
                platformTransactionManager.commit(status);
            }
        }
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param e
     * @param flowBean
     * @param flowContext <br>
     */
    @Override
    public void error(Exception e, FlowBean flowBean, FlowContext flowContext) {
        Map<String, Object> configAttrMap = flowContext.getFlowConfig().getConfigAttrMap();

        String transactional = (String) configAttrMap.get("transactional");
        if ("true".equals(transactional)) {
            TransactionStatus status = statusHolder.get().isEmpty() ? null : statusHolder.get().pop();
            if (status != null) {
                platformTransactionManager.rollback(status);
            }
        }
    }

}
