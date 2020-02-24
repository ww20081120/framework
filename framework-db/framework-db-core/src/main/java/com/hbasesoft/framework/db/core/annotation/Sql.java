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
    /**
     * Description: sql语句 <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    String value() default GlobalConstants.BLANK;

    /**
     * Description: sql文件的位置<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    String path() default GlobalConstants.BLANK;

    /**
     * Description: 返回值指定类型 <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    Class<?> bean() default Serializable.class;

    /**
     * 
     * Description: 数据源标识<br> 
     *  
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    String dbId() default GlobalConstants.BLANK;

    /**
     * 
     * Description: <br> 
     *  
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    String dataSource() default GlobalConstants.BLANK;
}
