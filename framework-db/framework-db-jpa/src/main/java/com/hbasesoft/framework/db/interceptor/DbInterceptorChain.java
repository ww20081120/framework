/****************************************************************************************
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.db.interceptor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.core.annotation.AnnotationAwareOrderComparator;

/**
 * 数据访问拦截链——{@link DbInterceptor} 的静态注册器与按操作类型分发器。
 *
 * <p>DAO 基类不是 Spring Bean（由 {@code AutoProxyBeanFactory} 纯 new 装配），无法注入容器资源，
 * 故对齐 {@code TransactionManagerHolder} 的既有静态持有者先例：Spring 侧装配类
 * （{@code DbInterceptorConfig}，SmartInitializingSingleton 时机）探测全部
 * {@link DbInterceptor} 类型 Bean 后调 {@link #install(List)} 写入本类，
 * 各执行收口点调 {@link #dispatch(DbOperationType, InterceptorContext)} 派发。</p>
 *
 * <p>装配时按四种 {@link DbOperationType} 预索引出已排序的不可变拦截器列表，
 * {@link #dispatch} 只遍历声明了当前操作类型的拦截器——派发开销与该类型拦截器数成正比；
 * 未注册任何拦截器时空链短路直接返回，数据访问行为与机制引入前完全一致（零变化保证）。
 * 排序语义对齐 Spring 全局约定（{@link org.springframework.core.Ordered} 接口与 &#64;Order 注解），
 * 在 {@link #install} 内统一执行，使装配方无需关心排序细节、测试可脱离容器验证顺序。</p>
 *
 * <p>拦截器抛出的异常不做任何捕获，原样传播中断当前执行（fail-fast 不吞）。</p>
 *
 * @author 王伟
 * @version 1.0
 * @since 4.4.3
 */
public final class DbInterceptorChain {

    /** 已装配的拦截器注册表——按操作类型预索引的不可变有序列表，原子引用整体替换保证装配后的安全发布 */
    private static final AtomicReference<Map<DbOperationType, List<DbInterceptor>>> REGISTRY =
        new AtomicReference<>(emptyRegistry());

    /**
     * 私有构造——纯静态工具类。
     */
    private DbInterceptorChain() {
    }

    /**
     * 装配拦截链（整体替换既有装配）。
     *
     * <p>逐个校验 {@code interceptTypes()} 声明集，null 或空集直接抛
     * {@link IllegalArgumentException}（消息携带拦截器类名，fail-fast 不静默跳过）；
     * 随后按 Spring 排序约定（{@link org.springframework.core.Ordered} / &#64;Order）排序，并按操作类型预索引为
     * 不可变列表，以原子引用整体替换注册表安全发布。</p>
     *
     * @param interceptors 待装配的拦截器列表，不允许为 null（空列表表示装配空链）
     */
    public static void install(final List<DbInterceptor> interceptors) {
        if (interceptors == null) {
            throw new IllegalArgumentException("装配拦截链时传入的拦截器列表不允许为 null");
        }
        List<DbInterceptor> sorted = new ArrayList<>(interceptors);
        AnnotationAwareOrderComparator.sort(sorted);

        // 预索引：单次遍历完成声明校验与按类型分桶，四种操作类型各建一条已排序不可变链
        Map<DbOperationType, List<DbInterceptor>> indexed = new EnumMap<>(DbOperationType.class);
        for (DbOperationType type : DbOperationType.values()) {
            indexed.put(type, new ArrayList<>());
        }
        for (DbInterceptor interceptor : sorted) {
            Set<DbOperationType> types = interceptor.interceptTypes();
            if (types == null || types.isEmpty()) {
                throw new IllegalArgumentException("拦截器[" + interceptor.getClass().getName()
                    + "] 的 interceptTypes() 返回 null 或空集，请在声明集中至少声明一种操作类型");
            }
            for (DbOperationType type : types) {
                if (type != null) {
                    indexed.get(type).add(interceptor);
                }
            }
        }
        for (DbOperationType type : DbOperationType.values()) {
            indexed.put(type, Collections.unmodifiableList(indexed.get(type)));
        }
        REGISTRY.set(Collections.unmodifiableMap(indexed));
    }

    /**
     * 清空拦截链，回到未装配的空链状态。
     *
     * <p>供测试用例间隔离与运行期重装配场景使用；清空后所有收口点空链短路直通。</p>
     */
    static void clear() {
        REGISTRY.set(emptyRegistry());
    }

    /**
     * 按操作类型派发拦截链。
     *
     * <p>只遍历声明了当前操作类型的拦截器（装配时已过滤与排序），空链短路直接返回；
     * 拦截器抛出的异常原样传播。</p>
     *
     * @param type 当前执行的操作类型
     * @param context 拦截上下文（承载可原地改写的 Criteria 三件套或实体）
     */
    public static void dispatch(final DbOperationType type, final InterceptorContext context) {
        List<DbInterceptor> chain = REGISTRY.get().get(type);
        if (chain.isEmpty()) {
            // 空链短路：未注册拦截器时零成本放行，行为与机制引入前完全一致
            return;
        }
        for (DbInterceptor interceptor : chain) {
            switch (type) {
                case INSERT -> interceptor.interceptInsert(context);
                case QUERY -> interceptor.interceptQuery(context);
                case UPDATE -> interceptor.interceptUpdate(context);
                case DELETE -> interceptor.interceptDelete(context);
                default -> throw new IllegalArgumentException("不支持的数据访问操作类型：" + type);
            }
        }
    }

    /**
     * 构造四键齐全的空注册表。
     *
     * @return 每种操作类型均对应空列表的不可变注册表
     */
    private static Map<DbOperationType, List<DbInterceptor>> emptyRegistry() {
        Map<DbOperationType, List<DbInterceptor>> indexed = new EnumMap<>(DbOperationType.class);
        for (DbOperationType type : DbOperationType.values()) {
            indexed.put(type, Collections.emptyList());
        }
        return Collections.unmodifiableMap(indexed);
    }
}
