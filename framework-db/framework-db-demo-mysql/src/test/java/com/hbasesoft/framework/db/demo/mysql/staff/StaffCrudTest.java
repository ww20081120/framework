package com.hbasesoft.framework.db.demo.mysql.staff;

import com.hbasesoft.framework.db.demo.mysql.config.AbstractMysqlTestConfig;
import com.hbasesoft.framework.db.demo.mysql.config.TestDataHelper;
import com.hbasesoft.framework.db.demo.mysql.dao.StaffDao;
import com.hbasesoft.framework.db.demo.mysql.entity.StaffEntity;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
public class StaffCrudTest extends AbstractMysqlTestConfig {

    @Resource(name = "staffMySqlDao")
    private StaffDao staffDao;

    @Test
    void shouldSaveAndRetrieveEntity() {
        StaffEntity entity = TestDataHelper.createStaff("John", "Doe", "项目一部", 75000.00);
        staffDao.save(entity);

        assertThat(entity.getId()).isNotNull();

        StaffEntity retrieved = staffDao.get(entity.getId());
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getFirstName()).isEqualTo("John");
        assertThat(retrieved.getLastName()).isEqualTo("Doe");
        assertThat(retrieved.getDepartment()).isEqualTo("项目一部");
        assertThat(retrieved.getSalary()).isEqualTo(75000.00);
    }

    @Test
    void shouldSaveBatchEntities() {
        List<StaffEntity> batch = TestDataHelper.createStaffBatch();
        staffDao.saveBatch(batch);

        Integer count = staffDao.get(q -> q.count("id"), Integer.class);
        assertThat(count).isEqualTo(10);
    }

    @Test
    void shouldUpdateEntity() {
        StaffEntity entity = TestDataHelper.createStaff("John", "Doe", "项目一部", 75000.00);
        staffDao.save(entity);

        entity.setFirstName("张三");
        staffDao.update(entity);

        StaffEntity updated = staffDao.get(entity.getId());
        assertThat(updated.getFirstName()).isEqualTo("张三");
    }

    @Test
    void shouldUpdateBatchEntities() {
        List<StaffEntity> batch = TestDataHelper.createStaffBatch();
        staffDao.saveBatch(batch);

        List<StaffEntity> all = staffDao.queryAll();
        for (StaffEntity e : all) {
            if (e.getSalary() > 60000) {
                e.setDepartment("管理层");
            }
        }
        staffDao.updateBatch(all);

        Integer count = staffDao.get(q -> q.count("id").eq("department", "管理层"), Integer.class);
        assertThat(count).isGreaterThan(0);
    }

    @Test
    void shouldDeleteEntity() {
        StaffEntity entity = TestDataHelper.createStaff("John", "Doe", "项目一部", 75000.00);
        staffDao.save(entity);

        Integer before = staffDao.get(q -> q.count("id"), Integer.class);
        staffDao.delete(entity);
        Integer after = staffDao.get(q -> q.count("id"), Integer.class);

        assertThat(after).isEqualTo(before - 1);
    }

    @Test
    void shouldDeleteById() {
        StaffEntity entity = TestDataHelper.createStaff("John", "Doe", "项目一部", 75000.00);
        staffDao.save(entity);
        Integer id = entity.getId();

        Integer before = staffDao.get(q -> q.count("id"), Integer.class);
        staffDao.deleteById(id);
        Integer after = staffDao.get(q -> q.count("id"), Integer.class);

        assertThat(after).isEqualTo(before - 1);
    }

    @Test
    void shouldDeleteByIds() {
        List<StaffEntity> batch = TestDataHelper.createStaffBatch();
        staffDao.saveBatch(batch);

        List<Integer> ids = batch.stream()
            .map(StaffEntity::getId)
            .collect(Collectors.toList())
            .subList(0, 3);

        staffDao.deleteByIds(ids);

        Integer remaining = staffDao.get(q -> q.count("id"), Integer.class);
        assertThat(remaining).isEqualTo(7);
    }

    @Test
    void shouldDeleteBatch() {
        List<StaffEntity> batch = TestDataHelper.createStaffBatch();
        staffDao.saveBatch(batch);

        List<StaffEntity> toDelete = batch.subList(0, 3);
        staffDao.deleteBatch(toDelete);

        Integer remaining = staffDao.get(q -> q.count("id"), Integer.class);
        assertThat(remaining).isEqualTo(7);
    }
}
