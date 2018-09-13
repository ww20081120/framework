/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.db.demo;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.utils.Assert;
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

    public void save() {
    }

    public void delete() {
    }

    public void batchSave() {
    }

    public void batchExecute() {
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
