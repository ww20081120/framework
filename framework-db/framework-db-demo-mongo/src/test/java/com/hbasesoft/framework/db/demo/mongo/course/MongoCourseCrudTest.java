package com.hbasesoft.framework.db.demo.mongo.course;

import com.hbasesoft.framework.db.demo.mongo.config.AbstractMongoTestConfig;
import com.hbasesoft.framework.db.demo.mongo.config.MongoTestDataHelper;
import com.hbasesoft.framework.db.demo.mongo.dao.CourseDao;
import com.hbasesoft.framework.db.demo.mongo.entity.CourseEntity;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class MongoCourseCrudTest extends AbstractMongoTestConfig {

    @Resource(name = "courseMongoDBDao")
    private CourseDao courseDao;

    @BeforeEach
    void cleanUp() {
        List<CourseEntity> all = courseDao.queryAll();
        if (all != null && !all.isEmpty()) {
            courseDao.deleteBatch(all);
        }
    }

    @Test
    void shouldSaveAndRetrieveCourse() {
        CourseEntity course = MongoTestDataHelper.createCourse("语文", "基础语文");
        courseDao.save(course);
        assertThat(course.getId()).isNotNull();
        CourseEntity retrieved = courseDao.get(course.getId());
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getCourseName()).isEqualTo("语文");
    }

    @Test
    void shouldSaveBatchCourses() {
        List<CourseEntity> courses = Arrays.asList(
            MongoTestDataHelper.createCourse("语文", "基础语文"),
            MongoTestDataHelper.createCourse("数学", "高等数学"),
            MongoTestDataHelper.createCourse("英语", "英语口语")
        );
        courseDao.saveBatch(courses);
        List<CourseEntity> all = courseDao.queryAll();
        assertThat(all).hasSize(3);
    }

    @Test
    void shouldDeleteCourse() {
        CourseEntity course = MongoTestDataHelper.createCourse("语文", "基础语文");
        courseDao.save(course);
        courseDao.delete(course);
        assertThat(courseDao.get(course.getId())).isNull();
    }

    @Test
    void shouldQueryAllCourses() {
        courseDao.saveBatch(Arrays.asList(
            MongoTestDataHelper.createCourse("语文", "基础语文"),
            MongoTestDataHelper.createCourse("数学", "高等数学")
        ));
        List<CourseEntity> all = courseDao.queryAll();
        assertThat(all).hasSize(2);
    }

    @Test
    void shouldGetByProperty() {
        courseDao.saveBatch(Arrays.asList(
            MongoTestDataHelper.createCourse("语文", "基础语文"),
            MongoTestDataHelper.createCourse("数学", "高等数学")
        ));
        CourseEntity course = courseDao.get(q -> q.eq(CourseEntity.COURSE_NAME, "语文"));
        assertThat(course).isNotNull();
        assertThat(course.getRemark()).isEqualTo("基础语文");
    }
}
