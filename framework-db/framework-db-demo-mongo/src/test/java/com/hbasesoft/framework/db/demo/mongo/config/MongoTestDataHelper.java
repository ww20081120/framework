package com.hbasesoft.framework.db.demo.mongo.config;

import com.hbasesoft.framework.db.demo.mongo.entity.CourseEntity;
import com.hbasesoft.framework.db.demo.mongo.entity.StudentEntity;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public final class MongoTestDataHelper {

    private MongoTestDataHelper() {
    }

    public static StudentEntity createStudent(String name, int age) {
        StudentEntity student = new StudentEntity();
        student.setId(UUID.randomUUID().toString().replace("-", ""));
        student.setName(name);
        student.setAge(age);
        return student;
    }

    public static List<StudentEntity> createStudentBatch() {
        return Arrays.asList(
            createStudent("张三", 18),
            createStudent("李四", 19),
            createStudent("王五", 20),
            createStudent("赵六", 18),
            createStudent("钱七", 21)
        );
    }

    public static CourseEntity createCourse(String courseName, String remark) {
        CourseEntity course = new CourseEntity();
        course.setId(UUID.randomUUID().toString().replace("-", ""));
        course.setCourseName(courseName);
        course.setRemark(remark);
        return course;
    }
}
