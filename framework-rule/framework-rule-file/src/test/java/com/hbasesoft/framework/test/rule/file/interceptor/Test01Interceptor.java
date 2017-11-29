/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.test.rule.file.interceptor;

import org.springframework.stereotype.Component;

import com.hbasesoft.framework.rule.core.AbstractFlowCompnentInterceptor;
import com.hbasesoft.framework.rule.core.FlowBean;
import com.hbasesoft.framework.rule.core.FlowContext;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2017年9月4日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.test.rule.file.interceptor <br>
 */
@Component
public class Test01Interceptor extends AbstractFlowCompnentInterceptor {

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
        System.out.println(flowContext.getFlowConfig().getName() + "===before 01=====" + flowBean + flowContext);
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
        System.out.println(flowContext.getFlowConfig().getName() + "===after 01=====" + flowBean + flowContext);
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
        System.out.println(flowContext.getFlowConfig().getName() + "===throw 01=====" + flowBean + flowContext);
    }

}
