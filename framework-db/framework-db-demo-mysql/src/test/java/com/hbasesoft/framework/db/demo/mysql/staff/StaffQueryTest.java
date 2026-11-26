package com.hbasesoft.framework.db.demo.mysql.staff;

import com.hbasesoft.framework.db.demo.mysql.config.AbstractMysqlTestConfig;
import com.hbasesoft.framework.db.demo.mysql.config.TestDataHelper;
import com.hbasesoft.framework.db.demo.mysql.dao.StaffDao;
import com.hbasesoft.framework.db.demo.mysql.entity.CountEntity;
import com.hbasesoft.framework.db.demo.mysql.entity.StaffEntity;
import com.hbasesoft.framework.db.core.utils.PagerList;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
public class StaffQueryTest extends AbstractMysqlTestConfig {

    @Resource(name = "staffMySqlDao")
    private StaffDao staffDao;

    @BeforeEach
    void setUp() {
        staffDao.saveBatch(TestDataHelper.createStaffBatch());
    }

    @Test
    void shouldGetByQueryWrapper() {
        StaffEntity entity = staffDao.get(q -> q.eq("firstName", "Michael").eq("lastName", "Smith"));
        assertThat(entity).isNotNull();
        assertThat(entity.getSalary()).isEqualTo(65000.00);
    }

    @Test
    void shouldGetByQueryWrapperWithProjection() {
        Integer count = staffDao.get(q -> q.count("id").eq("lastName", "Doe"), Integer.class);
        assertThat(count).isEqualTo(2);
    }

    @Test
    void shouldQueryAll() {
        List<StaffEntity> all = staffDao.queryAll();
        assertThat(all).hasSize(10);
    }

    @Test
    void shouldQueryByQueryWrapper() {
        List<StaffEntity> entities = staffDao.query(q -> q.le("salary", 60000.00));
        assertThat(entities).hasSize(5);
    }

    @Test
    void shouldQueryByQueryWrapperWithProjection() {
        List<CountEntity> entities = staffDao.query(
            q -> q.count("id", "total")
                .select("department", "name")
                .groupBy("department"),
            CountEntity.class);
        assertThat(entities).hasSize(3);
    }

    @Test
    void shouldQueryByQueryWrapperToMap() {
        @SuppressWarnings("rawtypes")
        List<Map> entities = staffDao.query(
            q -> q.count("id", "count").max("id").min("id", "mid")
                .avg("salary", "avgSalary").sum("salary", "sumSalary")
                .groupBy("department"),
            Map.class);
        assertThat(entities).isNotEmpty();
    }

    @Test
    void shouldQueryPagerByQueryWrapper() {
        PagerList<StaffEntity> page = staffDao.queryPager(q -> q.le("salary", 60000.00), 1, 2);
        assertThat(page).hasSize(2);
        assertThat(page.getTotalCount()).isEqualTo(5);
    }

    @Test
    void shouldQueryPagerByQueryWrapperWithProjection() {
        List<StaffEntity> entities = staffDao.queryPager(
            q -> q.select("id").select("firstName").select("lastName").le("salary", 60000.00),
            1, 2, StaffEntity.class);
        assertThat(entities).hasSize(2);
    }

    @Test
    void shouldUpdateByQueryWrapper() {
        staffDao.update(q -> q.set("salary", 62000.00).le("salary", 60000.00));
        Integer count = staffDao.get(q -> q.count("id").ge("salary", 62000.00), Integer.class);
        assertThat(count).isEqualTo(10);
    }

    @Test
    void shouldDeleteByQueryWrapper() {
        staffDao.delete(q -> q.in("lastName", "Doe", "Johnson"));
        Integer remaining = staffDao.get(q -> q.count("id"), Integer.class);
        assertThat(remaining).isEqualTo(7);
    }
}
