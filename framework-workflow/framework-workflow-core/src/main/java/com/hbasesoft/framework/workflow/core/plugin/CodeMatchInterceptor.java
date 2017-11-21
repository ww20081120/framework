/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.workflow.core.plugin;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;

import org.springframework.util.Assert;

import com.hbasesoft.framework.common.ServiceException;
import com.hbasesoft.framework.common.utils.CommonUtil;
import com.hbasesoft.framework.workflow.core.FlowBean;
import com.hbasesoft.framework.workflow.core.FlowComponentInterceptor;
import com.hbasesoft.framework.workflow.core.FlowContext;

/**
 * <Description> 条件组合<br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2017年9月4日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.test.rule.plugin.condition <br>
 */
public class CodeMatchInterceptor implements FlowComponentInterceptor {

    private String[] codes;

    public CodeMatchInterceptor() {
    }

    public CodeMatchInterceptor(String... codes) {
        this.codes = codes;
    }

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
        Map<String, Object> attrMap = flowContext.getFlowConfig().getConfigAttrMap();
        return !Arrays.stream(codes).anyMatch(code -> {
            // 匹配包含 code ，而且值匹配不上的
            return attrMap.containsKey(code) && !CommonUtil.match((String) attrMap.get(code), getAttr(code, flowBean));
        });
    }

    private String getAttr(String code, FlowBean flowBean) {
        try {
            PropertyDescriptor pd = new PropertyDescriptor(code, flowBean.getClass());
            Method rM = pd.getReadMethod();
            Assert.notNull(rM, code + "对应的get方法不存在");
            return (String) rM.invoke(flowBean);
        }
        catch (Exception e) {
            throw new ServiceException(e);
        }

    }

    public String[] getCodes() {
        return codes;
    }

    public void setCodes(String[] codes) {
        this.codes = codes;
    }

}
