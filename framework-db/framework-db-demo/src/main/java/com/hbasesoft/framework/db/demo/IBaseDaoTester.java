/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.db.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.hbasesoft.framework.db.demo.dao.StudentDao;
import com.hbasesoft.framework.db.demo.entity.CountEntity;
import com.hbasesoft.framework.db.demo.entity.StudentEntity;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.GlobalConstants;
import com.hbasesoft.framework.common.utils.Assert;
import com.hbasesoft.framework.common.utils.CommonUtil;
import com.hbasesoft.framework.common.utils.io.IOUtil;
import com.hbasesoft.framework.db.demo.dao.mysql.ICourseMySqlDao;
import com.hbasesoft.framework.db.demo.entity.CourseEntity;

import jakarta.annotation.Resource;
/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2018年9月13日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.db.demo <br>
 */
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IBaseDaoTester {

    /** Number */
    private static final int NUM_10 = 10;

    /** Number */
    private static final int NUM_16 = 16;

    /** Number */
    private static final int NUM_18 = 18;

    /** Number */
    private static final int NUM_19 = 19;

    /** Number */
    private static final int NUM_200000 = 200000;

    /** Number */
    private static final int NUM_3 = 3;

    /** Number */
    private static final int NUM_5 = 5;

    /** dao */
    @Resource
    private ICourseMySqlDao iCourseMySqlDao;

    /** dao */
    @Resource
    private StudentDao studentDao;

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @BeforeEach
    @Transactional
    public void createTable() {
        studentDao.createTable();
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Test
    @Transactional
    public void countCourseByCriteria() {
//        CriteriaBuilder cb = iCourseDao.criteriaBuilder();
//        CriteriaQuery<Long> query = cb.createQuery(Long.class);
//        Root<CourseEntity> root = query.from(CourseEntity.class);
//        query.select(cb.count(root));
//        Long count = iCourseDao.getByCriteria(query);
//        Assert.isTrue(count.intValue() == NUM_3, ErrorCodeDef.FAILURE);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Test
    @Transactional
    public void countCourseBySpecification() {
//        Long course = iCourseDao.getBySpecification(
//            (root, query, cb) -> query.multiselect(cb.count(root.get("id"))).getRestriction(), Long.class);
//        Assert.isTrue(course.intValue() == NUM_3, ErrorCodeDef.FAILURE);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Test
    @Transactional
    public void countCourseByLamberda() {
        Long course = iCourseMySqlDao.getByLambda(q -> q.count(CourseEntity::getId), Long.class);
        Assert.isTrue(course.intValue() == NUM_3, ErrorCodeDef.FAILURE);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Test
    @Transactional
    public void countCourse() {
        Long course = iCourseMySqlDao.get(q -> q.count("id"), Long.class);
        Assert.isTrue(course.intValue() == NUM_3, ErrorCodeDef.FAILURE);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Test
    @Transactional
    public void countAliasByLamberda() {
        CountEntity course = iCourseMySqlDao.getByLambda(q -> q.count(CourseEntity::getId, CountEntity::getTotal),
            CountEntity.class);
        Assert.isTrue(course.getTotal().intValue() == NUM_3, ErrorCodeDef.FAILURE);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Test
    @Transactional
    public void countAlias() {
        CountEntity course = iCourseMySqlDao.get(q -> q.count("id", "total"), CountEntity.class);
        Assert.isTrue(course.getTotal().intValue() == NUM_3, ErrorCodeDef.FAILURE);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @SuppressWarnings("unchecked")
    @Test
    @Transactional
    public void countAlias2Map() {
        Map<String, Object> course = iCourseMySqlDao.get(q -> q.count("id", "count"), Map.class);
        Assert.isTrue(((Long) course.get("count")).intValue() == NUM_3, ErrorCodeDef.FAILURE);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Test
    @Transactional
    public void countCoursePass() {
        int count = studentDao.countCoursePass("语文");
        Assert.isTrue(count == 2, ErrorCodeDef.FAILURE);
        System.out.println("语文考及格的有两人");
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Test
    @Transactional
    public void delete() {
        StudentEntity entity = new StudentEntity();
        entity.setAge(NUM_16);
        entity.setName("张三丰");

        studentDao.save(entity);
        String id = entity.getId();

        studentDao.delete(entity);

        entity = studentDao.get(id);
        Assert.isNull(entity, ErrorCodeDef.FAILURE);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Test
    @Transactional
    public void delete2() {
        StudentEntity entity = new StudentEntity();
        entity.setAge(NUM_16);
        entity.setName("张三丰");

        studentDao.save(entity);
        String id = entity.getId();
        studentDao.delete(q -> q.eq("id", id));
        entity = studentDao.get(q -> q.eq("id", id));

        Assert.isNull(entity, ErrorCodeDef.FAILURE);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Test
    @Transactional
    public void deleteAll() {
        List<StudentEntity> entities = studentDao.queryAll();
        studentDao.deleteBatch(entities);
        int size = studentDao.countStudentSize();
        Assert.isTrue(size == 0, ErrorCodeDef.FAILURE);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Test
    @Transactional
    public void deleteAllEntitiesByIds() {
        int s1 = studentDao.countStudentSize();
        studentDao.deleteByIds(Arrays.asList("1", "2", "3"));
//        iStudentDao.clear();
        int s2 = studentDao.countStudentSize();
        Assert.isTrue(s1 - s2 == NUM_3, ErrorCodeDef.FAILURE);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Test
    @Transactional
    public void deleteById() {
        StudentEntity entity = new StudentEntity();
        entity.setAge(NUM_16);
        entity.setName("张三丰");

        studentDao.save(entity);
        String id = entity.getId();

        studentDao.deleteById(id);

//        iStudentDao.clear();

        entity = studentDao.get(id);
        Assert.isNull(entity, ErrorCodeDef.FAILURE);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Test
    @Transactional
    public void executeBatch() {
        int s1 = studentDao.countStudentSize();
        IOUtil.batchProcessFile(new File("Student.csv"), line -> {
            if (StringUtils.isNotEmpty(line)) {
                String[] strs = StringUtils.split(line, GlobalConstants.SPLITOR);
                if (strs.length >= 2) {
                    return new Object[] {
                        CommonUtil.getTransactionID(), strs[0], strs[1]
                    };
                }
            }
            return null;
        }, (students, pageIndex, pageSize) -> {
//            iStudentDao.executeBatch("insert into t_student (id, name, age) values (?, ?, ?)", students,
//                GlobalConstants.DEFAULT_LINES);
            return true;
        });
        int s2 = studentDao.countStudentSize();
        Assert.isTrue(s2 - s1 == NUM_200000, ErrorCodeDef.FAILURE);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Test
    @Transactional
    public void get() {
        StudentEntity entity = studentDao.get("1");
        Assert.equals(entity.getName(), "张三", ErrorCodeDef.FAILURE);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Test
    @Transactional
    public void getByHql() {
//        StudentEntity entity = iStudentDao
//            .getByHql("from com.hbasesoft.framework.db.demo.entity.StudentEntity where id = '1'");
//        Assert.equals(entity.getName(), "张三", ErrorCodeDef.FAILURE);

    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Test
    @Transactional
    public void getByProperty() {
        CourseEntity entity = iCourseMySqlDao.get(q -> q.eq(CourseEntity.COURSE_NAME, "语文"));
        Assert.equals(entity.getId(), "1", ErrorCodeDef.FAILURE);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Test
    @Transactional
    public void groupBy() {
        List<StudentEntity> entity = studentDao.queryByLambda(
            q -> q.select(StudentEntity::getAge).count(StudentEntity::getId).groupBy(StudentEntity::getAge));
        System.out.println(entity);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Test
    @Transactional
    public void groupByAlias() {
        List<CountEntity> entity = studentDao.queryByLambda(
            q -> q.select(StudentEntity::getAge, CountEntity::getName)
                .count(StudentEntity::getId, CountEntity::getTotal).groupBy(StudentEntity::getAge),
            CountEntity.class);
        System.out.println(entity);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @SuppressWarnings("rawtypes")
    @Test
    @Transactional
    public void groupByAlias2Map() {
        List<Map> entity = studentDao.query(q -> q.select("age").count("id", "count").max("id").min("id", "mid")
            .avg("age", "avgAge").sum("age", "sumAge").groupBy("age"), Map.class);
        System.out.println(entity);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Test
    @Transactional
    public void queryAll() {
        List<StudentEntity> entities = studentDao.queryAll();
        int size = studentDao.countStudentSize();
        Assert.isTrue(entities.size() == size, ErrorCodeDef.FAILURE);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Test
    @Transactional
    public void queryByHql() {
//        List<StudentEntity> entities = iStudentDao
//            .queryByHql("from com.hbasesoft.framework.db.demo.entity.StudentEntity where id = '1'");
//        Assert.isTrue(entities.size() == 1, ErrorCodeDef.FAILURE);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Test
    @Transactional
    public void queryByProperty() {
        List<StudentEntity> entities = studentDao.query(q -> q.eq(StudentEntity.AGE, NUM_18));
        Assert.isTrue(entities.size() == 2, ErrorCodeDef.FAILURE);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Test
    @Transactional
    public void queryStudentCourse() {
        List<StudentEntity> entityes = studentDao.queryStudentCourse(null, 1, NUM_5);
        Assert.isTrue(entityes.size() == NUM_5, ErrorCodeDef.FAILURE);

        entityes = studentDao.queryStudentCourse(null, 1, NUM_3);
        Assert.isTrue(entityes.size() == NUM_3, ErrorCodeDef.FAILURE);

        StudentEntity entity = new StudentEntity();
        entity.setAge(NUM_19);
        entityes = studentDao.queryStudentCourse(entity, 1, NUM_10);
        Assert.isTrue(entityes.size() == NUM_3, ErrorCodeDef.FAILURE);

        entity = new StudentEntity();
        entity.setAge(NUM_18);
        entity.setName("张%");
        entityes = studentDao.queryStudentCourse(entity, 1, NUM_10);
        Assert.isTrue(entityes.size() == NUM_3, ErrorCodeDef.FAILURE);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Test
    @Transactional
    public void save() {
        StudentEntity entity = new StudentEntity();
        entity.setAge(NUM_16);
        entity.setName("张三丰");

        studentDao.save(entity);
        String id = entity.getId();

        entity = studentDao.get(id);
        Assert.equals(entity.getName(), "张三丰", ErrorCodeDef.FAILURE);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @throws UnsupportedEncodingException
     * @throws FileNotFoundException <br>
     */
    @Test
    @Transactional
    public void saveBatch() throws UnsupportedEncodingException, FileNotFoundException {
        int s1 = studentDao.countStudentSize();
        IOUtil.batchProcessFile(new File("Student.csv"), line -> {
            if (StringUtils.isNotEmpty(line)) {
                String[] strs = StringUtils.split(line, GlobalConstants.SPLITOR);
                if (strs.length >= 2) {
                    StudentEntity entity = new StudentEntity();
                    entity.setName(strs[0]);
                    entity.setAge(Integer.parseInt(strs[1]));
                    return entity;
                }
            }
            return null;
        }, (students, pageIndex, pageSize) -> {
            studentDao.saveBatch(students);
            return true;
        }, GlobalConstants.DEFAULT_LINES);
        int s2 = studentDao.countStudentSize();
        Assert.isTrue(s2 - s1 == NUM_200000, ErrorCodeDef.FAILURE);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Test
    @Transactional
    public void update() {
        StudentEntity entity = studentDao.get("1");
        Assert.notEquals(entity.getName(), "李四", ErrorCodeDef.FAILURE);
        entity.setName("李四");
        studentDao.update(entity);

        StudentEntity e2 = studentDao.get("1");
        Assert.equals(e2.getName(), "李四", ErrorCodeDef.FAILURE);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Test
    @Transactional
    public void update2() {
        StudentEntity entity = studentDao.get("1");
        assertNotEquals(entity.getName(), "李四");
        studentDao.update(q -> q.set("name", "李四").eq("id", "1"));
//        iStudentDao.clear();
        StudentEntity e2 = studentDao.get("1");
        assertEquals(e2.getName(), "李四");
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Test
    @Transactional
    public void updateBySql() {
        StudentEntity entity = studentDao.get("1");
        Assert.notEquals(entity.getName(), "李四", ErrorCodeDef.FAILURE);

//        iStudentDao.updateBySql("update t_student set name = '李四' where id = '1'");
//
//        // 因为上面已经查询过一次，hibernate做了缓存，在事务未提交前，操作的都是缓存，所以得清理掉， 才能从数据库中重新查询。
//        iStudentDao.clear();

        StudentEntity e2 = studentDao.get("1");
        Assert.equals(e2.getName(), "李四", ErrorCodeDef.FAILURE);
    }
}
