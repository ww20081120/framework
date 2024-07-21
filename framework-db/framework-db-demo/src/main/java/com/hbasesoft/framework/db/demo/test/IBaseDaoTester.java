/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.db.demo.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.GlobalConstants;
import com.hbasesoft.framework.common.utils.Assert;
import com.hbasesoft.framework.common.utils.CommonUtil;
import com.hbasesoft.framework.common.utils.io.IOUtil;
import com.hbasesoft.framework.db.core.utils.PagerList;
import com.hbasesoft.framework.db.demo.Application;
import com.hbasesoft.framework.db.demo.dao.ICourseDao;
import com.hbasesoft.framework.db.demo.dao.IStudentDao;
import com.hbasesoft.framework.db.demo.entity.CourseEntity;
import com.hbasesoft.framework.db.demo.entity.StudentEntity;

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
    private static final int NUM_3 = 3;

    /** Number */
    private static final int NUM_5 = 5;

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

    /** dao */
    @Resource
    private IStudentDao iStudentDao;

    /** dao */
    @Resource
    private ICourseDao iCourseDao;

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
        iStudentDao.createTable();
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
        int count = iStudentDao.countCoursePass("语文");
        Assert.isTrue(count == 2, ErrorCodeDef.SYSTEM_ERROR);
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
    public void queryStudentCourse() {
        List<StudentEntity> entityes = iStudentDao.queryStudentCourse(null, 1, NUM_5);
        Assert.isTrue(entityes.size() == NUM_5, ErrorCodeDef.SYSTEM_ERROR);

        entityes = iStudentDao.queryStudentCourse(null, 1, NUM_3);
        Assert.isTrue(entityes.size() == NUM_3, ErrorCodeDef.SYSTEM_ERROR);

        StudentEntity entity = new StudentEntity();
        entity.setAge(NUM_19);
        entityes = iStudentDao.queryStudentCourse(entity, 1, NUM_10);
        Assert.isTrue(entityes.size() == NUM_3, ErrorCodeDef.SYSTEM_ERROR);

        entity = new StudentEntity();
        entity.setAge(NUM_18);
        entity.setName("张%");
        entityes = iStudentDao.queryStudentCourse(entity, 1, NUM_10);
        Assert.isTrue(entityes.size() == NUM_3, ErrorCodeDef.SYSTEM_ERROR);
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

        iStudentDao.save(entity);
        String id = entity.getId();

        entity = iStudentDao.get(id);
        Assert.equals(entity.getName(), "张三丰", ErrorCodeDef.SYSTEM_ERROR);
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

        iStudentDao.save(entity);
        String id = entity.getId();

        iStudentDao.delete(entity);

        entity = iStudentDao.get(id);
        Assert.isNull(entity, ErrorCodeDef.SYSTEM_ERROR);
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
    public void deleteByCriteria() {
        StudentEntity entity = new StudentEntity();
        entity.setAge(NUM_16);
        entity.setName("张三丰");

        iStudentDao.save(entity);
        String id = entity.getId();

        CriteriaBuilder cb = iStudentDao.getCriteriaBuilder();
        CriteriaDelete<StudentEntity> query = cb.createCriteriaDelete(StudentEntity.class);
        Root<StudentEntity> root = query.from(StudentEntity.class);
        query.where(cb.equal(root.get("id"), id));
        iStudentDao.deleteByCriteria(query);

        entity = iStudentDao.getBySpecification((r1, q1, cb1) -> {
            return q1.where(cb1.equal(r1.get("id"), id)).getRestriction();
        });

        Assert.isNull(entity, ErrorCodeDef.SYSTEM_ERROR);
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
    public void deleteBySpecification() {
        StudentEntity entity = new StudentEntity();
        entity.setAge(NUM_16);
        entity.setName("张三丰");

        iStudentDao.save(entity);
        String id = entity.getId();

        iStudentDao.deleteBySpecification((r1, q1, cb1) -> {
            return q1.where(cb1.equal(r1.get("id"), id)).getRestriction();
        });

        entity = iStudentDao.getBySpecification((r1, q1, cb1) -> {
            return q1.where(cb1.equal(r1.get("id"), id)).getRestriction();
        });

        Assert.isNull(entity, ErrorCodeDef.SYSTEM_ERROR);
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
    public void deleteByQuery() {
        StudentEntity entity = new StudentEntity();
        entity.setAge(NUM_16);
        entity.setName("张三丰");

        iStudentDao.save(entity);
        String id = entity.getId();

        iStudentDao.delete(q -> q.eq("id", id).build());

        entity = iStudentDao.get(q -> q.eq("id", id).build());

        Assert.isNull(entity, ErrorCodeDef.SYSTEM_ERROR);
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
    public void deleteByLambdaQuery() {
        StudentEntity entity = new StudentEntity();
        entity.setAge(NUM_16);
        entity.setName("张三丰");

        iStudentDao.save(entity);
        String id = entity.getId();

        iStudentDao.deleteByLambda(q -> q.eq(StudentEntity::getId, id).build());

        entity = iStudentDao.getByLambda(q -> q.eq(StudentEntity::getId, id).build());

        Assert.isNull(entity, ErrorCodeDef.SYSTEM_ERROR);
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
        int s1 = iStudentDao.countStudentSize();
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
            iStudentDao.saveBatch(students);
            return true;
        }, GlobalConstants.DEFAULT_LINES);
        int s2 = iStudentDao.countStudentSize();
        Assert.isTrue(s2 - s1 == NUM_200000, ErrorCodeDef.SYSTEM_ERROR);
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
        int s1 = iStudentDao.countStudentSize();
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
            iStudentDao.executeBatch("insert into t_student (id, name, age) values (?, ?, ?)", students,
                GlobalConstants.DEFAULT_LINES);
            return true;
        });
        int s2 = iStudentDao.countStudentSize();
        Assert.isTrue(s2 - s1 == NUM_200000, ErrorCodeDef.SYSTEM_ERROR);
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
        StudentEntity entity = iStudentDao.get("1");
        Assert.equals(entity.getName(), "张三", ErrorCodeDef.SYSTEM_ERROR);
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
        CourseEntity entity = iCourseDao.getByProperty(CourseEntity.COURSE_NAME, "语文");
        Assert.equals(entity.getId(), "1", ErrorCodeDef.SYSTEM_ERROR);
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
        List<StudentEntity> entities = iStudentDao.queryByProperty(StudentEntity.AGE, NUM_18);
        Assert.isTrue(entities.size() == 2, ErrorCodeDef.SYSTEM_ERROR);
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
        List<StudentEntity> entities = iStudentDao.queryAll();
        int size = iStudentDao.countStudentSize();
        Assert.isTrue(entities.size() == size, ErrorCodeDef.SYSTEM_ERROR);
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

        iStudentDao.save(entity);
        String id = entity.getId();

        iStudentDao.deleteById(id);

        entity = iStudentDao.get(id);
        Assert.isNull(entity, ErrorCodeDef.SYSTEM_ERROR);
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
        List<StudentEntity> entities = iStudentDao.queryAll();
        iStudentDao.delete(entities);
        int size = iStudentDao.countStudentSize();
        Assert.isTrue(size == 0, ErrorCodeDef.SYSTEM_ERROR);
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
        int s1 = iStudentDao.countStudentSize();
        iStudentDao.deleteByIds(Arrays.asList("1", "2", "3"));
        int s2 = iStudentDao.countStudentSize();
        Assert.isTrue(s1 - s2 == NUM_3, ErrorCodeDef.SYSTEM_ERROR);
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
    public void notIn() {
        List<StudentEntity> datas = iStudentDao.query(q -> q.notIn("id", "1", "2", "3").build());
        System.out.println(datas);
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
    public void notInList() {
        List<String> ids = Arrays.asList("1", "2", "3");
        List<StudentEntity> datas = iStudentDao.query(q -> q.notIn("id", ids).build());
        System.out.println(datas);
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
    public void notInListLambda() {
        List<String> ids = Arrays.asList("1", "2", "3");
        List<StudentEntity> datas = iStudentDao.queryByLambda(q -> q.notIn(StudentEntity::getId, ids).build());
        System.out.println(datas);
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
        StudentEntity entity = iStudentDao.get("1");
        Assert.notEquals(entity.getName(), "李四", ErrorCodeDef.SYSTEM_ERROR);
        entity.setName("李四");
        iStudentDao.update(entity);

        StudentEntity e2 = iStudentDao.get("1");
        Assert.equals(e2.getName(), "李四", ErrorCodeDef.SYSTEM_ERROR);
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
    public void updateBySpecification() {
        iStudentDao.updateBySpecification((root, query, cb) -> {
            return query.set(root.get("name"), "李四").where(cb.equal(root.get("id"), "1")).getRestriction();
        });

        StudentEntity e2 = iStudentDao.get("1");
        Assert.equals(e2.getName(), "李四", ErrorCodeDef.SYSTEM_ERROR);
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
    public void updateByQuery() {
        iStudentDao.update(q -> q.set("name", "李四").eq("id", 1).build());

        StudentEntity e2 = iStudentDao.get("1");
        Assert.equals(e2.getName(), "李四", ErrorCodeDef.SYSTEM_ERROR);
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
    public void updateByLambdaQuery() {
        iStudentDao.updateByLambda(q -> q.set(StudentEntity::getName, "李四").eq(StudentEntity::getId, 1).build());

        StudentEntity e2 = iStudentDao.get("1");
        Assert.equals(e2.getName(), "李四", ErrorCodeDef.SYSTEM_ERROR);
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
        StudentEntity entity = iStudentDao.get("1");
        Assert.notEquals(entity.getName(), "李四", ErrorCodeDef.SYSTEM_ERROR);

        iStudentDao.updateBySql("update t_student set name = '李四' where id = '1'");

        // 因为上面已经查询过一次，hibernate做了缓存，在事务未提交前，操作的都是缓存，所以得清理掉， 才能从数据库中重新查询。
        iStudentDao.clear();

        StudentEntity e2 = iStudentDao.get("1");
        Assert.equals(e2.getName(), "李四", ErrorCodeDef.SYSTEM_ERROR);
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
        List<StudentEntity> entities = iStudentDao
            .queryByHql("from com.hbasesoft.framework.db.demo.entity.StudentEntity where id = '1'");
        Assert.isTrue(entities.size() == 1, ErrorCodeDef.SYSTEM_ERROR);
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
        StudentEntity entity = iStudentDao
            .getByHql("from com.hbasesoft.framework.db.demo.entity.StudentEntity where id = '1'");
        Assert.equals(entity.getName(), "张三", ErrorCodeDef.SYSTEM_ERROR);

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
    public void queryPagerByCriteria() {
        DetachedCriteria criteria = DetachedCriteria.forClass(StudentEntity.class);
        PagerList<StudentEntity> entities = iStudentDao.queryPagerByCriteria(criteria, 1, 1);
        Assert.isTrue(entities.size() < entities.getTotalCount(), ErrorCodeDef.SYSTEM_ERROR);
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
    public void queryPagerBySpecification() {
        PagerList<StudentEntity> entities = iStudentDao.queryPagerBySpecification((root, query, cb) -> {
            return query.where(cb.equal(root.get("name"), "张三")).getRestriction();
        }, 1, 1);
        Assert.isTrue(entities.size() == entities.getTotalCount(), ErrorCodeDef.SYSTEM_ERROR);
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
    public void queryPagerByQuery() {
        PagerList<StudentEntity> entities = iStudentDao.queryPager(q -> q.eq("name", "张三").build(), 1, 1);
        Assert.isTrue(entities.size() == entities.getTotalCount(), ErrorCodeDef.SYSTEM_ERROR);
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
    public void queryPagerByLambdaQuery() {
        PagerList<StudentEntity> entities = iStudentDao
            .queryPagerByLambda(q -> q.eq(StudentEntity::getName, "张三").build(), 1, 2);
        Assert.isTrue(entities.size() == entities.getTotalCount(), ErrorCodeDef.SYSTEM_ERROR);
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
    public void queryByCriteria() {
        DetachedCriteria criteria = DetachedCriteria.forClass(StudentEntity.class);
        criteria.add(Restrictions.eq(StudentEntity.AGE, NUM_18));
        List<StudentEntity> es1 = iStudentDao.queryByCriteria(criteria);

        List<StudentEntity> es2 = iStudentDao.queryByProperty(StudentEntity.AGE, NUM_18);
        Assert.isTrue(es1.size() == es2.size(), ErrorCodeDef.SYSTEM_ERROR);
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
    public void queryBySpecification() {
        List<StudentEntity> es1 = iStudentDao.queryBySpecification((root, query, cb) -> {
            return query.where(cb.equal(root.get(StudentEntity.AGE), NUM_18)).getRestriction();
        });

        List<StudentEntity> es2 = iStudentDao.queryByProperty(StudentEntity.AGE, NUM_18);
        Assert.isTrue(es1.size() == es2.size(), ErrorCodeDef.SYSTEM_ERROR);
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
    public void queryByQuery() {
        List<StudentEntity> es1 = iStudentDao.query(q -> q.eq("age", NUM_18).build());
        Assert.isTrue(es1.size() == 2, ErrorCodeDef.SYSTEM_ERROR);
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
    public void queryByLambdaQuery() {
        List<StudentEntity> es1 = iStudentDao.queryByLambda(q -> q.eq(StudentEntity::getAge, NUM_18).build());
        Assert.isTrue(es1.size() == 2, ErrorCodeDef.SYSTEM_ERROR);
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
    public void getByCriteria() {
        DetachedCriteria criteria = DetachedCriteria.forClass(CourseEntity.class);
        criteria.add(Restrictions.eq(CourseEntity.COURSE_NAME, "语文"));
        CourseEntity e1 = iCourseDao.getByCriteria(criteria);

        CourseEntity e2 = iCourseDao.getByProperty(CourseEntity.COURSE_NAME, "语文");

        Assert.equals(e1.getId(), e2.getId(), ErrorCodeDef.SYSTEM_ERROR);
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
    public void getBySpecification() {
        CourseEntity e1 = iCourseDao.getBySpecification((root, query, cb) -> {
            return query.where(cb.equal(root.get(CourseEntity.COURSE_NAME), "语文")).getRestriction();
        });

        CourseEntity e2 = iCourseDao.getByProperty(CourseEntity.COURSE_NAME, "语文");

        Assert.equals(e1.getId(), e2.getId(), ErrorCodeDef.SYSTEM_ERROR);
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
    public void getByQuery() {
        CourseEntity e1 = iCourseDao.get(q -> q.eq(CourseEntity.COURSE_NAME, "语文").build());

        CourseEntity e2 = iCourseDao.getByProperty(CourseEntity.COURSE_NAME, "语文");

        Assert.equals(e1.getId(), e2.getId(), ErrorCodeDef.SYSTEM_ERROR);
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
    public void getByLambdaQuery() {
        CourseEntity e1 = iCourseDao.getByLambda(q -> q.eq(CourseEntity::getCourseName, "语文").build());

        CourseEntity e2 = iCourseDao.getByProperty(CourseEntity.COURSE_NAME, "语文");

        Assert.equals(e1.getId(), e2.getId(), ErrorCodeDef.SYSTEM_ERROR);
    }
}
