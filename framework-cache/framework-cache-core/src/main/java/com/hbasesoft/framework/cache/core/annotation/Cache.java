/**
 * 
 */
package com.hbasesoft.framework.cache.core.annotation;

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
public @interface Cache {

    String node();

    String key() default GlobalConstants.BLANK;

    int expireTime() default 0;
}
