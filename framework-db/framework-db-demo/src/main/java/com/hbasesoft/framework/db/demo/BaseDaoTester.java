/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.db.demo;

import static com.hbasesoft.framework.db.demo.util.DataUtil.loadData;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


import com.hbasesoft.framework.db.core.config.DataParam;
import com.hbasesoft.framework.db.core.criteria.LambdaDeleteWrapper;
import com.hbasesoft.framework.db.core.utils.PagerList;
import com.hbasesoft.framework.db.demo.dao.StaffDao;
import com.hbasesoft.framework.db.demo.entity.CountEntity;
import com.hbasesoft.framework.db.demo.entity.StaffEntity;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.hbasesoft.framework.db.demo.util.DataUtil;

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

    /** 1 */
    private static final int NUM_1 = 1;

    /** */
    private static final double NUM_62000 = 62000;

    /** */
    private static final int NUM_10 = 10;

    /** */
    private static final int NUM_13 = 13;

    /** */
    private static final double NUM_60000 = 60000;

    /** */
    private static final int NUM_6 = 6;

    /** */
    private static final int NUM_9 = 9;

    /** */
    private static final int NUM_1000 = 1000;

    /** */
    private static final int NUM_1003 = 1003;

    /** */
    private static final int NUM_5 = 5;

    /** */
    private static final double NUM_65000 = 65000.00;

    /** staff dao */
    @Resource(name = "staffMongoDBDao")
    private StaffDao staffDao;

//
//    @Transactional
//    @BeforeEach
//    public void initTable() {
//        staffDao.excuteSql(loadData("data/t_staff/t_staff.sql"),new DataParam());
//    }

    /**
     * Description: 保存数据<br>
     */
//    @Transactional
    @Test
    public void save() {
        StaffEntity entity = JSONObject.parseObject(loadData("data/t_staff/staff_save.json"), StaffEntity.class);
//        staffDao.save(entity);
//        assertTrue(entity.getId() != null);
        List<StaffEntity> staffEntities = staffDao.queryAll();
        System.out.println(staffEntities.get(0));
        assertEquals(staffDao.get(q -> q.count("id"), Integer.class), 1);
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
        assertEquals(staffDao.get(q -> q.count("id"), Integer.class), NUM_1000);
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
            if (Integer.valueOf(entity.getId()) > NUM_5) {
                entity.setDepartment("行政部");
            }
        }
        staffDao.updateBatch(all);
        assertEquals(staffDao.get(q -> q.count("id").eq("department", "行政部"), Integer.class), NUM_5);
    }

    /**
     * Description: 根据条件来做更新<br>
     */
    @Transactional
    @Test
    public void updateByCriteria() {
//       List<StaffEntity> all = JSONArray.parseArray(loadData("data/t_staff/staff_saveBatch.json"), StaffEntity.class);
//       staffDao.saveBatch(all);
//
//        // 把薪资小于65000的人都调到62000
//        assertEquals(staffDao.get(q -> q.count("id").ge("salary", NUM_60000), Integer.class), NUM_9);
//
//        LambdaUpdateWrapper<StaffEntity> wrapper = new LambdaUpdateWrapper<>();
//        // 设置更新条件：薪资小于等于60000
//                wrapper.le(StaffEntity::getSalary, NUM_60000)
//                        // 设置要更新的字段及其新值
//                        .set(StaffEntity::getSalary, NUM_62000);
//
//        staffDao.updateBySpecification(wrapper);
//
//        // 所有人薪资都大于60000
//        assertEquals(staffDao.get(q -> q.count("id").ge("salary", NUM_62000), Integer.class), NUM_13);
    }

    /**
     * Description: 根据条件来做更新<br>
     */
    @Transactional
    @Test
    public void updateBySpecification() {
//       List<StaffEntity> all = JSONArray.parseArray(loadData("data/t_staff/staff_saveBatch.json"), StaffEntity.class);
//        staffDao.saveBatch(all);
//
//        // 把薪资小于65000的人都调到62000
//        assertEquals(staffDao.get(q -> q.count("id").ge("salary", NUM_60000), Integer.class), NUM_9);
//
//        // 创建UpdateWrapper实例
//        UpdateWrapper<StaffEntity> wrapper = new UpdateWrapper<>();
//        // 设置更新条件：薪资小于等于60000
//                wrapper.le("salary", NUM_60000)
//                        // 设置要更新的字段及其新值
//                        .set("salary", NUM_62000);
//
//        // 调用updateBySpecification方法执行更新
//        staffDao.updateBySpecification(wrapper);
//
//        // 所有人薪资都大于60000
//        assertEquals(staffDao.get(q -> q.count("id").ge("salary", NUM_62000), Integer.class), NUM_13);
    }

    /**
     * Description: 根据条件来做更新<br>
     */
