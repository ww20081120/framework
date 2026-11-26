package com.hbasesoft.framework.db.demo.mongo.student;

import com.hbasesoft.framework.db.demo.mongo.config.AbstractMongoTestConfig;
import com.hbasesoft.framework.db.demo.mongo.config.MongoTestDataHelper;
import com.hbasesoft.framework.db.demo.mongo.dao.StudentDao;
import com.hbasesoft.framework.db.demo.mongo.entity.StudentEntity;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class MongoStudentQueryTest extends AbstractMongoTestConfig {

    @Resource(name = "studentMongoDBDao")
    private StudentDao studentDao;

    @BeforeEach
    void setUp() {
        List<StudentEntity> all = studentDao.queryAll();
        if (all != null && !all.isEmpty()) {
            studentDao.deleteBatch(all);
        }
        studentDao.saveBatch(MongoTestDataHelper.createStudentBatch());
    }

    @Test
    void shouldQueryAll() {
        List<StudentEntity> all = studentDao.queryAll();
        assertThat(all).hasSize(5);
    }

    @Test
    void shouldGetByQueryWrapper() {
        StudentEntity student = studentDao.get(q -> q.eq("name", "张三"));
        assertThat(student).isNotNull();
        assertThat(student.getAge()).isEqualTo(18);
    }

    @Test
    void shouldQueryByQueryWrapper() {
        List<StudentEntity> students = studentDao.query(q -> q.eq(StudentEntity.AGE, 18));
        assertThat(students).hasSize(2);
    }

    @Test
    void shouldGetByLambda() {
        StudentEntity student = studentDao.getByLambda(q -> q.eq(StudentEntity::getName, "张三"));
        assertThat(student).isNotNull();
        assertThat(student.getAge()).isEqualTo(18);
    }

    @Test
    void shouldQueryByLambda() {
        List<StudentEntity> students = studentDao.queryByLambda(q -> q.eq(StudentEntity::getAge, 18));
        assertThat(students).hasSize(2);
    }

    @Test
    void shouldQueryPager() {
        var page = studentDao.queryPager(q -> q.ge("age", 18), 1, 2);
        assertThat(page).hasSize(2);
        assertThat(page.getTotalCount()).isEqualTo(5);
    }

    @Test
    void shouldQueryPagerByLambda() {
        var page = studentDao.queryPagerByLambda(q -> q.ge(StudentEntity::getAge, 18), 1, 2);
        assertThat(page).hasSize(2);
        assertThat(page.getTotalCount()).isEqualTo(5);
    }

    @Test
    void shouldDeleteByQueryWrapper() {
        studentDao.delete(q -> q.eq("name", "张三"));
        List<StudentEntity> remaining = studentDao.queryAll();
        assertThat(remaining).hasSize(4);
    }

    @Test
    void shouldDeleteByLambda() {
        studentDao.deleteByLambda(q -> q.eq(StudentEntity::getName, "张三"));
        List<StudentEntity> remaining = studentDao.queryAll();
        assertThat(remaining).hasSize(4);
    }

    @Test
    void shouldUpdateByQueryWrapper() {
        studentDao.update(q -> q.set("age", 99).eq("name", "张三"));
        StudentEntity updated = studentDao.get(q -> q.eq("name", "张三"));
        assertThat(updated.getAge()).isEqualTo(99);
    }

    @Test
    void shouldUpdateByLambda() {
        studentDao.updateByLambda(q -> q.set(StudentEntity::getAge, 99).eq(StudentEntity::getName, "张三"));
        StudentEntity updated = studentDao.getByLambda(q -> q.eq(StudentEntity::getName, "张三"));
        assertThat(updated.getAge()).isEqualTo(99);
    }
}
