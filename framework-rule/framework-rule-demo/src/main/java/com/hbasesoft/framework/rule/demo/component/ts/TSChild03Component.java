/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.rule.demo.component.ts;

import java.util.List;

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
@Component("TSChild03Component")
public class TSChild03Component implements FlowComponent<TestFlowBean> {
   // @Resource
    private EmployeeRepository employeeDao;

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
        List<Employee> employeeList = employeeDao.findByAgeGreaterThan(20);
        for (Employee employee : employeeList) {
            employee.setAge(employee.getAge() + 1);
            employeeDao.update(employee);
        }
        return true;
    }
}