//    @Transactional
    @Test
    public void update2() {
        List<StaffEntity> all = JSONArray.parseArray(loadData("data/t_staff/staff_saveBatch.json"), StaffEntity.class);
        staffDao.saveBatch(all);

        // 把薪资小于65000的人都调到62000
        assertEquals(staffDao.get(q -> q.count("id").ge("salary", NUM_60000), Integer.class), NUM_6);

        staffDao.update(q -> q.set("salary", NUM_62000).le("salary", NUM_60000));

        // 所有人薪资都大于60000
        assertEquals(staffDao.get(q -> q.count("id").ge("salary", NUM_62000), Integer.class), NUM_10);
    }

    /**
     * Description: 根据条件来做更新<br>
     */
//    @Transactional
    @Test
    public void update3() {
        List<StaffEntity> all = JSONArray.parseArray(loadData("data/t_staff/staff_saveBatch.json"), StaffEntity.class);
        staffDao.saveBatch(all);

        assertEquals(staffDao.get(q -> q.count("id").eq("department", "财务部"), Integer.class), 0);

        staffDao.update(q -> q.set("salary", NUM_62000).set("department", "财务部").in("lastName", "Doe", "Smith"));

        assertEquals(staffDao.get(q -> q.count("id").eq("department", "财务部"), Integer.class), NUM_3);
    }

    /**
     * Description: 根据条件来做更新<br>
     */
    @Transactional
    @Test
    public void updateByLambda() {
        List<StaffEntity> all = JSONArray.parseArray(loadData("data/t_staff/staff_saveBatch.json"), StaffEntity.class);
        staffDao.saveBatch(all);

        assertEquals(
            staffDao.getByLambda(q -> q.count(StaffEntity::getId).eq(StaffEntity::getDepartment, "财务部"), Integer.class),
            0);

        staffDao.updateByLambda(q -> q.set(StaffEntity::getSalary, NUM_62000).set(StaffEntity::getDepartment, "财务部")
            .in(StaffEntity::getLastName, "Doe", "Smith"));

        assertEquals(
            staffDao.getByLambda(q -> q.count(StaffEntity::getId).eq(StaffEntity::getDepartment, "财务部"), Integer.class),
            NUM_3);
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
        assertEquals(staffDao.get(q -> q.count("id"), Integer.class), 1);

        staffDao.delete(entity);
        assertEquals(staffDao.get(q -> q.count("id"), Integer.class), 0);
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
        assertEquals(staffDao.get(q -> q.count("id"), Integer.class), 1);
        StaffEntity staffEntity = staffDao.get(entity.getId());
        System.out.println(staffEntity);
        staffDao.deleteById(entity.getId());
        assertEquals(staffDao.get(q -> q.count("id"), Integer.class), 0);
    }

    /**
     * Description: 批量删除<br>
     */
    @Transactional
    @Test
    public void deleteBatch() {
        List<StaffEntity> all = JSONArray.parseArray(loadData("data/t_staff/staff_saveBatch.json"), StaffEntity.class);
        staffDao.saveBatch(all);

        assertEquals(staffDao.getByLambda(q -> q.count(StaffEntity::getId).eq(StaffEntity::getDepartment, "项目一部"),
            Integer.class), NUM_3);

        List<StaffEntity> entites = staffDao.queryByLambda(q -> q.in(StaffEntity::getLastName, "Doe", "Johnson"));
        staffDao.deleteBatch(entites);

        assertEquals(staffDao.getByLambda(q -> q.count(StaffEntity::getId).eq(StaffEntity::getDepartment, "项目一部"),
            Integer.class), 1);
    }

    /**
     * Description: 根据id批量删除<br>
     */
    @Transactional
    @Test
    public void deleteByIds() {
        List<StaffEntity> all = JSONArray.parseArray(loadData("data/t_staff/staff_saveBatch.json"), StaffEntity.class);
        staffDao.saveBatch(all);

        assertEquals(staffDao.getByLambda(q -> q.count(StaffEntity::getId).eq(StaffEntity::getDepartment, "项目一部"),
            Integer.class), NUM_3);

        List<StaffEntity> entites = staffDao.queryByLambda(q -> q.in(StaffEntity::getLastName, "Doe", "Johnson"));
        staffDao.deleteByIds(entites.stream().map(s -> s.getId()).collect(Collectors.toList()));

        assertEquals(staffDao.getByLambda(q -> q.count(StaffEntity::getId).eq(StaffEntity::getDepartment, "项目一部"),
            Integer.class), 1);
    }

    /**
     * Description: 根据条件删除<br>
     */
    @Transactional
    @Test
    public void deleteByCriteria() {
        List<StaffEntity> all = JSONArray.parseArray(loadData("data/t_staff/staff_saveBatch.json"), StaffEntity.class);
        staffDao.saveBatch(all);

        assertEquals(staffDao.getByLambda(q -> q.count(StaffEntity::getId).eq(StaffEntity::getDepartment, "项目一部"),
            Integer.class), NUM_3);

//        CriteriaBuilder cb = staffDao.criteriaBuilder();
//        CriteriaDelete<StaffEntity> cd = cb.createCriteriaDelete(StaffEntity.class);
//        Root<StaffEntity> r = cd.from(StaffEntity.class);
//        cd.where(r.get("lastName").in("Doe", "Johnson"));
//        staffDao.deleteByCriteria(cd);

        LambdaDeleteWrapper<StaffEntity> wrapper = new LambdaDeleteWrapper<StaffEntity>()
            .in(StaffEntity::getLastName, "Doe", "Johnson");
        staffDao.deleteByLambda(wrapper);

        assertEquals(staffDao.getByLambda(q -> q.count(StaffEntity::getId).eq(StaffEntity::getDepartment, "项目一部"),
            Integer.class), 1);
    }

    /**
     * Description: 根据条件删除<br>
     */
    @Transactional
    @Test
    public void deleteBySpecification() {
        List<StaffEntity> all = JSONArray.parseArray(loadData("data/t_staff/staff_saveBatch.json"), StaffEntity.class);
        staffDao.saveBatch(all);

        assertEquals(staffDao.getByLambda(q -> q.count(StaffEntity::getId).eq(StaffEntity::getDepartment, "项目一部"),
            Integer.class), NUM_3);

//       staffDao.deleteBySpecification((r, q, cb) -> q.where(r.get("lastName").in("Doe", "Johnson")).getRestriction());

//       assertEquals(staffDao.getByLambda(q -> q.count(StaffEntity::getId).eq(StaffEntity::getDepartment, "项目一部"),
//            Integer.class), 1);
    }

    /**
     * Description: 根据条件删除<br>
     */
    @Transactional
    @Test
    public void delete2() {
        List<StaffEntity> all = JSONArray.parseArray(loadData("data/t_staff/staff_saveBatch.json"), StaffEntity.class);
        staffDao.saveBatch(all);

        assertEquals(staffDao.getByLambda(q -> q.count(StaffEntity::getId).eq(StaffEntity::getDepartment, "项目一部"),
            Integer.class), NUM_3);

        staffDao.delete(q -> q.in("lastName", "Doe", "Johnson"));

        assertEquals(staffDao.getByLambda(q -> q.count(StaffEntity::getId).eq(StaffEntity::getDepartment, "项目一部"),
            Integer.class), 1);
    }

    /**
     * Description: 根据条件删除<br>
     */
    @Transactional
    @Test
    public void deleteByLambda() {
        List<StaffEntity> all = JSONArray.parseArray(loadData("data/t_staff/staff_saveBatch.json"), StaffEntity.class);
        staffDao.saveBatch(all);

        assertEquals(staffDao.getByLambda(q -> q.count(StaffEntity::getId).eq(StaffEntity::getDepartment, "项目一部"),
            Integer.class), NUM_3);

        staffDao.deleteByLambda(q -> q.in(StaffEntity::getLastName, "Doe", "Johnson"));

        assertEquals(staffDao.getByLambda(q -> q.count(StaffEntity::getId).eq(StaffEntity::getDepartment, "项目一部"),
            Integer.class), 1);
    }

    /**
     * Description: 根据id来获取数据<br>
     */
    @Transactional
    @Test
    public void get() {
        List<StaffEntity> all = JSONArray.parseArray(loadData("data/t_staff/staff_saveBatch.json"), StaffEntity.class);
        staffDao.saveBatch(all);

        StaffEntity entity = staffDao.get(q -> q.eq("firstName", "Michael").eq("lastName", "Smith"));

        assertEquals(entity.getSalary(), NUM_65000);
    }

    /**
     * Description: 根据条件查询 <br>
     */
    @Transactional
    @Test
    public void get2() {
        StaffEntity entity = JSONObject.parseObject(loadData("data/t_staff/staff_save.json"), StaffEntity.class);
        staffDao.save(entity);
        assertTrue(entity.getId() != null);

        StaffEntity newEntity = staffDao.get(entity.getId());

        assertTrue(DataUtil.equals(entity, newEntity));
    }

    /**
     * Description: 根据条件查询<br>
     */
    @Transactional
    @Test
    public void get3() {
        List<StaffEntity> all = JSONArray.parseArray(loadData("data/t_staff/staff_saveBatch.json"), StaffEntity.class);
        staffDao.saveBatch(all);
        assertEquals(staffDao.get(q -> q.count("id").eq("lastName", "Doe"), Integer.class), 2);
    }

    /**
     * Description: 根据条件查询 <br>
     */
    @Transactional
    @Test
    public void getByCriteria() {
//       List<StaffEntity> all = JSONArray.parseArray(loadData("data/t_staff/staff_saveBatch.json"), StaffEntity.class);
//        staffDao.saveBatch(all);

//        CriteriaBuilder cb = staffDao.criteriaBuilder();
//        CriteriaQuery<Long> q = cb.createQuery(Long.class);
//        Root<StaffEntity> r = q.from(StaffEntity.class);
//
//        q.select(cb.count(r.get("id"))).where(cb.equal(r.get("lastName"), "Doe"));
//
//        assertEquals(staffDao.getByCriteria(q).intValue(), 2);
    }

    /**
     * Description: 根据条件查询 <br>
     */
    @Transactional
    @Test
    public void getBySpecification() {
//       List<StaffEntity> all = JSONArray.parseArray(loadData("data/t_staff/staff_saveBatch.json"), StaffEntity.class);
//        staffDao.saveBatch(all);

//        StaffEntity entity = staffDao.getBySpecification((r, q, cb) -> q
//            .where(cb.equal(r.get("firstName"), "Michael"), cb.equal(r.get("lastName"), "Smith")).getRestriction());

//        assertEquals(entity.getSalary(), NUM_65000);
    }

    /**
     * Description: 根据条件查询<br>
     */
    @Transactional
    @Test
    public void getBySpecification2() {
//       List<StaffEntity> all = JSONArray.parseArray(loadData("data/t_staff/staff_saveBatch.json"), StaffEntity.class);
//        staffDao.saveBatch(all);
//        assertEquals(staffDao.getBySpecification((r, q, cb) -> q.multiselect(cb.count(r.get("id")))
//            .where(cb.equal(r.get("lastName"), "Doe")).getRestriction(), Long.class).intValue(), 2);
    }

    /**
     * Description: 根据条件查询 <br>
     */
    @Transactional
    @Test
    public void getByLambda() {
        List<StaffEntity> all = JSONArray.parseArray(loadData("data/t_staff/staff_saveBatch.json"), StaffEntity.class);
        staffDao.saveBatch(all);

        StaffEntity entity = staffDao
            .getByLambda(q -> q.eq(StaffEntity::getFirstName, "Michael").eq(StaffEntity::getLastName, "Smith"));

        assertEquals(entity.getSalary(), NUM_65000);
    }

    /**
     * Description: 根据条件查询 <br>
     */
    @Transactional
    @Test
    public void getByLambda2() {
        List<StaffEntity> all = JSONArray.parseArray(loadData("data/t_staff/staff_saveBatch.json"), StaffEntity.class);
        staffDao.saveBatch(all);
        assertEquals(
            staffDao.getByLambda(q -> q.count(StaffEntity::getId).eq(StaffEntity::getLastName, "Doe"), Integer.class),
            2);
    }

    /**
     * Description: 查询所有数据<br>
     */
    @Transactional
    @Test
    public void queryAll() {
        List<StaffEntity> all = JSONArray.parseArray(loadData("data/t_staff/staff_saveBatch.json"), StaffEntity.class);
        staffDao.saveBatch(all);
        assertEquals(staffDao.queryAll().size(), all.size());
    }

    /**
     * Description: 根据条件查询<br>
     */
    @Transactional
    @Test
    public void queryPagerByCriteria() {
        List<StaffEntity> all = JSONArray.parseArray(loadData("data/t_staff/staff_saveBatch.json"), StaffEntity.class);
        staffDao.saveBatch(all);

//        CriteriaBuilder cb = staffDao.criteriaBuilder();
//        CriteriaQuery<StaffEntity> q = cb.createQuery(StaffEntity.class);
//        Root<StaffEntity> r = q.from(StaffEntity.class);
//        q.where(cb.lessThanOrEqualTo(r.get("salary"), NUM_60000));
//
//        assertEquals(staffDao.queryPagerByCriteria(q, 1, 2).size(), 2);
    }

    /**
     * Description: <br>
     */
    @Transactional
    @Test
    public void queryPagerBySpecification() {
//       List<StaffEntity> all = JSONArray.parseArray(loadData("data/t_staff/staff_saveBatch.json"), StaffEntity.class);
//        staffDao.saveBatch(all);

//        assertEquals(
//            staffDao
//                .queryPagerBySpecification(
//                    (r, q, cb) -> q.where(cb.lessThanOrEqualTo(r.get("salary"), NUM_60000)).getRestriction(), 1, 2)
//                .size(),
//            2);

    }

    /**
     * Description: <br>
     */
    @Transactional
    @Test
    public void queryPagerBySpecification2() {
//       List<StaffEntity> all = JSONArray.parseArray(loadData("data/t_staff/staff_saveBatch.json"), StaffEntity.class);
//        staffDao.saveBatch(all);

//        List<StaffEntity> entites = staffDao.queryPagerBySpecification((r, q, cb) -> q
//            .multiselect(r.get("id").alias("id"), r.get("firstName").alias("first_name"),
//                r.get("lastName").alias("last_name"))
//            .where(cb.lessThanOrEqualTo(r.get("salary"), NUM_60000)).getRestriction(), 1, 2, StaffEntity.class);
//        assertEquals(entites.size(), 2);
//        assertNull(entites.get(0).getDepartment());
        
//        QueryWrapper<StaffEntity> wrapper = new QueryWrapper<>();
//        wrapper.le("salary", NUM_60000);
//        PagerList<StaffEntity> staffEntities = staffDao.queryPagerBySpecification(wrapper, 1, 2, StaffEntity.class);
//        assertEquals(staffEntities.size(), 2);
    }

    /**
     * Description: <br>
     */
    @Transactional
    @Test
    public void queryPager() {
        List<StaffEntity> all = JSONArray.parseArray(loadData("data/t_staff/staff_saveBatch.json"), StaffEntity.class);
        staffDao.saveBatch(all);
        assertEquals(staffDao.queryPager(q -> q.le("salary", NUM_60000), 1, 2).size(), 2);
    }

    /**
     * Description: <br>
     */
    @Transactional
    @Test
    public void queryPager2() {
        List<StaffEntity> all = JSONArray.parseArray(loadData("data/t_staff/staff_saveBatch.json"), StaffEntity.class);
        staffDao.saveBatch(all);
        List<StaffEntity> entites = staffDao.queryPager(
            q -> q.select("id").select("firstName").select("lastName").le("salary", NUM_60000), 1, 2,
            StaffEntity.class);
        assertEquals(entites.size(), 2);
        assertNull(entites.get(0).getDepartment());
    }

    /**
     * Description: <br>
     */
    @Transactional
    @Test
    public void queryPagerByLambda() {

        List<StaffEntity> all = JSONArray.parseArray(loadData("data/t_staff/staff_saveBatch.json"), StaffEntity.class);
        staffDao.saveBatch(all);
        assertEquals(staffDao.queryPagerByLambda(q -> q.le(StaffEntity::getSalary, NUM_60000), 1, 2).size(), 2);

    }

    /**
     * Description: <br>
     */
    @Transactional
    @Test
    public void queryPagerByLambda2() {

        List<StaffEntity> all = JSONArray.parseArray(loadData("data/t_staff/staff_saveBatch.json"), StaffEntity.class);
//        staffDao.saveBatch(all);
        List<StaffEntity> entites = staffDao.queryPagerByLambda(q -> q.select(StaffEntity::getId)
            .select(StaffEntity::getFirstName).select(StaffEntity::getLastName).le(StaffEntity::getSalary, NUM_60000),
            1, 2, StaffEntity.class);
        assertEquals(entites.size(), 2);
        assertNull(entites.get(0).getDepartment());
    }

    /**
     * Description: 根据条件查询 <br>
     */
    @Transactional
    @Test
    public void queryByCriteria() {
        List<StaffEntity> all = JSONArray.parseArray(loadData("data/t_staff/staff_saveBatch.json"), StaffEntity.class);
        staffDao.saveBatch(all);

//        CriteriaBuilder cb = staffDao.criteriaBuilder();
//        CriteriaQuery<Tuple> q = cb.createTupleQuery();
//        Root<StaffEntity> r = q.from(StaffEntity.class);
//        q.multiselect(cb.count(r.get("id")), r.get("department")).groupBy(r.get("department"));
//
//         staffDao.queryByLambda(q -> q.count());
        System.out.println(1);
        PagerList<StaffEntity> results = staffDao.queryPagerByLambda(q ->
                q.groupBy(StaffEntity::getDepartment), 1, NUM_10, StaffEntity.class);


        assertEquals(results.size(), NUM_3);
    }

    /**
     * Description: 根据条件查询 <br>
     */
    @Transactional
    @Test
    public void queryBySpecification() {
//       List<StaffEntity> all = JSONArray.parseArray(loadData("data/t_staff/staff_saveBatch.json"), StaffEntity.class);
//        staffDao.saveBatch(all);

//        List<StaffEntity> entites = staffDao
//            .queryBySpecification((r, q, cb) -> q.where(cb.le(r.get("salary"), NUM_60000)).getRestriction());
//
//        assertEquals(entites.size(), NUM_5);
    }

    /**
     * Description: 根据条件查询 <br>
     */
    @Transactional
    @Test
    public void queryBySpecification2() {
//       List<StaffEntity> all = JSONArray.parseArray(loadData("data/t_staff/staff_saveBatch.json"), StaffEntity.class);
//        staffDao.saveBatch(all);

//        List<StaffEntity> entites = staffDao.queryBySpecification(
//            (r, q, cb) -> q.multiselect(cb.count(r.get("id")).alias("id"), r.get("department").alias("department"))
//                .groupBy(r.get("department")).getRestriction(),
//            StaffEntity.class);
//
//        assertEquals(entites.size(), NUM_3);
    }

    /**
     * Description: 根据条件查询 <br>
     */
    @Transactional
    @Test
    public void query() {
        List<StaffEntity> all = JSONArray.parseArray(loadData("data/t_staff/staff_saveBatch.json"), StaffEntity.class);
        staffDao.saveBatch(all);

        List<StaffEntity> entites = staffDao.query(q -> q.le("salary", NUM_60000));

        assertEquals(entites.size(), NUM_5);
    }

    /**
     * Description:根据条件查询 <br>
     */
    @Transactional
    @Test
    public void query2() {

        List<StaffEntity> all = JSONArray.parseArray(loadData("data/t_staff/staff_saveBatch.json"), StaffEntity.class);
        staffDao.saveBatch(all);

        List<CountEntity> entites = staffDao
            .query(q -> q
                    .count("id", "total")
                    .select("department", "name")
                    .groupBy("department"), CountEntity.class);

        assertEquals(entites.size(), NUM_3);
    }

    /**
     * Description: 根据条件查询 <br>
     */
    @Transactional
    @Test
    public void queryByLambda() {

        List<StaffEntity> all = JSONArray.parseArray(loadData("data/t_staff/staff_saveBatch.json"), StaffEntity.class);
        staffDao.saveBatch(all);

        List<StaffEntity> entites = staffDao.queryByLambda(q -> q.le(StaffEntity::getSalary, NUM_60000));

        assertEquals(entites.size(), NUM_5);

    }

    /**
     * Description: 根据条件查询 <br>
     */
    @Transactional
    @Test
    public void queryByLambda2() {

        List<StaffEntity> all = JSONArray.parseArray(loadData("data/t_staff/staff_saveBatch.json"), StaffEntity.class);
//        staffDao.saveBatch(all);

        List<CountEntity> entites = staffDao.queryByLambda(
            q -> q.count(StaffEntity::getId, CountEntity::getTotal)
                .select(StaffEntity::getDepartment, CountEntity::getName).groupBy(StaffEntity::getDepartment),
            CountEntity.class);

        assertEquals(entites.size(), NUM_3);

    }


    /**
     *
     */
    @Test
    public void queryDepth() {

        List<StaffEntity> all = JSONArray.parseArray(loadData("data/t_staff/staff_saveBatch.json"), StaffEntity.class);
        staffDao.saveBatch(all);

        List<StaffEntity> entites = staffDao
                .query(q -> q.select("tTest.testName", "name")
                        .eq("tTest.userName", "john_doe"), StaffEntity.class);

        assertEquals(entites.size(), NUM_1);

    }

    /**
     *
     */
    @Test
    public void querymongo1() {
        DataParam param = new DataParam();
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("department", "项目三部");
        param.setParamMap(paramMap);
        param.setBeanType(StaffEntity.class);
        param.setReturnType(List.class);

        String mongodbSql = "{\n"
                + "    \"aggregate\": \"t_staff\",\n"
                + "    \"pipeline\": [\n"
                + "        {\n"
                + "            \"$match\": {\n"
                + "                \"department\": \"$department\"\n"
                + "            }\n"
                + "        }\n"
                + "    ],\n"
                + "    \"cursor\": {}\n"
                + "}\n";

        List<StaffEntity> results = (List<StaffEntity>) staffDao.query(mongodbSql, param);
        assertTrue(results.size() > 0);
        for (StaffEntity staff : results) {
            assertEquals("项目三部", staff.getDepartment());
        }
    }

    /**
     *
     */
    @Test
    public void querymongo2() {
        DataParam param = new DataParam();
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("department", "项目三部");
        paramMap.put("pageIndex", 0); // 第几页
        paramMap.put("pageSize", NUM_10); // 每页大小
        param.setParamMap(paramMap);
        param.setBeanType(StaffEntity.class);

        String mongodbSql = "{\n"
                + "    \"aggregate\": \"t_staff\",\n"
                + "    \"pipeline\": [\n"
                + "        {\n"
                + "            \"$match\": {\n"
                + "                \"department\": \"$department\"\n"
                + "            }\n"
                + "        },\n"
                + "        {\"$skip\": \"$pageIndex\"},\n"
                + "        {\"$limit\": \"$pageSize\"}\n"
                + "    ],\n"
                + "    \"cursor\": {}\n"
                + "}\n";

        PagerList<StaffEntity> results = (PagerList<StaffEntity>) staffDao.query(mongodbSql, param);

        assertTrue(results.size() > 0);
        for (StaffEntity staff : results) {
            assertEquals("项目三部", staff.getDepartment());
        }
    }

    /**
     *
     */
    @Test
    public void ddlmongo1() {
        DataParam param = new DataParam();
        Map<String, Object> paramMap = new HashMap<>();
        StaffEntity.TestName testName = new StaffEntity.TestName("张三", "李四");
        paramMap.put("t_test", testName);
        param.setParamMap(paramMap);
        param.setBeanType(StaffEntity.class);

        DataParam queryParam = new DataParam();
        Map<String, Object> queryParamMap = new HashMap<>();
        queryParamMap.put("t_test", testName);
        queryParam.setParamMap(queryParamMap);
        queryParam.setReturnType(List.class);
        queryParam.setBeanType(StaffEntity.class);
        String insertSql = "{ \"insert\": \"t_staff\", \"documents\": "
                + "[{ \"_id\": 101, \"first_name\": \"John\", \"last_name\": \"Doe\", \"position\": \"Developer\", "
                + "\"department\": \"Engineering\", \"salary\": 75000.0, \"hire_date\": "
                + "{ \"$date\": \"2021-06-01T00:00:00Z\" }, \"t_test\": {\"userName\": \"${t_test.userName}\""
                + ",\"testName\": \"${t_test.testName}\"},\"name\": \"John Doe\" }]}";
        int insertResult =  staffDao.excuteSql(insertSql, param);
        insertSql = "{ \"insert\": \"t_staff\", \"documents\": [{ \"_id\": 15, \"first_name\": \"John\", "
                + "\"last_name\": \"Doe\", \"position\": \"Developer\", \"department\": \"Engineering\", "
                + "\"salary\": 75000.0, \"hire_date\": { \"$date\": \"2021-06-01T00:00:00Z\" }, "
                + "\"t_test\": {\"userName\": \"john_doe\",\"testName\": \"奥特玛\"},\"name\": \"John Doe\" }]}";
        insertResult =  staffDao.excuteSql(insertSql, param);
        String queryCommand = "{ \"find\": \"t_staff\", \"filter\": { \"t_test.userName\": \"$t_test.userName\" } }";
        List<StaffEntity> query = (List<StaffEntity>) staffDao.query(queryCommand, queryParam);
        assertEquals(query.size(), NUM_1);

        String updateSql = "{ \"update\": \"t_staff\", \"updates\": [{ \"q\": { \"t_test.userName\": \"john_doe\" }"
                + ", \"u\": { \"$set\": { \"t_test.userName\": \"$t_test.userName\" } },"
                + " \"upsert\": false,\"multi\": true }]}";
        int updateResult = staffDao.excuteSql(updateSql, param);
        query = (List<StaffEntity>) staffDao.query(queryCommand, queryParam);
        assertEquals(query.size(), 2);

        String deleteSql = "{ \"delete\": \"t_staff\", \"deletes\": [{ \"q\": "
                + "{ \"t_test.userName\": \"$t_test.userName\" }, \"limit\": 0 }]}";
        int deleteResult = staffDao.excuteSql(deleteSql, param);
        queryParam.setReturnType(void.class);
        query = (List<StaffEntity>) staffDao.query(queryCommand, queryParam);
        assertEquals(query.size(), 0);

    }



}
