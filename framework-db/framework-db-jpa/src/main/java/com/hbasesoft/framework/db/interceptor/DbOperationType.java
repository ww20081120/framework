/****************************************************************************************
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.db.interceptor;

/**
 * 数据访问操作类型——{@link DbInterceptor} 拦截点声明的枚举。
 *
 * <p>与 {@link DbInterceptor} 的四个挂点方法一一对应，拦截器通过
 * {@link DbInterceptor#interceptTypes()} 声明自身关心的操作类型集合，
 * 未声明的操作类型不会被派发到对应挂点。</p>
 *
 * @author 王伟
 * @version 1.0
 * @since 4.4.3
 */
public enum DbOperationType {

    /** 插入——对应 {@link DbInterceptor#interceptInsert(InterceptorContext)}。 */
    INSERT,

    /** 查询——对应 {@link DbInterceptor#interceptQuery(InterceptorContext)}。 */
    QUERY,

    /** 更新——对应 {@link DbInterceptor#interceptUpdate(InterceptorContext)}。 */
    UPDATE,

    /** 删除——对应 {@link DbInterceptor#interceptDelete(InterceptorContext)}。 */
    DELETE
}
