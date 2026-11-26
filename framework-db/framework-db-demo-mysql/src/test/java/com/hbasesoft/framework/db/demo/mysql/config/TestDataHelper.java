package com.hbasesoft.framework.db.demo.mysql.config;

import com.hbasesoft.framework.db.demo.mysql.entity.CourseEntity;
import com.hbasesoft.framework.db.demo.mysql.entity.StaffEntity;
import com.hbasesoft.framework.db.demo.mysql.entity.StudentEntity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public final class TestDataHelper {

    private TestDataHelper() {
    }

    public static StaffEntity createStaff(String firstName, String lastName,
                                           String department, Double salary) {
        StaffEntity staff = new StaffEntity();
        staff.setFirstName(firstName);
        staff.setLastName(lastName);
        staff.setDepartment(department);
        staff.setSalary(salary);
        staff.setPosition("Developer");
        staff.setHireDate(new Date());
        staff.setName(firstName + " " + lastName);
        return staff;
    }

    public static List<StaffEntity> createStaffBatch() {
        List<StaffEntity> list = new ArrayList<>();
        list.add(createStaff("John", "Doe", "项目一部", 75000.00));
        list.add(createStaff("Jane", "Doe", "项目一部", 80000.00));
        list.add(createStaff("Michael", "Smith", "项目一部", 65000.00));
        list.add(createStaff("Emily", "Johnson", "项目二部", 55000.00));
        list.add(createStaff("David", "Brown", "项目二部", 70000.00));
        list.add(createStaff("Jessica", "Jones", "项目二部", 60000.00));
        list.add(createStaff("Matthew", "Williams", "项目二部", 65000.00));
        list.add(createStaff("Olivia", "Taylor", "项目二部", 55000.00));
        list.add(createStaff("Daniel", "Anderson", "项目三部", 50000.00));
        list.add(createStaff("Sophia", "Thomas", "项目三部", 45000.00));
        return list;
    }

    public static StudentEntity createStudent(String name, int age) {
        StudentEntity student = new StudentEntity();
        student.setName(name);
        student.setAge(age);
        return student;
    }

    public static CourseEntity createCourse(String courseName, String remark) {
        CourseEntity course = new CourseEntity();
        course.setCourseName(courseName);
        course.setRemark(remark);
        return course;
    }
}
