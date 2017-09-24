/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.workflow.plugin.transaction.test.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hbasesoft.workflow.plugin.transaction.test.entity.Employee;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2017年9月5日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.workflow.plugin.transaction.test.repository <br>
 */
public interface EmployeeRepository extends JpaRepository<Employee, String> {

    List<Employee> findByName(String name);

    List<Employee> findByAgeGreaterThan(int age);

    @Modifying
    @Query("update Employee e set e.name = :#{#employee.name}, e.age = :#{#employee.age} where e.id = :#{#employee.id}")
    int update(@Param("employee") Employee employee);
}
