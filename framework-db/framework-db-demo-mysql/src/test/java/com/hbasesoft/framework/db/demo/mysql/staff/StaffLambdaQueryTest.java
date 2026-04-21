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

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
public class StaffLambdaQueryTest extends AbstractMysqlTestConfig {

    @Resource(name = "staffMySqlDao")
    private StaffDao staffDao;

    @BeforeEach
    void setUp() {
        staffDao.saveBatch(TestDataHelper.createStaffBatch());
    }

    @Test
    void shouldGetByLambda() {
        StaffEntity entity = staffDao.getByLambda(
            q -> q.eq(StaffEntity::getFirstName, "Michael").eq(StaffEntity::getLastName, "Smith"));

        assertThat(entity).isNotNull();
        assertThat(entity.getSalary()).isEqualTo(65000.00);
    }

    @Test
    void shouldGetByLambdaWithProjection() {
        Integer count = staffDao.getByLambda(
            q -> q.count(StaffEntity::getId).eq(StaffEntity::getLastName, "Doe"), Integer.class);

        assertThat(count).isEqualTo(2);
    }

    @Test
    void shouldQueryByLambda() {
        List<StaffEntity> entities = staffDao.queryByLambda(
            q -> q.le(StaffEntity::getSalary, 60000.00));

        assertThat(entities).hasSize(5);
    }

    @Test
    void shouldQueryByLambdaWithProjection() {
        List<CountEntity> entities = staffDao.queryByLambda(
            q -> q.count(StaffEntity::getId, CountEntity::getTotal)
                .select(StaffEntity::getDepartment, CountEntity::getName)
                .groupBy(StaffEntity::getDepartment),
            CountEntity.class);

        assertThat(entities).hasSize(3);
    }

    @Test
    void shouldQueryPagerByLambda() {
        PagerList<StaffEntity> page = staffDao.queryPagerByLambda(
            q -> q.le(StaffEntity::getSalary, 60000.00), 1, 2);

        assertThat(page).hasSize(2);
        assertThat(page.getTotalCount()).isEqualTo(5);
    }

    @Test
    void shouldQueryPagerByLambdaWithProjection() {
        PagerList<StaffEntity> page = staffDao.queryPagerByLambda(
            q -> q.select(StaffEntity::getId)
                .select(StaffEntity::getFirstName)
                .select(StaffEntity::getLastName)
                .le(StaffEntity::getSalary, 60000.00),
            1, 2, StaffEntity.class);

        assertThat(page).hasSize(2);
        assertThat(page.getTotalCount()).isEqualTo(5);
    }

    @Test
    void shouldDeleteByLambda() {
        staffDao.deleteByLambda(q -> q.in(StaffEntity::getLastName, "Doe", "Johnson"));

        Integer remaining = staffDao.getByLambda(
            q -> q.count(StaffEntity::getId), Integer.class);
        assertThat(remaining).isEqualTo(7);
    }

    @Test
    void shouldUpdateByLambda() {
        staffDao.updateByLambda(
            q -> q.set(StaffEntity::getSalary, 62000.00)
                .set(StaffEntity::getDepartment, "财务部")
                .in(StaffEntity::getLastName, "Doe", "Smith"));

        Integer count = staffDao.getByLambda(
            q -> q.count(StaffEntity::getId).eq(StaffEntity::getDepartment, "财务部"),
            Integer.class);
        assertThat(count).isEqualTo(3);
    }
}
