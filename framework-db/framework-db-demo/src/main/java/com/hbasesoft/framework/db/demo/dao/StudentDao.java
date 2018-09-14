/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.db.demo.dao;

import java.util.List;

import com.hbasesoft.framework.db.Dao;
import com.hbasesoft.framework.db.core.annotation.Param;
import com.hbasesoft.framework.db.core.annotation.Sql;
import com.hbasesoft.framework.db.demo.entity.StudentEntity;
import com.hbasesoft.framework.db.hibernate.IGenericBaseDao;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2018年9月13日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.db.demo.dao <br>
 */
@Dao
public interface StudentDao extends IGenericBaseDao {

    void createTable();

    @Sql("select count(1) from t_student_course sc, t_course c "
        + "where sc.course_id = c.id and sc.score >= 60 and c.course_name = :courseName")
    int countCoursePass(@Param("courseName") String courseName);

    @Sql("select count(1) from t_student")
    int countStudentSize();

    @Sql(bean = StudentEntity.class)
    List<StudentEntity> queryStudentCourse(@Param("entity") StudentEntity studentEntity,
        @Param(Param.PAGE_INDEX) int pageIndex, @Param(Param.PAGE_SIZE) int pageSize);
}
