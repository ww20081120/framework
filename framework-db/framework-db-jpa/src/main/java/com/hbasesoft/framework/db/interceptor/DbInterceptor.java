/****************************************************************************************
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.db.interceptor;

import java.util.Set;

/**
 * 数据访问拦截扩展 SPI——在 BaseHibernateDao 各操作收口处对 Criteria 语句或实体做改写。
 *
 * <p>心智模型对齐 MyBatis Interceptor：实现方通过 {@link #interceptTypes()} 声明自己关心的
 * 操作类型集合，框架只把声明过的操作派发到对应挂点；四个挂点均为 default 空实现，
 * "只声明单个拦截点、其余保持零操作"是最小合法实现。</p>
 *
 * <p>本 SPI 仅提供拦截机制（拦截点声明 + 上下文传递 + 改写原地生效），
 * 不携带任何业务语义；具体业务插件基于本 SPI 自行实现。</p>
 *
 * <p>四个挂点的收口位置：</p>
 * <ul>
 * <li>{@link #interceptInsert(InterceptorContext)}：save 收口 persist 之前——可经
 * {@link InterceptorContext#entity()} 直接改写实体公共字段（心智对齐 MyBatis-Plus
 * MetaObjectHandler）；</li>
 * <li>{@link #interceptQuery(InterceptorContext)}：查询收口的 toPredicate 之后、语句执行之前——可经
 * {@link InterceptorContext#criteriaQuery()} 追加谓词；</li>
 * <li>{@link #interceptUpdate(InterceptorContext)}：updateBySpecification 收口的 set 子句装配后——
 * set 子句与 where 条件均可经 {@link InterceptorContext#criteriaUpdate()} 改写；</li>
 * <li>{@link #interceptDelete(InterceptorContext)}：deleteBySpecification 收口——where 条件可经
 * {@link InterceptorContext#criteriaDelete()} 改写。</li>
 * </ul>
 *
 * @author 王伟
 * @version 1.0
 * @since 4.4.3
 */
public interface DbInterceptor {

    /**
     * 声明本拦截器关心的操作类型集合，未声明的操作类型不会被派发到对应挂点。
     *
     * @return 操作类型集合，不允许为 null 或空集
     */
    Set<DbOperationType> interceptTypes();

    /**
     * 插入挂点：save 收口 persist 之前调用，可经 {@link InterceptorContext#entity()}
     * 直接改写待持久化实体的公共字段。
     *
     * @param context 拦截上下文（entity 承载待持久化实体）
     */
    default void interceptInsert(InterceptorContext context) {
        // 默认零操作
    }

    /**
     * 查询挂点：查询收口的 toPredicate 之后、语句执行之前调用，可经
     * {@link InterceptorContext#criteriaQuery()} 追加谓词。
     *
     * @param context 拦截上下文（criteriaQuery 承载 CriteriaQuery）
     */
    default void interceptQuery(InterceptorContext context) {
        // 默认零操作
    }

    /**
     * 更新挂点：updateBySpecification 收口的 set 子句装配后调用，set 子句与
     * where 条件均可经 {@link InterceptorContext#criteriaUpdate()} 改写。
     *
     * @param context 拦截上下文（criteriaQuery 承载 CriteriaUpdate）
     */
    default void interceptUpdate(InterceptorContext context) {
        // 默认零操作
    }

    /**
     * 删除挂点：deleteBySpecification 收口处调用，where 条件可经
     * {@link InterceptorContext#criteriaDelete()} 改写。
     *
     * @param context 拦截上下文（criteriaQuery 承载 CriteriaDelete）
     */
    default void interceptDelete(InterceptorContext context) {
        // 默认零操作
    }
}
