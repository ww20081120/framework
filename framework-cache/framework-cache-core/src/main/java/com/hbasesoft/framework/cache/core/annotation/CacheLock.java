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
 * <Description> 分布式锁<br>
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
public @interface CacheLock {

    /**
     * Description: 锁名称<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    String value();

    /**
     * Description: 附属关键字<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    String key() default GlobalConstants.BLANK;

    /**
     * Description: 锁过期时间<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    int expireTime() default 10;

    /**
     * Description: 锁超时时间<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    long timeOut() default 2000;
}
