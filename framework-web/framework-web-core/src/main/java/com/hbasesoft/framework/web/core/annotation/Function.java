/**
 * 
 */
package com.hbasesoft.framework.web.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.hbasesoft.framework.web.core.WebConstant;

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
public @interface Function {
    String value() default WebConstant.ALL_IN_ONE_PERMISSION;

    String[] event() default {};
}
