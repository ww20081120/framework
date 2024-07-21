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
import com.hbasesoft.framework.db.demo.dao.CourseDao;
import com.hbasesoft.framework.db.demo.dao.StudentDao;
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
@Transactional
@Deprecated
public class GenericDaoTester {

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

    /** studentDao */
    @Resource
    private StudentDao studentDao;

    /** courseDao */
    @Resource
    private CourseDao courseDao;

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @BeforeEach
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
    public void countCoursePass() {
        int count = studentDao.countCoursePass("语文");
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
    public void queryStudentCourse() {
        List<StudentEntity> entityes = studentDao.queryStudentCourse(null, 1, NUM_5);
        Assert.isTrue(entityes.size() == NUM_5, ErrorCodeDef.SYSTEM_ERROR);

        entityes = studentDao.queryStudentCourse(null, 1, NUM_3);
        Assert.isTrue(entityes.size() == NUM_3, ErrorCodeDef.SYSTEM_ERROR);

        StudentEntity entity = new StudentEntity();
        entity.setAge(NUM_19);
        entityes = studentDao.queryStudentCourse(entity, 1, NUM_10);
        Assert.isTrue(entityes.size() == NUM_3, ErrorCodeDef.SYSTEM_ERROR);

        entity = new StudentEntity();
        entity.setAge(NUM_18);
        entity.setName("张%");
        entityes = studentDao.queryStudentCourse(entity, 1, NUM_10);
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
    public void save() {
        StudentEntity entity = new StudentEntity();
        entity.setAge(NUM_16);
        entity.setName("张三丰");

        studentDao.save(entity);
        String id = entity.getId();

        entity = studentDao.get(StudentEntity.class, id);
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
    public void delete() {
        StudentEntity entity = new StudentEntity();
        entity.setAge(NUM_16);
        entity.setName("张三丰");

        studentDao.save(entity);
        String id = entity.getId();

        studentDao.delete(entity);

        entity = studentDao.get(StudentEntity.class, id);
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
    public void batchSave() throws UnsupportedEncodingException, FileNotFoundException {
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
            studentDao.batchSave(students);
            return true;
        }, GlobalConstants.DEFAULT_LINES);
        int s2 = studentDao.countStudentSize();
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
    public void batchExecute() {
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
            studentDao.batchExecute("insert into t_student (id, name, age) values (?, ?, ?)", students,
                GlobalConstants.DEFAULT_LINES);
            return true;
        });
        int s2 = studentDao.countStudentSize();
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
    public void get() {
        StudentEntity entity = studentDao.get(StudentEntity.class, "1");
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
    public void findUniqueByProperty() {
        CourseEntity entity = courseDao.findUniqueByProperty(CourseEntity.class, CourseEntity.COURSE_NAME, "语文");
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
    public void findByProperty() {
        List<StudentEntity> entities = studentDao.findByProperty(StudentEntity.class, StudentEntity.AGE, NUM_18);
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
    public void loadAll() {
        List<StudentEntity> entities = studentDao.loadAll(StudentEntity.class);
        int size = studentDao.countStudentSize();
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
    public void deleteEntityById() {
        StudentEntity entity = new StudentEntity();
        entity.setAge(NUM_16);
        entity.setName("张三丰");

        studentDao.save(entity);
        String id = entity.getId();

        studentDao.deleteEntityById(StudentEntity.class, id);

        entity = studentDao.get(StudentEntity.class, id);
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
    public void deleteAllEntitie() {
        List<StudentEntity> entities = studentDao.loadAll(StudentEntity.class);
        studentDao.deleteAllEntitie(entities);
        int size = studentDao.countStudentSize();
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
    public void deleteAllEntitiesByIds() {
        int s1 = studentDao.countStudentSize();
        studentDao.deleteAllEntitiesByIds(StudentEntity.class, Arrays.asList("1", "2", "3"));
        int s2 = studentDao.countStudentSize();
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
    public void updateEntity() {
        StudentEntity entity = studentDao.get(StudentEntity.class, "1");
        Assert.notEquals(entity.getName(), "李四", ErrorCodeDef.SYSTEM_ERROR);
        entity.setName("李四");
        studentDao.updateEntity(entity);

        StudentEntity e2 = studentDao.get(StudentEntity.class, "1");
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
    public void findByQueryString() {
        List<StudentEntity> entities = studentDao
            .findByQueryString("from com.hbasesoft.framework.db.demo.entity.StudentEntity where id = '1'");
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
    public void updateBySqlString() {
        StudentEntity entity = studentDao.get(StudentEntity.class, "1");
        Assert.notEquals(entity.getName(), "李四", ErrorCodeDef.SYSTEM_ERROR);

        studentDao.updateBySqlString("update t_student set name = '李四' where id = '1'");

        // 因为上面已经查询过一次，hibernate做了缓存，在事务未提交前，操作的都是缓存，所以得清理掉， 才能从数据库中重新查询。
        studentDao.clear();

        StudentEntity e2 = studentDao.get(StudentEntity.class, "1");
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
    public void singleResult() {
        StudentEntity entity = studentDao
            .singleResult("from com.hbasesoft.framework.db.demo.entity.StudentEntity where id = '1'");
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
    public void getPageList() {
        DetachedCriteria criteria = DetachedCriteria.forClass(StudentEntity.class);
        PagerList<StudentEntity> entities = studentDao.getPageList(criteria, 1, 1);
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
    public void getListByCriteriaQuery() {
        DetachedCriteria criteria = DetachedCriteria.forClass(StudentEntity.class);
        criteria.add(Restrictions.eq(StudentEntity.AGE, NUM_18));
        List<StudentEntity> es1 = studentDao.getListByCriteriaQuery(criteria);

        List<StudentEntity> es2 = studentDao.findByProperty(StudentEntity.class, StudentEntity.AGE, NUM_18);
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
    public void getCriteriaQuery() {
        DetachedCriteria criteria = DetachedCriteria.forClass(CourseEntity.class);
        criteria.add(Restrictions.eq(CourseEntity.COURSE_NAME, "语文"));
        CourseEntity e1 = courseDao.getCriteriaQuery(criteria);

        CourseEntity e2 = courseDao.findUniqueByProperty(CourseEntity.class, CourseEntity.COURSE_NAME, "语文");

        Assert.equals(e1.getId(), e2.getId(), ErrorCodeDef.SYSTEM_ERROR);
    }
}
