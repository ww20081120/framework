package com.hbasesoft.framework.db.demo.mysql.scan;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import com.hbasesoft.framework.db.core.annotation.handler.SQLHandler;
import com.hbasesoft.framework.db.demo.mysql.config.AbstractMysqlTestConfig;
import com.hbasesoft.framework.db.demo.mysql.entity.StaffEntity;

import jakarta.annotation.Resource;

/**
 * 验证 {@link SQLHandler#invoke(Class)} 在扫描 {@code @Dao} 接口时会跳过 default 方法 以及 lambda 编译生成的合成方法 (如
 * {@code _$deserializeLambda$}), 不再误判为缺失 SQL 文件.
 * <p>
 * 复现 issue: 启动时报 {@code 初始化sql失败, 未找到 com.hbasesoft...Dao_$deserializeLambda$ 的 sql}
 * </p>
 */
@Transactional
public class SQLHandlerLambdaDefaultMethodTest extends AbstractMysqlTestConfig {

    @Resource
    private LambdaDefaultScanDao lambdaDefaultScanDao;

    @Test
    void invoke_shouldNotFailOnLambdaAndDefaultMethods() {
       List<StaffEntity> resp = lambdaDefaultScanDao.findHighSalary();
       System.out.println(resp.size());
    }
}
