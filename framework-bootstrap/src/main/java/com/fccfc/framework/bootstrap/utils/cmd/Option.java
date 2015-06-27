/**
 * 
 */
package com.fccfc.framework.bootstrap.utils.cmd;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.fccfc.framework.common.GlobalConstants;

/**
 * <Description> <br>
 * 
 * @author wangwei<br>
 * @version 1.0<br>
 * @CreateDate 2015年6月27日 <br>
 * @see com.fccfc.framework.bootstrap.utils.cmd <br>
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Option {

    String name();

    String value() default GlobalConstants.BLANK;

    boolean required() default false;

}
