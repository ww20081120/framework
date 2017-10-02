/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.test.rule.plugin.transaction.component;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.hbasesoft.framework.rule.core.FlowBean;
import com.hbasesoft.framework.rule.core.FlowComponent;
import com.hbasesoft.framework.rule.core.FlowContext;
import com.hbasesoft.framework.rule.plugin.transaction.entity.Employee;
import com.hbasesoft.framework.test.rule.plugin.transaction.TestFlowBean;
import com.hbasesoft.framework.test.rule.plugin.transaction.repository.EmployeeRepository;

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
@Component("Child02Component")
public class Child02Component implements FlowComponent {
    @Resource
    private EmployeeRepository employeeDao;

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
        TestFlowBean testFlowBean = (TestFlowBean) flowBean;
        List<Employee> employeeList = employeeDao.findByName(testFlowBean.getName());
        for (Employee employee : employeeList) {
            employee.setName(employee.getName() + 1);
            employeeDao.update(employee);
        }
        return true;
    }
}
