/****************************************************************************************
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.db.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.annotation.Configuration;

import com.hbasesoft.framework.db.interceptor.DbInterceptor;
import com.hbasesoft.framework.db.interceptor.DbInterceptorChain;
import com.hbasesoft.framework.db.interceptor.DbOperationType;

/**
 * 数据访问拦截链装配类——探测容器内全部 {@link DbInterceptor} 类型 Bean 并装配进
 * {@link DbInterceptorChain} 静态注册器。
 *
 * <p>业务方实现 {@link DbInterceptor} 接口并注册为 Spring Bean 即被本类自动装配生效，
 * 无需任何额外配置。装配时机采用 {@link SmartInitializingSingleton}（全部单例初始化完成后回调）：
 * BeanFactoryPostProcessor 时机拦截器 Bean 尚未注册，不可用；DAO 收口点运行时直接读
 * {@link DbInterceptorChain} 静态注册表，与装配时机解耦。</p>
 *
 * <p>容器内未注册任何拦截器时不触碰注册表（保持空链短路直通），数据访问行为与
 * 机制引入前完全一致。</p>
 *
 * @author 王伟
 * @version 1.0
 * @since 4.4.3
 */
@Configuration
public class DbInterceptorConfig implements SmartInitializingSingleton {

    /** Bean 工厂——用于探测拦截器 Bean 并在 fail-fast 时指明 Bean 名 */
    private final ListableBeanFactory beanFactory;

    /**
     * 构造装配类。
     *
     * @param beanFactory Spring Bean 工厂（容器注入 ApplicationContext 兼容）
     */
    public DbInterceptorConfig(final ListableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    /**
     * 全部单例就绪后探测拦截器 Bean、校验声明集并装配拦截链。
     */
    @Override
    public void afterSingletonsInstantiated() {
        Map<String, DbInterceptor> beans = beanFactory.getBeansOfType(DbInterceptor.class);
        if (beans.isEmpty()) {
            // 未注册拦截器：保持空链，收口点零成本直通
            return;
        }
        List<DbInterceptor> interceptors = new ArrayList<>(beans.size());
        for (Map.Entry<String, DbInterceptor> entry : beans.entrySet()) {
            Set<DbOperationType> types = entry.getValue().interceptTypes();
            if (types == null || types.isEmpty()) {
                throw new IllegalArgumentException("数据访问拦截器 Bean[" + entry.getKey() + "] 的 interceptTypes()"
                    + " 返回 null 或空集，请在声明集中至少声明一种操作类型");
            }
            interceptors.add(entry.getValue());
        }
        DbInterceptorChain.install(interceptors);
    }
}
