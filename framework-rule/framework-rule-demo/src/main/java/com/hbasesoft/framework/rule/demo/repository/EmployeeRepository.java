/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.rule.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hbasesoft.framework.rule.demo.bean.Employee;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2017年9月5日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.rule.demo.repository <br>
 */
public interface EmployeeRepository extends JpaRepository<Employee, String> {

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param name
     * @return <br>
     */
    List<Employee> findByName(String name);

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param age
     * @return <br>
     */
    List<Employee> findByAgeGreaterThan(int age);

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param employee
     * @return <br>
     */
    @Modifying
    @Query(nativeQuery = true)
    int update(@Param("entity") Employee employee);
}
