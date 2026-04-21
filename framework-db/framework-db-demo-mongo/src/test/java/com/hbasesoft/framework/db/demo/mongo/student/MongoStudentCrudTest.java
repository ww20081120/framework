package com.hbasesoft.framework.db.demo.mongo.student;

import com.hbasesoft.framework.db.demo.mongo.config.AbstractMongoTestConfig;
import com.hbasesoft.framework.db.demo.mongo.config.MongoTestDataHelper;
import com.hbasesoft.framework.db.demo.mongo.dao.StudentDao;
import com.hbasesoft.framework.db.demo.mongo.entity.StudentEntity;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class MongoStudentCrudTest extends AbstractMongoTestConfig {

    @Resource(name = "studentMongoDBDao")
    private StudentDao studentDao;

    @BeforeEach
    void cleanUp() {
        List<StudentEntity> all = studentDao.queryAll();
        if (all != null && !all.isEmpty()) {
            studentDao.deleteBatch(all);
        }
    }

    @Test
    void shouldSaveAndRetrieveStudent() {
        StudentEntity student = MongoTestDataHelper.createStudent("张三", 18);
        studentDao.save(student);
        assertThat(student.getId()).isNotNull();
        StudentEntity retrieved = studentDao.get(student.getId());
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getName()).isEqualTo("张三");
        assertThat(retrieved.getAge()).isEqualTo(18);
    }

    @Test
    void shouldSaveBatchStudents() {
        List<StudentEntity> students = MongoTestDataHelper.createStudentBatch();
        studentDao.saveBatch(students);
        List<StudentEntity> all = studentDao.queryAll();
        assertThat(all).hasSize(5);
    }

    @Test
    void shouldUpdateStudent() {
        StudentEntity student = MongoTestDataHelper.createStudent("张三", 18);
        studentDao.save(student);
        student.setName("李四");
        studentDao.update(student);
        StudentEntity updated = studentDao.get(student.getId());
        assertThat(updated.getName()).isEqualTo("李四");
    }

    @Test
    void shouldUpdateBatchStudents() {
        List<StudentEntity> students = MongoTestDataHelper.createStudentBatch();
        studentDao.saveBatch(students);
        for (StudentEntity s : students) {
            s.setAge(25);
        }
        studentDao.updateBatch(students);
        List<StudentEntity> all = studentDao.queryAll();
        assertThat(all).allMatch(s -> s.getAge() == 25);
    }

    @Test
    void shouldDeleteStudent() {
        StudentEntity student = MongoTestDataHelper.createStudent("张三", 18);
        studentDao.save(student);
        studentDao.delete(student);
        assertThat(studentDao.get(student.getId())).isNull();
    }

    @Test
    void shouldDeleteById() {
        StudentEntity student = MongoTestDataHelper.createStudent("张三", 18);
        studentDao.save(student);
        studentDao.deleteById(student.getId());
        assertThat(studentDao.get(student.getId())).isNull();
    }

    @Test
    void shouldDeleteByIds() {
        List<StudentEntity> students = MongoTestDataHelper.createStudentBatch();
        studentDao.saveBatch(students);
        List<String> ids = students.stream()
            .map(StudentEntity::getId)
            .collect(Collectors.toList())
            .subList(0, 3);
        studentDao.deleteByIds(ids);
        List<StudentEntity> remaining = studentDao.queryAll();
        assertThat(remaining).hasSize(2);
    }
}
