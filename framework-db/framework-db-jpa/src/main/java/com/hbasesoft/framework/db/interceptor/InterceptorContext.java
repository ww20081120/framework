/****************************************************************************************
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.db.interceptor;

import jakarta.persistence.criteria.CommonAbstractCriteria;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Root;

/**
 * 拦截上下文——承载 {@link DbInterceptor} 挂点可访问、可改写的 Criteria 三件套与目标信息。
 *
 * <p>所有字段均为构造原始引用而非拷贝：拦截器通过 {@link #entity()} 直接改写实体属性、
 * 通过 {@link #criteriaQuery()} / {@link #criteriaUpdate()} / {@link #criteriaDelete()}
 * 直接追加谓词或改写 set / where 子句，改写原地生效。</p>
 *
 * <p>{@code criteriaQuery} 字段按操作类型承载不同实例：QUERY 场景为
 * {@link CriteriaQuery}，UPDATE 场景为 {@link CriteriaUpdate}，DELETE 场景为
 * {@link CriteriaDelete}——三者公共父类为 {@link CommonAbstractCriteria}，
 * 消费端应按当前操作类型选用对应的类型化取值方法，取错形式将抛出
 * {@link IllegalStateException}。</p>
 *
 * @author 王伟
 * @version 1.0
 * @since 4.4.3
 */
public class InterceptorContext {

    /** 操作目标实体的 Criteria 根对象。 */
    private final Root<?> root;

    /** 按操作类型承载的 Criteria 语句（CriteriaQuery / CriteriaUpdate / CriteriaDelete 之一）。 */
    private final CommonAbstractCriteria criteriaQuery;

    /** 构造 Criteria 元素的工厂。 */
    private final CriteriaBuilder criteriaBuilder;

    /** 待持久化实体（仅 INSERT 场景承载，其余场景为 null）。 */
    private final Object entity;

    /** 操作目标实体类型。 */
    private final Class<?> entityClazz;

    /** 操作目标表名。 */
    private final String tableName;

    /**
     * 构造拦截上下文。
     *
     * @param root 操作目标实体的 Criteria 根对象
     * @param criteriaQuery 按操作类型承载的 Criteria 语句
     * @param criteriaBuilder 构造 Criteria 元素的工厂
     * @param entity 待持久化实体（仅 INSERT 场景承载，其余场景传 null）
     * @param entityClazz 操作目标实体类型
     * @param tableName 操作目标表名
     */
    public InterceptorContext(final Root<?> root, final CommonAbstractCriteria criteriaQuery,
        final CriteriaBuilder criteriaBuilder, final Object entity, final Class<?> entityClazz,
        final String tableName) {
        this.root = root;
        this.criteriaQuery = criteriaQuery;
        this.criteriaBuilder = criteriaBuilder;
        this.entity = entity;
        this.entityClazz = entityClazz;
        this.tableName = tableName;
    }

    /**
     * 获取操作目标实体的 Criteria 根对象。
     *
     * @return 构造传入的原始 {@link Root} 引用
     */
    public Root<?> root() {
        return root;
    }

    /**
     * 获取构造传入的 Criteria 语句原始引用。
     *
     * @return 构造传入的原始 {@link CommonAbstractCriteria} 引用
     */
    public CommonAbstractCriteria criteria() {
        return criteriaQuery;
    }

    /**
     * 以查询语句形式取值（QUERY 场景）。
     *
     * @return 构造传入的原始 {@link CriteriaQuery} 引用
     * @throws IllegalStateException 当前上下文承载的不是查询语句时抛出
     */
    public CriteriaQuery<?> criteriaQuery() {
        return cast(CriteriaQuery.class, "QUERY");
    }

    /**
     * 以更新语句形式取值（UPDATE 场景）。
     *
     * @return 构造传入的原始 {@link CriteriaUpdate} 引用
     * @throws IllegalStateException 当前上下文承载的不是更新语句时抛出
     */
    public CriteriaUpdate<?> criteriaUpdate() {
        return cast(CriteriaUpdate.class, "UPDATE");
    }

    /**
     * 以删除语句形式取值（DELETE 场景）。
     *
     * @return 构造传入的原始 {@link CriteriaDelete} 引用
     * @throws IllegalStateException 当前上下文承载的不是删除语句时抛出
     */
    public CriteriaDelete<?> criteriaDelete() {
        return cast(CriteriaDelete.class, "DELETE");
    }

    /**
     * 获取构造 Criteria 元素的工厂。
     *
     * @return 构造传入的原始 {@link CriteriaBuilder} 引用
     */
    public CriteriaBuilder criteriaBuilder() {
        return criteriaBuilder;
    }

    /**
     * 获取待持久化实体（仅 INSERT 场景承载）。
     *
     * @return 构造传入的原始实体引用，非 INSERT 场景为 null
     */
    public Object entity() {
        return entity;
    }

    /**
     * 获取操作目标实体类型。
     *
     * @return 操作目标实体类型
     */
    public Class<?> entityClazz() {
        return entityClazz;
    }

    /**
     * 获取操作目标表名。
     *
     * @return 操作目标表名
     */
    public String tableName() {
        return tableName;
    }

    /**
     * 按期望的操作类型对承载语句做类型安全取值。
     *
     * @param <T> 期望的 Criteria 语句类型
     * @param type 期望的 Criteria 语句类型字面量
     * @param expected 期望的操作类型名（用于异常消息）
     * @return 类型匹配的语句原始引用
     * @throws IllegalStateException 当前上下文承载的语句类型与期望不符时抛出
     */
    private <T extends CommonAbstractCriteria> T cast(final Class<T> type, final String expected) {
        if (!type.isInstance(criteriaQuery)) {
            throw new IllegalStateException(
                "当前拦截上下文承载的并非 " + expected + " 语句，请改用与操作类型匹配的取值方法");
        }
        return type.cast(criteriaQuery);
    }
}
