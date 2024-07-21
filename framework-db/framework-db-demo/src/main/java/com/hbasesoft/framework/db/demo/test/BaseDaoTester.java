/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.db.demo.test;

import static com.hbasesoft.framework.db.demo.util.DataUtil.loadData;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Root;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hbasesoft.framework.db.demo.Application;
import com.hbasesoft.framework.db.demo.dao.StaffDao;
import com.hbasesoft.framework.db.demo.entity.StaffEntity;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2024年7月20日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.db.demo <br>
 */
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BaseDaoTester {

    /** 100 */
    private static final int NUM_100 = 100;

    /** 3 */
    private static final int NUM_3 = 3;

    /** */
    private static final double NUM_62000 = 62000;

    /** */
    private static final int NUM_10 = 10;

    /** */
    private static final double NUM_60000 = 60000;

    /** */
    private static final int NUM_6 = 6;

    /** */
    private static final int NUM_1000 = 1000;

    /** */
    private static final int NUM_5 = 5;

    /** staff dao */
    @Autowired
    private StaffDao staffDao;

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Transactional
    @BeforeEach
    public void initTable() {
        staffDao.updateBySql(loadData("data/t_staff/t_staff.sql"));
    }

    /**
     * Description: 保存数据<br>
     */
    @Transactional
    @Test
    public void save() {
        StaffEntity entity = JSONObject.parseObject(loadData("data/t_staff/staff_save.json"), StaffEntity.class);
        staffDao.save(entity);
        assertTrue(entity.getId() != null);
        assertEquals(staffDao.get(q -> q.count("id").build(), Integer.class), 1);
    }

    /**
     * Description: 批量保存<br>
     */
    @Transactional
    @Test
    public void saveBatch() {
        String datas = loadData("data/t_staff/staff_saveBatch.json");
        List<StaffEntity> all = new ArrayList<>();
        for (int i = 0; i < NUM_100; i++) {
            all.addAll(JSONArray.parseArray(datas, StaffEntity.class));
        }
        assertEquals(all.size(), NUM_1000);
        staffDao.saveBatch(all);
        assertEquals(staffDao.get(q -> q.count("id").build(), Integer.class), NUM_1000);
    }

    /**
     * Description: 更新数据<br>
     */
    @Transactional
    @Test
    public void update() {
        StaffEntity entity = JSONObject.parseObject(loadData("data/t_staff/staff_save.json"), StaffEntity.class);
        staffDao.save(entity);
        assertTrue(entity.getId() != null);
        assertEquals(entity.getFirstName(), "John");
        entity.setFirstName("张三");
        staffDao.update(entity);
        StaffEntity newEntity = staffDao.get(entity.getId());
        assertEquals(newEntity.getFirstName(), "张三");
    }

    /**
     * Description: 批量更新<br>
     */
    @Transactional
    @Test
    public void updateBatch() {
        List<StaffEntity> all = JSONArray.parseArray(loadData("data/t_staff/staff_saveBatch.json"), StaffEntity.class);
        staffDao.saveBatch(all);
        all = staffDao.queryAll();
        assertEquals(all.size(), NUM_10);
        for (StaffEntity entity : all) {
            if (entity.getId() > NUM_5) {
                entity.setDepartment("行政部");
            }
        }
        staffDao.updateBatch(all);
        assertEquals(staffDao.get(q -> q.count("id").eq("department", "行政部").build(), Integer.class), NUM_5);
    }

    /**
     * Description: 根据条件来做更新<br>
     */
    @Transactional
    @Test
    public void updateByCriteria() {
        List<StaffEntity> all = JSONArray.parseArray(loadData("data/t_staff/staff_saveBatch.json"), StaffEntity.class);
        staffDao.saveBatch(all);

        // 把薪资小于65000的人都调到62000
        assertEquals(staffDao.get(q -> q.count("id").ge("salary", NUM_60000).build(), Integer.class), NUM_6);

        CriteriaBuilder cb = staffDao.getCriteriaBuilder();
        CriteriaUpdate<StaffEntity> cu = cb.createCriteriaUpdate(StaffEntity.class);
        Root<StaffEntity> root = cu.from(StaffEntity.class);
        cu.set(root.get("salary"), NUM_62000);
        cu.where(cb.lessThanOrEqualTo(root.get("salary"), NUM_60000));
        staffDao.updateByCriteria(cu);

        // 所有人薪资都大于60000
        assertEquals(staffDao.get(q -> q.count("id").ge("salary", NUM_62000).build(), Integer.class), NUM_10);
    }

    /**
     * Description: 根据条件来做更新<br>
     */
    @Transactional
    @Test
    public void updateBySpecification() {
        List<StaffEntity> all = JSONArray.parseArray(loadData("data/t_staff/staff_saveBatch.json"), StaffEntity.class);
        staffDao.saveBatch(all);

        // 把薪资小于65000的人都调到62000
        assertEquals(staffDao.get(q -> q.count("id").ge("salary", NUM_60000).build(), Integer.class), NUM_6);

        staffDao.updateBySpecification((r, q, cb) -> q.set(r.get("salary"), NUM_62000)
            .where(cb.lessThanOrEqualTo(r.get("salary"), NUM_60000)).getRestriction());

        // 所有人薪资都大于60000
        assertEquals(staffDao.get(q -> q.count("id").ge("salary", NUM_62000).build(), Integer.class), NUM_10);
    }

    /**
     * Description: 根据条件来做更新<br>
     */
    @Transactional
    @Test
    public void update2() {
        List<StaffEntity> all = JSONArray.parseArray(loadData("data/t_staff/staff_saveBatch.json"), StaffEntity.class);
        staffDao.saveBatch(all);

        // 把薪资小于65000的人都调到62000
        assertEquals(staffDao.get(q -> q.count("id").ge("salary", NUM_60000).build(), Integer.class), NUM_6);

        staffDao.update(q -> q.set("salary", NUM_62000).le("salary", NUM_60000).build());

        // 所有人薪资都大于60000
        assertEquals(staffDao.get(q -> q.count("id").ge("salary", NUM_62000).build(), Integer.class), NUM_10);
    }

    /**
     * Description: 根据条件来做更新<br>
     */
    @Transactional
    @Test
    public void update3() {
        List<StaffEntity> all = JSONArray.parseArray(loadData("data/t_staff/staff_saveBatch.json"), StaffEntity.class);
        staffDao.saveBatch(all);

        assertEquals(staffDao.get(q -> q.count("id").eq("department", "财务部").build(), Integer.class), 0);

        staffDao
            .update(q -> q.set("salary", NUM_62000).set("department", "财务部").in("lastName", "Doe", "Smith").build());

        assertEquals(staffDao.get(q -> q.count("id").eq("department", "财务部").build(), Integer.class), NUM_3);
    }

    /**
     * Description: 根据条件来做更新<br>
     */
    @Transactional
    @Test
    public void updateByLambda() {
        List<StaffEntity> all = JSONArray.parseArray(loadData("data/t_staff/staff_saveBatch.json"), StaffEntity.class);
        staffDao.saveBatch(all);

        assertEquals(staffDao.getByLambda(
            q -> q.count(StaffEntity::getId).eq(StaffEntity::getDepartment, "财务部").build(), Integer.class), 0);

        staffDao.updateByLambda(q -> q.set(StaffEntity::getSalary, NUM_62000).set(StaffEntity::getDepartment, "财务部")
            .in(StaffEntity::getLastName, "Doe", "Smith").build());

        assertEquals(staffDao.getByLambda(
            q -> q.count(StaffEntity::getId).eq(StaffEntity::getDepartment, "财务部").build(), Integer.class), NUM_3);
    }

    /**
     * Description: 删除数据<br>
     */
    @Transactional
    @Test
    public void delete() {
        StaffEntity entity = JSONObject.parseObject(loadData("data/t_staff/staff_save.json"), StaffEntity.class);
        staffDao.save(entity);
        assertTrue(entity.getId() != null);
        assertEquals(staffDao.get(q -> q.count("id").build(), Integer.class), 1);

        staffDao.delete(entity);
        assertEquals(staffDao.get(q -> q.count("id").build(), Integer.class), 0);
    }

    /**
     * Description: 根据id来删除<br>
     */
    @Transactional
    @Test
    public void deleteById() {
        StaffEntity entity = JSONObject.parseObject(loadData("data/t_staff/staff_save.json"), StaffEntity.class);
        staffDao.save(entity);
        assertTrue(entity.getId() != null);
        assertEquals(staffDao.get(q -> q.count("id").build(), Integer.class), 1);

        staffDao.deleteById(entity.getId());
        assertEquals(staffDao.get(q -> q.count("id").build(), Integer.class), 0);
    }

    /**
     * Description: 批量删除<br>
     */
    @Transactional
    @Test
    public void deleteBatch() {
        List<StaffEntity> all = JSONArray.parseArray(loadData("data/t_staff/staff_saveBatch.json"), StaffEntity.class);
        staffDao.saveBatch(all);

        assertEquals(staffDao.getByLambda(
            q -> q.count(StaffEntity::getId).eq(StaffEntity::getDepartment, "项目一部").build(), Integer.class), NUM_3);

        List<StaffEntity> entites = staffDao
            .queryByLambda(q -> q.in(StaffEntity::getLastName, "Doe", "Johnson").build());
        staffDao.deleteBatch(entites);

        assertEquals(staffDao.getByLambda(
            q -> q.count(StaffEntity::getId).eq(StaffEntity::getDepartment, "项目一部").build(), Integer.class), 1);
    }

    /**
     * Description: 根据id批量删除<br>
     */
    public void deleteByIds() {

    }

    /**
     * Description: 根据条件删除<br>
     */
    public void deleteByCriteria() {

    }

    /**
     * Description: 根据条件删除<br>
     */
    public void deleteBySpecification() {

    }

    /**
     * Description: 根据条件删除<br>
     */
    public void delete2() {

    }

    /**
     * Description: 根据条件删除<br>
     */
    public void deleteByLambda() {

    }

    /**
     * Description: 根据id来获取数据<br>
     */
    public void get() {

    }

    /**
     * Description: 根据条件查询 <br>
     */
    public void get2() {

    }

    /**
     * Description: 根据条件查询<br>
     */
    public void get3() {

    }

    /**
     * Description: 根据条件查询 <br>
     */
    public void getByCriteria() {

    }

    /**
     * Description: 根据条件查询 <br>
     */
    public void getBySpecification() {

    }

    /**
     * Description: 根据条件查询<br>
     */
    public void getBySpecification2() {

    }

    /**
     * Description: 根据条件查询 <br>
     */
    public void getByLambda() {

    }

    /**
     * Description: 根据条件查询 <br>
     */
    public void getByLambda2() {

    }

    /**
     * Description: 查询所有数据<br>
     */
    public void queryAll() {

    }

    /**
     * Description: 根据属性查询数据<br>
     */
    public void queryByProperty() {

    }

    /**
     * Description: 根据条件查询<br>
     */
    public void queryPagerByCriteria() {

    }

    /**
     * Description: <br>
     */
    public void queryPagerBySpecification() {

    }

    /**
     * Description: <br>
     */
    public void queryPagerBySpecification2() {

    }

    /**
     * Description: <br>
     */
    public void queryPager() {

    }

    /**
     * Description: <br>
     */
    public void queryPager2() {

    }

    /**
     * Description: <br>
     */
    public void queryPagerByLambda() {

    }

    /**
     * Description: <br>
     */
    public void queryPagerByLambda2() {

    }

    /**
     * Description: 根据条件查询 <br>
     */
    public void queryByCriteria() {

    }

    /**
     * Description: 根据条件查询 <br>
     */
    public void queryBySpecification() {

    }

    /**
     * Description: 根据条件查询 <br>
     */
    public void query() {

    }

    /**
     * Description:根据条件查询 <br>
     */
    public void query2() {

    }

    /**
     * Description: 根据条件查询 <br>
     */
    public void queryByLambda() {

    }

    /**
     * Description: 根据条件查询 <br>
     */
    public void queryByLambda2() {

    }

}
