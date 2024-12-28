/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.db.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.hbasesoft.framework.db.core.config.DaoTypeDef;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2017年9月23日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.db <br>
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({
    ElementType.TYPE, ElementType.METHOD
})
public @interface DataSource {

    /**
     * Description: 数据库的类型，默认db-jdbc支持的数据库类型，其他还有mongdb、elasticsearch、cassandra等nosql数据库<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    DaoTypeDef type() default DaoTypeDef.db;

    /**
     * @return String
     */
    String value();

    /**
     * 用于在项目中自行控制数据源的切换 <br/>
     * 1、提供一个实现了com.hbasesoft.framework.db.core.annotation.handler.EnhanceDynamicDataSourceHandler类
     * 2、该类注册在spring容器中，注解中enhanceCode填写spring中的bean的名称
     * 
     * @return String
     */
    String enhanceCode() default "";
}
