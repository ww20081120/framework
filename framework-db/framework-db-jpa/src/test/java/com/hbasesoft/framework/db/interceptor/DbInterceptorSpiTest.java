/****************************************************************************************
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.db.interceptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Proxy;
import java.util.Set;

import org.junit.jupiter.api.Test;

import jakarta.persistence.criteria.CommonAbstractCriteria;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Root;

/**
 * {@link DbInterceptor} SPI 与 {@link InterceptorContext} 的行为契约测试。
 *
 * <p>验证拦截点声明、四 default 挂点的最小实现合法性、上下文取值器返回构造原始引用
 * （拦截器原地改写生效的前提），以及类型化取值器的操作类型守卫。</p>
 *
 * @author 王伟
 * @version 1.0
 * @since 4.4.3
 */
class DbInterceptorSpiTest {

    @Test
    void interceptTypesShouldReturnDeclaredSet() {
        DbInterceptor interceptor = () -> Set.of(DbOperationType.QUERY, DbOperationType.UPDATE);
        assertEquals(Set.of(DbOperationType.QUERY, DbOperationType.UPDATE), interceptor.interceptTypes(),
            "interceptTypes() 应原样返回声明集");
    }

    @Test
    void defaultMethodsShouldBeNoOp() {
        // 仅声明单个拦截点、其余依赖 default 空实现的最小实现必须合法
        DbInterceptor minimal = () -> Set.of(DbOperationType.INSERT);
        InterceptorContext context = insertContext();
        minimal.interceptInsert(context);
        minimal.interceptQuery(context);
        minimal.interceptUpdate(context);
        minimal.interceptDelete(context);
    }

    @Test
    void accessorsShouldReturnConstructorReferences() {
        Root<?> root = proxy(Root.class);
        CommonAbstractCriteria query = proxy(CriteriaQuery.class);
        CriteriaBuilder builder = proxy(CriteriaBuilder.class);
        Object entity = new Object();
        InterceptorContext context = new InterceptorContext(root, query, builder, entity, Object.class, "t_demo");

        assertSame(root, context.root(), "root 应为构造原始引用而非拷贝");
        assertSame(query, context.criteriaQuery(), "criteriaQuery 应为构造原始引用而非拷贝");
        assertSame(builder, context.criteriaBuilder(), "criteriaBuilder 应为构造原始引用而非拷贝");
        assertSame(entity, context.entity(), "entity 应为构造原始引用，拦截器经引用原地改写属性");
        assertEquals(Object.class, context.entityClazz());
        assertEquals("t_demo", context.tableName());
        assertSame(query, context.criteriaQuery(), "QUERY 场景类型化取值应成功");
    }

    @Test
    void typedAccessorsShouldCastByOperationType() {
        CommonAbstractCriteria update = proxy(CriteriaUpdate.class);
        CommonAbstractCriteria delete = proxy(CriteriaDelete.class);

        InterceptorContext updateContext = new InterceptorContext(proxy(Root.class), update,
            proxy(CriteriaBuilder.class), null, Object.class, "t_demo");
        assertSame(update, updateContext.criteriaUpdate(), "UPDATE 场景应以 CriteriaUpdate 形式取值");

        InterceptorContext deleteContext = new InterceptorContext(proxy(Root.class), delete,
            proxy(CriteriaBuilder.class), null, Object.class, "t_demo");
        assertSame(delete, deleteContext.criteriaDelete(), "DELETE 场景应以 CriteriaDelete 形式取值");
    }

    @Test
    void typedAccessorShouldRejectWrongOperationType() {
        InterceptorContext queryContext = queryContext();
        assertThrows(IllegalStateException.class, queryContext::criteriaUpdate,
            "QUERY 上下文不允许以 CriteriaUpdate 形式取值");
        assertThrows(IllegalStateException.class, queryContext::criteriaDelete,
            "QUERY 上下文不允许以 CriteriaDelete 形式取值");
    }

    @Test
    void entityMayBeNullForNonInsertScenario() {
        InterceptorContext context = queryContext();
        assertNull(context.entity(), "非 INSERT 场景 entity 允许为 null");
    }

    private static InterceptorContext queryContext() {
        return new InterceptorContext(proxy(Root.class), proxy(CriteriaQuery.class),
            proxy(CriteriaBuilder.class), null, Object.class, "t_demo");
    }

    private static InterceptorContext insertContext() {
        return new InterceptorContext(proxy(Root.class), proxy(CriteriaQuery.class),
            proxy(CriteriaBuilder.class), new Object(), Object.class, "t_demo");
    }

    /**
     * 构造仅实现指定接口的空代理，替身 jakarta Criteria 接口（本测试只验证 SPI 引用传递，不触碰实现行为）。
     */
    @SuppressWarnings("unchecked")
    private static <T> T proxy(Class<?> type) {
        return (T) Proxy.newProxyInstance(DbInterceptorSpiTest.class.getClassLoader(), new Class<?>[] {
            type
        }, (p, method, args) -> method.getReturnType() == boolean.class ? Boolean.FALSE : null);
    }
}
