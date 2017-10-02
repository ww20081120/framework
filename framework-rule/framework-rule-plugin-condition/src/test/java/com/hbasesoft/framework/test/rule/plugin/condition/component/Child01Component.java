/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.test.rule.plugin.condition.component;

import org.springframework.stereotype.Component;

import com.hbasesoft.framework.rule.core.FlowBean;
import com.hbasesoft.framework.rule.core.FlowComponent;
import com.hbasesoft.framework.rule.core.FlowContext;
import com.hbasesoft.framework.test.rule.plugin.condition.TestFlowBean;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2017年9月4日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.test.rule.plugin.condition.component <br>
 */
@Component("Child01Component")
public class Child01Component implements FlowComponent {

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param flowBean
     * @param flowContext
     * @return
     * @throws Exception <br>
     */
    @Override
    public boolean process(FlowBean flowBean, FlowContext flowContext) throws Exception {
        System.out.println("我今年" + ((TestFlowBean) flowBean).getAge() + "岁了，我上小学了");
        return true;
    }

}
