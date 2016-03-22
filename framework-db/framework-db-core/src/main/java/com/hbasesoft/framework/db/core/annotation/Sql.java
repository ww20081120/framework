/**
 * 
 */
package com.hbasesoft.framework.db.core.annotation;

import java.io.Serializable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.hbasesoft.framework.common.GlobalConstants;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2014年10月23日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.dao.annotation <br>
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Sql {
    /** sql语句 */
    String value() default GlobalConstants.BLANK;

    /** sql文件的位置 */
    String path() default GlobalConstants.BLANK;

    /** 返回值指定类型 */
    Class<?> bean() default Serializable.class;

    /** 数据源标识 */
    String dbId() default GlobalConstants.BLANK;

    String dataSource() default GlobalConstants.BLANK;
}
