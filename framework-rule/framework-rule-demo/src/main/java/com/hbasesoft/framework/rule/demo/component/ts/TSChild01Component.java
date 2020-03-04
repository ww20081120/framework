/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.rule.demo.component.ts;

import org.springframework.stereotype.Component;

import com.hbasesoft.framework.rule.core.FlowComponent;
import com.hbasesoft.framework.rule.core.FlowContext;
import com.hbasesoft.framework.rule.demo.bean.Employee;
import com.hbasesoft.framework.rule.demo.bean.TestFlowBean;
import com.hbasesoft.framework.rule.demo.repository.EmployeeRepository;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2017年9月4日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.workflow.plugin.rule.test.component <br>
 */
@Component("TSChild01Component")
public class TSChild01Component implements FlowComponent<TestFlowBean> {

    /** repository */
    // @Resource
    private EmployeeRepository employeeRepository;

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param testFlowBean
     * @param flowContext
     * @return b
     * @throws Exception <br>
     */
    @Override
    public boolean process(TestFlowBean testFlowBean, FlowContext flowContext) throws Exception {
        Employee employee = new Employee();
        employee.setName(testFlowBean.getName());
        employee.setAge(testFlowBean.getAge());
        employeeRepository.save(employee);
        return true;
    }

}
