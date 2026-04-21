package com.hbasesoft.framework.db.demo.mysql.student;

import com.hbasesoft.framework.db.demo.mysql.config.AbstractMysqlTestConfig;
import com.hbasesoft.framework.db.demo.mysql.config.TestDataHelper;
import com.hbasesoft.framework.db.demo.mysql.dao.StudentDao;
import com.hbasesoft.framework.db.demo.mysql.entity.StudentEntity;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
public class StudentCrudTest extends AbstractMysqlTestConfig {

    @Resource(name = "studentMySqlDao")
    private StudentDao studentDao;

    @Test
    void shouldSaveAndRetrieveStudent() {
        StudentEntity student = TestDataHelper.createStudent("张三", 18);
        studentDao.save(student);
        assertThat(student.getId()).isNotNull();
        StudentEntity retrieved = studentDao.get(student.getId());
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getName()).isEqualTo("张三");
        assertThat(retrieved.getAge()).isEqualTo(18);
    }

    @Test
    void shouldSaveBatchStudents() {
        List<StudentEntity> students = Arrays.asList(
            TestDataHelper.createStudent("张三", 18),
            TestDataHelper.createStudent("李四", 19),
            TestDataHelper.createStudent("王五", 20)
        );
        studentDao.saveBatch(students);
        List<StudentEntity> all = studentDao.queryAll();
        assertThat(all).hasSize(3);
    }

    @Test
    void shouldUpdateStudent() {
        StudentEntity student = TestDataHelper.createStudent("张三", 18);
        studentDao.save(student);
        student.setName("李四");
        studentDao.update(student);
        StudentEntity updated = studentDao.get(student.getId());
        assertThat(updated.getName()).isEqualTo("李四");
    }

    @Test
    void shouldUpdateByQueryWrapper() {
        StudentEntity student = TestDataHelper.createStudent("张三", 18);
        studentDao.save(student);

        studentDao.update(q -> q.set("name", "李四").eq("id", student.getId()));

        // 验证没有旧名字的学生
        Integer count = studentDao.get(q -> q.count("id").eq("name", "张三"), Integer.class);
        assertThat(count).isEqualTo(0);

        // 验证有新名字的学生
        count = studentDao.get(q -> q.count("id").eq("name", "李四").eq("age", 18), Integer.class);
        assertThat(count).isEqualTo(1);
    }

    @Test
    void shouldDeleteStudent() {
        StudentEntity student = TestDataHelper.createStudent("张三", 18);
        studentDao.save(student);

        int before = studentDao.queryAll().size();
        studentDao.delete(student);
        int after = studentDao.queryAll().size();

        assertThat(after).isEqualTo(before - 1);
    }

    @Test
    void shouldDeleteById() {
        StudentEntity student = TestDataHelper.createStudent("张三", 18);
        studentDao.save(student);
        String id = student.getId();

        int before = studentDao.queryAll().size();
        studentDao.deleteById(id);
        int after = studentDao.queryAll().size();

        assertThat(after).isEqualTo(before - 1);
    }

    @Test
    void shouldDeleteByQueryWrapper() {
        StudentEntity student = TestDataHelper.createStudent("张三", 18);
        studentDao.save(student);

        int before = studentDao.queryAll().size();
        studentDao.delete(q -> q.eq("id", student.getId()));
        int after = studentDao.queryAll().size();

        assertThat(after).isEqualTo(before - 1);
    }

    @Test
    void shouldQueryByProperty() {
        studentDao.saveBatch(Arrays.asList(
            TestDataHelper.createStudent("张三", 18),
            TestDataHelper.createStudent("李四", 18),
            TestDataHelper.createStudent("王五", 20)
        ));
        List<StudentEntity> students = studentDao.query(q -> q.eq(StudentEntity.AGE, 18));
        assertThat(students).hasSize(2);
    }

    @Test
    void shouldQueryPagerStudents() {
        studentDao.saveBatch(Arrays.asList(
            TestDataHelper.createStudent("张三", 18),
            TestDataHelper.createStudent("李四", 19),
            TestDataHelper.createStudent("王五", 20),
            TestDataHelper.createStudent("赵六", 21),
            TestDataHelper.createStudent("钱七", 22)
        ));
        var page = studentDao.queryPager(q -> q.ge("age", 18), 1, 2);
        assertThat(page).hasSize(2);
        assertThat(page.getTotalCount()).isEqualTo(5);
    }

    @Test
    void shouldUpdateByLambda() {
        StudentEntity student = TestDataHelper.createStudent("张三", 18);
        studentDao.save(student);

        studentDao.updateByLambda(q -> q.set(StudentEntity::getName, "李四").eq(StudentEntity::getId, student.getId()));

        // 验证没有旧名字的学生
        Integer count = studentDao.get(q -> q.count("id").eq("name", "张三"), Integer.class);
        assertThat(count).isEqualTo(0);

        // 验证有新名字的学生
        count = studentDao.get(q -> q.count("id").eq("name", "李四").eq("age", 18), Integer.class);
        assertThat(count).isEqualTo(1);
    }
}
