/**
 * 
 */
package com.fccfc.framework.db.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.fccfc.framework.db.core.DaoConstants;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2014年10月23日 <br>
 * @since V1.0<br>
 * @see com.fccfc.framework.dao.annotation <br>
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Param {
    String pageSize = DaoConstants.PAGE_SIZE;

    String pageIndex = DaoConstants.PAGE_INDEX;

    String value() default "";
}
