package com.hbasesoft.framework.db.demo.dao;

import com.hbasesoft.framework.db.core.BaseDao;
import com.hbasesoft.framework.db.demo.entity.StudentEntity;

import java.util.List;

/**
 * @version 1.0
 * @Package: com.hbasesoft.framework.db.demo.dao.mongodb
 * @ClassName: StudentDao
 * @Author: lhy
 * @Date: 2025/3/24 11:28
 * @Description: TODD
 */


public interface StudentDao extends BaseDao<StudentEntity> {

    /**
     *
     */
    void createTable();

    /**
     *
     * @param courseName
     * @return int
     */
    int countCoursePass(String courseName);

    /**
     *
     * @return int
     */
    int countStudentSize();

    /**
     *
     * @param studentEntity
     * @param pageIndex
     * @param pageSize
     * @return List<StudentEntity>
     */
    List<StudentEntity> queryStudentCourse(StudentEntity studentEntity,
                                           int pageIndex, int pageSize);
}
