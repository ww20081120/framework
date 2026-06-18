package com.hbasesoft.framework.db.demo.mysql.scan;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import com.hbasesoft.framework.db.BaseJpaDao;
import com.hbasesoft.framework.db.core.annotation.handler.SQLHandler;
import com.hbasesoft.framework.db.core.config.DaoConfig;

/**
 * 验证 {@link SQLHandler#invoke(Class)} 在扫描 {@code @Dao} 接口时会跳过 default 方法
 * 以及 lambda 编译生成的合成方法 (如 {@code _$deserializeLambda$}), 不再误判为缺失 SQL 文件.
 * <p>
 * 复现 issue: 启动时报
 * {@code 初始化sql失败, 未找到 com.hbasesoft...Dao_$deserializeLambda$ 的 sql}
 * </p>
 */
class SQLHandlerLambdaDefaultMethodTest {

    @Test
    void invoke_shouldNotFailOnLambdaAndDefaultMethods() {
        DaoConfig daoConfig = new DaoConfig();
        // baseDaoType 用于让 AbstractAnnotationHandler 收集 BaseDao 的方法签名, 以验证
        // "default 方法 + lambda 合成方法" 都不会被误判为需要 SQL 的抽象方法
        daoConfig.setBaseDaoType(BaseJpaDao.class);

        SQLHandler sqlHandler = new SQLHandler(daoConfig);

        assertThat(Arrays.stream(LambdaDefaultScanDao.class.getDeclaredMethods()).anyMatch(Method::isSynthetic))
            .isTrue();
        assertThatCode(() -> sqlHandler.invoke(LambdaDefaultScanDao.class))
            .doesNotThrowAnyException();
    }
}
