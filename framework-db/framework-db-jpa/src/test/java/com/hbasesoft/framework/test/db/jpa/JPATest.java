/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.test.db.jpa;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.hbasesoft.framework.boostrap.normal.Application;
import com.hbasesoft.framework.test.db.jpa.entity.Employee;
import com.hbasesoft.framework.test.db.jpa.repository.EmployeeRepository;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2017年9月23日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.test.db.jpa <br>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
public class JPATest {

    @Resource
    private EmployeeRepository employeeRepository;

    @Test
    public void findByName() {
        System.out.println(employeeRepository.findByName("小明"));
    }

    @Test
    @Transactional(readOnly = true)
    public void save() {
        Employee employee = new Employee();
        employee.setAge(10);
        employee.setName("小明1");
        employeeRepository.save(employee);
    }

    @Test
    public void queryEmployee() {
        System.out.println(employeeRepository.queryEmployee());
        System.out.println(employeeRepository.queryEmployee());
        System.out.println(employeeRepository.queryEmployee());
        System.out.println(employeeRepository.queryEmployee());
    }

}
