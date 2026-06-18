package com.hbasesoft.framework.db.demo.mysql.scan;

import java.util.List;

import com.hbasesoft.framework.db.BaseJpaDao;
import com.hbasesoft.framework.db.Dao;
import com.hbasesoft.framework.db.demo.mysql.entity.StaffEntity;

/**
 * 用于构造 default 方法 + lambda 合成方法的扫描场景.
 */
@Dao
public interface LambdaDefaultScanDao extends BaseJpaDao<StaffEntity> {

    /**
     * @return 高薪员工
     */
    default List<StaffEntity> findHighSalary() {
        return queryByLambda(q -> q
            .eq(StaffEntity::getFirstName, "Michael")
            .ge(StaffEntity::getSalary, 50000.0));
    }
}
