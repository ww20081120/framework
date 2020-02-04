/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.db.demo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.GlobalConstants;
import com.hbasesoft.framework.common.utils.Assert;
import com.hbasesoft.framework.common.utils.CommonUtil;
import com.hbasesoft.framework.common.utils.io.IOUtil;
import com.hbasesoft.framework.db.core.utils.PagerList;
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
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
public class BaseDaoTester {

    @Resource
    private IStudentDao iStudentDao;

    @Resource
    private ICourseDao iCourseDao;

    @Before
    public void createTable() {
        iStudentDao.createTable();
    }

    @Test
    public void countCoursePass() {
        int count = iStudentDao.countCoursePass("语文");
        Assert.isTrue(count == 2, ErrorCodeDef.SYSTEM_ERROR_10001);
        System.out.println("语文考及格的有两人");
    }

    @Test
    public void queryStudentCourse() {
        List<StudentEntity> entityes = iStudentDao.queryStudentCourse(null, 1, 5);
        Assert.isTrue(entityes.size() == 5, ErrorCodeDef.SYSTEM_ERROR_10001);

        entityes = iStudentDao.queryStudentCourse(null, 1, 3);
        Assert.isTrue(entityes.size() == 3, ErrorCodeDef.SYSTEM_ERROR_10001);

        StudentEntity entity = new StudentEntity();
        entity.setAge(19);
        entityes = iStudentDao.queryStudentCourse(entity, 1, 10);
        Assert.isTrue(entityes.size() == 3, ErrorCodeDef.SYSTEM_ERROR_10001);

        entity = new StudentEntity();
        entity.setAge(18);
        entity.setName("张%");
        entityes = iStudentDao.queryStudentCourse(entity, 1, 10);
        Assert.isTrue(entityes.size() == 3, ErrorCodeDef.SYSTEM_ERROR_10001);
    }

    @Test
    public void save() {
        StudentEntity entity = new StudentEntity();
        entity.setAge(16);
        entity.setName("张三丰");

        iStudentDao.save(entity);
        String id = entity.getId();

        entity = iStudentDao.get(id);
        Assert.equals(entity.getName(), "张三丰", ErrorCodeDef.SYSTEM_ERROR_10001);
    }

    @Test
    public void delete() {
        StudentEntity entity = new StudentEntity();
        entity.setAge(16);
        entity.setName("张三丰");

        iStudentDao.save(entity);
        String id = entity.getId();

        iStudentDao.delete(entity);

        entity = iStudentDao.get(id);
        Assert.isNull(entity, ErrorCodeDef.SYSTEM_ERROR_10001);
    }

