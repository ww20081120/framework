/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.rule.core.plugin;

import java.io.Serializable;
import java.util.Map.Entry;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import com.hbasesoft.framework.common.annotation.NoTracerLog;
import com.hbasesoft.framework.rule.core.AbstractFlowCompnentInterceptor;
import com.hbasesoft.framework.rule.core.FlowContext;

/**
 * <Description> 条件组合<br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2017年9月4日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.workflow.plugin.rule <br>
 */
@NoTracerLog
public class ConditionInterceptor extends AbstractFlowCompnentInterceptor {

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
    public boolean before(final Serializable flowBean, final FlowContext flowContext) {
        String condition = (String) flowContext.getFlowConfig().getConfigAttrMap().get("condition");
        if (StringUtils.isNotEmpty(condition)) {

            // 构造上下文：准备比如变量定义等等表达式运行需要的上下文数据
            EvaluationContext context = new StandardEvaluationContext(flowBean);

            if (MapUtils.isNotEmpty(flowContext.getExtendUtils())) {
                for (Entry<String, Object> util : flowContext.getExtendUtils().entrySet()) {
                    context.setVariable(util.getKey(), util.getValue());
                }
            }

            if (MapUtils.isNotEmpty(flowContext.getParamMap())) {
                for (Entry<String, Object> param : flowContext.getParamMap().entrySet()) {
                    context.setVariable(param.getKey(), param.getValue());
                }
            }

            // 创建解析器：提供SpelExpressionParser默认实现
            ExpressionParser parser = new SpelExpressionParser();

            // 解析表达式：使用ExpressionParser来解析表达式为相应的Expression对象
            Expression expression = parser.parseExpression(condition);

            Object value = expression.getValue(context);

            return value != null && "true".equals(value.toString().toLowerCase());
        }
        return true;
    }
}
