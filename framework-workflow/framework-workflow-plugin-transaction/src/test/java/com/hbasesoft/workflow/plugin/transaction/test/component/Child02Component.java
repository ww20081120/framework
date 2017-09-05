/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.workflow.plugin.transaction.test.component;

import java.util.List;

import javax.annotation.Resource;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Component;

import com.hbasesoft.framework.workflow.core.FlowBean;
import com.hbasesoft.framework.workflow.core.FlowComponent;
import com.hbasesoft.framework.workflow.core.FlowContext;
import com.hbasesoft.workflow.plugin.transaction.test.TestFlowBean;
import com.hbasesoft.workflow.plugin.transaction.test.dao.EmployeeDao;
import com.hbasesoft.workflow.plugin.transaction.test.pojo.Employee;

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
    private EmployeeDao employeeDao;

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
        DetachedCriteria criteria = DetachedCriteria.forClass(Employee.class);
        criteria.add(Restrictions.eq("name", testFlowBean.getName()));
        List<Employee> employeeList = employeeDao.getListByCriteriaQuery(criteria);
        for (Employee employee : employeeList) {
            employee.setName(employee.getName() + 1);
            employeeDao.updateEntity(employee);
        }
        return true;
    }
}