    @Test
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
        }, 1000);
        int s2 = iStudentDao.countStudentSize();
        Assert.isTrue(s2 - s1 == 200000, ErrorCodeDef.SYSTEM_ERROR_10001);
    }

    @Test
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
            iStudentDao.executeBatch("insert into t_student (id, name, age) values (?, ?, ?)", students, 1000);
            return true;
        });
        int s2 = iStudentDao.countStudentSize();
        Assert.isTrue(s2 - s1 == 200000, ErrorCodeDef.SYSTEM_ERROR_10001);
    }

    @Test
    public void get() {
        StudentEntity entity = iStudentDao.get("1");
        Assert.equals(entity.getName(), "张三", ErrorCodeDef.SYSTEM_ERROR_10001);
    }

    @Test
    public void getByProperty() {
        CourseEntity entity = iCourseDao.getByProperty(CourseEntity.COURSE_NAME, "语文");
        Assert.equals(entity.getId(), "1", ErrorCodeDef.SYSTEM_ERROR_10001);
    }

    @Test
    public void queryByProperty() {
        List<StudentEntity> entities = iStudentDao.queryByProperty(StudentEntity.AGE, 18);
        Assert.isTrue(entities.size() == 2, ErrorCodeDef.SYSTEM_ERROR_10001);
    }

    @Test
    public void queryAll() {
        List<StudentEntity> entities = iStudentDao.queryAll();
        int size = iStudentDao.countStudentSize();
        Assert.isTrue(entities.size() == size, ErrorCodeDef.SYSTEM_ERROR_10001);
    }

    @Test
    public void deleteById() {
        StudentEntity entity = new StudentEntity();
        entity.setAge(16);
        entity.setName("张三丰");

        iStudentDao.save(entity);
        String id = entity.getId();

        iStudentDao.deleteById(id);

        entity = iStudentDao.get(id);
        Assert.isNull(entity, ErrorCodeDef.SYSTEM_ERROR_10001);
    }

    @Test
    public void deleteAll() {
        List<StudentEntity> entities = iStudentDao.queryAll();
        iStudentDao.delete(entities);
        int size = iStudentDao.countStudentSize();
        Assert.isTrue(size == 0, ErrorCodeDef.SYSTEM_ERROR_10001);
    }

    @Test
    public void deleteAllEntitiesByIds() {
        int s1 = iStudentDao.countStudentSize();
        iStudentDao.deleteByIds(Arrays.asList("1", "2", "3"));
        int s2 = iStudentDao.countStudentSize();
        Assert.isTrue(s1 - s2 == 3, ErrorCodeDef.SYSTEM_ERROR_10001);
    }

    @Test
    public void update() {
        StudentEntity entity = iStudentDao.get("1");
        Assert.notEquals(entity.getName(), "李四", ErrorCodeDef.SYSTEM_ERROR_10001);
        entity.setName("李四");
        iStudentDao.update(entity);

        StudentEntity e2 = iStudentDao.get("1");
        Assert.equals(e2.getName(), "李四", ErrorCodeDef.SYSTEM_ERROR_10001);
    }

    @Test
    public void queryByHql() {
        List<StudentEntity> entities = iStudentDao
            .queryByHql("from com.hbasesoft.framework.db.demo.entity.StudentEntity where id = '1'");
        Assert.isTrue(entities.size() == 1, ErrorCodeDef.SYSTEM_ERROR_10001);
    }

    @Test
    public void updateBySql() {
        StudentEntity entity = iStudentDao.get("1");
        Assert.notEquals(entity.getName(), "李四", ErrorCodeDef.SYSTEM_ERROR_10001);

        iStudentDao.updateBySql("update t_student set name = '李四' where id = '1'");

        // 因为上面已经查询过一次，hibernate做了缓存，在事务未提交前，操作的都是缓存，所以得清理掉， 才能从数据库中重新查询。
        iStudentDao.clear();

        StudentEntity e2 = iStudentDao.get("1");
        Assert.equals(e2.getName(), "李四", ErrorCodeDef.SYSTEM_ERROR_10001);
    }

    @Test
    public void getByHql() {
        StudentEntity entity = iStudentDao
            .getByHql("from com.hbasesoft.framework.db.demo.entity.StudentEntity where id = '1'");
        Assert.equals(entity.getName(), "张三", ErrorCodeDef.SYSTEM_ERROR_10001);

    }

    @Test
    public void queryPagerByCriteria() {
        DetachedCriteria criteria = DetachedCriteria.forClass(StudentEntity.class);
        PagerList<StudentEntity> entities = iStudentDao.queryPagerByCriteria(criteria, 1, 1);
        Assert.isTrue(entities.size() < entities.getTotalCount(), ErrorCodeDef.SYSTEM_ERROR_10001);
    }

    @Test
    public void queryByCriteria() {
        DetachedCriteria criteria = DetachedCriteria.forClass(StudentEntity.class);
        criteria.add(Restrictions.eq(StudentEntity.AGE, 18));
        List<StudentEntity> es1 = iStudentDao.queryByCriteria(criteria);

        List<StudentEntity> es2 = iStudentDao.queryByProperty(StudentEntity.AGE, 18);
        Assert.isTrue(es1.size() == es2.size(), ErrorCodeDef.SYSTEM_ERROR_10001);
    }

    @Test
    public void getByCriteria() {
        DetachedCriteria criteria = DetachedCriteria.forClass(CourseEntity.class);
        criteria.add(Restrictions.eq(CourseEntity.COURSE_NAME, "语文"));
        CourseEntity e1 = iCourseDao.getByCriteria(criteria);

        CourseEntity e2 = iCourseDao.getByProperty(CourseEntity.COURSE_NAME, "语文");

        Assert.equals(e1.getId(), e2.getId(), ErrorCodeDef.SYSTEM_ERROR_10001);
    }
}
