package com.hbasesoft.framework.db.demo.mysql.course;

import com.hbasesoft.framework.db.demo.mysql.config.AbstractMysqlTestConfig;
import com.hbasesoft.framework.db.demo.mysql.config.TestDataHelper;
import com.hbasesoft.framework.db.demo.mysql.dao.CourseDao;
import com.hbasesoft.framework.db.demo.mysql.entity.CourseEntity;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
public class CourseCrudTest extends AbstractMysqlTestConfig {

    @Resource(name = "courseMySqlDao")
    private CourseDao courseDao;

    @Test
    void shouldSaveAndRetrieveCourse() {
        CourseEntity course = TestDataHelper.createCourse("语文", "基础语文课程");
        courseDao.save(course);
        assertThat(course.getId()).isNotNull();
        CourseEntity retrieved = courseDao.get(course.getId());
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getCourseName()).isEqualTo("语文");
        assertThat(retrieved.getRemark()).isEqualTo("基础语文课程");
    }

    @Test
    void shouldSaveBatchCourses() {
        List<CourseEntity> courses = Arrays.asList(
            TestDataHelper.createCourse("语文", "基础语文"),
            TestDataHelper.createCourse("数学", "高等数学"),
            TestDataHelper.createCourse("英语", "英语口语")
        );
        courseDao.saveBatch(courses);
        List<CourseEntity> all = courseDao.queryAll();
        assertThat(all).hasSize(3);
    }

    @Test
    void shouldDeleteCourse() {
        CourseEntity course = TestDataHelper.createCourse("语文", "基础语文");
        courseDao.save(course);
        courseDao.delete(course);
        assertThat(courseDao.get(course.getId())).isNull();
    }

    @Test
    void shouldQueryAllCourses() {
        courseDao.saveBatch(Arrays.asList(
            TestDataHelper.createCourse("语文", "基础语文"),
            TestDataHelper.createCourse("数学", "高等数学")
        ));
        List<CourseEntity> all = courseDao.queryAll();
        assertThat(all).hasSize(2);
    }

    @Test
    void shouldGetByProperty() {
        courseDao.saveBatch(Arrays.asList(
            TestDataHelper.createCourse("语文", "基础语文"),
            TestDataHelper.createCourse("数学", "高等数学")
        ));
        CourseEntity course = courseDao.get(q -> q.eq(CourseEntity.COURSE_NAME, "语文"));
        assertThat(course).isNotNull();
        assertThat(course.getRemark()).isEqualTo("基础语文");
    }

    @Test
    void shouldCountCourses() {
        courseDao.saveBatch(Arrays.asList(
            TestDataHelper.createCourse("语文", "基础语文"),
            TestDataHelper.createCourse("数学", "高等数学"),
            TestDataHelper.createCourse("英语", "英语口语")
        ));
        Long count = courseDao.get(q -> q.count("id"), Long.class);
        assertThat(count).isEqualTo(3);
    }

    @Test
    void shouldCountByLambda() {
        courseDao.saveBatch(Arrays.asList(
            TestDataHelper.createCourse("语文", "基础语文"),
            TestDataHelper.createCourse("数学", "高等数学"),
            TestDataHelper.createCourse("英语", "英语口语")
        ));
        Long count = courseDao.getByLambda(q -> q.count(CourseEntity::getId), Long.class);
        assertThat(count).isEqualTo(3);
    }
}
