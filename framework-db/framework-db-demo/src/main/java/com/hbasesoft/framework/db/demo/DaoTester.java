/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.db.demo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
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
import com.hbasesoft.framework.db.demo.dao.StudentDao;
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
public class DaoTester {

    @Resource
    private StudentDao studentDao;

    @Before
    public void createTable() {
        studentDao.createTable();
    }

    @Test
    public void countCoursePass() {
        int count = studentDao.countCoursePass("语文");
        Assert.isTrue(count == 2, ErrorCodeDef.SYSTEM_ERROR_10001);
        System.out.println("语文考及格的有两人");
    }

    @Test
    public void save() {
        StudentEntity entity = new StudentEntity();
        entity.setAge(16);
        entity.setName("张三丰");

        studentDao.save(entity);
        String id = entity.getId();

        entity = studentDao.get(StudentEntity.class, id);
        Assert.equals(entity.getName(), "张三丰", ErrorCodeDef.SYSTEM_ERROR_10001);
    }

    @Test
    public void delete() {
        StudentEntity entity = new StudentEntity();
        entity.setAge(16);
        entity.setName("张三丰");

        studentDao.save(entity);
        String id = entity.getId();

        studentDao.delete(entity);

        entity = studentDao.get(StudentEntity.class, id);
        Assert.isNull(entity, ErrorCodeDef.SYSTEM_ERROR_10001);
    }

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
        }, 1000);
        int s2 = studentDao.countStudentSize();
        Assert.isTrue(s2 - s1 == 200000, ErrorCodeDef.SYSTEM_ERROR_10001);
    }

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
            studentDao.batchExecute("insert into t_student (id, name, age) values (?, ?, ?)", students, 1000);
            return true;
        });
        int s2 = studentDao.countStudentSize();
        Assert.isTrue(s2 - s1 == 200000, ErrorCodeDef.SYSTEM_ERROR_10001);
    }

    public void get() {
    }

    public void getEntity() {
    }

    public void findUniqueByProperty() {
    }

    public void findByProperty() {
    }

    @Test
    public void loadAll() {
        System.out.println(studentDao.loadAll(StudentEntity.class));
    }

    public void deleteEntityById() {
    }

    public void deleteAllEntitie() {
    }

    public void deleteAllEntitiesByIds() {
    }

    public void updateEntity() {
    }

    public void findByQueryString() {
    }

    public void updateBySqlString() {
    }

    public void findListbySql() {
    }

    public void findByPropertyisOrder() {
    }

    public void singleResult() {
    }

    public void getPageList() {
    }

    public void getListByCriteriaQuery() {
    }

    public void getCriteriaQuery() {
    }

    public void findHql() {
    }

    public void executeProcedure() {
    }

    public void saveOrUpdate() {
    }
}
