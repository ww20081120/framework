/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.db.demo.dao;

import java.util.List;

import com.hbasesoft.framework.common.annotation.Key;
import com.hbasesoft.framework.db.Dao;
import com.hbasesoft.framework.db.core.DaoConstants;
import com.hbasesoft.framework.db.core.annotation.Sql;
import com.hbasesoft.framework.db.demo.entity.StudentEntity;
import com.hbasesoft.framework.db.hibernate.BaseJpaDao;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2019年4月3日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.db.demo.dao <br>
 */
@Dao
public interface IStudentDao extends BaseJpaDao<StudentEntity> {

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    void createTable();

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param courseName
     * @return <br>
     */
    @Sql("select count(1) from t_student_course sc, t_course c "
        + "where sc.course_id = c.id and sc.score >= 60 and c.course_name = :courseName")
    int countCoursePass(@Key("courseName") String courseName);

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    @Sql("select count(1) from t_student")
    int countStudentSize();

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param studentEntity
     * @param pageIndex
     * @param pageSize
     * @return <br>
     */
    @Sql
    List<StudentEntity> queryStudentCourse(@Key("entity") StudentEntity studentEntity,
        @Key(DaoConstants.PAGE_INDEX) int pageIndex, @Key(DaoConstants.PAGE_SIZE) int pageSize);
}
